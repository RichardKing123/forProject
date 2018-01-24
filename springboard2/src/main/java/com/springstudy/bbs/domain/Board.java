package com.springstudy.bbs.domain;

import java.sql.Timestamp;

public class Board {
	// no, title, content, writer, reg_date, read_count, pass, file1
	private int no;
	private String title;
	private String content;
	private String writer;
	private Timestamp regDate;
	private int readCount;
	private String pass;
	private String file1;
	private int recommend;
	private int thank;
	
	public Board() { }
	public Board(int no, String title, String content, String writer,
			Timestamp regDate, int readCount, String pass, String file1) {
		this.no = no;
		this.title = title;
		this.content = content;
		this.writer = writer;
		this.regDate = regDate;
		this.readCount = readCount;
		this.pass = pass;
		this.file1 = file1;
	}
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getFile1() {
		return file1;
	}
	public void setFile1(String file1) {
		this.file1 = file1;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
	public int getThank() {
		return thank;
	}
	public void setThank(int thank) {
		this.thank = thank;
	}
	
}
