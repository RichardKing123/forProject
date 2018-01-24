package com.springstudy.bbs.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springstudy.bbs.domain.Board;
import com.springstudy.bbs.domain.Reply;
import com.springstudy.bbs.exception.BoardPassCheckFailException;
import com.springstudy.bbs.service.BoardService;

//스프링 MVC의 컨트롤러임을 선언하고 있다.
@Controller
public class BoardController {
	
	/* 인스턴스 필드에 @Autowired annotation을 사용하면 접근지정자가 
	 * private이고 setter 메서드가 없다 하더라도 문제없이 주입 된다.
	 * 하지만 우리는 항상 setter 메서드를 준비하는 습관을 들일 수 있도록 하자.
	 * 
	 * setter 주입 방식은 스프링이 기본 생성자를 통해 이 클래스의 인스턴스를
	 * 생성한 후 setter 주입 방식으로 BoardService 타입의 객체를 주입하기 때문에  
	 * 기본 생성자가 존재해야 하지만 이 클래스에 다른 생성자가 존재하지 않으므로
	 * 컴파일러에 의해 기본 생성자가 만들어 진다.
	 **/
	@Autowired
	private BoardService boardService;
	
	private final static String DEFAULT_PATH = "/resources/upload";
	
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}
	
	/* @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입
	 * 
	 * 1. Controller 메서드의 파라미터 타입으로 지정할 수 있는 객체와 애노테이션 
	 * @RequestMapping 애노테이션이 적용된 컨트롤러 메서드의 파라미터에
	 * 아래와 같은 객체와 애노테이션을 사용할 수 있도록 지원하고 있다.
	 * 
	 * - HttpServletRequest, HttpServletResponse
	 *   요청/응답을 처리하기 위한 서블릿 API
	 *   
	 * - HttpSession 
	 *   HTTP 세션을 위한 서블릿 API  
	 * 
	 * - org.springframework.ui.Model, ModelMap, java.util.Map
	 *   뷰에 모델 데이터를 전달하기 위한 모델 객체
	 *   
	 * - 커맨드 객체(VO, DTO)
	 *   요청 데이터를 저장할 객체
	 * 
	 * - Errors, BindingResult    
	 *   검증 결과를 저장할 객체로 커맨드 객체 바로 뒤에 위치 시켜야 한다.
	 *   
	 * - @RequestParam
	 *   HTTP 요청 파라미터의 값을 메서드의 파라미터로 매핑하기 위한 애노테이션  
	 * 
	 * - @RequestHeader
	 *   HTTP 요청 헤더의 값을 파라미터로 받기 위한 애노테이션
	 *   
	 * - @RequestCookie
	 *   Cookie 데이터를 파라미터로 받기 위한 애노테이션
	 *   
	 * - @RequestVariable
	 *   RESTful API 방식의 파라미터를 받기 위한 경로 변수 설정 애노테이션
	 *   
	 * - @RequestBody
	 *   요청 몸체의 데이터를 자바 객체로 변환하기 위한 애노테이션
	 *   String이나 JSON으로 넘어오는 요청 몸체의 데이터를 자바 객체로
	 *   변환하기 위한 사용하는 애노테이션 이다.
	 *   
	 * - Writer, OutputStream
	 *   응답 데이터를 직접 작성할 때 메서드의 파라미터로 지정해 사용한다.
	 *   
	 * 2. Controller 메서드의 반환 타입으로 지정할 수 있는 객체와 애노테이션
	 * - String
	 *   뷰 이름을 반환할 때 메서드의 반환 타입으로 지정
	 * 
	 * - void
	 *   컨트롤러의 메서드에서 직접 응답 데이터를 작성할 경우 지정
	 * 
	 * - ModelAndView
	 *   모델과 뷰 정보를 함께 반환해야 할 경우 지정
	 *   이전의 컨트롤는 스프링이 지원한는 Controller 인터페이스를
	 *   구현해야 했는데 이때 많이 사용하던 반환 타입이다.
	 * 
	 * - 자바 객체 
	 *   메서드에 @ResponseBody가 적용된 경우나 메서드에서 반환되는
	 *   객체를 JSON 또는 XML과 같은 양식으로 응답을 변환 할 경우에 사용한다. 
	 **/	
	
	/* 게시 글 리스트 보기 요청을 처리하는 메서드(게시 글 리스트, 검색 리스트)
	 * 
	 * @RequestMapping은 클래스 레벨과 메서드 레벨에 지정할 수 있다.
	 * @RequestMapping의 ()에 처리할 요청 URI만 지정할 때는 value 속성을
	 * 생략하고 처리할 요청 URI를 String 또는 String 배열을 지정할 수 있지만
	 * 다른 속성을 같이 지정할 경우 value 속성에 처리할 요청 URI를 지정해야 한다.
	 * 또한 method 속성을 지정해 컨트롤러가 처리할 HTTP 요청 방식을
	 * 지정할 수 있는데 아래는 "/boardList", "/list"로 들어오는 GET 방식의 
	 * 요청을 이 메서드가 처리할 수 있도록 설정한 것이다.
	 * method 속성을 생략하면 기본 값은 RequestMethod.GET 이다.	  
	 *  
	 * 요청을 처리한 결과를 뷰에 전달하기 위해 사용하는 것이 모델이다.
	 * 컨트롤러는 요청을 처리한 결과 데이터를 모델에 담아 뷰로 전달하고 뷰는
	 * 모델로 부터 데이터를 읽어와 클라이언트로 보낼 결과 페이지를 만들게 된다.
	 *   
	 * 스프링은 컨트롤러에서 모델에 데이터를 담을 수 있는 다양한 방법을 제공하는데
	 * 아래와 같이 파라미터에 Model을 지정하는 방식이 많이 사용된다. 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터에 Model
	 * 을 지정하면 스프링이 이 메서드를 호출하면서 Model 타입의 객체를 넘겨준다.
	 * 우리는 Model을 받아 이 객체에 결과 데이터를 담기만 하면 뷰로 전달된다.
	 *	
	 * 스프링은 클라이언트로 부터 넘어 오는 요청 파라미터를 받을 수 있는 여러 가지
	 * 방법을 제공하고 있다. 아래와 같이 Controller 메서드에 요청 파라미터 이름과
	 * 동일한 이름의 메서드 파라미터를 지정하면 스프링으로부터 요청 파라미터를 넘겨
	 * 받을 수 있다. 만약 요청 파라미터와 메서드의 파라미터 이름이 다른 경우 아래와
	 * 같이 메서드의 파라미터 앞에 @RequestParam("요청 파라미터 이름")을 
	 * 사용해 요청 파라미터의 이름을 지정하면 된다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드의 파라미터에 
	 * @RequestParam 애노테이션을 사용해 요청 파라미터 이름을 지정하면 
	 * 이 애노테이션이 앞에 붙은 매개변수에 요청 파라미터 값을 바인딩 시켜준다.
	 * 
	 * @RequestParam 애노테이션에 사용할 수 있는 속성은 아래와 같다.
	 * value : HTTP 요청 파라미터의 이름을 지정한다.
	 * required : 요청 파라미터가 필수인지 설정하는 속성으로 기본값은 true 이다.
	 * 			이 값이 true인 상태에서 요청 파라미터의 값이 존재하지 않으면
	 * 			스프링은 Exception을 발생시킨다.
	 * defaultValue : 요청 파라미터가 없을 경우 사용할 기본 값을 문자열로 지정한다.
	 * 
	 * @RequestParam(value="no" required=false defaultValue="1")
	 * 
	 * @RequestParam 애노테이션은 요청 파라미터 값을 읽어와 Controller 메서드의
	 * 파라미터 타입에 맞게 변환해 준다. 만약 요청 파라미터를 Controller 메서드의 
	 * 파라미터 타입으로 변환할 수 없는 경우 스프링은 400 에러를 발생시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드의 파라미터
	 * 이름과 요청 파라미터의 이름이 같은 경우 @RequestParam 애노테이션을
	 * 지정하지 않아도 스프링으로부터 요청 파라미터를 받을 수 있다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명은 boardList() 메서드 주석 위쪽에서 설명한 주석을 참고하기 바란다.
	 *  
	 * 아래는 pageNum이라는 요청 파라미터가 없을 경우 required=false를
	 * 지정해 필수 조건을 주지 않았고 기본 값을  defaultValue="1"로 지정해
	 * 메서드의 파라미터인 pageNum으로 받을 수 있도록 하였다.
	 * defaultValue="1"이 메서드의 파라미터인 pageNum에 바인딩될 때
	 * 스프링이 int 형으로 형 변환하여 바인딩 시켜준다. 또한 검색 타입과 검색어를
	 * 받기 위해 type과 keyword를 메서드의 파라미터로 지정하고 요청 파라미터가
	 * 없을 경우를 대비해 required=false를 지정해 필수 조건을 주지 않았고
	 * 기본 값을 defaultValue="null"로 지정해 type과 keyword로 
	 * 받을 수 있도록 하였다.
	 **/	
	@RequestMapping(value= {"/boardList", "/list"})
	public String boardList(Model model, 
			@RequestParam(value="pageNum", required=false, 
						defaultValue="1") int pageNum,
			@RequestParam(value="type", required=false,  
						defaultValue="null") String type,
			@RequestParam(value="keyword", required=false,
						defaultValue="null") String keyword) {		

		/* Service 클래스를 이용해 게시 글 리스트를 가져온다.
		 * Service 클래스 안에서 일반 리스트 요청인지, 검색 요청인지를
		 * 체크해서 각각에 맞는 필요한 데이터를 반환하도록 구현하면 된다.  
		 **/		
		Map<String, Object> modelMap = 
				boardService.boardList(pageNum, type, keyword);
		
		/* 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장한다.
		 * 모델에는 도메인 객체나 비즈니스 로직을 처리한 결과를 저장한다. 
		 **/		
		model.addAllAttributes(modelMap);		
		
		/* servlet-context.xml에 설정한 ViewResolver에서 prefix와 suffix에
		 * 지정한 정보를 제외한 뷰 이름을 문자열로 반환하면 된다.
		 * 
		 * 아래와 같이 뷰 이름을 반환하면 포워드 되어 제어가 뷰 페이지로 이동한다.
		 **/
		return "boardList";
	}
	
	/* 게시 글 상세보기 요청을 처리하는 메서드
	 * 
	 * 아래는 "/boardDetail"로 들어오는 GET 방식 요청을 처리하는 메서드를 지정한
	 * 것이다. method 속성을 생략했기 때문에 RequestMethod.GET이 적용된다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명과 @RequestParam에 대한 설명은 boardList() 메서드의 주석을 
	 * 참고하기 바란다.
	 * 
	 * 아래는 pageNum이라는 요청 파라미터가 없을 경우 required=false를
	 * 지정해 필수 조건을 주지 않았고 기본 값을  defaultValue="1"로 지정해
	 * 메서드의 파라미터인 pageNum으로 받을 수 있도록 하였다.
	 * defaultValue="1"이 메서드의 파라미터인 pageNum에 바인딩될 때
	 * 스프링이 int 형으로 형 변환하여 바인딩 시켜준다. 또한 검색 타입과 검색어를
	 * 받기 위해 type과 keyword를 메서드의 파라미터로 지정하고 요청 파라미터가
	 * 없을 경우를 대비해 required=false를 지정해 필수 조건을 주지 않았고
	 * 기본 값을 defaultValue="null"로 지정해 type과 keyword로 
	 * 받을 수 있도록 하였다.
	 **/
	@RequestMapping("/boardDetail")
	public String boardDetail(Model model, int no, 
			@RequestParam(value="pageNum", required=false, 
					defaultValue="1") int pageNum,
			@RequestParam(value="type", required=false,  
					defaultValue="null") String type,
			@RequestParam(value="keyword", required=false,
					defaultValue="null") String keyword) throws Exception {
		
		/* 요청 파라미터에서 type이나 keyword가 비어 있으면 일반 
		 * 게시 글 리스트를 요청하는 것으로 간주하여 false 값을 갖게 한다.
		 * Controller에서 type이나 keyword의 요청 파라미터가 없으면
		 * 기본 값을 "null"로 지정했기 때문에 아래와 같이 체크했다.
		 **/
		boolean searchOption = (type.equals("null") 
				|| keyword.equals("null")) ? false : true; 		
		
		/* Service 클래스를 이용해 no에 해당하는 게시 글 하나의 정보를 읽어온다.
		 * 두 번째 인수에 true를 지정해 게시 글 읽은 횟수를 1 증가 시킨다.
		 **/
		
		//annotation을 이용한 트랜잭션 처리 메소드(트랜잭션 처리 전과 같은 코드문임)
		//Board board = boardService.getBoard(no, true);
		
		//코드기반 트랜잭션처리 메소드
		Board board = boardService.getBoardCode(no, true);
		List<Reply> replyList = boardService.replyList(no);
		/* 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장한다.
		 * 모델에는 도메인 객체나 비즈니스 로직을 처리한 결과를 저장한다. 
		 **/	
		model.addAttribute("replyList", replyList);
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("searchOption", searchOption);
		
		/*boolean fileExist = board.getFile1().equals("null") ? false : true;
		
		if(!fileExist) {
			model.addAttribute("fileName", URLEncoder.encode(board.getFile1(), "utf-8"));
		}*/
		
		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if(searchOption) {
			
			/* IE에서 링크로 요청 시 파라미터에 한글이 포함되면 IE는 URLEncoding을
			 * 하지 않고 서버로 전송하는데 톰캣 7.06x 버전에서 정상적으로 동작하던 것이
			 * 7.07x 버전에서는 Invalid character found in the request target
			 * 이라는 에러가 발생한다. 이 문제는 아래와 같이 java.net 패키지의 URLEncoder
			 * 클래스를 이용해 수동으로 URLEncoding을 해주면 해결할 수 있다.
			 * 크롬 브라우저는 링크로 요청 시 파라미터에 한글이 포함되어 있으면 브라우저 
			 * 주소창에는 한글 그대로 표시되지만 UTF-8로 URLEncoding을 해준다.
			 **/		
			model.addAttribute("keyword", URLEncoder.encode(keyword, "utf-8"));
			model.addAttribute("type", type);
			model.addAttribute("word", keyword);
		}
		
		/* servlet-context.xml에 설정한 ViewResolver에서 prefix와 suffix에
		 * 지정한 정보를 제외한 뷰 이름을 문자열로 반환하면 된다.
		 * 
		 * 아래와 같이 뷰 이름을 반환하면 포워드 되어 제어가 뷰 페이지로 이동한다. 
		 **/
		return "boardDetail";
	}
	
	/* 게시 글쓰기 폼에서 들어오는 게시 글쓰기 요청을 처리하는 메서드
	 * 
	 * @RequestMapping의 ()에 value="/writeProcess", method=RequestMethod.Post를
	 * 지정해 "/writeProcess"로 들어오는 POST 방식의 요청을 처리하는 메서드를
	 * 지정한 것이다.
	 * 
	 * 이 메서드 바로 아래에 ModelAndView insertBoardOld(HttpServletRequest request)
	 * 메서드에서는 폼으로부터 넘어온 데이터를 HttpServletRequest를 파라미터로
	 * 받아 각각의 파라미터를 처리한 후 Board 객체에 담았다. 하지만
	 * 스프링은 폼으로부터 전달된 파라미터를 객체로 처리 할 수 있는 아래와 같은
	 * 방법을 제공하고 있다. 아래와 같이 요청 파라미터를 전달받을 때 사용하는 
	 * 객체를 커맨드 객체라고 부르며 이 커맨드 객체는 자바빈 규약에 따라 프로퍼티에
	 * 대한 setter를 제공하도록 작성해야 한다. 그리고 파라미터 이름이 커맨드 객체의
	 * 프로퍼티와 동일하도록 폼 컨트롤의 name 속성을 지정해야 한다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 컨트롤러 메서드에 커맨드 객체를
	 * 파라미터로 지정하면 커맨드 객체의 프로퍼티와 동일한 이름을 가진 요청 
	 * 파라미터의 데이터를 스프링이 자동으로 설정해 준다. 이때 스프링은 자바빈
	 * 규약에 따라 적절한 setter 메서드를 사용해 값을 설정한다.
	 * 
	 * 커맨드 객체의 프로퍼티와 일치하는 파라미터 이름이 없다면 기본 값으로 설정된다.
	 * 또한 프로퍼티의 데이터 형에 맞게 적절히 형 변환 해 준다. 형 변환을 할 수 없는
	 * 경우 스프링은 400 에러를 발생 시킨다. 예를 들면 프로퍼티가 정수형 일 때 매칭 되는
	 * 값이 정수형으로 형 변환 할 수 없는 경우 400 에러를 발생 시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명과 @RequestParam에 대한 설명은 boardList() 메서드의 주석을 
	 * 참고하기 바란다. 
	 **/
	@RequestMapping(value="/writeProcess", method=RequestMethod.POST)
	public String insertBoard(HttpServletRequest request, 
			String title, String writer, String content,String pass, 
			@RequestParam(value="file1", required=false) MultipartFile multipartFile) throws IOException {
		
		System.out.println("originName : " + multipartFile.getOriginalFilename());
		System.out.println("name : " + multipartFile.getName());
		
		Board board = new Board();
		board.setTitle(title);
		board.setWriter(writer);
		board.setContent(content);
		board.setPass(pass);
		
		if (!multipartFile.isEmpty()) {
			
			String filePath = request.getServletContext().getRealPath(DEFAULT_PATH);
	
			UUID uid = UUID.randomUUID();
			
			String saveName = uid.toString() + "_" + multipartFile.getOriginalFilename();
			
			File file = new File(filePath, saveName);
			System.out.println("insertBoard - newName : " + file.getName());
			
			multipartFile.transferTo(file);
			
			board.setFile1(saveName);
		}
		
		boardService.insertBoard(board);	

		return "redirect:boardList";
	}
	
	
	//게시글쓰기(다중 파일 업로드) 폼에서 들어오는 게시 글쓰기 요청을 처리하는 메소드
	@RequestMapping(value="/writeMultiFile", method=RequestMethod.POST)
	public String insertBoardMulti(HttpServletRequest request, String title, 
			String writer, String content, String pass,
			@RequestParam(value="file1", required=false) MultipartFile multipartFile,
			@RequestParam(value="files", required=false) MultipartFile[] multiFiles) throws Exception {
		
		System.out.println("files : " + multiFiles.length + " from BoardController");
		System.out.println("originName : " + multipartFile.getName() + " from BoardController");
		System.out.println("name" + multipartFile.getName() + " from BoardController");
		
		Board board = new Board();
		board.setTitle(title);
		board.setWriter(writer);
		board.setContent(content);
		board.setPass(pass);
		
		String filePath = request.getServletContext().getRealPath(DEFAULT_PATH);
		boardService.insertBoardMultiCode(filePath, board, multipartFile, multiFiles);

		return "redirect:boardList";
	}
	
	
	
	
	/* 게시 글 수정 폼 요청을 처리하는 메서드
	 * 
	 * 아래는 "/update"로 들어오는 GET 방식 요청을 처리하는 메서드를 지정한
	 * 것이다. method 속성을 생략하면 기본 값은 RequestMethod.GET 이다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드의 파라미터에
	 * HttpServletResponse와 PrintWriter를 지정했고 요청 파라미터를 받을
	 * no와 pass도 지정했다. 이렇게 Controller 메서드의 파라미터에 필요한 
	 * 객체나 요청 파라미터 이름과 동일한 이름의 파라미터를 지정하면 스프링이 자동으로
	 * 설정해 준다. 만약 요청 파라미터와 메서드의 파라미터 이름이 다른 경우 Controller
	 * 메서드의 파라미터 앞에 @RequestParam("요청 파라미터 이름")을 사용해
	 * 요청 파라미터의 이름을 지정하면 스프링이 데이터 형에 맞게 적절히 형 변환까지
	 * 해 준다. 형 변환을 할 수 없는 경우 스프링은 400 에러를 발생 시킨다. 예를 들면
	 * Controller 메서드의 파라미터가 정수형 일 때 요청 파라미터의 값이 정수형으로
	 * 형 변환 할 수 없는 경우 400 에러를 발생 시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명과 @RequestParam에 대한 설명은 boardList() 메서드의 주석을 
	 * 참고하기 바란다.
	 **/
	@RequestMapping(value="/update")
	public String updateBoard(Model model, HttpServletResponse response, 
			PrintWriter out, int no, String pass,
			@RequestParam(value="pageNum", required=false, 
					defaultValue="1") int pageNum,
			@RequestParam(value="type", required=false,  
					defaultValue="null") String type,
			@RequestParam(value="keyword", required=false,
					defaultValue="null") String keyword) throws Exception {
		
		// BoardService 클래스를 이용해 게시판 테이블에서 비밀번호가 맞는지 체크한다. 
		boolean result = boardService.isPassCheck(no, pass);
		
		// 비밀번호가 맞지 않으면
		if(! result) {

			throw new BoardPassCheckFailException("비밀번호가 맞지 않습니다.");

		}
		
		/* 요청 파라미터에서 type이나 keyword가 비어 있으면 일반 
		 * 게시 글 리스트를 요청하는 것으로 간주하여 false 값을 갖게 한다.
		 * Controller에서 type이나 keyword의 요청 파라미터가 없으면
		 * 기본 값을 "null"로 지정했기 때문에 아래와 같이 체크했다.
		 **/
		boolean searchOption = (type.equals("null") 
				|| keyword.equals("null")) ? false : true; 
		
		/* Service 클래스를 이용해 no에 해당하는 게시 글 하나의 정보를 읽어온다.
		 * 두 번째 인수로 false를 지정해 게시 글 읽은 횟 수를 증가시키지 않는다. 
		 **/
		Board board = boardService.getBoard(no, false);
		
		/* 파라미터로 받은 모델 객체에 뷰로 보낼 모델을 저장한다.
		 * 모델에는 도메인 객체나 비즈니스 로직을 처리한 결과를 저장한다. 
		 **/
		model.addAttribute("board", board);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("searchOption", searchOption);
		
		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if(searchOption) {
			
			/* IE에서 링크로 요청 시 파라미터에 한글이 포함되면 IE는 URLEncoding을
			 * 하지 않고 서버로 전송하는데 톰캣 7.06x 버전에서 정상적으로 동작하던 것이
			 * 7.07x 버전에서는 Invalid character found in the request target
			 * 이라는 에러가 발생한다. 이 문제는 아래와 같이 java.net 패키지의 URLEncoder
			 * 클래스를 이용해 수동으로 URLEncoding을 해주면 해결할 수 있다.
			 * 크롬 브라우저는 링크로 요청 시 파라미터에 한글이 포함되어 있으면 브라우저 
			 * 주소창에는 한글 그대로 표시되지만 UTF-8로 URLEncoding을 해준다.
			 **/
			model.addAttribute("keyword", URLEncoder.encode(keyword, "utf-8"));
			model.addAttribute("type", type);
			model.addAttribute("word", keyword);
		}
		
		/* servlet-context.xml에 설정한 ViewResolver에서 prefix와 suffix에
		 * 지정한 정보를 제외한 뷰 이름을 문자열로 반환하면 된다.
		 * 
		 * 아래와 같이 뷰 이름을 반환하면 포워드 되어 제어가 뷰 페이지로 이동한다. 
		 **/
		return "updateForm";
	}
	
	/* 게시 글 수정 폼에서 들어오는 게시 글 수정 요청을 처리하는 메서드
	 * 
	 * @RequestMapping의 ()에 value="/update", method=RequestMethod.Post를
	 * 지정해 "/update"로 들어오는 POST 방식의 요청을 처리하는 메서드를 지정한 것이다.
	 *
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드의 파라미터에
	 * HttpServletResponse와 PrintWriter를 지정했고 요청 파라미터를 받을
	 * Board 객체를 지정했다. 또한 리다이렉트할 때 pageNum을 파라미터로 보내기
	 * 위해서 RedirectAttributes 객체를 메서드의 파라미터로 지정했다.
	 *  
	 * 스프링은 폼으로부터 전달된 파라미터를 객체로 처리 할 수 있는 아래와 같은
	 * 방법을 제공하고 있다. 아래와 같이 요청 파라미터를 전달받을 때 사용하는 
	 * 객체를 커맨드 객체라고 부르며 이 커맨드 객체는 자바빈 규약에 따라 프로퍼티에
	 * 대한 setter를 제공하도록 작성해야 한다. 그리고 파라미터 이름이 커맨드 객체의
	 * 프로퍼티와 동일하도록 폼 컨트롤의 name 속성을 지정해야 한다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 컨트롤러 메서드에 커맨드 객체를
	 * 파라미터로 지정하면 커맨드 객체의 프로퍼티와 동일한 이름을 가진 요청 
	 * 파라미터의 데이터를 스프링이 자동으로 설정해 준다. 이때 스프링은 자바빈
	 * 규약에 따라 적절한 setter 메서드를 사용해 값을 설정한다.
	 * 
	 * 커맨드 객체의 프로퍼티와 일치하는 파라미터 이름이 없다면 기본 값으로 설정된다.
	 * 또한 프로퍼티의 데이터 형에 맞게 적절히 형 변환 해 준다. 형 변환을 할 수 없는
	 * 경우 스프링은 400 에러를 발생 시킨다. 예를 들면 프로퍼티가 정수형 일 때 매칭 되는
	 * 값이 정수형으로 형 변환 할 수 없는 경우 400 에러를 발생 시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명과 @RequestParam에 대한 설명은 boardList() 메서드의 주석을 
	 * 참고하기 바란다. 
	 **/
	@RequestMapping(value="update", method=RequestMethod.POST)
	public String updateBoard(HttpServletResponse response, 
			PrintWriter out, Board board,
			RedirectAttributes reAttrs, 
			@RequestParam(value="pageNum", required=false, 
					defaultValue="1") int pageNum,
			@RequestParam(value="type", required=false,  
					defaultValue="null") String type,
			@RequestParam(value="keyword", required=false,
					defaultValue="null") String keyword) throws Exception {		
		
		// BoardService 클래스를 이용해 게시판 테이블에서 비밀번호가 맞는지 체크한다. 
		boolean result = boardService.isPassCheck(board.getNo(), board.getPass());
		
		// 비밀번호가 맞지 않으면
		if(! result) {
			throw new BoardPassCheckFailException("비밀번호가 맞지 않습니다.");

		}
		
		/* 요청 파라미터에서 type이나 keyword가 비어 있으면 일반 
		 * 게시 글 리스트를 요청하는 것으로 간주하여 false 값을 갖게 한다.
		 * Controller에서 type이나 keyword의 요청 파라미터가 없으면
		 * 기본 값을 "null"로 지정했기 때문에 아래와 같이 체크했다.
		 **/
		boolean searchOption = (type.equals("null") 
				|| keyword.equals("null")) ? false : true; 
		
		// BoardService 클래스를 이용해 게시판 테이블에서 게시 글을 수정한다.
		boardService.updateBoard(board);
		
		/* 클라이언트 요청을 처리한 후 리다이렉트 해야 할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * Redirect 되는 경우 주소 끝에 파라미터를 지정해 GET방식의 파라미터로
		 * 전송할 수 있지만 스프링프레임워크가 지원하는 RedirectAttributs객체를
		 * 이용하면 한 번만 사용할 임시 데이터와 지속적으로 사용할 파라미터를 구분해
		 * 지정할 수 있다.
		 * 
		 * 아래와 같이 RedirectAttributs의 addAttribute() 메서드를 사용해
		 * 지속적으로 사용할 파라미터를 지정하면 자동으로 주소 뒤에 파라미터로
		 * 추가되며 addFlashAttribute() 메서드를 사용해 파라미터로 지정하면
		 * 한 번만 사용할 수 있도록 주소 뒤에 파라미터로 추가되지 않는다.
		 * addAttribute() 메서드를 사용해 파라미터로 지정한 데이터는 페이지를
		 * 새로 고침해도 계속해서 주소 뒤에 파라미터로 남아있지만 addFlashAttribute()
		 * 메서드를 사용해 지정한 파라미터는 사라지기 때문에 1회성으로 필요한
		 * 데이터를 addFlashAttribute() 메서드를 사용해 지정하면 편리하다.
		 * 
		 * 파라미터에 한글이 포함되는 경우 URLEncoding을 코드로 구현해야 하지만
		 * web.xml에서 스프링프레임워크가 지원하는 CharacterEncodingFilter를
		 * 설정했기 때문에 Filter에 의해 UTF-8로 인코딩 되어 클라이언트로 응답된다.
		 * 
		 * 아래는 게시 글 리스트로 Redirect 되면서 같이 보내야할 searchOption을
		 * RedirectAttributs를 이용해 파라미터로 전달하는 예이다. 
		 **/
		reAttrs.addAttribute("searchOption", searchOption);
		
		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if(searchOption) {			
			
			/* Redirect 되는 경우 주소 끝에 파라미터를 지정해 GET방식의 파라미터로
			 * 전송할 수 있지만 스프링프레임워크가 지원하는 RedirectAttributs객체를
			 * 이용하면 한 번만 사용할 임시 데이터와 지속적으로 사용할 파라미터를 구분해
			 * 지정할 수 있다.
			 * 
			 * 게시 글 상세 보기 요청을 처리하는 boardDetail() 메서드에서 뷰 페이지에서
			 * 링크에 사용할 keyword를 java.net 패키지의 URLEncoder 클래스를
			 * 이용해 수동으로 인코딩한 후 모델에 담아 뷰 페이지로 전달하였다.
			 * 
			 * 리다이렉트 될 때 필요한 파라미터를 스프링이 제공하는 RedirectAttributs의
			 * addAttribute() 메서드를 사용해 파라미터를 지정하면 자동으로 주소 뒤에 
			 * 요청 파라미터로 추가되며 파라미터에 한글이 포함되는 경우 URLEncoding을
			 * java.net 패키지의 URLEncoder 클래스를 이용해 인코딩 해줘야 하지만
			 * web.xml에서 스프링프레임워크가 지원하는 CharacterEncodingFilter를
			 * 설정했기 때문에 Filter에 의해 UTF-8로 인코딩 되어 클라이언트로 응답된다.
			 * 
			 * 아래는 검색 리스트로 Redirect 되면서 같이 보내야할 keyword와 type을
			 * RedirectAttributs를 이용해 파라미터로 전달하는 예이다. 
			 **/			
			reAttrs.addAttribute("keyword", keyword);
			reAttrs.addAttribute("type", type);
		}
		
		reAttrs.addAttribute("pageNum", pageNum);		
		//reAttrs.addFlashAttribute("test", "1회용 파라미터 받음 - test");
		return "redirect:boardList";
	}
	
	/* 게시 글 상세보기에서 들어오는 게시 글 삭제 요청을 처리하는 메서드
	 * 
	 * 아래는 "/delete", "/deleteBoard"로 들어오는 GET 방식 요청을 처리하는
	 * 메서드를 지정한 것으로 method 속성을 생략됐기 때문에 GET 방식 요청을 처리한다. 
	 * 
	 * @RequestMapping 애노테이션이 적용된 Controller 메서드의 파라미터에
	 * HttpServletResponse와 PrintWriter를 지정했고 요청 파라미터를 받을
	 * no와 pass도 지정했다. 그리고 리다이렉트 할 때 pageNum을 파라미터로 보내기
	 * 위해서 RedirectAttributes 객체를 메서드의 파라미터로 지정했다. 
	 * 이렇게 Controller 메서드의 파라미터에 필요한 객체나 요청 파라미터 이름과 동일한
	 * 이름의 파라미터를 지정하면 스프링이 자동으로 설정해 준다. 만약 요청 파라미터와
	 * 메서드의 파라미터 이름이 다른 경우 Controller 메서드의 파라미터 앞에 
	 * @RequestParam("요청 파라미터 이름")을 사용해 요청 파라미터의 이름을
	 * 지정하면 스프링이 데이터 형에 맞게 적절히 형 변환까지 해 준다. 형 변환을 할 수
	 * 없는 경우 스프링은 400 에러를 발생 시킨다. 예를 들면 Controller 메서드의
	 * 파라미터가 정수형 일 때 요청 파라미터의 값이 정수형으로 형 변환 할 수 없는 
	 * 경우 400 에러를 발생 시킨다.
	 * 
	 * @RequestMapping 애노테이션이 적용된 메서드의 파라미터와 반환 타입에
	 * 대한 설명과 @RequestParam에 대한 설명은 boardList() 메서드의 주석을 
	 * 참고하기 바란다.
	 **/
	@RequestMapping({"/delete", "deleteBoard"})
	public String deleteBoard(HttpServletResponse response, 
			PrintWriter out, int no, String pass,
			RedirectAttributes reAttrs, 
			@RequestParam(value="pageNum", required=false, 
				defaultValue="1") int pageNum,
			@RequestParam(value="type", required=false,  
				defaultValue="null") String type,
			@RequestParam(value="keyword", required=false,
				defaultValue="null") String keyword) throws Exception {
		
		// BoardService 클래스를 이용해 게시판 테이블에서 비밀번호가 맞는지 체크한다. 
		boolean result = boardService.isPassCheck(no, pass);
		
		// 비밀번호가 맞지 않으면
		if(! result) {

			/* 컨트롤러에서 null을 반환하거나 메서드의 반환 타입이 void일 경우
			 * Writer나 OutputStream을 이용해 응답 결과를 직접 작성할 수 있다.
			 * DispatcherServlet을 경유해 리소스 자원에 접근하는 경우에
			 * 자바스크립트의 history.back()은 약간의 문제를 일으킬 수 있다.
			 * history 객체를 이용하는 경우 서버로 요청을 보내는 것이 아니라
			 * 브라우저의 접속 이력에서 이전 페이지로 이동되기 때문에 발생한다. 
			 **/
			response.setContentType("text/html; charset=utf-8");				
			out.println("<script>");
			out.println("	alert('비밀번호가 맞지 않습니다.');");
			out.println("	history.back();");
			out.println("</script>");

			return null;
		}
		
		/* 요청 파라미터에서 type이나 keyword가 비어 있으면 일반 
		 * 게시 글 리스트를 요청하는 것으로 간주하여 false 값을 갖게 한다.
		 * Controller에서 type이나 keyword의 요청 파라미터가 없으면
		 * 기본 값을 "null"로 지정했기 때문에 아래와 같이 체크했다.
		 **/
		boolean searchOption = (type.equals("null") 
				|| keyword.equals("null")) ? false : true; 
		
		// BoardService 클래스를 이용해 게시판 테이블에서 게시 글을 수정한다.
		boardService.deleteBoard(no);
		
		/* 클라이언트 요청을 처리한 후 리다이렉트 해야 할 경우 아래와 같이 redirect:
		 * 접두어를 붙여 뷰 이름을 반환하면 된다. 뷰 이름에 redirect 접두어가 붙으면
		 * HttpServletResponse를 사용해서 지정한 경로로 Redirect 된다. 
		 * redirect 접두어 뒤에 경로를 지정할 때 "/"로 시작하면 ContextRoot를
		 * 기준으로 절대 경로 방식으로 Redirect 된다. "/"로 시작하지 않으면 현재 
		 * 경로를 기준으로 상대 경로로 Redirect 된다. 또한 다른 사이트로 Redirect
		 * 되기를 원한다면 redirect:http://사이트 주소를 지정한다.
		 * 
		 * Redirect 되는 경우 주소 끝에 파라미터를 지정해 GET방식의 파라미터로
		 * 전송할 수 있지만 스프링프레임워크가 지원하는 RedirectAttributs객체를
		 * 이용하면 한 번만 사용할 임시 데이터와 지속적으로 사용할 파라미터를 구분해
		 * 지정할 수 있다.
		 * 
		 * 아래와 같이 RedirectAttributs의 addAttribute() 메서드를 사용해
		 * 지속적으로 사용할 파라미터를 지정하면 자동으로 주소 뒤에 파라미터로
		 * 추가되며 addFlashAttribute() 메서드를 사용해 파라미터로 지정하면
		 * 한 번만 사용할 수 있도록 주소 뒤에 파라미터로 추가되지 않는다. 
		 * addAttribute() 메서드를 사용해 파라미터로 지정한 데이터는 페이지를
		 * 새로 고침해도 계속해서 주소 뒤에 파라미터로 남아있지만 addFlashAttribute()
		 * 메서드를 사용해 지정한 파라미터는 사라지기 때문에 1회성으로 필요한
		 * 데이터를 addFlashAttribute() 메서드를 사용해 지정하면 편리하다.
		 * 
		 * 파라미터에 한글이 포함되는 경우 URLEncoding을 코드로 구현해야 하지만
		 * web.xml에서 스프링프레임워크가 지원하는 CharacterEncodingFilter를
		 * 설정했기 때문에 Filter에 의해 UTF-8로 인코딩 되어 클라이언트로 응답된다.
		 * 
		 * 아래는 게시 글 리스트로 Redirect 되면서 같이 보내야할 searchOption을
		 * RedirectAttributs를 이용해 파라미터로 전달하는 예이다. 
		 **/
		reAttrs.addAttribute("searchOption", searchOption);
		
		// 검색 요청이면 type과 keyword를 모델에 저장한다.
		if(searchOption) {
			
			/* Redirect 되는 경우 주소 끝에 파라미터를 지정해 GET방식의 파라미터로
			 * 전송할 수 있지만 스프링프레임워크가 지원하는 RedirectAttributs객체를
			 * 이용하면 한 번만 사용할 임시 데이터와 지속적으로 사용할 파라미터를 구분해
			 * 지정할 수 있다.
			 * 
			 * 게시 글 상세 보기 요청을 처리하는 boardDetail() 메서드에서 뷰 페이지에서
			 * 링크에 사용할 keyword를 java.net 패키지의 URLEncoder 클래스를
			 * 이용해 수동으로 인코딩한 후 모델에 담아 뷰 페이지로 전달하였다.
			 * 
			 * 리다이렉트 될 때 필요한 파라미터를 스프링이 제공하는 RedirectAttributs의
			 * addAttribute() 메서드를 사용해 파라미터를 지정하면 자동으로 주소 뒤에 
			 * 요청 파라미터로 추가되며 파라미터에 한글이 포함되는 경우 URLEncoding을
			 * java.net 패키지의 URLEncoder 클래스를 이용해 인코딩 해줘야 하지만
			 * web.xml에서 스프링프레임워크가 지원하는 CharacterEncodingFilter를
			 * 설정했기 때문에 Filter에 의해 UTF-8로 인코딩 되어 클라이언트로 응답된다.
			 * 
			 * 아래는 검색 리스트로 Redirect 되면서 같이 보내야할 keyword와 type을
			 * RedirectAttributs를 이용해 파라미터로 전달하는 예이다. 
			 **/			
			reAttrs.addAttribute("keyword", keyword);
			reAttrs.addAttribute("type", type);
		}
		
		reAttrs.addAttribute("pageNum", pageNum);
		//reAttrs.addFlashAttribute("test", "1회용 파라미터 받음 - test");
		return "redirect:boardList";
	}	
	
	//파일 다운로드를 위한 메소드 구현 및 매핑
	@RequestMapping("/fileDownload")
	public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String fileName = request.getParameter("fileName");
		System.out.println("beforeFileName : " + fileName);
		
		String filePath = request.getServletContext().getRealPath(DEFAULT_PATH);
		System.out.println("afterFileName : " + fileName);
		
		File file = new File(filePath, fileName);
		System.out.println("file.getName() : " + file.getName());
		
		//응답 데이터에 파일 다운로드 관련 컨텐츠 타입 설정 필요함
		response.setContentType("application/download;charset=UTF-8");
		response.setContentLength((int)file.length());
		
		
		//클라이언트로 내려보내는 파일명 - 위에서 파라미터로 받아온거라서 인코딩 안 되어있으므로 인코딩 필요
		fileName = URLEncoder.encode(file.getName(), "UTF-8");
		System.out.println("다운로드 fileName : " + fileName);
		
		//전송되는 파일의 이름을 한글 그대로(원본 파일 그대로) 보내주기 위한 설정(뭐지 이거?? 찾아보고 모르면 질문)
		response.setHeader("Content-Disposition", "attachment : filename=\"" + fileName + "\";");
		
		//파일 전송시 전송되는 데이터 인코딩을 바이너리로 설정(이것도 모르겠다 질문)
		response.setHeader("Content-Transfer-Encoding", "binary");
		
		//파일을 클라이언트로 보내기 위해 스트림을 열자
		OutputStream out = response.getOutputStream();
		FileInputStream fis = null;
		
		
		//클라이언트에게 보낼 파일을 읽어서 스트림을 통해 클라이언트로 출력하기 위해 FileInputStream 클래스를 이용해서 file을 스트림에 담음 
		fis = new FileInputStream(file);
		
		
		//스프링이 제공하는 FileCopyUtils를 이용해 응답스트림에 파일을 복사(이것도 찾아보고 모르면 질문)
		FileCopyUtils.copy(fis, out);
		
		//fis가 null이 아니면 파일 전송 된거니까 data leakage 방지위해 fis를 닫아줘야 함
		if (fis != null) {
			fis.close();
		}
		
		//파일 데이터를 클라이언트로 출력한다(버퍼에 남은 모든 데이터 출력하는 것)
		out.flush();
		
	}
	
	@ExceptionHandler(TypeMismatchException.class)
	public String bedRequestException(Model model) {
		model.addAttribute("title", "요청 파라미터가 잘못 되었습니다(board)");
		return "errors/controllerException";
	}
	
	@ExceptionHandler(BoardPassCheckFailException.class)
	public String boardPassCheckFail(Model model) {
		model.addAttribute("title", "비밀번호가 맞지 않음(board)");
		return "errors/runtimeException";
	}
	
	
	
	
}
