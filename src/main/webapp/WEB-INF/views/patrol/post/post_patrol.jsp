<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 3/29/2017
  Time: 5:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="<c:url value="/resources/css/styles.css" />" rel="stylesheet">
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/post-patrol?page=0">サイト パトロール ( 投稿 )</a></li>
</ul>

<div class="home-page">
    <div class="search-posts" style="margin-top: 20px">
        <form:form class="" role="form" modelAttribute="postPatrolRequest" action="post-patrol" id="postPatrolForm" method="GET">
            <div class="row">
                <div class="form-group user-checkbox">
                    <div class="col-md-2">
                        <label style="margin-top: 15px; margin-left: 30px;">ステータス</label>
                    </div>
                    <div class="checkbox-item checkbox inline-block">
                        <label style="padding-left: 0px">
                            <form:radiobutton checked="true" id="unCenCbx" onclick="isCheckedCtrlStt(0)" path="postStatus" value="0"/>
                            未対応
                        </label>
                    </div>
                    <div class="checkbox-item checkbox inline-block">
                        <label>
                            <form:radiobutton id="cecCbx" onclick="isCheckedCtrlStt(1)" path="postStatus" value="1"/>
                            対応中
                        </label>
                    </div>
                    <div class="checkbox-item checkbox inline-block">
                        <label>
                            <form:radiobutton id="doneCbx" onclick="isCheckedCtrlStt(2)" path="postStatus" value="2"/>
                            対応完了
                        </label>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px; line-height: 34px;" id="cptDateTitle">最終更新日時</label>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="fromUpdateAt" class="form-control pull-right datepicker" id="fuaPicker" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="error">
                                <span style="color: red;" id="errFuaPicker">${updatedAtError}</span>
                                <form:errors path="fromUpdateAt" class="control-label" style="color:red;" />
                            </div>
                        </div>
                        <span class="add-tilde"></span>
                    </div>
                    <div class="col-md-3">
                        <div class="input-group date">
                            <div class="input-group-addon">
                                <i class="fa fa-calendar"></i>
                            </div>
                            <form:input type="text" path="toUpdateAt" class="form-control pull-right datepicker" id="tuaPicker" placeholder="YYYY/MM/DD"/>
                        </div>
                        <div class="row form-group" style="color: red; margin-left: 10px;">
                            <span id = "errTuaPicker"></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row" style="margin-top: 15px">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px; line-height: 34px;">オペレーター名</label>
                    </div>
                    <div class="col-md-3">
                        <form:input path="operatorNames" type="text" class="form-control"/>
                        <div class="error">
                            <span style="color: red;">${operatorIdErr}</span>
                            <form:errors path="operatorNames" class="control-label" style="color:red;" />
                        </div>
                    </div>
                </div>
            </div>

            <div class="row" id="completedDate" style="margin-top: 15px; display: none">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px;" id="compDateLabelId">対応中変更日時</label>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="fromCompleteAt" class="form-control pull-right datepicker" id="fcaDate" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="error">
                                <span style="color: red;" id="errFcaDate">${completedAtError}</span>
                                <form:errors path="fromCompleteAt" class="control-label" style="color:red;" />
                            </div>
                        </div>
                        <span class="add-tilde"></span>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="toCompleteAt" class="form-control pull-right datepicker" id="tcaDate" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 10px;">
                                <span id = "errTcaDate"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-2" style="line-height: 34px; margin-top: 3px">
                        <label style="margin-left: 30px;">絞り込み</label>
                    </div>
                    <div class="col-md-6">
                        <div class="checkbox-item checkbox inline-block" id="pendingSttCbx">
                            <label style="padding-left: 0px">
                                <form:checkbox class="cboxOnlyOne"  path="pendingStt" value="2"/>
                                保留中
                            </label>
                        </div>

                        <div class="checkbox-item checkbox inline-block" id="repairedSttCbx">
                            <label>
                                <form:checkbox class="cboxOnlyOne" path="repairedStt" id="repairedStt" value="3"/>
                                修正完了
                            </label>
                        </div>

                        <div class="checkbox-item checkbox inline-block" id="doneSttCbx">
                            <label>
                                <form:checkbox class="cboxOnlyOne" path="reportStt" value="4"/>
                                違反通報有り
                            </label>
                            <form:input type="number" hidden="true" path="page" value="${curPage}"></form:input>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <button type="button" onclick="searchPostPatrol()" class="btn btn-primary" >検索</button>
                    </div>
                </div>
            </div>
        </form:form>
    </div>

    <c:if test="${not empty posts}">
        <div class="pull-right" style="margin: 20px">
            <div class="inline-block" style="line-height: 34px; position: absolute; right: 115px;">
                <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                    <input type="button" style="margin-right: 10px" id="exportPostPatrolCsvBtn" onclick="exportPostPatrolCsv()" class="btn btn-primary" value="CSV出力"></input>
                </security:authorize>
                <b>${startElement} - ${curElements} の ${totalElms}</b>
            </div>
            <ul class="pagination" style="list-style: none; margin: 0px !important;">
                <div class="inline-block pagination-user-patrol">
                    <c:choose>
                        <c:when test="${curPage == 0}">
                            <li class="paginate_button disabled"><a href="#">&lt;</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="#" class="search-paging" data-page="${curPage - 1}">&lt;</a></li>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="inline-block pagination-user-patrol">
                    <c:choose>
                        <c:when test="${curPage == totalPage}">
                            <li class="paginate_button disabled"><a href="#">&gt;</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="#" class="search-paging" data-page="${curPage + 1}">&gt;</a></li>
                        </c:otherwise>
                    </c:choose>
                </div>
            </ul>
        </div>
    </c:if>

    <div class="result-posts" style="margin-top: 70px !important;">
        <div class="row">
            <div class="form-group col-md-12">
                <div class="row">
                    <div class="col-sm-12">
                        <c:if test="${not empty posts}">
                            <table id="example2" class="table table-bordered table-hover dataTable table-striped" role="grid" aria-describedby="example2_info">
                                <thead>
                                <tr role="row">
                                    <th class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending">投稿画像</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿商品名</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿者ニックネーム</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >ステータス</th>
                                    <th class="sorting minwidth=150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >最終更新日時</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" style="visibility: hidden" id="repairCompletedCol">修正完了</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" id="sttCol">保留 ステータス</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >違反通報者</th>
                                    <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >オペレーター</th>
                                </tr>
                                </thead>
                                <tbody>

                                <c:forEach var="post" items="${posts}">
                                    <tr role="row" class="odd">
                                        <td>
                                                <%--${post.postThumbnailImagePaths}--%>
                                            <c:choose>
                                                <c:when test="${empty post.postThumbnailImagePaths}">
                                                    <a href="#">
                                                        <img src="${pageContext.request.contextPath}/resources/images/noimage.png"
                                                             width="40" height="40">
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:forEach var="file" items="${post.postThumbnailImagePaths}"
                                                               varStatus="loop">

                                                        <c:choose>
                                                            <c:when test="${loop.index == '0'}">
                                                                <a href="#" class="showImageClick" data-src-link="${file}"
                                                                   data-link-index="${loop.index}"
                                                                   data-list-link="${post.postOriginalImagePaths}"
                                                                   data-text-1="${fn:escapeXml(post.postName)}"
                                                                   data-text-2="${post.userNickName}">
                                                                    <img src="${file}" width="40" height="40"
                                                                         onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="showImageClick" data-src-link="${file}"
                                                                   data-link-index="${loop.index}"
                                                                   data-text-1="${fn:escapeXml(post.postName)}"
                                                                   data-text-2="${post.userNickName}"
                                                                   data-list-link="${post.postOriginalImagePaths}">${loop.index + 1}</a>
                                                            </c:otherwise>
                                                        </c:choose>

                                                    </c:forEach>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="table-td-center">
                                            <a href="<%= request.getContextPath()%>/backend/post-patrol-detail?postId=${post.postId}">${fn:escapeXml(post.postName)}</a>
                                        </td>
                                        <td class="table-td-center">
                                                ${post.userNickName}
                                        </td>
                                        <td class="table-td-center">
                                            <c:choose>
                                                <c:when test="${post.ptrlStatus == 'UNCENSORED'}">
                                                    未対応
                                                </c:when>
                                                <c:when test="${post.ptrlStatus == 'CENSORING'}">
                                                    対応中
                                                </c:when>
                                                <c:otherwise>
                                                    対応完了
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="table-td-center minwidth150">
                                            <javatime:format value="${post.userUpdatedAt}" pattern="Y年MM月dd日 HH:mm" var="timePatrol"/>
                                            <c:out value="${timePatrol}"/>
                                        </td>
                                        <td class="table-td-center repairCompVal" style="visibility: hidden">
                                            ${post.suspendInPast}
                                        </td>
                                        <td class="table-td-center valSttCol">
                                            ${post.pendingInPast}
                                        </td>
                                        <td class="table-td-center">
                                                ${post.lastReportUser}
                                        </td>
                                        <td class="table-td-center">
                                            <c:choose>
                                                <c:when test="${post.adminName == null}">
                                                    &nbsp;
                                                </c:when>
                                                <c:otherwise>
                                                    ${post.adminName}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                                <tfoot>
                                    <%-- <tr><th rowspan="1" colspan="1">Rendering engine</th><th rowspan="1" colspan="1">Browser</th><th rowspan="1" colspan="1">Platform(s)</th><th rowspan="1" colspan="1">Engine version</th><th rowspan="1" colspan="1">CSS grade</th></tr>--%>
                                </tfoot>
                            </table>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade modal-sm bg-primary-modal " id="postPatrolWarning" role="document" style="margin: 30px auto; background: none;">
        <div class="modal-content">
            <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 警告する</h4>
            </div>
            <div class="modal-body">このレコードは他の人によって操作されています。別のレコードを選択してください</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {

        if (!$("#unCenCbx").is(':checked')) {
            $('.repairCompVal').show().css("visibility", "visible");
            $('#repairCompletedCol').show().css("visibility", "visible");
            $('#completedDate').show().css("visibility", "visible");
        } else {
            $('#repairCompletedCol').hide().css("visibility", "hidden");
            $('.repairCompVal').hide().css("visibility", "hidden");
        }

        if ($('#doneCbx').is(':checked')) {
            $('#compDateLabelId').html("対応完了日時");
        } else {
            $('#compDateLabelId').html("対応中変更日時");
        }

        if ($('#cecCbx').is(':checked') || $('#doneCbx').is(':checked')) {
            $('#repairedSttCbx').show().css("visibility", "visible");
            moveRepairedDiv(1);
        } else {
            $('#repairedSttCbx').hide().css("visibility", "hidden");
            $('#repairedStt').prop('checked', false);
            moveRepairedDiv(0);
        }

        var url = document.URL;
        if (url.includes("patrol_warning=")) {
            $("#postPatrolWarning").modal("show");
        }
    });

    function exportPostPatrolCsv() {
        if (validatePostPatrolSearchForm()) {
            $('#postPatrolForm').attr('action', 'posts-patrol/csv');
            $('#postPatrolForm').submit();
            $('#postPatrolForm').attr('action','post-patrol');
        }
    }

    function isCheckedCtrlStt(ctrlStt) {
        if (ctrlStt == 0) {
            $('#completedDate').hide().css("visibility", "hidden");
            $("#fcaDate").datepicker('setDate', null);
            $('#tcaDate').datepicker('setDate', null);
            $('#repairedSttCbx').hide().css("visibility", "hidden");
            $('#repairedStt').prop('checked', false);
            moveRepairedDiv(0);
        } else {
            $('#completedDate').show().css("visibility", "visible");
            $('#repairedSttCbx').show().css("visibility", "visible");
            moveRepairedDiv(1);
        }

        if (ctrlStt == 2) {
            $('#compDateLabelId').html("対応完了日時");
        } else {
            $('#compDateLabelId').html("対応中変更日時");
        }
    }

    function moveRepairedDiv(ctrlStt) {
        if (ctrlStt == 0) {
            $('#doneSttCbx').css("marginLeft", "-75px");
        } else {
            $('#doneSttCbx').css("marginLeft", "20px");
        }
    }

    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }

    function searchPostPatrol() {
        if (validatePostPatrolSearchForm()) {
            $("#postPatrolForm").submit();
        }
    }
    
    function validatePostPatrolSearchForm() {
        var flag = true;
        if(!isEmpty($('#fuaPicker').val()) && !validateDateFormat($('#fuaPicker').val())) {
            $('#errFuaPicker').html("<spring:message code="SH_E100133"/>");
            flag = false;
        } else {
            $('#errFuaPicker').html("");
        }
        if(!isEmpty($("#tuaPicker").val()) && !validateDateFormat($('#tuaPicker').val())) {
            $('#errTuaPicker').html("<spring:message code="SH_E100133"/>");
            flag = false;
        } else {
            $('#errTuaPicker').html("");
        }
        if(!isEmpty($("#fcaDate").val()) && !validateDateFormat($('#fcaDate').val())) {
            $('#errFcaDate').html("<spring:message code="SH_E100133"/>");
            flag = false;
        } else {
            $('#errFcaDate').html("");
        }
        if(!isEmpty($("#tcaDate").val()) && !validateDateFormat($('#tcaDate').val())) {
            $('#errTcaDate').html("<spring:message code="SH_E100133"/>");
            flag = false;
        } else {
            $('#errTcaDate').html("");
        }
        return flag;
    }
</script>
