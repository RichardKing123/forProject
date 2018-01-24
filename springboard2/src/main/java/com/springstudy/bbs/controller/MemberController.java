package com.springstudy.bbs.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.springstudy.bbs.domain.Member;
import com.springstudy.bbs.exception.MemberNotFoundException;
import com.springstudy.bbs.exception.MemberPassCheckFailException;
import com.springstudy.bbs.service.MemberService;

@Controller
@SessionAttributes("member")
public class MemberController {

	private MemberService memberService;

	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(Model model, @RequestParam("id") String id, @RequestParam("pass") String pass,
			HttpSession session, HttpServletResponse response) throws ServletException, IOException {

		int result = memberService.login(id, pass);

		if(result == -1) {			
			throw new MemberNotFoundException(id + "는 존재하지 않는 아이디 입니다.");			
			
		} else if(result == 0) {
			throw new MemberPassCheckFailException("비밀번호가 맞지 않습니다.");
		}		

		Member member = memberService.getMember(id);
		session.setAttribute("isLogin", true);

		model.addAttribute("member", member);

		return "redirect:/boardList";
	}

	@RequestMapping(value = "/logout")
	public String logout(HttpSession session) {

		session.invalidate();

		return "redirect:/boardList";
	}

	@ExceptionHandler(MemberNotFoundException.class)
	public String memberNotFound(Model model) {
		model.addAttribute("title", "존재하지 않는 아이디");
		return "errors/controllerException";
	}

	@ExceptionHandler(MemberPassCheckFailException.class)
	public String memberPassCheckFail(Model model) {
		model.addAttribute("title", "비밀번호가 맞지 않음(member)");
		return "errors/controllerException";
	}

	/*
	 * Exception 발생 테스트 메서드 - 요청 URI에 따라 강제 예외 발생 메서드
	 * http://localhost:8080/springstudy-bbs06/internal 과 같이 테스트 한다.
	 **/
	@RequestMapping({ "/internal", "/badRequest", "/notFound", "/runtime", "methodNotSupported" })
	public String internalException(HttpServletRequest request) throws Exception {

		int index = request.getRequestURL().lastIndexOf("/");
		String command = request.getRequestURL().substring(index);
		if (command.equals("/interval")) {
			int i = 100 / 0;
		} else if (command.equals("/badRequest")) {
			throw new TypeMismatchException("잘못된 요청", String.class);
		} else if (command.equals("/notFound")) {
			throw new NoSuchRequestHandlingMethodException(request);
		} else if (command.equals("/runtime")) {
			throw new RuntimeException("요청을 처리하는 과정에서 에러 발생");
		} else if (command.equals("/methodNotSupported")) {
			throw new HttpRequestMethodNotSupportedException("요청 방식이 잘못됨");
		}
		
		return "";
	}

}
