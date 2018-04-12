package jp.bo.bocc.system.apiconfig.resolver;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import jp.bo.bocc.system.exception.BadRequestException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author user3227576
 *         <p><a href="http://stackoverflow.com/a/27260650" /></p>
 */
public class JsonPathArgumentResolver implements HandlerMethodArgumentResolver {

	private static final String JSONBODYATTRIBUTE = "JSON_REQUEST_BODY";

	private ObjectMapper om = new ObjectMapper();

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(JsonArg.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String jsonBody = getRequestBody(webRequest);

		JsonNode rootNode = om.readTree(jsonBody);

		JsonArg jsonArgAnnotation = parameter.getParameterAnnotation(JsonArg.class);
		String path = jsonArgAnnotation.value();
		if (StringUtils.isEmpty(path)) path = parameter.getParameterName();

		JsonNode node = rootNode.path(path);
		if (node.isMissingNode()) {
			if (jsonArgAnnotation.required()) {
				throw new BadRequestException("Missing parameter: '" + path + "'");
			} else {
				return null;
			}
		}

		return om.readValue(node.toString(), parameter.getParameterType());
	}

	private String getRequestBody(NativeWebRequest webRequest) {
		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

		String jsonBody = (String) webRequest.getAttribute(JSONBODYATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
		if (jsonBody == null) {
			try {
				jsonBody = IOUtils.toString(servletRequest.getInputStream());
				webRequest.setAttribute(JSONBODYATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
			} catch (IOException e) {
				throw new BadRequestException("Bad request", e);
			}
		}
		return jsonBody;
	}
}