package com.dinnproject.dinn.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FileRequest {

	/* 파일ID */
	private int fileId;
	/* 원본 파일명 */
	private String orgFileNm;
	/* 저장 파일명 */
	private String savFileNm;
	/* 파일 크기 */
	private long fileSize;
	/* 파일 저장경로 */
	private String uploadPath;

}
