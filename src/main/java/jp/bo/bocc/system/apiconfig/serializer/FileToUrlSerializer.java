package jp.bo.bocc.system.apiconfig.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jp.bo.bocc.entity.ShrFile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author manhnt
 */
public class FileToUrlSerializer extends JsonSerializer<ShrFile> {

	@Autowired
	private HttpServletRequest request;

	private String imageServerUrl;
	public FileToUrlSerializer(String imageServerUrl) {
				this.imageServerUrl = imageServerUrl;
	}

	@Override
	public void serialize(ShrFile file, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		gen.writeString(imageServerUrl + file.getPath());
	}

}
