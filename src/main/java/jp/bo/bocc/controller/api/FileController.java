package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.repository.FileRepository;
import jp.bo.bocc.service.FileService;
import jp.bo.bocc.service.StorageService;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import jp.bo.bocc.system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author manhnt
 */
@RestController
public class FileController {

	@Autowired
	private FileService fileService;

	@Autowired
	private StorageService storageService;

	@Autowired
	FileRepository fileRepository;

	@GetMapping("/files/{id:\\d+}.{ext:\\w+}")
	@NoAuthentication
	public ResponseEntity<InputStreamResource> getFile(@PathVariable Long id) {
		ShrFile file = fileService.get(id);
		if (file == null) throw new ResourceNotFoundException("File not found");
		InputStreamResource stream = new InputStreamResource(storageService.getInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(file.getSize());
		return new ResponseEntity<>(stream, headers, HttpStatus.OK);
	}

	@GetMapping("/file/test")
	@NoAuthentication
	public ResponseEntity<List<ShrFile>> test() {
		return new ResponseEntity<List<ShrFile>>(fileRepository.findAll(), HttpStatus.OK);
	}
}
