package com.dinnproject.dinn.vo;

import lombok.Getter;

@Getter
public class FileVo {

	/* 파일ID */
	private int fileId;
	/* 저장 파일명 */
	private String savFileNm;
	/* 원본 파일명 */
	private String orgFileNm;
	/* 파일 크기 */
	private long fileSize;
	/* 파일 저장경로 */
	private String uploadPath;
	/* 삭제여부 */
	private String delYn;
	/* 생성일시 */
	private String cretDate;
	/* 생성자Id */
	private String cretId;
	/* 수정일시 */
	private String amdDate;
	/* 수정자Id */
	private String amdId;

}
