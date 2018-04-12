<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 3/21/2017
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<link href="<c:url value="/resources/css/styles.css" />" rel="stylesheet">
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/posts?page=0">投稿一覧・検索</a></li>
</ul>

<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <form:form class="" role="form" id="condition_post_form" modelAttribute="postSearch" action="posts?page="
                   method="GET">
            <div class="row form-group" style="color: red; margin-left: 20px;">
                <span id="msg"></span>
            </div>
            <div class="row">
                <div class="form-group">
                    <div class="col-md-3">
                        <label>投稿 ID</label>
                        <form:input type="text" path="postId" class="form-control" id="postId"/>
                        <div class="error">
                            <span id="postIdErr" style="color: red;">${postId}</span>
                            <form:errors path="postId" class="control-label" style="color:red;"/>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <label>投稿者名</label>
                        <form:input type="text" path="userName" class="form-control"/>
                        <div class="error">
                            <span style="color: red;">${userName}</span>
                            <form:errors path="userName" class="control-label" style="color:red;"/>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <label>投稿ジャンル</label>
                        <div class="">
                            <div class="checkbox inline-block">
                                <label>
                                    <form:checkbox id="buyCbx" path="sellType" value="0"/>
                                    出品投稿
                                </label>
                            </div>

                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox id="saleCbx" path="buyType" value="1"/>
                                    リクエスト投稿
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4"></div>
                </div>
            </div>

            <div class="row">
                    <%--<div class="form-group">--%>

                <div class="col-md-3">
                    <label>カテゴリ</label>
                    <div class="form-group">
                        <form:select class="form-control get-child-categories select-button" path="parentCat"
                                     id="parentCat">
                            <form:option value="">すべて</form:option>
                            <c:forEach items="${parentCats}" var="item">
                                <form:option value="${item.key}">${item.value}</form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>
                <div class="col-md-3">
                    <label>&nbsp;</label>
                    <div class="form-group">
                        <form:select class="form-control select-button" name="childCat" id="childCat" path="childCat">
                            <form:option value="">すべて</form:option>
                            <c:forEach items="${childCats}" var="item">
                                <form:option value="${item.key}">${item.value}</form:option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>
                <div class="col-md-6"></div>
                    <%--</div>--%>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="form-group user-checkbox">
                        <label>ステータス</label>
                        <div>
                            <div class="checkbox inline-block">
                                <label>
                                    <form:checkbox path="pubSellStt" value="0"/>
                                    公開中
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="inCnvStt" value="1"/>
                                    取引中
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="tendSellStt" value="2"/>
                                    取引意思確認中
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="soldStt" value="3"/>
                                    取引完了
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="reservedStt" value="2"/>
                                    強制停止・違反
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="delStt" value="4"/>
                                    削除済
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4"></div>
            </div>

            <div class="row">
                <div class="col-md-2">
                    <label>
                        期間
                    </label>
                </div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-3">
                        <div class="form-group">
                            <form:select class="form-control select-button" path="dateType">
                                <form:option value="1">出品日が</form:option>
                                <form:option value="2">最終更新日が</form:option>
                            </form:select>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="inline-block">
                            <div class="form-group">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <form:input type="text" path="fromDate" class="form-control pull-right datepicker"
                                                id="startDate" placeholder="YYYY/MM/DD"/>
                                </div>
                                <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                    <span id="msgStartDate"></span>
                                </div>
                            </div>
                        </div>
                        <span class="add-tilde"></span>
                    </div>

                    <div class="col-md-2">
                        <div class="inline-block">
                            <div class="form-group">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <form:input type="text" path="toDate" class="form-control pull-right datepicker"
                                                id="endDate" placeholder="YYYY/MM/DD"/>
                                    <form:input type="number" hidden="true" path="page" value="${curPage}"></form:input>
                                </div>
                                <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                    <span id="msgEndDate"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <button type="button" onclick="searchPostForm()" class="btn btn-primary">検索</button>
                        </div>
                    </div>
                </div>

            </div>
        </form:form>
    </div>

    <div class="row">
        <c:choose>
            <c:when test="${not empty posts}">
                <div class="col-md-12">
                    <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
                        <ul class="pagination">
                            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                                <div class="col-md-2 pagination-button" style="margin-right: 20px">
                                    <input type="button" id="exportPostCsvBtn" onclick="exportPostCSV()" class="btn btn-primary" value="CSV出力"></input>
                                </div>
                            </security:authorize>
                            <div class="col-md-6">
                                <h5><b>${startElement} - ${curElements} の ${totalElements}</b></h5>
                            </div>
                            <c:choose>
                                <c:when test="${curPage == 0}">
                                    <li class="paginate_button disabled"><a href="#">&lt;</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="#" class="search-paging" data-page="${curPage - 1}">&lt;</a></li>
                                </c:otherwise>
                            </c:choose>

                            <c:choose>
                                <c:when test="${curPage == totalPage}">
                                    <li class="paginate_button disabled"><a href="#">&gt;</a></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="#" class="search-paging" data-page="${curPage + 1}">&gt;</a></li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="col-md-12">
                    <h5><b style="color:red;">
                        ${dataError}
                    </b></h5>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="result-posts">
    <div class="row">
        <div class="form-group col-md-12">
            <div class="row">
                <div class="col-sm-12">
                    <c:if test="${not empty posts}">
                        <table id="example2" class="table table-bordered table-hover dataTable table-striped"
                               role="grid" aria-describedby="example2_info" style="table-layout: fixed; width: 100%">
                            <thead>
                            <tr role="row">
                                <th style="text-align: center" class="col-post sorting sorting minwidth150" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending">投稿画像
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">投稿 ID<br>投稿名
                                </th>
                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2"
                                    rowspan="1" colspan="1">大カテゴリ<br>中カテゴリ
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">依頼
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">投稿者名
                                </th>
                                <th style="text-align: center" class="col-post sorting minwidth150" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">投稿登録日<br>最新更新日
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">価格
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">ステータス
                                </th>
                                <th style="text-align: center" class="col-post sorting" tabindex="0"
                                    aria-controls="example2" rowspan="1" colspan="1">（通報）
                                </th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="post" items="${posts}">
                                <tr role="row" class="odd" style="word-wrap: break-word">
                                    <td>
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
                                                               data-text-1="${post.postName}"
                                                               data-text-2="${post.shmUser.nickName}">
                                                                <img src="${file}" width="40" height="40"
                                                                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <a class="showImageClick" data-src-link="${file}"
                                                               data-link-index="${loop.index}"
                                                               data-text-1="${post.postName}"
                                                               data-text-2="${post.shmUser.nickName}"
                                                               data-list-link="${post.postOriginalImagePaths}">${loop.index + 1}</a>
                                                        </c:otherwise>
                                                    </c:choose>

                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="sorting_1 table-td-center">
                                        <a href="<%= request.getContextPath()%>/backend/post-detail?id=${post.postId}">${post.postId}</a>
                                        <br>
                                        <a href="<%= request.getContextPath()%>/backend/post-detail?id=${post.postId}">${fn:escapeXml(post.postName)}</a>
                                    </td>
                                    <td class="table-td-center">
                                            ${post.postCategoryParent.categoryName}
                                        <br>
                                            ${post.postCategory.categoryName}
                                    </td>
                                    <td class="table-td-center">
                                        <c:choose>
                                            <c:when test="${post.postType == 'SELL'}">
                                                出品
                                            </c:when>
                                            <c:otherwise>
                                                リクエスト
                                            </c:otherwise>
                                        </c:choose>
                                    <td class="table-td-center">
                                        <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${post.shmUser.id}">${post.shmUser.firstName} ${post.shmUser.lastName}
                                    </td>
                                    <td class="table-td-center minwidth150">
                                        <javatime:format value="${post.createdAt}" pattern="Y年MM月dd日" var="createdAt"/>
                                        <c:out value="${createdAt}"/>
                                        <br>
                                        <javatime:format value="${post.userUpdateAt == null ? post.createdAt : post.userUpdateAt}" pattern="Y年MM月dd日" var="updateddAt"/>
                                        <c:out value="${updateddAt}"/>
                                    </td>
                                    <td class="table-td-center"><fmt:formatNumber pattern="#,##0"
                                                                                  value="${post.postPrice}"/>円
                                    </td>
                                    <td class="table-td-center">
                                        <a href="<%= request.getContextPath()%>/backend/list-talk?id=${post.postId}">
                                            <c:choose>
                                                <c:when test="${post.postSellStatus == 'PUBLIC' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                    公開中
                                                </c:when>
                                                <c:when test="${post.postSellStatus == 'IN_CONVERSATION' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                    取引中
                                                </c:when>
                                                <c:when test="${post.postSellStatus == 'TEND_TO_SELL' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                    取引意思確認中
                                                </c:when>
                                                <c:when test="${post.postSellStatus == 'SOLD' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                    取引完了
                                                </c:when>
                                                <c:when test="${post.postCtrlStatus == 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                    強制停止・違反
                                                </c:when>
                                                <c:when test="${post.postSellStatus == 'DELETED'}">
                                                    削除済
                                                </c:when>
                                            </c:choose>
                                        </a>
                                    </td>
                                    <td class="table-td-center">
                                        【${post.postReportTimes}】
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
<script>
    var parentVal = $("#parentCat").val();
    if (isEmpty(parentVal)) {
        $('#childCat').html("");
        $('#childCat').prop('disabled', true);
    }else{
        $('#childCat').prop('disabled', false);
    }

    function validateSearch() {
        var flag = true;
        var onlyDigitsAndCommaRegex = /^[0-9,]*$/;

        if (isEmpty($('#postId').val())) {
            $('#postIdErr').html("");
        }
        if (!isEmpty($('#postId').val()) && !onlyDigitsAndCommaRegex.test($('#postId').val().trim())) {
            $('#postIdErr').html("<spring:message code="SH_E100046"/>");
            flag = false;
        }

        var startDate = $('#startDate').val();
        if (isEmpty(startDate)) {
            $('#msgStartDate').html("");
        }
        if (!isEmpty(startDate) && !validateDateFormat(startDate)) {
            $('#msgStartDate').html("入力フォーマットが違います。")
            flag = false;
            console.log("startDate: " + !validateDateFormat(startDate));
        }

        var endDate = $('#endDate').val();
        if (isEmpty(endDate)) {
            $('#msgEndDate').html("");
        }
        if (!isEmpty(endDate) && !validateDateFormat(endDate)) {
            $('#msgEndDate').html("入力フォーマットが違います。")
            flag = false;
            console.log("endDate: " + !validateDateFormat(endDate));
        }

        if (!isEmpty(startDate) && !isEmpty(endDate) && validateDateFormat(startDate) && validateDateFormat(endDate)) {
            var startTime = new Date(startDate);
            var endTime = new Date(endDate);
            if (startTime.getTime() > endTime.getTime()) {
                $('#msgStartDate').html("<spring:message code="SH_E100048"/>")
                flag = false;
            }
        }

        return flag;
    }

    function exportPostCSV() {
        if (validateSearch()) {
            $('#condition_post_form').attr('action', 'posts/csv');
            $('#condition_post_form').submit();
            $('#condition_post_form').attr('action', 'posts?page=');
        }
    };

    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }

    function searchPostForm() {
        if (validateSearch()) {
            $('#condition_post_form').submit();
        }
    }
</script>
