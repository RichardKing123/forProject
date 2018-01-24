package com.springstudy.bbs.dao;

import java.util.List;

import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.domain.FileName;
import com.springstudy.bbs.domain.Reply;

public interface BoardDao {
	
	/* 한 페이지에 보여 질 게시 글 리스트와 검색 리스트 요청 시 호출되는 메소드
	 * 현재 페이지에 해당하는 게시 글 리스트를 DB에서 읽어와 반환 하는 메소드
	 **/
	public abstract List<Board> boardList(
			int startRow, int num, String type, String keyword);
	
	/* 게시 글 상세보기 요청 시 호출되는 메서드
	 * no에 해당하는 게시 글 을 DB에서 읽어와 Board 객체로 반환 하는 메서드 
	 * isCount == true 면 게시 글 읽은 횟수를 1 증가시킨다.
	 **/
	public abstract Board getBoard(int no, boolean isCount);
	
	/* 게시 글쓰기 요청 시 호출되는 메서드
	 * 게시 글쓰기 요청 시 게시 글 내용을 Board 객체로 받아 DB에 추가하는 메서드 
	 **/
	public abstract void insertBoard(Board board);
	
	/* 게시 글 수정, 삭제 시 비밀번호 입력을 체크하는 메서드
	 * 
	 * - 게시 글의 비밀번호가 맞으면 : true를 반환
	 * - 게시 글의 비밀번호가 맞지 않으면 : false를 반환
	 **/
	public boolean isPassCheck(int no, String pass);
	
	/* 게시 글 수정 요청 시 호출되는 메서드
	 * 게시 글 수정 요청 시 수정된 내용을 Board 객체로 받아 DB에 수정하는 메서드 
	 **/
	public abstract void updateBoard(Board board);
	
	/* 게시 글 삭제 요청 시 호출되는 메서드 
	 * no에 해당 하는 게시 글을 DB에서 삭제하는 메서드 
	 **/
	public abstract void deleteBoard(int no);
	
	/* 게시 글 수를 계산하기 위해 호출되는 메서드 - paging 처리에 사용
	 * 게시 글 리스트와 검색 리스트에 대한 게시 글 수를 반환 하는 메서드
	 **/
	public abstract int getBoardCount(String type, String keyword);
	
	public abstract void insertFiles(List<FileName> fileNames);
	
	public abstract List<Reply> replyList(int no);
	
	public abstract void updateRecommend(int no, String recommend);
	
	public abstract Board getRecommend(int no);
	
	public abstract void addReply(Reply reply);
	
	public abstract void updateReply(Reply reply);
	
	public abstract void deleteReply(int no);
}
