package com.springstudy.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springstudy.bbs.dao.MemberDao;
import com.springstudy.bbs.domain.Member;

@Service
public class MemberServiceImpl implements MemberService {

	private MemberDao memberDao;

	@Autowired
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	@Override
	public int login(String id, String pass) {

		return memberDao.login(id, pass);
	}

	@Override
	public Member getMember(String id) {
		
		return memberDao.getMember(id);		
		
	}

}
