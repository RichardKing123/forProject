<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>    
<article>
	<%-- 
		스프링 태그 라이브러리를 이용해 root-context.xml에서 설정한
		titleMessages.properties 메시지 자원에 접근해 code 속성 지정한
		키에 해당하는 메시지를 출력한다.
	--%>
	<h2 class="memberTitle"><spring:message code="login.title" /></h2>
	<form name="loginForm" id="loginForm" action="login" method="post">
		<div>아 이 디 : <input type="text" name="id" /></div>
		<div>비밀번호 : <input type="password" name="pass" /></div>
		<div class="btnSubmit"><input type="submit" value="로그인" /></div>
	</form>
</article>