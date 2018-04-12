<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url var="firstUrl" value="${baseUrlPagination}&pageNumber=1" />
<c:url var="lastUrl" value="${baseUrlPagination}&pageNumber=${deploymentLog.totalPages}" />
<c:url var="prevUrl" value="${baseUrlPagination}&pageNumber=${currentIndex - 1}" />
<c:url var="nextUrl" value="${baseUrlPagination}&pageNumber=${currentIndex + 1}" />

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-admin-user">ユーザー権限設定マスタ</a></li>
</ul>
<script>
    var ctx = "${pageContext.request.contextPath}";
</script>

<div class="box box-default" style="margin-top: 20px">
    <div class="box-body">
        <form:form action="search-admin-user" modelAttribute="ad" method="GET">
            <div class="form-group" style="margin-top: 30px;">
                <div class="row">
                    <div class="col-md-4">
                        <label class="col-md-4 control-label style-label" style=" text-align: left">権限ソート</label>
                        <div class="col-md-8">
                            <form:select path="adminRole" class=" form-control select-button">
                                <form:option value="" label="すべて"/>
                                <form:options items="${roleList}" />
                            </form:select>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <label class="col-md-4 control-label" style=" text-align: left">ユーザー名</label>
                        <div class="col-md-8">
                            <form:input path="adminName" class="form-control" type="text"/>
                        </div>
                    </div>
                    <div class="col-md-2 clearfix">
                        <button type="submit" class="btn btn-default pull-right" style="margin-bottom: 10px">
                            <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                            検索
                        </button>
                    </div>
                    <div class="col-md-2 box-tools clearfix">
                        <div class="form-group pull-right ">
                            <a href="<%= request.getContextPath()%>/backend/register-admin" class="btn btn-primary" role="button">新しいメンバーを登録</a>
                        </div>
                    </div>
                </div>
            </div>
        </form:form>
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
                        <c:when test="${curPage == 1}">
                            <li class="paginate_button disabled"><a href="#">&lt;</a></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="#" class="search-paging2" data-page="${curPage - 1}">&lt;</a></li>
                        </c:otherwise>
                    </c:choose>

                    <c:choose>
                        <c:when test="${curPage == totalPage}">
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
    <div class="form-group">
        <div class="row">
            <div class="col-md-12">
                <div class="row">
                    <div class="col-sm-12">
                        <table id="example2" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example2_info" style="table-layout: fixed; width: 100%">
                            <thead >
                            <tr role="row">
                                <th class="sorting_asc text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending" style="width: 80px">ID</th>
                                <th class="sorting_asc text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">名前</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">ステータス</th>
                                <th class="sorting text-center minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">登録日</th>
                                <th class="sorting text-center" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending"></th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach var="admin" items="${listAdmin}"  varStatus="status">
                                <tr role="row" style="text-align: center; word-wrap: break-word">
                                    <td class="sorting_1"><c:out value="${admin.adminId}"/></td>
                                    <td class="sorting_1"><c:out value="${admin.adminName}"/></td>

                                    <td>
                                        <span class="label label-primary">
                                                ${admin.adminRoleTxt}
                                        </span>
                                    </td>

                                    <td class="minwidth150">
                                        <javatime:format value="${admin.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                        <c:out value="${createdAt}"/>
                                    </td>
                                    <td class="minwidth150">
                                        <a href="<%= request.getContextPath()%>/backend/detail-admin-user.html?id=${admin.adminId}">
                                            <span class="glyphicon glyphicon-eye-open" style="margin-right: 15px;"></span>
                                        </a>
                                        <a href="<%= request.getContextPath()%>/backend/edit-admin-user.html?id=${admin.adminId}">
                                            <span class="glyphicon glyphicon-pencil" style="margin: 0px"></span>
                                        </a>

                                        <c:choose>
                                            <c:when test="${currentAdmin == admin.adminEmail}">
                                                <a href="#" class="btn-modal admin-btn-del" data-toggle="modal"  data-target=".bg-danger-modal"
                                                   data-href = "#"  style="margin-left: 15px">
                                                    <span class="glyphicon glyphicon-trash"></span>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="#" class="btn-modal delete-admin" data-toggle="modal"  data-target=".bg-danger-modal" data-id="${admin.adminId}"
                                                   data-href = "#"  style="margin-left: 15px">
                                                    <span class="glyphicon glyphicon-trash"></span>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>


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
    <div class="modal fade modal-sm bg-danger-modal " role="document" style="margin: 30px auto; background: none;">
        <div class="modal-content">
            <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                <h4 class="modal-title">
                    <i class="glyphicon glyphicon-question-sign"></i> 確認する
                </h4>
            </div>
            <div class="modal-body">このアカウントを削除しますか。</div>
            <div id="btnDeleteAdmin" data-id="" class="modal-footer">
                <a href="#" class="btn btn-primary btn-modal-confirm">削除する</a>
                <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
            </div>
        </div>
    </div>
</div>
<script>
    $(".delete-admin").click(function() {
        var adminId = $(this).data("id");
        $('#btnDeleteAdmin').attr('data-id',adminId);
    });

    $("#btnDeleteAdmin").click(function() {
        var adminRequest = {}
        adminRequest["adminId"] = $(this).data("id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/delete-admin-user",
            data : JSON.stringify(adminRequest),
            timeout : 100000,
            success: function(response) {
                window.location.href = ctx + "/backend/list-admin-user";
            },
            error: function(){
            }
        });
    });

</script>

