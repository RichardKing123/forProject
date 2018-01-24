package com.springstudy.bbs.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.domain.Reply;

public interface BoardService {
	
	/* BoardDao를 이용해 게시판 테이블에서 한 페이지에 해당하는 게시 글
	 * 리스트와 페이징 처리에 필요한 데이터를 Map 객체로 반환 하는 메소드
	 **/
	public abstract Map<String, Object> boardList(
			int pageNum, String type, String keyword);
	
	/* BoardDao를 이용해 게시판 테이블에서
	 * no에 해당하는 게시 글 을 읽어와 반환하는 메서드 
 	 * isCount == true 면 게시 상세보기, false 면 게시 글 수정 폼 요청임 
	 **/
	public abstract Board getBoard(int no, boolean isCount);
	
	// BoardDao를 이용해 새로운 게시 글을 추가하는 메서드
	public abstract void insertBoard(Board board);

	/* 게시 글 수정, 삭제 시 BoardDao를 이용해 비밀번호 입력을 체크하는 메서드
	 * 
	 * - 게시 글의 비밀번호가 맞으면 : true를 반환
	 * - 게시 글의 비밀번호가 맞지 않으면 : false를 반환
	 **/
	public boolean isPassCheck(int no, String pass);
	
	// BoardDao를 이용해 게시 글을 수정하는 메서드
	public abstract void updateBoard(Board board);
	
	// BoardDao를 이용해 no에 해당하는 게시 글을 삭제하는 메서드
	public abstract void deleteBoard(int no);
	
	//코드기반 트랜잭션 처리 메소드
	//no에 해당하는 게시글 읽어와서 반환하는 메소드
	public Board getBoardCode(int no, boolean isCount);

	//annotation 기반 트랜잭션 처리 메소드
	public Board getBoardAnnotation(int no, boolean isCount);
	
	//annotation 기반 트랜잭션 처리 메소드
	public abstract void insertBoardMulti(String filePath, 
			Board board, MultipartFile multipartFile, MultipartFile[] multipartFiles) throws Exception;
	
	//코드기반 트랜잭션 처리 메소드
	public abstract Object insertBoardMultiCode(String filePath, 
			Board board, MultipartFile multipartFile, MultipartFile[] multipartFiles) throws Exception;
	
	public abstract List<Reply> replyList(int no);
	
	public Map<String , Integer> recommend(int no, String recommend);
	
	public void addReply(Reply reply);
	
	public void updateReply(Reply reply);
	
	public void deleteReply(int no);
	
}
