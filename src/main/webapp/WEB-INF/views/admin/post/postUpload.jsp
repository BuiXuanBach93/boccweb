<%--
  Created by IntelliJ IDEA.
  User: Namlong
  Date: 6/23/2017
  Time: 10:14 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/post-sample-upload">サンプルの投稿をアップロードしてください。</a></li>
</ul>

<script>var ctx = "${pageContext.request.contextPath}";</script>
<section class="content">
    <div class="home-page">
        <form:form id="uploadPostForm" method="post" modelAttribute="postUploadRequest"
                   action="${pageContext.request.contextPath}/backend/post-sample-upload" enctype="multipart/form-data">
            <div class=" row search-posts">
                <div class="row center-block pull-left" style="margin-right: 20px;">
                    <label for="chosseFileExcel">投稿のサンプルエクセルファイルを選択してください。</label>
                    <input id="chosseFileExcel" type="file" name="excelFile" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"/>
                </div>
                <div class="row center-block">
                    <label for="chosseFileZip">投稿の画像のZIPファイルを選択してください。</label>
                    <input id="chosseFileZip" type="file" name="imgZipFile" accept=".zip"/>
                </div>
            </div>
            <div class="row center-block">
                <input type="button" id="submitButtonID" value="アップロードします。" class="btn btn-primary">
            </div>
            <br />
            <div class="row center-block">
                <label>${message}</label>

                <c:if test="${not empty errorMsg}">
                <label style="color: red">
                    ${errorMsg}
                </label>
                </c:if>
            </div>
        </form:form>
    </div>
</section>

<script>
    $('#submitButtonID').click(function(){
        if( document.getElementById("chosseFileExcel").files.length == 0 || document.getElementById("chosseFileZip").files.length == 0){
            alert('Files cannot be empty!');
        }else{
            $('#uploadPostForm').submit();
        }
    });
</script>