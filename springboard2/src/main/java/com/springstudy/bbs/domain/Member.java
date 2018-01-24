package com.springstudy.bbs.domain;

import java.sql.Timestamp;

/* 한 명의 회원 정보를 저장하는 클래스(VO, Beans, DTO)
 * 회원 정보를 저장하고 있는 테이블의 필드와 1:1 맵핑되는 Domain 클래스
 **/
public class Member {	
	
	private String name, id, pass, email, mobile;
	private String phone, zipcode, address1, address2;
	private boolean  emailGet;
	private Timestamp regDate;		
	
	public Member() { }	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public boolean isEmailGet() {
		return emailGet;
	}
	public void setEmailGet(boolean emailGet) {
		this.emailGet = emailGet;
	}
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}	
}
