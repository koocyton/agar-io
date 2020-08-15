package com.doopp.agar.server.module;

import com.doopp.agar.utils.IdWorker;
import com.doopp.agar.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import reactor.netty.http.client.HttpClient;

import java.text.SimpleDateFormat;

public class ApplicationModule extends AbstractModule {

	@Override
	public void configure() {
		// bind(PublishAdvertTask.class).in(Singleton.class);
		// bind(CountryService.class).to(CountryServiceImpl.class).in(Singleton.class);
	}

	@Singleton
	@Provides
	public IdWorker idWorker(@Named("agar-server.idWorker.workerId") Long workerId,
							 @Named("agar-server.idWorker.dataCenterId") Long dataCenterId) {
		return new IdWorker(workerId, dataCenterId);
	}

	@Singleton
	@Provides
	// @Named("jsonUtil")
	public JsonUtil jsonUtil() {
		return new JsonUtil(objectMapper());
	}

	@Singleton
	@Provides
	public HttpClient httpClient () {
		return HttpClient.create();
	}

	private ObjectMapper objectMapper() {
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
		simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

		return (new ObjectMapper())
				// 解决实体未包含字段反序列化时抛出异常
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				// 对于空的对象转json的时候不抛出错误
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				// 允许属性名称没有引号
				.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
				// 允许单引号
				.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
				// 时间格式化
				.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
				// 驼峰转蛇型
				.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
				// Long 转字符串
				.registerModule(simpleModule);
	}
}
