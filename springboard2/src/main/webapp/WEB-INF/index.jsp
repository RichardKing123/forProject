<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8" />
<title>Spring MVC 게시판 01</title>
<!--
	WEB-INF/spring/appServlet/servlet-context.xml에서 정적 리소스와
	관련된 url 맵핑을 아래와 같이 	설정했기 때문에 css의 위치를 
	"resources/css/index.css"와 같이 지정해야 한다.	

	<mvc:resources mapping="/resources/**" location="/resources/" />
-->
<link rel="stylesheet" type="text/css" href="resources/css/index.css" />
<link rel="stylesheet" type="text/css" href="resources/css/board.css" />
<link rel="stylesheet" type="text/css" href="resources/css/member.css" />
<script src="resources/js/jquery-3.2.1.min.js"></script>
<script src="resources/js/formcheck.js"></script>
</head>
<body>
	<%@ include file="template/header.jsp" %>
	<jsp:include page="${ param.body }" />
	<%@ include file="template/footer.jsp" %>
</body>
</html>