package com.dinnproject.dinn.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dinnproject.dinn.message.FileRequest;
import com.dinnproject.dinn.message.FileResponse;
import com.dinnproject.dinn.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

	@Autowired
	private final FileService fileService;
	
	@GetMapping("/select/files")
	public List<FileResponse> selectAllFiles() {
		
		return fileService.selectAllFiles();
		
	}
	
	@PostMapping("/upload/multi-file")
	public List<FileRequest> multiFileUpload(@RequestPart("uploadFileData") FileRequest fileRequest, @RequestPart("files") List<MultipartFile> multipartFiles) throws IOException {
		
		return fileService.multifileUpload(multipartFiles);
		
	}
	
	@PostMapping("/upload/single-file")
	public FileRequest singleFileUpload(@RequestPart("uploadFileData") FileRequest fileRequest, @RequestPart("file") MultipartFile multipartFile) throws IOException {
		
		return fileService.singlefileUpload(multipartFile);
		
	}
	
	@GetMapping("/download/single-file")
	public ResponseEntity<Object> singleFileDownload(@RequestParam int id) {
		
		return fileService.singlefileDownload(id);
				
	}

}
