$(document).ready(function() {
	
	$("#commend").click(function(){
		
		$.ajax({
			url : "recommend.ajax",
			type : "post",
			data : {recommend : "recommend", no : $("#no").val()},
			dataType : "json",
			success : function(data) {
				alert("추천하기가 반영되었습니다");
				$("#commend > .recommend").text("(" + data.recommend + ")");
				$("#thank > .recommend").text("(" + data.thank + ")");
			},
			error : function(xhr, status, error) {
				alert("error : " + xhr.statusText + ", " + status + ", " + error);
			}
		});
	}).hover(function() {
		$(this).css({"cursor":"pointer"});
	});
	
	$("#thank").click(function() {
		
		$.ajax({
			url : "recommend.ajax",
			type : "post",
			data : {recommend : "thank", no : $("#no").val()},
			dataType : "json",
			success : function(data) {
				alert("땡큐가 반영되었습니다");
				$("#commend > .recommend").text("(" + data.recommend + ")");
				$("#thank > .recommend").text("(" + data.thank + ")");
			},
			error : function(xhr, status, error) {
				alert("error : " + xhr.statusText + ", " + status + ", " + error);
			}
		});
	}).hover(function() {
		$(this).css({"cursor":"pointer"});
	});

	$("#replyWrite").hover(function() {
		$(this).css({"cursor":"pointer"})
	})
	
	//댓글 입력버튼 클릭 시
	$(document).on("click", "#replyWrite", function(){
		if($("#replyForm").css("display") == "block"){
		
			//댓글쓰기 창이 수정쪽에 위치하는 경우 replyWrite를 클릭했을 때 슬라이드 업으로 접고
			//댓글쓰기창으로 이동시킨 뒤 슬라이드 다운
			var $next = $(this).parent().next();
			if(!$($next).is("#replyForm")) {
				$("#replyForm").slideUp(300);
			}
			setTimeOut(function() {
				$("#replyForm").insertBefore("#replyTitle").slideDown(300);
			}, 300);
		} else {
			$("#replyForm").insertBefore("#replyTitle").slideDown(300);
		}
		//댓글 쓰기와 댓글 수정이 같은 폼을 사용하기 때문에 id를 동적으로 댓글쓰기폼으로 변겅하고 
		//수정 버튼 클릭시 추가된 속성인 date-no를 제거한다
		$("#replyForm").find("form").attr("id", "replyWriteForm").removeAttr("date-no");
		//혹시 수정 버튼 클릭 후 내용을 적었을 수 있으니 content의 내용을 공백으로 만들어준다
		$("#replyContent").val("")
	});
	
	//댓글 입력 폼 submit 될 때
	$(document).on("submit", "#replyWriteForm", function() {
		
		if($("#replyContent").val().length <= 5) {
			alert("댓글은 5자 이상 입력해야 합니다");
			return false;
		}
		var params = $(this).serialize();
		
		$.ajax({
			url : "replyWrite.ajax",
			type : "post",
			data : params,
			dataType : "json",
			success : function(resultData, status, xhr) {
				$("#replyTable").empty();
				
				$.each(resultData, function(index, value) {
					
					var date = new Date(value.regDate);
					var  strDate  =  date.getFullYear()  +  "-"  +  ((date.getMonth()  +  1  <  10) 
							?  "0"  +  (date.getMonth()  +  1)  :  (date.getMonth()  +  1))  +  "-"  
							+  date.getDate()  +  "-"  +  ((date.getHours()  <  10) 
							?  "0"  +  date.getHours()  :  date.getHours())  +  ":" 
							+  (date.getMinutes()  <  10  ?  "0"  +  date.getMinutes() 
							:  date.getMinutes())  +  ":"  +  (date.getSeconds()  <  10 
							?  "0"  +  date.getSeconds()  :  date.getSeconds());
					var  result  = 
							"<tr  class='reply_"  +  value.no  +  "'>" 
							+  "<td>"
							+  " <div  class='replyUser'>"
							+  " <span  class='member'>"  +  value.replyWriter  +  "</span>"
							+  " </div>"
							+  " <div  class='replyModify'>"
							+  " <span  class='replyDate'>"  +  strDate  +  "</span>"
							+  " <a  href='#'  class='modifyReply'  data-no='"  +  value.no  + "'>"
							+  " <img  src='resources/images/reply_btn_modify.gif'  alt='댓글 수정하기'/>"
							+  " </a>"
							+  " <a  href='#'  class='deleteReply'  data-no='"  +  value.no  +  "'>"
							+  " <img  src='resources/images/reply_btn_delete.gif'  alt='댓글 삭제하기'/>"
							+  " </a>"
							+  " <a  href='javascript:reportReply('div_"  +  value.no  +  "');'>"
							+  " <img  src='resources/images/reply_btn_notify.gif'  alt='신고하기'/>"
							+  " </a>"
							+  " </div>"
							+  " <div  class='replyContent'  id='div_"  +  value.no  +  "'>"
							+  " <pre><span>"  +  value.replyContent  +  "</span></pre>"
							+  " </div>"
							+  "</td>"
							+  "</tr>";
	
							
					$("#replyTable").append(result);
				});
				
				$("#replyForm").slideUp(300).add("#replyContent").val("");
				
				console.log("write : " + $("#replyFrom").length);
			},
			error : function(xhr, status, error) {
				alert("ajax 실패 : " + status + " - " + xhr.status);
			}
		});
		return false;
	});
	
	//댓글 수정버튼 클릭 시
	$(document).on("click", ".modifyReply", function() {
		
		//수정하기가 클릭된 부모 요소의 다음 형제 요소를 구함(즉 replyContent)
		var $next = $(this).parent().next();
		
		//위 요소의 두번째 자식은 댓글 쓰기 수정 폼이 됨. 그러므로 댓글 수정 폼이 있는 곳은 추가 작업이 필요 없고
		//댓글 폼이 없는 곳에만 폼을 가져와 그 위치에 추가하는 작업만 하면 됨
		if ($($next.children()[1]).attr("id") == undefined) {
			
			console.log(".modifyReply  click  :  visible  -  " 
					+  $("#replyForm").is(":visible")
					+  ",  hidden  -  "  +  $("#replyForm").is(":hidden")
					+  ",  length  -  "  +  $("#replyForm").length);
			
			
			var reply = $next.first().text();
			
			if($("#replyForm").css("display") == 'block') {
				$("#replyForm").slideUp(300);
			}
			setTimeout(function() {
				$("#replyContent").val($.trim(reply));
				$("#replyForm").appendTo($next).slideDown(300);
			}, 300);
			//아이디를 댓글 수정 폼으로 동적으로 변경
			$("#replyForm").find("form").attr({"id":"replyUpdateForm", "data-no":$(this).attr("data-no")});
		}
		//앵커 태그의 기본 기능인 링크로 연결되는것을 취소
		return false;
	});
	
	//댓글 수정 폼이 submit될 때
	$(document).on("submit", "#replyUpdateForm", function() {
		
		if($("#replyContent").val().length <= 5) {
			alert("댓글은 5자 이상 입력해야 합니다");
			return false;
		}
		
		var params = $(this).serialize() + "&no=" + $(this).attr("data-no");
		var updateForm;
		
		$.ajax({
			url : "replyUpdate.ajax",
			type : "post",
			data : "params",
			dataType : "json",
			success : function(resultData, status, xhr) {
				//아래에서 $("#replyForm").empty()가 호출되면 댓글 쓰기 폼도 같이 삭제되므로 백업 해 놔야 함
				$updateForm = $("#replyForm");
				
				console.log("update  -  before  empty()  :  "  +  $updateForm.length);
				
				$("#replyTable").empty();
				
				$.each(resultData, function(index, value) {
					var  date  =  new  Date(value.regDate);
					var  strDate  =  date.getFullYear()  +  "-"  +  ((date.getMonth()  +  1  <  10) 
						?  "0"  +  (date.getMonth()  +  1)  :  (date.getMonth()  +  1))  +  "-"  
						+  date.getDate()  +  "-"  +  ((date.getHours()  <  10) 
						?  "0"  +  date.getHours()  :  date.getHours())  +  ":" 
						+  (date.getMinutes()  <  10  ?  "0"  +  date.getMinutes() 
						:  date.getMinutes())  +  ":"  +  (date.getSeconds()  <  10 
						?  "0"  +  date.getSeconds()  :  date.getSeconds());
					var  result  = 
						"<tr  class='reply_"  +  value.no  +  "'>" 
						+  "<td>"
						+  " <div  class='replyUser'>"
						+  " <span  class='member'>"  +  value.replyWriter  +  "</span>"
						+  " </div>"
						+  " <div  class='replyModify'>"
						+  " <span  class='replyDate'>"  +  strDate  +  "</span>"
						+  " <a  href='#'  class='modifyReply'  data-no='"  +  value.no  + "'>"
						+  " <img  src='resources/images/reply_btn_modify.gif'  alt='댓글 수정하기'/>"
						+  " </a>"
						+  " <a  href='#'  class='deleteReply'  data-no='"  +  value.no  +  "'>"
						+  " <img  src='resources/images/reply_btn_delete.gif'  alt='댓글 삭제하기'/>"
						+  " </a>"
						+  " <a  href='javascript:reportReply('div_"  +  value.no  +  "');'>"
						+  " <img  src='resources/images/reply_btn_notify.gif'  alt='신고하기'/>"
						+  " </a>"
						+  " </div>"
						+  " <div  class='replyContent'  id='div_"  +  value.no  +  "'>"
						+  " <pre><span>"  +  value.replyContent  +  "</span></pre>"
						+  " </div>"
						+  "</td>"
						+  "</tr>";
					
					// 댓글 테이블의 기존 내용을 삭제하고 다시 추가한다.
					$("#replyTable").append(result);				
				});
				console.log("update  after  empty()  :  #replyForm  -  " 
						+  $("#replyForm").length 
						+  ",  $updateFrom  :  "  +  $updateForm.length);
				
				$updateForm.find("form").attr("id", "replyWriteForm").removeAttr("data-no")
				.end().css("display","none").appendTo("article").find("#replyContent").val("");
			},
			error : function(xhr, status, error) {
				alert("ajax 실패 : " + status + " - " + xhr.status);
			}
		});
		return false;
	});
	
	$(document).on("click", ".deleteReply", function() {
		
		var no = $(this).attr("data-no");
		var writer = $(this).parent().prev().find(".member").text();
		var bbsNo = $("#replyForm input[name=bbsNo]").val();
		var result = confirm(writer + "님이 작성한 " + no + "번 댓글을 삭제하시겠습니까?");
		
		var params = "no=" + no + "&bbsNo=" + bbsNo;
		if(result) {
			$.ajax({
				url : "replyDelete.ajax",
				type : "post",
				data : params,
				dataType : "json",
				success : function(resultData, status, xhr){
					
					$("#replyForm").find("form").attr("id", "replyWriteForm").removeAttr("data-no")
					.end().css("display", "none").appendTo("article").find("#replyContent").val();
					
					$("#replyTable").empty();
					
					
					$.each(resultData,  function(index,  value)  {
						// 날짜 데이터를 출력 포맷에 맞게 수정
						var  date  =  new  Date(value.regDate);
						var  strDate  =  date.getFullYear()  +  "-"  +  ((date.getMonth()  +  1  <  10) 
							?  "0"  +  (date.getMonth()  +  1)  :  (date.getMonth()  +  1))  +  "-"  
							+  date.getDate()  +  "-"  +  ((date.getHours()  <  10) 
							?  "0"  +  date.getHours()  :  date.getHours())  +  ":" 
							+  (date.getMinutes()  <  10  ?  "0"  +  date.getMinutes() 
							:  date.getMinutes())  +  ":"  +  (date.getSeconds()  <  10 
							?  "0"  +  date.getSeconds()  :  date.getSeconds());
						var  result  = 
							"<tr  class='reply_"  +  value.no  +  "'>" 
							+  "<td>"
							+  " <div  class='replyUser'>"
							+  " <span  class='member'>"  +  value.replyWriter  + 
							"</span>"
							+  " </div>"
							+  " <div  class='replyModify'>"
							+  " <span  class='replyDate'>"  +  strDate  +  "</span>"
							+  " <a  href='#'  class='modifyReply'  data-no='"  +  value.no 
							+  "'>"
							+  " <img  src='resources/images/reply_btn_modify.gif' alt='댓글 수정하기'/>"
							+  " </a>"
							+  " <a  href='#'  class='deleteReply'  data-no='"  +  value.no  + 
							"'>"
							+  " <img  src='resources/images/reply_btn_delete.gif'alt='댓글 삭제하기'/>"
							+  " </a>"
							+  " <a  href='javascript:reportReply('div_"  +  value.no  + 
							"');'>"
							+  " <img  src='resources/images/reply_btn_notify.gif' alt='신고하기'/>"
							+  " </a>"
							+  " </div>"
							+  " <div  class='replyContent'  id='div_"  +  value.no  +  "'>"
							+  " <pre><span>"  +  value.replyContent  +  "</span></pre>"
							+  " </div>"
							+  "</td>"
							+  "</tr>";
						
						// 댓글 테이블의 기존 내용을 삭제하고 다시 추가한다.
						$("#replyTable").append(result); 
					});
				},
				error : function(xhr, status, error){
					alert("ajax 실패 : " + status + " - " + xhr.status);
				}
			});
		}
		//앵커태그에 의해 페이지 이동되는것을 취소
		return false;
	});
	
	
	//신고하기 버튼 임시로 연결한 함수
	function reportReply(elemId) {
		var result = confirm("이 댓글을 신고하시겠습니까?");
		if(result == true) {
			alert("report - " + result);
		}
	}
})