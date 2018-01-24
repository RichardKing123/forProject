<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<article>
<form name="updateForm" id="updateForm" action="update" method="post">
	<%--
		no는 DB에서 게시 글을 수정하기 위해 필요하고 pageNum은 게시 글이 
		수정된 후에 이전에 사용자가 머물렀던 게시 글 리스트의 동일한 페이지로
		보내기 위해 필요한 정보이다.  
	--%>
	<input type="hidden" name="no" value="${ board.no }" />
	<input type="hidden" name="pageNum" value="${ pageNum }" />
	<%-- 
		검색 요청일 경우 다시 keyword에 해당하는 검색 리스트로
		돌려보내기 위해서 아래의 파라미터가 필요하다.
	 --%>
	<c:if test="${ searchOption }">
		<input type="hidden" name="type" value="${ type }" />
		<input type="hidden" name="keyword" value="${ word }" />
	</c:if>		
<table class="readTable">
	<tr>
		<td colspan="4" class="boardTitle"><h2>게시 글 수정하기</h2></td>
	</tr>
	<tr><td colspan="4">&nbsp;</td></tr>
	<tr>
		<th class="readTh">글쓴이</th>
		<td class="readTd">
			<input type="text" name="writer" id="writer" size="30" 
				maxlength="10" value="${ board.writer }" readonly/>
		</td>
		<th class="readTh">비밀번호</th>
		<td class="readTd">
			<input type="password" name="pass" id="pass" size="30" 
				maxlength="10" />
		</td>
	</tr>
	<tr>
		<th class="readTh">제&nbsp;&nbsp;목</th>
		<td class="readTd" colspan="3">
			<input type="text" name="title" id="title" size="50" 
				maxlength="50" value="${ board.title }"/>
		</td>				
	</tr>
	<tr>
		<th class="readTh">내&nbsp;&nbsp;용</th>
		<td class="readTd" colspan="3">
			<textarea name="content" id="content" rows="20" 
				cols="72">${ board.content } </textarea>
		</td>				
	</tr>
	<tr>
		<th class="readTh">파일첨부</th>
		<td class="readTd" colspan="3">
			<input type="file" name="file" id="file" size="50" 
				${ empty board.file1 ? "" : "disabled" } />
		</td>				
	</tr>
	<tr>
			<td colspan="4">&nbsp;</td></tr>
	<tr>
	<tr>		
		<td class="tdSpan" colspan="4">
			<input type="reset" value="다시쓰기" />
			&nbsp;&nbsp;<input type="submit" value="수정하기" />
			<%-- 일반 게시 글 리스트에서 온 요청이면 일반 게시 글 리스트로 돌려보낸다. --%>
			<c:if test="${ not searchOption }">		
				&nbsp;&nbsp;<input type="button" value="목록보기" 
					onclick="javascript:window.location.href=
						'boardList?pageNum=${ pageNum }'"/>
			</c:if>
			<%-- 검색 리스트에서 온 요청이면 검색 리스트의 동일한 페이지로 돌려보낸다. --%>
			<c:if test="${ searchOption }">
				&nbsp;&nbsp;<input type="button" value="목록보기" 
					onclick="javascript:window.location.href=
						'boardList?pageNum=${ pageNum }&type=${ type }&keyword=${ keyword }'"/>
				<%-- 
					위의 쿼리 스트링을 작성할 때 같은 줄에서 띄어쓰기 하는 것은 문제되지
					않지만 줄 바꿔서 작성하게 되면 스크립트 에러가 발생한다.
				--%>		
			</c:if>				
		</td>
	</tr>
</table>
</form>			
</article>