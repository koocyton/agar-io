package com.doopp.agar.server.filter;

import com.doopp.agar.api.service.UserService;
import com.doopp.agar.define.StatusCode;
import com.doopp.agar.message.StandardException;
import com.doopp.agar.pojo.User;
import com.doopp.agar.server.util.ApplicationContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class ApiFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        // 不过滤的uri
        String[] notFilterUris = new String[]{
                "/api/login",
        };

        // 请求的uri
        String uri = request.getRequestURI();

        // 是否过滤
        boolean doFilter = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilterUri : notFilterUris) {
            if (uri.indexOf(notFilterUri)==0) {
                doFilter = false;
                break;
            }
        }

        try {
            if (doFilter) {
                // 从 Header 里拿到 Authentication ( Base64.encode )
                String userToken = request.getHeader("User-Token");
                if (userToken ==null) {
                    throw new StandardException(StatusCode.TOKEN_CHECK_FAILED);
                }
                UserService userService = ApplicationContextUtil.getBean(UserService.class);
                // 验证失败，这里会抛出异常
                User user = userService.getUserByToken(userToken);
                if (user ==null) {
                    throw new StandardException(StatusCode.USER_NOT_FOUND);
                }
                else {
                    request.getSession().setAttribute("SessionUser", user);
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (StandardException e) {
            e.printStackTrace();
            writeExceptionResponse(e.getCode(), response, e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            if (e.getCause() instanceof StandardException) {
                StandardException se = (StandardException) e.getCause();
                writeExceptionResponse(se.getCode(), response, se.getMessage());
            }
            else if (e.getCause()!=null && e.getCause().getCause()!=null) {
                writeExceptionResponse(StatusCode.FAIL.code(), response, e.getCause().getCause().getMessage());
            }
            else if (e.getCause()!=null) {
                writeExceptionResponse(StatusCode.FAIL.code(), response, e.getCause().getMessage());
            }
            else {
                writeExceptionResponse(StatusCode.FAIL.code(), response, e.getMessage());
            }
        }
    }

    private static void writeExceptionResponse(int errorCode, HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"code\":" + errorCode + ", \"msg\":\"" + errorMessage + "\", \"data\":null}");
    }
}
