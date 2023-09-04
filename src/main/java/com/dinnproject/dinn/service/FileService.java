package com.dinnproject.dinn.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dinnproject.dinn.mapper.FileMapper;
import com.dinnproject.dinn.message.FileRequest;
import com.dinnproject.dinn.message.FileResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {
	
	@Value("${file.dir}")
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
		
		String savFileNm = generateSavFileNm(multipartFile.getOriginalFilename());
		String uploadPath = getUploadPath() + File.separator + savFileNm;
		
		File uploadFile = new File(uploadPath);
		
		try {
			multipartFile.transferTo(uploadFile);
		} catch (Error e) {
			throw new RuntimeErrorException(e);
		}
		
		fileRequest.setOrgFileNm(multipartFile.getOriginalFilename());
		fileRequest.setSavFileNm(savFileNm);
		fileRequest.setFileSize(multipartFile.getSize());
		fileRequest.setUploadPath(uploadPath);
		
		fileMapper.savFile(fileRequest);
		
		return fileRequest;
		
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
	private String getUploadPath() {
		
		File rootDir = new File(fileDir);
		
		if (!rootDir.exists()); {
			rootDir.mkdir();
		}
		
		return makeDir();
		
	}
	
	/**
	 * root 파일 저장 경로 하위에 업로드 폴더 생성
	 * @return
	 */
	private String makeDir() {
		
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString();
		String path = fileDir + File.separator + today;
				
		File dir = new File(path);
		if (dir.exists() == false) {
			dir.mkdir();
		}
		
		return dir.getPath();
		
	}



}
