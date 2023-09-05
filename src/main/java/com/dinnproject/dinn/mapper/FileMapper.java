package com.dinnproject.dinn.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dinnproject.dinn.message.FileRequest;
import com.dinnproject.dinn.message.FileResponse;

@Mapper
public interface FileMapper {
	
	List<FileResponse> selectAllFiles();
	
	int savFile(@Param("request") FileRequest request);
	
	String selectFilePath(int id);

}
