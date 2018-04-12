<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url var="firstUrl" value="${baseUrlPagination}&pageNumber=1" />
<c:url var="lastUrl" value="${baseUrlPagination}&pageNumber=${deploymentLog.totalPages}" />
<c:url var="prevUrl" value="${baseUrlPagination}&pageNumber=${currentIndex - 1}" />
<c:url var="nextUrl" value="${baseUrlPagination}&pageNumber=${currentIndex + 1}" />

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュボード
    </a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-category-setting?pageNumber=0">カテゴリ管理・追加</a></li>
</ul>
<script>
    var ctx = "${pageContext.request.contextPath}";
</script>

<div class="category-setting" style="margin-top: 20px;">
    <div class="box box-default">
        <div class="box-body" style="margin-top: 20px;">
            <div class="form-group" >
                <div class="row">
                    <div class="col-md-1">
                        <a href="<%= request.getContextPath()%>/backend/create-category-setting" role="button" class="btn btn-default"
                           style="background-color: #f98017; color: #FFFFFF;" >新規登録</a>
                    </div>
                    <div class="col-md-1">
                        <a data-toggle="modal"  data-target=".bg-danger-modal-reset" role="button" class="btn btn-default"
                           style="background-color: #f98017; color: #FFFFFF;" >デフォルト削除</a>
                    </div>
                    <div class="col-md-2"></div>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${totalElements > 0}">
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
                                <li><a href="#" class="search-paging2" data-page="${curPage - 1}">&lt;</a></li>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${curPage+1 == totalPage}">
                                <li class="paginate_button disabled"><a href="#">&gt;</a></li>
                            </c:when>
                            <c:otherwise>
                                <li><a href="#" class="search-paging2" data-page="${curPage + 1}">&gt;</a></li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </div>
    </c:if>

    <div class="box box-default">
        <div class="box-body">
            <div class="form-group">
                <div class="row text-center">
                    <c:if test="${sizeResult != 0}">
                        <div class="col-md-12">
                            <div class="row">
                                <div class="col-sm-12">
                                    <table id="example1" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example1_info">
                                        <thead>
                                        <tr role="row">
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">カテゴリID</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">カテゴリ名称</th>
                                            <th class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">デフォルト</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">編集</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">停止</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        <c:forEach var="list" items="${listCategorySettings}"  varStatus="loop" >
                                            <tr role="row">
                                                <td class="sorting_1"><c:out value="${list.categorySettingId}"/></td>
                                                <td class="sorting_1">
                                                    <a href="<%= request.getContextPath()%>/backend/edit-category-setting?categorySettingId=${list.categorySettingId}">
                                                            ${fn:escapeXml(list.categoryName)}
                                                    </a>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${list.isDefault == true}">
                                                        <td>
                                                            <a href="#" class="btn-modal default-category-setting" data-toggle="modal"  data-target=".bg-danger-modal-default" data-id="${list.categorySettingId}" data-default="${list.isDefault}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span style="color: red">デフォルト</span>
                                                            </a>
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td>
                                                            <a href="#" class="btn-modal default-category-setting" data-toggle="modal"  data-target=".bg-danger-modal-default" data-id="${list.categorySettingId}" data-default="${list.isDefault}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span>未デフォルト</span>
                                                            </a>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                                <td class="minwidth150">
                                                    <a href="<%= request.getContextPath()%>/backend/edit-category-setting?categorySettingId=${list.categorySettingId}">
                                                        <span class="glyphicon glyphicon-pencil" style="margin-right: 15px"></span>
                                                    </a>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${list.categoryStatus == 'ACTIVE'}">
                                                        <td class="minwidth150">
                                                            <a href="#" class="btn-modal suspend-category-setting" data-toggle="modal"  data-target=".bg-danger-modal-suspend" data-id="${list.categorySettingId}" data-status="${list.categoryStatus}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span style="color: red">表示中</span>
                                                            </a>
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td class="minwidth150">
                                                            <a href="#" class="btn-modal suspend-category-setting" data-toggle="modal"  data-target=".bg-danger-modal-suspend" data-id="${list.categorySettingId}" data-status="${list.categoryStatus}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span>停止中</span>
                                                            </a>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                        <tfoot></tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>

                    </c:if>

                    <span style="color: red; padding-top: 10px;" id="errMsg"></span>
                </div>
                <input type="hidden" id="inputCategorySettingId"/>
            </div>

            <div id="modalConfirmSuspend" class="modal fade modal-sm bg-danger-modal-suspend " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title">
                            <i class="glyphicon glyphicon-question-sign"></i> 確認する
                        </h4>
                    </div>
                    <div id="modalTextSuspend" class="modal-body">すぐにカテゴリが反映されます。ご注意の上公開してください</div>
                    <div class="modal-footer">
                        <a id="btnSuspendCategory" data-id="" href="#" class="btn btn-primary btn-modal-confirm">OK</a>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

            <div id="modalConfirmDefault" class="modal fade modal-sm bg-danger-modal-default " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title">
                            <i class="glyphicon glyphicon-question-sign"></i> 確認する
                        </h4>
                    </div>
                    <div id="modalTextDefault" class="modal-body">すぐにカテゴリが反映されます。ご注意の上公開してください</div>
                    <div class="modal-footer">
                        <a id="btnDefaultCategory" data-id="" href="#" class="btn btn-primary btn-modal-confirm">OK</a>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

            <div id="modalConfirmReset" class="modal fade modal-sm bg-danger-modal-reset " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title">
                            <i class="glyphicon glyphicon-question-sign"></i> 確認する
                        </h4>
                    </div>
                    <div class="modal-body">全てのカテゴリを未デフォルトに変更します</div>
                    <div class="modal-footer">
                        <a id="btnResetCategory" data-id="" href="#" class="btn btn-primary btn-modal-confirm">削除する</a>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script>
    $(".suspend-category-setting").click(function() {
        var categorySettingId = $(this).data("id");
        document.getElementById("inputCategorySettingId").value = categorySettingId;

        var categoryStatus = $(this).data("status");
        if(categoryStatus == 'ACTIVE'){
            document.getElementById("modalTextSuspend").innerHTML= "このカテゴリを非表示に変更します";
        }else{
            document.getElementById("modalTextSuspend").innerHTML= "このカテゴリを表示中に変更します";
        }
    });

    $("#btnResetCategory").click(function() {
        $("#modalConfirmReset").modal('hide');
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
        var categorySettingRequest = {}
        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/reset-category-setting",
            data : JSON.stringify(categorySettingRequest),
            timeout : 100000,
            success: function(response) {
                window.location.href = ctx + "/backend/list-category-setting?pageNumber=0";
            },
            error: function(){
            }
        });
    });

    $("#btnSuspendCategory").click(function() {
        $("#modalConfirmSuspend").modal('hide');
        var categorySettingRequest = {}
        var categorySettingId = $('#inputCategorySettingId').val();
        categorySettingRequest["categorySettingId"] = categorySettingId;
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/suspend-category-setting",
            data : JSON.stringify(categorySettingRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    window.location.href = ctx + "/backend/list-category-setting?pageNumber=0";
                }
            },
            error: function(){
            }
        });
    });

    $(".default-category-setting").click(function() {
        var categorySettingId = $(this).data("id");
        document.getElementById("inputCategorySettingId").value = categorySettingId;

        var isDefault = $(this).data("default");
        if(isDefault == true){
            document.getElementById("modalTextDefault").innerHTML= "このカテゴリを未デフォルトに変更します";
        }else{
            document.getElementById("modalTextDefault").innerHTML= "このカテゴリをデフォルトに変更します";
        }

    });

    $("#btnDefaultCategory").click(function() {
        $("#modalConfirmDefault").modal('hide');
        var categorySettingRequest = {}
        var categorySettingId = $('#inputCategorySettingId').val();
        categorySettingRequest["categorySettingId"] = categorySettingId;

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/default-category-setting",
            data : JSON.stringify(categorySettingRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    window.location.href = ctx + "/backend/list-category-setting?pageNumber=0";
                }
            },
            error: function(){
            }
        });
    });

</script>