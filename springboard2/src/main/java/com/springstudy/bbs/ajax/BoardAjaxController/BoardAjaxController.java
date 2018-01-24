package com.springstudy.bbs.ajax.BoardAjaxController;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springstudy.bbs.domain.Reply;
import com.springstudy.bbs.service.BoardService;

@Controller
public class BoardAjaxController {
	
	@Autowired
	BoardService boardService;
	
	@RequestMapping("/recommend.ajax")
	@ResponseBody
	public Map<String, Integer> recommend(int no, String recommend) {
		
		
		return boardService.recommend(no, recommend);
	}
	
	//리플을 작성한 다음 다시 댓글목록을 새로 쏴 줘야 하기 때문에 replyList를 반환함
	@RequestMapping("/replyWrite.ajax")
	@ResponseBody
	public List<Reply> addReply(Reply reply) {
		
		boardService.addReply(reply);
		
		return boardService.replyList(reply.getBbsNo());
	}
	
	//댓글 업데이트 후 새로운 댓글 목록을 출력해야 하기 때문에 list타입으로 반환
	@RequestMapping("/replyUpdate.ajax")
	@ResponseBody
	public List<Reply> updateReply(Reply reply) {
		
		boardService.updateReply(reply);
		
		return boardService.replyList(reply.getBbsNo());
	}
	
	//댓글 삭제 후 새로운 댓글 목록을 출력해야 하기에 list로 반환
	@RequestMapping("/replyDelete.ajax")
	@ResponseBody
	public List<Reply> deleteReply(int no, int bbsNo) {
		
		boardService.deleteReply(no);
		
		return boardService.replyList(bbsNo);
	}
	
}
