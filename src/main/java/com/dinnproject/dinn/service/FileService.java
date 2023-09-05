package com.dinnproject.dinn.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dinnproject.dinn.mapper.FileMapper;
import com.dinnproject.dinn.message.FileRequest;
import com.dinnproject.dinn.message.FileResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
	
	@Value("${file.dir:/file}")
	private String fileDir;
	
	private final FileMapper fileMapper;
	
	/**
	 * DB에 저장된 파일 목록 조회
	 * @return
	 */
	public List<FileResponse> selectAllFiles() {
		
		return fileMapper.selectAllFiles();
		
	}
	
	/**
	 * 다중 파일 업로드
	 * @param multipartFiles
	 * @return
	 * @throws IOException 
	 */
	public List<FileRequest> multifileUpload(List<MultipartFile> multipartFiles) throws IOException {
		
		List<FileRequest> files = new ArrayList<>();
		
		for (MultipartFile multipartFile : multipartFiles) {
			if (multipartFile.isEmpty()) {
				continue;
			}
			files.add(singlefileUpload(multipartFile));
		}
		
		return files;
		
	}
	
	/**
	 * 단일 파일 업로드
	 * @param multipartFile
	 * @return 저장할 파일 정보
	 * @throws IOException 
	 */
	public FileRequest singlefileUpload(MultipartFile multipartFile) throws IOException {
		
		FileRequest fileRequest = new FileRequest();
		
		if (multipartFile.isEmpty() || multipartFile == null) {
			return null;
		}
		
		// 파일명 난수화		
		String savFileNm = generateSavFileNm(multipartFile.getOriginalFilename());
		
		Path uploadPath = Paths.get(getUploadPath() + "/" + savFileNm);

		Path uploadFile = Paths.get(uploadPath.toString());
		
		try {
			multipartFile.transferTo(uploadFile);
		} catch (Error e) {
			throw new RuntimeErrorException(e);
		}
		
		fileRequest.setOrgFileNm(multipartFile.getOriginalFilename());
		fileRequest.setSavFileNm(savFileNm);
		fileRequest.setFileSize(multipartFile.getSize());
		fileRequest.setUploadPath(uploadPath.toString());
		
		fileMapper.savFile(fileRequest);
		
		return fileRequest;
		
	}
	
	/**
	 * 단일 파일 다운로드
	 * @param id
	 * @return ResponseEntity<Object>
	 */
	public ResponseEntity<Object> singlefileDownload(int id) {
		
		String path = fileMapper.selectFilePath(id);
		
		try {
			
			Path filePath = Paths.get(path);
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			File file = new File(path);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getName()).build());
			
			return new ResponseEntity<Object>(resource, headers, HttpStatus.OK);
			
		} catch (Exception e) {
			
			log.info("singlefileDownload 에러, message={}", e.getMessage(), e);
			
			return new ResponseEntity<Object>(null, HttpStatus.CONFLICT);
			
		}
		
	}
	
	/**
	 * 저장 파일명(savFileNm) 생성
	 * @param filename
	 * @return
	 */
	private String generateSavFileNm(String filename) {
		
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String extension = StringUtils.getFilenameExtension(filename);
		return uuid + "." + extension;
		
	}
	
	/**
	 * root 파일 저장 경로 확인 및 업로드 경로 반환
	 * @return 업로드 경로
	 */
	private Path getUploadPath() throws IOException {
		
		Path rootDir = Paths.get(fileDir);
		
		if(!Files.isDirectory(rootDir)) {
			Files.createDirectory(rootDir);
		}
		
		return makeDir();
		
	}
	
	/**
	 * root 파일 저장 경로 하위에 업로드 폴더 생성
	 * @return
	 */
	private Path makeDir() throws IOException {
		
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
		
		Path dir = Paths.get(fileDir + "/" + today);
		if (!Files.isDirectory(dir)) {
			Files.createDirectory(dir);
		}
		
		return dir;
		
	}



}
