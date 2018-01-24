package com.springstudy.bbs.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.springstudy.bbs.domain.Member;

@Repository
public class MemberDaoImpl implements MemberDao {

	private SqlSessionTemplate sqlSession;
	
	private final String NAME_SPACE = "com.springstudy.mappers.MemberMapper";
	
	@Autowired
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	public int login(String id, String pass) {
		
		Member member = sqlSession.selectOne(NAME_SPACE + ".login", id);
		
		int result = -1;
		
		// id가 존재하지 않을 때
		if (member == null) {
			return result;
		}
		
		// 비밀번호 동일 할 시 로그인 성공
		if (member.getPass().equals(pass)) {
			result = 1;
		// 비밀번호 다를 시	
		} else {
			result = 0;
		}
		
		
		return result;
	}

	@Override
	public Member getMember(String id) {
		
		return sqlSession.selectOne(NAME_SPACE + ".getMember", id);
	}

}
