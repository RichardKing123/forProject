package com.springstudy.bbs.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.domain.FileName;
import com.springstudy.bbs.domain.Reply;

// 이 클래스가 데이터 액세스(데이터 저장소) 계층의 컴포넌트(Bean) 임을 선언한다.
@Repository
public class BoardDaoImpl implements BoardDao {

	/* src/main/resources/repository/mappers/BoardMapper.xml에
	 * 정의한 Mapper namespace를 상수로 정의
	 **/
	private final String NAME_SPACE = "com.springstudy.bbs.mapper.BoardMapper";
	
	/* mybatis-spring 모듈은 MyBatis의 SqlSession 기능과 스프링 DB 지원 기능을
	 * 연동해 주는 SqlSessionTemplate 클래스를 제공한다. SqlSessionTemplate은
	 * SqlSession을 구현해 스프링 연동 부분을 구현하였기 때문에 우리가 만드는 DAO에서
	 * SqlSessionTemplate 객체를 사용해 SqlSession에 정의된 메서드를 사용할 수 있다.
	 * 
	 * SqlSession과 SqlSessionTemplate는 같은 역할을 담당하고 있지만 트랜잭션
	 * 처리에서 다른 부분이 있다. SqlSession은 commit(), rollback() 메서드를
	 * 명시적으로 호출해 트랜잭션을 처리 하지만 SqlSessionTemplate은 스프링이
	 * 트랜잭션을 처리할 수 있도록 구현되어 있기 때문에 별도로 commit(), rollback()
	 * 메서드를 호출할 필요가 없다.
	 **/	
	private SqlSessionTemplate sqlSession;
		
	/* setter 주입 방식은 스프링이 기본 생성자를 통해 이 클래스의 인스턴스를
	 * 생성한 후 setter 주입 방식으로 SqlSessionTemplate 타입의 객체를
	 * 주입하기 때문에 기본 생성자가 존재해야 하지만 이 클래스에 다른 생성자가
	 * 존재하지 않으므로 컴파일러에 의해 기본 생성자가 만들어 진다.
	 **/
	@Autowired
	public void setSqlSession(SqlSessionTemplate sqlSession) {
		this.sqlSession = sqlSession;
	}	
	
	/* 한 페이지에 보여 질 게시 글 리스트와 검색 리스트 요청 시 호출되는 메소드
	 * 현재 페이지에 해당하는 게시 글 리스트를 DB에서 읽어와 반환 하는 메소드
	 **/
	@Override
	public List<Board> boardList(
			int startRow, int num, String type, String keyword) {
		
		// SQL 파라미터가 여러 개일 경우 Map을 이용하여 지정한다.
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startRow", startRow);
		params.put("num", num);
		params.put("type", type);
		params.put("keyword", keyword);
		
		/* BoardMapper.xml에서 맵핑 구문을 작성하고 아래와 같이 SqlSession
		 * 객체의 메서드를 호출하면서 맵핑 설정을 지정하게 되면 이 메서드 안에서
		 * PreparedStatement 객체를 생성하고 PreparedStatement 객체에
		 * 필요한 파라미터가 설정된다.
		 * 
		 * SqlSessionTemplate 객체의 select(), selectOne(), selectList()
		 * 메서드를 호출하면 PreparedStatement 객체의 executeQuery() 메서드를
		 * 실행하고 쿼리를 발행한 결과인 ResultSet 객체에서 데이터를 읽어와 모델 
		 * 클래스인 Board 객체를 생성하고 이 객체에 값을 설정하게 된다.
		 * 		
		 * 아래와 같이 SqlSessionTemplate의 메서드가 호출되면
		 * repository/mappers/BoardMapper.xml 맵퍼 파일에서
		 * mapper 태그의 namespace 속성에 지정한 
		 * com.springstudy.bbs.mapper.BoardMapper인 맵퍼가 선택되고
		 * 그 하부에 <select> 태그의 id 속성에 지정한 boardList인 맵핑 구문이
		 * 선택된다. 그리고 MyBatis 내부에서 JDBC 코드로 변환되어 실행된다.
		 * 
		 * 매핑 구문에 resultType 속성에 Board를 지정했기 때문에 요청한 
		 * 페이지에 해당하는 게시 글 리스트가 담긴 List<Board> 객체가 
		 * 반환된다. Board 테이블에 게시 글 정보가 하나도 없으면 null이 반환 된다. 
		 * 
		 * 만약 SQL 파라미터를 지정해야 한다면 두 번째 인수에 필요한 파라미터를
		 * 지정하면 되는데 파라미터가 여러 개일 경우 Map 객체에 담아 두 번째
		 * 인수로 지정하면 된다.
		 **/
		return sqlSession.selectList(NAME_SPACE + ".boardList", params);
	}
	
	/* 게시 글 수를 계산하기 위해 호출되는 메서드 - paging 처리에 사용
	 * 게시 글 리스트와 검색 리스트에 대한 게시 글 수를 반환 하는 메서드
	 **/
	@Override
	public int getBoardCount(String type, String keyword) {
		
		// SQL 파라미터가 여러 개일 경우 Map을 이용해 지정하면 된다.
		Map<String, String> params = new HashMap<String, String>();		
		params.put("type", type);
		params.put("keyword", keyword);
		
		return sqlSession.selectOne(NAME_SPACE + ".getBoardCount", params);
	}
	
	/* 게시 글 상세보기 요청 시 호출되는 메서드
	 * no에 해당하는 게시 글 을 DB에서 읽어와 Board 객체로 반환 하는 메서드 
 	 * isCount == true 면 게시 글 읽은 횟수를 1 증가시킨다.
	 **/
	@Override
	public Board getBoard(int no, boolean isCount) {
		
		// 게시 글 상세보기 요청만 게시 글 읽은 횟수를 증가시킨다.
		if(isCount) {
			sqlSession.update(NAME_SPACE + ".incrementReadCount", no);
		}
		
		// getBoard 맵핑 구문을 호출하면서 게시 글 번호인 no를 파라미터로 지정했다.		 
		return sqlSession.selectOne(NAME_SPACE + ".getBoard", no);
	}

	/* 게시 글쓰기 요청 시 호출되는 메서드
	 * 게시 글쓰기 요청 시 게시 글 내용을 Board 객체로 받아 DB에 추가하는 메서드 
	 **/
	@Override
	public void insertBoard(Board board) {
		
		// insertBoard 맵핑 구문을 호출하면서 Board 객체를 파라미터로 지정했다.
		sqlSession.insert(NAME_SPACE + ".insertBoard", board);
	}
	
	/* 게시 글 수정, 삭제 시 비밀번호 입력을 체크하는 메서드
	 * 
	 * - 게시 글의 비밀번호가 맞으면 : true를 반환
	 * - 게시 글의 비밀번호가 맞지 않으면 : false를 반환
	 **/
	public boolean isPassCheck(int no, String pass) {	

		boolean result = false;
		
		// isPassCheck 맵핑 구문을 호출하면서 게시 글 번호인 no를 파라미터로 지정했다.
		String dbPass = sqlSession.selectOne(
				NAME_SPACE + ".isPassCheck",	no);

		// 비밀번호가 맞으면 true가 반환된다.
		if(dbPass.equals(pass)) {
			result = true;		
		}
		return result;
	}
	
	/* 게시 글 수정 요청 시 호출되는 메서드
	 * 게시 글 수정 요청 시 수정된 내용을 Board 객체로 받아 DB에 수정하는 메서드 
	 **/
	@Override
	public void updateBoard(Board board) {
		
		// updateBoard 맵핑 구문을 호출하면서 Board 객체를 파라미터로 지정했다.
		sqlSession.update(NAME_SPACE + ".updateBoard", board);
	}

	/* 게시 글 삭제 요청 시 호출되는 메서드 
	 * no에 해당 하는 게시 글을 DB에서 삭제하는 메서드 
	 **/
	@Override
	public void deleteBoard(int no) {
		
		// deleteBoard 맵핑 구문을 호출하면서 no를 파라미터로 지정했다.
		sqlSession.delete(NAME_SPACE + ".deleteBoard", no);
	}

	@Override
	public void insertFiles(List<FileName> fileNames) {
		
		sqlSession.insert(NAME_SPACE + ".insertFileNames", fileNames);
	}

	@Override
	public List<Reply> replyList(int no) {
		
		return sqlSession.selectList(NAME_SPACE + ".replyList", no);
	}

	@Override
	public void updateRecommend(int no, String recommend) {
		Map<String , Object> params = new HashMap<String, Object>();
		params.put("no", no);
		params.put("recommend", recommend);
		sqlSession.update(NAME_SPACE + ".updateRecommend", params);
	}

	@Override
	public Board getRecommend(int no) {
		return sqlSession.selectOne(NAME_SPACE + ".getRecommend", no);
	}

	@Override
	public void addReply(Reply reply) {
		
		sqlSession.insert(NAME_SPACE + ".addReply", reply);
		
	}

	@Override
	public void updateReply(Reply reply) {
		
		sqlSession.update(NAME_SPACE + ".updateReply", reply);
		
	}

	@Override
	public void deleteReply(int no) {
		sqlSession.delete(NAME_SPACE + ".deleteReply", no);
		
	}
}
