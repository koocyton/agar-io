package com.doopp.agar.server;

import com.doopp.agar.server.configuration.ApplicationConfiguration;
import com.doopp.agar.server.configuration.MyWebMvcConfigurer;
import com.doopp.agar.server.socket.Hybi51Handshake;
import com.doopp.agar.server.socket.MySocketConnectionCallback;
import com.doopp.agar.server.util.ApplicationContextUtil;
import com.doopp.agar.server.util.ApplicationProperties;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.protocol.Handshake;
import io.undertow.websockets.core.protocol.version07.Hybi07Handshake;
import io.undertow.websockets.core.protocol.version08.Hybi08Handshake;
import io.undertow.websockets.core.protocol.version13.Hybi13Handshake;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.websocket;

public class UndertowServer implements InitializingBean, DisposableBean {

    private String webAppName;

    private Resource webAppRoot;

    private String host = "127.0.0.1";

    private int port = 8088;

    private Undertow server;

    private DeploymentManager manager;

    private Class<Filter>[] filterClasses;

    public void setPropertiesGroupName(String groupName) {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        this.host=applicationProperties.s(groupName + ".host");
        this.port=applicationProperties.i(groupName + ".port");
        this.webAppName=applicationProperties.s(groupName + ".webAppName");
        this.webAppRoot=applicationProperties.r(groupName + ".webAppRoot");
    }

    @SafeVarargs
    public final void setFilterClasses(Class<Filter>... filterClasses) {
        this.filterClasses = filterClasses;
    }

    @Override
    public void afterPropertiesSet() throws IOException, ServletException {

        manager = Servlets.defaultContainer().addDeployment(this.deploymentInfo());
        manager.deploy();

        MySocketConnectionCallback scc = ApplicationContextUtil.getBean(MySocketConnectionCallback.class);
        HttpHandler httpHandler = path()
                .addPrefixPath("/", manager.start())
                .addPrefixPath("/ws/agar-io", websocket());

        //SSLContext sslContext = SSLContext.getInstance("TLS");
        //sslContext.init(getKeyManagers(), null, null);

        server = Undertow.builder()
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .addHttpListener(port, host)
                // .addHttpsListener(sslPort, host, sslContext)
                .setHandler(httpHandler)
                .build();
        server.start();

        // System.out.print("\n >>> Undertow web server started at http://" + host + ":" + port + " and https://" + host + ":" + sslPort + "\n\n");
        String serverHost = host.equals("127.0.0.1") ? "localhost" : host;
        String serverPort = port==80 ? "" : ":" + port;
        System.out.printf("\n >>> Undertow web server started at http://%s%s\n\n", serverHost, serverPort);
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
        manager.stop();
        manager.undeploy();
        System.console().printf("Undertow web server on port " + port + " stopped");
    }

    private WebSocketProtocolHandshakeHandler websocket() {
        Set<Handshake> handshakes = new HashSet<>();
        handshakes.add(new Hybi51Handshake());
        // handshakes.add(new Hybi13Handshake());
        handshakes.add(new Hybi08Handshake());
        handshakes.add(new Hybi07Handshake());

        MySocketConnectionCallback scc = ApplicationContextUtil.getBean(MySocketConnectionCallback.class);
        return new WebSocketProtocolHandshakeHandler(handshakes, scc, ResponseCodeHandler.HANDLE_404);
    }

    private DeploymentInfo deploymentInfo () throws IOException {
        // config
        Set<Class<?>> hashSet = new HashSet<>();

        // add filter
        if (this.filterClasses!=null && this.filterClasses.length>=1) {
            for (Class<Filter> filterClass : this.filterClasses) {
                if (Filter.class.isAssignableFrom(filterClass)) {
                    hashSet.add(filterClass);
                }
            }
        }

        // create deploymentInfo
        ServletContainerInitializerInfo sciInfo = new ServletContainerInitializerInfo(WebAppServletContainerInitializer.class, hashSet);
        return Servlets.deployment()
                .addServletContainerInitializers(sciInfo)
                .setResourceManager(new FileResourceManager(webAppRoot.getFile(), 0))
                .setClassLoader(UndertowServer.class.getClassLoader())
                .setContextPath("")
                .setDeploymentName(webAppName);
    }

    private static class WebAppServletContainerInitializer implements ServletContainerInitializer {

        @Override
        public void onStartup(Set<Class<?>> classSet, ServletContext servletContext) throws ServletException {

            // set encode
            FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter", CharacterEncodingFilter.class);
            encodingFilter.setInitParameter("encoding", "UTF-8");
            encodingFilter.setInitParameter("forceEncoding", "true");
            encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

            // root web application context
            AnnotationConfigWebApplicationContext rootWebAppContext = new AnnotationConfigWebApplicationContext();
            rootWebAppContext.register(ApplicationConfiguration.class);
            rootWebAppContext.register(MyWebMvcConfigurer.class);
            servletContext.addListener(new ContextLoaderListener(rootWebAppContext));

            // set spring mvc dispatcher
            DispatcherServlet dispatcherServlet = new DispatcherServlet(rootWebAppContext);
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher-servlet", dispatcherServlet);
            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");

            // session filter
            classSet.forEach(aClass -> {
                if (OncePerRequestFilter.class.isAssignableFrom(aClass)) {
                    if (aClass.getName().endsWith("ApiFilter")) {
                        FilterRegistration.Dynamic apiFilter = servletContext.addFilter("apiFilter", (Class<Filter>) aClass);
                        apiFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/api/*");
                    }
                }
            });
        }
    }
}
