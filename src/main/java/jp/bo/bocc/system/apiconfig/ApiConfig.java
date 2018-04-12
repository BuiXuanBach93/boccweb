package jp.bo.bocc.system.apiconfig;

import com.fasterxml.jackson.databind.JsonSerializer;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AuthenticationInterceptor;
import jp.bo.bocc.system.apiconfig.resolver.JsonPathArgumentResolver;
import jp.bo.bocc.system.apiconfig.serializer.FileToUrlSerializer;
import jp.bo.bocc.system.exception.RestResponseEntityExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author manhnt
 */

@Configuration
@EnableWebMvc
@Import(SwaggerConfig.class)
@ComponentScan(basePackages = {"jp.bo.bocc.controller.api","jp.bo.bocc.controller.webmobile"}, basePackageClasses = {RestResponseEntityExceptionHandler.class})
public class ApiConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private Environment environment;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		super.addArgumentResolvers(argumentResolvers);
		argumentResolvers.add(new JsonPathArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authenticationInterceptor()).excludePathPatterns("/v2/api-docs").excludePathPatterns("/swagger-resources/**");
	}

	@Bean
	public HandlerInterceptor authenticationInterceptor() {
		return new AuthenticationInterceptor();
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
		builder.indentOutput(true)
				.dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
				.serializerByType(ShrFile.class, fileToUrlSerializer());
		converters.add(new MappingJackson2HttpMessageConverter(builder.build()));

		converters.add(new ResourceHttpMessageConverter());
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public JsonSerializer<?> fileToUrlSerializer() {
		final String imageServerUrl = environment.getProperty("image.server.url");
		return new FileToUrlSerializer(imageServerUrl);
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RequestContext requestContext() {
		return new RequestContext();
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedMethods("GET","POST","PUT","HEAD", "DELETE", "PATCH") //GET, POST, HEAD is default
				/*.allowedHeaders("Content-Type", "Authorization")*/
				.allowCredentials(false).maxAge(3600);
		super.addCorsMappings(registry);
	}
}
