<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <li><a href="<%= request.getContextPath()%>/backend/list-group-publish?pageNumber=0">法人グループ設定</a></li>
</ul>
<script>
    var ctx = "${pageContext.request.contextPath}";
</script>

<div class="group-publish" style="margin-top: 20px;">
    <div class="box box-default">
        <div class="box-body" style="margin-top: 20px;">
            <div class="form-group" >
                <div class="row">
                    <div class="col-md-1">
                        <a href="<%= request.getContextPath()%>/backend/create-group-publish" role="button" class="btn btn-default"
                           style="background-color: #f98017; color: #FFFFFF;" >新規登録</a>
                    </div>
                    <div class="col-md-2"></div>
                    <form:form action="list-group-publish" modelAttribute="groupPublish" method="GET">
                        <div>
                            <div class="col-md-2">法人グループ名</div>
                            <div class="col-md-4">
                                <form:input path="groupName" class="form-control" type="text"/>
                            </div>
                            <div class="col-md-2">
                                <button type="submit" class="btn btn-primary" style="margin-left: -20px;">
                                    <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                                    検索
                                </button>
                            </div>
                        </div>
                    </form:form>
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
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">法人グループNo</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">法人グループ名</th>
                                            <th class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">所属法人数</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">更新者</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">更新日時</th>
                                            <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">編集/削除</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        <c:forEach var="list" items="${listGroupPublish}"  varStatus="loop" >
                                            <tr role="row">
                                                <td class="sorting_1">${startElement+loop.index}</td>
                                                <td class="sorting_1"><c:out value="${list.groupName}"/></td>
                                                <td class="sorting_1"><c:out value="${list.legalNumber}"/></td>
                                                <td class="sorting_1"><c:out value="${list.shmAdmin.adminName}"/></td>
                                                <td class="minwidth150">
                                                    <javatime:format value="${list.updatedAt}" pattern="Y年MM月dd日 HH:mm" var="parsedDate"/>
                                                    <c:out value="${parsedDate}"/>
                                                </td>
                                                <td class="minwidth150">
                                                    <a href="<%= request.getContextPath()%>/backend/edit-group-publish?groupId=${list.groupId}">
                                                        <span class="glyphicon glyphicon-pencil" style="margin-right: 15px"></span>
                                                    </a>
                                                    <a href="#" class="btn-modal delete-publish-group" data-toggle="modal"  data-target=".bg-danger-modal" data-id="${list.groupId}"
                                                       data-href = "#"  style="margin-left: 15px">
                                                        <span class="glyphicon glyphicon-trash"></span>
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

                    </c:if>

                    <span style="color: red;" >${nullResult}</span>
                </div>
            </div>

            <div id="modalConfirmDelete" class="modal fade modal-sm bg-danger-modal " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title">
                            <i class="glyphicon glyphicon-question-sign"></i> 確認する
                        </h4>
                    </div>
                    <div class="modal-body">この法人グループを削除しますか ?</div>
                    <div class="modal-footer">
                        <a id="btnDeleteGroupPublish" data-id="" href="#" class="btn btn-primary btn-modal-confirm">削除する</a>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script>
    $(".delete-publish-group").click(function() {
        var groupId = $(this).data("id");
        $('#btnDeleteGroupPublish').attr('data-id',groupId);
    });

    $("#btnDeleteGroupPublish").click(function() {
        var groupPublishRequest = {}
        groupPublishRequest["groupId"] = $(this).data("id");

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/remove-group-publish",
            data : JSON.stringify(groupPublishRequest),
            timeout : 100000,
            success: function(response) {
                window.location.href = ctx + "/backend/list-group-publish?pageNumber=0";
            },
            error: function(){
            }
        });
    });

</script>