package com.springstudy.bbs.domain;

import java.sql.Timestamp;

/* 하나의 댓글 정보를 저장하는 클래스(Domain, VO, Beans, DTO)
 * 댓글 정보를 저장하고 있는 테이블의 필드와 1:1 맵핑되는 Domain 클래스
 **/
public class Reply {
	private int no;
	private int bbsNo;
	private String replyContent;
	private String replyWriter;
	private Timestamp regDate;
	
	public Reply() { }
	public Reply(int bbsNo, String replyContent, String replyWriter) {
		this.bbsNo = bbsNo;
		this.replyContent = replyContent;
		this.replyWriter = replyWriter;
	}	
	public Reply(int no, int bbsNo, String replyContent, 
			String replyWriter, Timestamp regDate) {
		
		this.no = no;
		this.bbsNo = bbsNo;
		this.replyContent = replyContent;
		this.replyWriter = replyWriter;
		this.regDate = regDate;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getBbsNo() {
		return bbsNo;
	}
	public void setBbsNo(int bbsNo) {
		this.bbsNo = bbsNo;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getReplyWriter() {
		return replyWriter;
	}
	public void setReplyWriter(String replyWriter) {
		this.replyWriter = replyWriter;
	}
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}			
}
