package com.springstudy.bbs.domain;

public class FileName {
	private int no;
	private String file2;
	private int bbsNo;
	
	public FileName() { }
	public FileName(String file2, int bbsNo) {		
		this.file2 = file2;
		this.bbsNo = bbsNo;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getFile2() {
		return file2;
	}
	public void setFile2(String file2) {
		this.file2 = file2;
	}
	public int getBbsNo() {
		return bbsNo;
	}
	public void setBbsNo(int bbsNo) {
		this.bbsNo = bbsNo;
	}	
}
