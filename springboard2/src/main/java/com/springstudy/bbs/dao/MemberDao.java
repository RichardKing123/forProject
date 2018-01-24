package com.springstudy.bbs.dao;

import com.springstudy.bbs.domain.Member;


public interface MemberDao {
	
	/** 
	 * 회원 로그인을 처리하는 메서드
	 * @param id는 회원 아이디
	 * @param pass는 회원 비밀번호
	 * @return 로그인 처리 결과를 정수로 반환 	
	 */	
	public int login(String id, String pass);		
	
	/**
	 * 한 명의 회원 정보를 반환하는 메서드
	 * @param id는 회원 아이디
	 * @return no에 해당하는 회원 정보를 Member 객체로 반환
	 **/
	public Member getMember(String id);
}
