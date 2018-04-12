<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-qa?page=0">お問合わせー覧</a></li>
</ul>

<div class="box box-default" style="margin-top: 20px;">
    <form:form id="qaSearchForm" action="search-qa" role="form" modelAttribute="qaSearch" method="GET">
        <div class="box-body">
            <div class="row">
                <label class="col-md-2" style="margin-top: 10px;margin-left: 30px">
                    ステータス
                </label>
                <div class="col-md-6 form-group user-checkbox">
                    <div class="right20 checkbox inline-block" id="isNoResponseDiv">
                        <label>
                            <form:checkbox path="isNoResponse" id="isNoResponse" onclick="hideBtnHaveFeedback()" value="true"/>
                            未対応
                        </label>
                    </div>
                    <div class="right20 checkbox inline-block" id="isInProgressDiv">
                        <label>
                            <form:checkbox path="isInProgress" onclick="showBtnHaveFeedback()" id="isInProgress" value="true"/>
                            対応中
                        </label>
                    </div>
                    <div class="right20 checkbox inline-block" id="isResolvedDiv">
                        <label>
                            <form:checkbox path="isResolved" onclick="hideBtnHaveFeedback()" id="isResolved" value="true"/>
                            対応済み（完了）
                        </label>
                    </div>
                </div>
            </div>

            <div class="row" style="margin-bottom: 30px">
                <label class="col-md-2" style="margin-top: 10px; margin-left: 30px">
                    初回問い合わせ日
                </label>
                <div class="col-md-3">
                    <div class="inline-block">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="createdAt" class="form-control pull-right datepicker" id="createdAt" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 20px;">
                                <span id = "msgStartDate"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 right20 checkbox inline-block" id="haveFeedbackDiv" style="margin-top: 5px !important;">
                    <label>
                        <form:checkbox path="haveFeedback" id="haveFeedback" value="true"/>
                        返信ありのみ表示
                    </label>
                </div>
                <div class="col-md-3">
                    <div class="form-group">
                        <input type="button" id="qaSearch" onclick="executeSearchQa()" class="btn btn-primary" value="検索"/>
                    </div>
                </div>
            </div>
        </div>
    </form:form>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
            <ul class="pagination" style="margin: 0px !important;">
                <div class="col-md-6">
                    <h5><b style="margin-right: 40px">${startElement} - ${curElements} の ${totalElements}</b></h5>
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
</div>

<div class="result-posts" style="margin: 0px !important;">
    <div class="form-group">
        <div class="row">
            <div class="col-md-12">
                <div class="row">
                    <div class="col-sm-12">
                        <table id="example2" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example2_info">
                            <thead >
                            <tr role="row">
                                <th class="sorting_asc text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending"></th>
                                <th class="sorting_asc text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">問合せ</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">問合せ者</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">タイトル</th>
                                <th class="sorting text-center minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">更新日時</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">ステータス</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending"></th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="qa" items="${listQa}" varStatus="status">
                                <tr role="row">
                                    <td class="sorting_1"> <c:if test="${qa.haveFeedback == true}">返信あり</c:if></td>
                                    <td class="sorting_1">${qa.qaId}</td>
                                    <td class="sorting_1">${qa.shmUser.nickName}</td>
                                    <td class="sorting_1">
                                        <c:choose>
                                            <c:when test="${qa.qaType == 'ACCOUNT_PROBLEM'}">
                                                アカウント 登録 · ログイン について
                                            </c:when>
                                            <c:when test="${qa.qaType == 'POST_PROBLEM'}">
                                                投稿方法 · 投稿 ルール について
                                            </c:when>
                                            <c:when test="${qa.qaType == 'USAGE_PROBLEM'}">
                                                トラブル · 詐欺 について
                                            </c:when>
                                            <c:when test="${qa.qaType == 'HELP'}">
                                                お 問い合わ · やりとり について
                                            </c:when>
                                            <c:otherwise>
                                                退会について
                                            </c:otherwise>
                                        </c:choose></td>
                                    </td>
                                    <td class="sorting_1">
                                        <javatime:format value="${qa.lastUpdateAt}" pattern="Y年MM月dd日 HH:mm" var="parsedEmpDate"/>
                                        <c:out value="${parsedEmpDate}"/></td>
                                    <td class="sorting_1"><c:choose>
                                        <c:when test="${qa.qaStatus == 'NO_RESPONSE'}">
                                            未対応
                                        </c:when>
                                        <c:when test="${qa.qaStatus == 'INPROGRESS'}">
                                            対応中
                                        </c:when>
                                        <c:otherwise>
                                            対応完了
                                        </c:otherwise>
                                    </c:choose></td>
                                    <td class="sorting_1">
                                        <a href="<%= request.getContextPath()%>/backend/detail-qa?qaId=${qa.qaId}">
                                            <button class="btn btn-primary">トーク へ</button>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                            <tfoot></tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        if($('#isInProgress').prop('checked') || (!$('#isInProgress').prop('checked') && !$('#isNoResponse').prop('checked') && !$('#isResolved').prop('checked'))) {
            $('#haveFeedbackDiv').show().css("visibility", "visible");
        } else {
           // $('#haveFeedbackDiv').hide().css("visibility", "hidden");
        }
    });
    function executeSearchQa(){
        var createdAt = $("#createdAt").val();
        createdAt = isEmpty(createdAt) ? '' : createdAt;
        if (!isEmpty(createdAt) && !validateDateFormat(createdAt)) {
            $('#msgStartDate').html("入力フォーマットが違います")
        }else {
            $('#qaSearchForm').submit();
        }
    }


    function hideBtnHaveFeedback() {
        if (!$('#isInProgress').prop('checked')) {
          //  $('#haveFeedbackDiv').hide().css("visibility", "hidden");
            $('#haveFeedback').prop('checked', false);
        }
        if($('#isInProgress').prop('checked') || (!$('#isInProgress').prop('checked') && !$('#isNoResponse').prop('checked') && !$('#isResolved').prop('checked'))) {
            $('#haveFeedbackDiv').show().css("visibility", "visible");
        }
    }

    function showBtnHaveFeedback() {
        if(!$('#isInProgress').prop('checked') && ($('#isNoResponse').prop('checked') || $('#isResolved').prop('checked'))) {
         //   $('#haveFeedbackDiv').hide().css("visibility", "hidden");
            $('#haveFeedback').prop('checked', false);
        }

        if($('#isInProgress').prop('checked') || (!$('#isInProgress').prop('checked') && !$('#isNoResponse').prop('checked') && !$('#isResolved').prop('checked'))) {
            $('#haveFeedbackDiv').show().css("visibility", "visible");
        }
    }
</script>