package jp.bo.bocc.system.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author manhnt
 */
@JsonSerialize(using = ServiceException.ServiceExceptionSerializer.class)
public class ServiceException extends RuntimeException {

	public final HttpStatus statusCode;

	public final String messsage;

	public String url;

	public String validVersion;

	public final Map<String, Object> errorDetail = new LinkedHashMap<>();

	public ServiceException(HttpStatus statusCode, String messsage) {
		super(messsage);
		this.statusCode = statusCode;
		this.messsage = messsage;
	}

	public ServiceException(HttpStatus statusCode, String messsage, Throwable e) {
		super(messsage, e);
		this.statusCode = statusCode;
		this.messsage = messsage;
	}

	public ServiceException(HttpStatus statusCode, String messsage, String url) {
		super(messsage);
		this.statusCode = statusCode;
		this.messsage = messsage;
		this.url = url;
	}

	public ServiceException(HttpStatus statusCode, String messsage, String url, String validVersion) {
		super(messsage);
		this.statusCode = statusCode;
		this.messsage = messsage;
		this.url = url;
		this.validVersion = validVersion;
	}

	public static class ServiceExceptionSerializer extends JsonSerializer {
		@Override
		public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
			if (value instanceof ServiceException) {
				ServiceException serviceException = (ServiceException) value;
				gen.writeStartObject();

				gen.writeFieldName("errorCode");
				gen.writeNumber(serviceException.statusCode.value());

				gen.writeFieldName("message");
				gen.writeString(serviceException.messsage);

				gen.writeFieldName("url_maintain");
				gen.writeString(serviceException.url);

				gen.writeFieldName("errorDetail");
				gen.writeObject(serviceException.errorDetail);

				gen.writeFieldName("validVersion");
				gen.writeObject(serviceException.validVersion);

				gen.writeEndObject();
			}
		}
	}
}
