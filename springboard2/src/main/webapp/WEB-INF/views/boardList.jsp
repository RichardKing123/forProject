<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<article>
<table class="listTable">
	<tr>
		<td class="boardTitle" colspan="5">
			<%-- 
			 스프링 태그 라이브러리를 이용해 root-context.xml에서 설정한
			 titleMessages.properties 메시지 자원에 접근해 code 속성 지정한
			 키에 해당하는 메시지를 출력한다.
			--%>
			<h2><spring:message code="bbs.list.title" /></h2>
		</td>
	</tr>
	<tr>
		<td colspan="5">
			<form name="searchForm" id="searchForm">
				<select name="type" id="type">						
					<option value="title">제목</option>
					<option value="writer">작성자</option>
					<option value="content">내용</option>
				</select>
				<input type="text" name="keyword" id="keyword" />
				<input type="submit" value="검색" />
			</form>
		</td>
	</tr>
	<%-- 검색 요청일 경우만 아래를 화면에 표시 한다. --%>
	<c:if test="${ searchOption }">
	<tr>
		<td colspan="5" id="searchComment">
			"${ word  }" 검색 결과</td>
	</tr>
	</c:if>
	<c:if test="${ not searchOption }">
	<tr>			
		<td colspan="5" class="listWrite">
			<a href="writeForm">글쓰기</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="writeFormMulti">글쓰기(다중업로드)</a>
		</td>
	</tr>
	</c:if>
	<c:if test="${ searchOption }">
	<tr>
		<%-- 검색 요청일 경우 일반 게시 글 리스트로 이동할 수 있도록 링크를 설정했다. --%>
		<td colspan="2" class="boardListLink">
			<a href="boardList">리스트</a>
		</td>
		<td colspan="3" class="listWrite">
			<a href="writeForm">글쓰기</a>&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="writeFormMulti">글쓰기(다중업로드)</a>
		</td>
	</tr>
	</c:if>
	<tr>
		<th class="listThNo">NO</th>
		<th class="listThTitle">제목</th>
		<th class="listThWriter">작성자</th>
		<th class="listThRegDate">작성일</th>
		<th class="listThReadCount">조회수</th>
	</tr>
<%-- 
	검색 요청 이면서 검색된 리스트가 존재할 경우
	게시 글 상세보기로 링크를 적용할 때 type과 keyword 
	파라미터를 적용해 링크를 설정한다. 
--%>	
<c:if test="${ searchOption and not empty boardList }">
	<c:forEach var="b" items="${ boardList }" varStatus="status">		
	<tr class="listTr">
		<td class="listTdNo">${ b.no  }</td>
		<td class="listTdTitle">
			<%--  
			/* IE에서 링크로 요청 시 파라미터에 한글이 포함되면 IE는 URLEncoding을
			 * 하지 않고 서버로 전송하는데 톰캣 7.06x 버전에서 정상적으로 동작하던
			 * 것이 7.07x 버전에서는 Invalid character found in the request 
			 * target 이라는 에러가 발생한다. 이 문제를 해결하기 위해 Service에서
			 * keyword를 java.net 패키지의 URLEncoder 클래스를 이용해 
			 * URLEncoding을 처리하였다.
			 * 크롬 브라우저는 링크로 요청 시 파라미터에 한글이 포함되어 있으면 브라우저 
			 * 주소창에는 한글 그대로 표시되지만 UTF-8로 URLEncoding을 해준다.
			 **/
			--%>			
			<a href="boardDetail?no=${ b.no }&pageNum=${ currentPage }
				&type=${ type }&keyword=${ keyword }">${ b.title }</a>
		</td>
		<td class="listTdWriter">${ b.writer }</td>
		<td class="listTdRegDate"><fmt:formatDate value="${ b.regDate }" 
			pattern="yyyy-MM-dd HH:mm:ss" /></td>
		<td class="listTdReadCount">${ b.readCount }</td>
	</tr>
	</c:forEach>
	<tr>
		<td colspan="5" class="listPage">
			<%--
			/* 현재 페이지 그룹의 시작 페이지가 pageGroup보다 크다는 것은
			 * 이전 페이지 그룹이 존재한다는 것으로 현재 페이지 그룹의 시작 페이지에
			 * pageGroup을 마이너스 하여 링크를 설정하면 이전 페이지 그룹의
			 * startPage로 이동할 수 있다.
		 	 **/
		 	 --%>
		 	<c:if test="${ startPage > pageGroup }">
				<a href="boardList?pageNum=${ startPage - pageGroup }
					&type=${ type }&keyword=${ keyword }">[이전]</a>
			</c:if>	
			<%--
			/* 현재 페이지 그룹의 startPage 부터 endPage 만큼 반복하면서
		 	 * 현재 페이지와 같은 그룹에 속한 페이지를 화면에 출력하고 링크를 설정한다.
		 	 * 현재 페이지는 링크를 설정하지 않는다.
		 	 **/
		 	--%>
			<c:forEach var="i" begin="${ startPage }" end="${ endPage }">
				<c:if test="${ i == currentPage }">
					[ ${ i } ]
				</c:if>			
				<c:if test="${ i != currentPage }">
					<a href="boardList?pageNum=${ i }&type=${ type }
						&keyword=${ keyword }">[ ${ i } ]</a>
				</c:if>			
			</c:forEach>
			<%-- 
			/* 현재 페이지 그룹의 마지막 페이지가 전체 페이지 보다 작다는 것은
			 * 다음 페이지 그룹이 존재한다는 것으로 현재 페이지 그룹의 시작 페이지에
			 * pageGroup을 플러스 하여 링크를 설정하면 다음 페이지 그룹의
			 * startPage로 이동할 수 있다.
		 	 **/
		 	 --%>
			<c:if test="${ endPage < pageCount }">
				<a href="boardList?pageNum=${ startPage + pageGroup }
					&type=${ type }&keyword=${ keyword }">[다음]</a>
			</c:if>		
		</td>
	</tr>
</c:if>	
<%-- 
	일반 게시 글 리스트 요청 이면서 게시 글 리스트가 존재할 경우
	게시 글 상세보기로 링크를 적용할 때 type과 keyword 
	파라미터는 필요 없다. 
--%>	
<c:if test="${ not searchOption and not empty boardList }">
	<c:forEach var="b" items="${ boardList }" varStatus="status">		
	<tr class="listTr">
		<td class="listTdNo">${ b.no  }</td>
		<td class="listTdTitle">
			<a href="boardDetail?no=
				${ b.no }&pageNum=${ currentPage }" >${ b.title }</a>
		</td>
		<td class="listTdWriter">${ b.writer }</td>
		<td class="listTdRegDate"><fmt:formatDate value="${ b.regDate }" 
			pattern="yyyy-MM-dd HH:mm:ss" /></td>
		<td class="listTdReadCount">${ b.readCount }</td>
	</tr>
	</c:forEach>
	<tr>
		<td colspan="5" class="listPage">
			<%--
			/* 현재 페이지 그룹의 시작 페이지가 pageGroup보다 크다는 것은
			 * 이전 페이지 그룹이 존재한다는 것으로 현재 페이지 그룹의 시작 페이지에
			 * pageGroup을 마이너스 하여 링크를 설정하면 이전 페이지 그룹의
			 * startPage로 이동할 수 있다.
		 	 **/
		 	 --%>
		 	<c:if test="${ startPage > pageGroup }"> 
				<a href="boardList?pageNum=${ startPage - pageGroup }">
					[이전]</a>
			</c:if>	
			<%--
			/* 현재 페이지 그룹의 startPage 부터 endPage 만큼 반복하면서
		 	 * 현재 페이지와 같은 그룹에 속한 페이지를 화면에 출력하고 링크를 설정한다.
		 	 * 현재 페이지는 링크를 설정하지 않는다.
		 	 **/
		 	--%>
			<c:forEach var="i" begin="${ startPage }" end="${ endPage }">
				<c:if test="${ i == currentPage }">
					[ ${ i } ]
				</c:if>			
				<c:if test="${ i != currentPage }">
					<a href="boardList?pageNum=${ i }">[ ${ i } ]</a>
				</c:if>			
			</c:forEach>
			<%-- 
			/* 현재 페이지 그룹의 마지막 페이지가 전체 페이지 보다 작다는 것은
			 * 다음 페이지 그룹이 존재한다는 것으로 현재 페이지 그룹의 시작 페이지에
			 * pageGroup을 플러스 하여 링크를 설정하면 다음 페이지 그룹의
			 * startPage로 이동할 수 있다.
		 	 **/
		 	 --%>
			<c:if test="${ endPage < pageCount }">
				<a href="boardList?pageNum=${ startPage + pageGroup }">
					[다음]</a>
			</c:if>		
		</td>
	</tr>
</c:if>
<%-- 검색 요청이면서 검색된 리스트가 존재하지 않을 경우 --%>
<c:if test="${ searchOption and empty boardList }">
	<tr>
		<td colspan="5" class="listTdSpan">
			"${ keyword }"가 포함된 게시 글이 존재하지 않습니다.</td>
	</tr>
</c:if>
<%-- 일반 게시 글 리스트 요청이면서 게시 글 리스트가 존재하지 않을 경우 --%>
<c:if test="${ not searchOption and empty boardList }">
	<tr>
		<td colspan="5" class="listTdSpan">게시 글이 존재하지 않습니다.</td>
	</tr>
</c:if>
</table>
</article>



