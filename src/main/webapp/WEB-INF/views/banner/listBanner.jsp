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
    <li><a href="<%= request.getContextPath()%>/backend/list-banner?pageNumber=0">特集バナー管理</a></li>
</ul>
<script>
    var ctx = "${pageContext.request.contextPath}";
</script>
<style>
    table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
    }
</style>
<div class="category-setting" style="margin-top: 20px;">
    <div class="box box-default">
        <div class="box-body" style="margin-top: 20px;">
            <div class="form-group" >
                <div class="row">
                    <div class="col-md-1">
                        <a href="<%= request.getContextPath()%>/backend/create-banner" role="button" class="btn btn-default"
                           style="background-color: #f98017; color: #FFFFFF;" >特集追加</a>
                    </div>
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
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">バナーID</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">特集名(バナーのaltに表示)</th>
                                            <th class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Platform(s): activate to sort column ascending">遷移先</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">バナー(715x100)</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">バナー編集</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">表示開始</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">表示終了</th>
                                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">表示</th>
                                        </tr>
                                        </thead>
                                        <tbody>

                                        <c:forEach var="list" items="${listBanners}"  varStatus="loop" >
                                            <tr role="row">
                                                <td class="sorting_1">${startElement+loop.index}</td>
                                                <td class="sorting_1"><a href="<%= request.getContextPath()%>/backend/edit-banner?bannerId=${list.bannerId}">${fn:escapeXml(list.bannerName)}</a></td>
                                                <c:choose>
                                                    <c:when test="${list.destinationType == 'WEB'}">
                                                        <c:choose>
                                                            <c:when test="${list.isBuildPage == false}">
                                                                <td class="sorting_1">${fn:escapeXml(list.bannerDestination)}</td>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <td class="sorting_1">キャンページ: ${fn:escapeXml(list.bannerDestination)}</td>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:when test="${list.destinationType == 'CATEGORY'}">
                                                        <td class="sorting_1">カテゴリID: ${fn:escapeXml(list.bannerDestination)}</td>
                                                    </c:when>
                                                    <c:when test="${list.destinationType == 'POST_ID'}">
                                                        <td class="sorting_1">投稿ID: ${fn:escapeXml(list.bannerDestination)}</td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td class="sorting_1">キーワード：${fn:escapeXml(list.bannerDestination)}</td>
                                                    </c:otherwise>
                                                </c:choose>
                                                <td>
                                                    <a href="#" class="showImageClick" data-src-link="${list.imageUrl}">
                                                        <img src="${list.imageUrl}" width="215" height="30" alt=""
                                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                                                    </a>
                                                </td>
                                                <td class="minwidth150">
                                                    <a href="<%= request.getContextPath()%>/backend/edit-banner?bannerId=${list.bannerId}">
                                                        <span class="glyphicon glyphicon-pencil" style="margin-right: 15px"></span>
                                                    </a>
                                                </td>
                                                <td class="minwidth150">
                                                    <javatime:format value="${list.fromDate}" pattern="Y年MM月dd日 HH:mm" var="parsedDate"/>
                                                    <c:out value="${parsedDate}"/>
                                                </td>
                                                <td class="minwidth150">
                                                    <javatime:format value="${list.toDate}" pattern="Y年MM月dd日 HH:mm" var="parsedDate"/>
                                                    <c:out value="${parsedDate}"/>
                                                </td>
                                                <c:choose>
                                                    <c:when test="${list.bannerStatus == 'ACTIVE'}">
                                                        <td class="minwidth150">
                                                            <a href="#" class="btn-modal suspend-banner" data-toggle="modal"  data-target=".bg-danger-modal-suspend" data-id="${list.bannerId}" data-status="${list.bannerStatus}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span style="color: red">表示中</span>
                                                            </a>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${list.bannerStatus == 'SUSPENDED'}">
                                                        <td class="minwidth150">
                                                            <a href="#" class="btn-modal suspend-banner" data-toggle="modal"  data-target=".bg-danger-modal-suspend" data-id="${list.bannerId}" data-status="${list.bannerStatus}"
                                                               data-href = "#"  style="margin-left: 15px">
                                                                <span style="color: blue">非表示</span>
                                                            </a>
                                                        </td>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <td class="minwidth150">
                                                            <a href="#" style="margin-left: 15px">
                                                                <span style="color: black">表示終了</span>
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
                    <span style="color: red;" >${nullResult}</span>
                </div>
                <input type="hidden" id="inputBannerId"/>
            </div>

            <div id="modalConfirmSuspend" class="modal fade modal-sm bg-danger-modal-suspend " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title">
                            <i class="glyphicon glyphicon-question-sign"></i> 確認する
                        </h4>
                    </div>
                    <div id="modalText" class="modal-body">表示ステータスを変更しますか。</div>
                    <div class="modal-footer">
                        <a id="btnSuspendBanner" data-id="" href="#" class="btn btn-primary btn-modal-confirm">変更</a>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<script>
    $(".suspend-banner").click(function() {
        var bannerId = $(this).data("id");
        var bannerStatus = $(this).data("status");
        document.getElementById("inputBannerId").value = bannerId;
        if(bannerStatus == 'ACTIVE'){
            document.getElementById("modalText").innerHTML= "このバナーを「非表示」に変更します";
        }else{
            document.getElementById("modalText").innerHTML= "このバナーを「表示」に変更します";
        }

    });

    $("#btnSuspendBanner").click(function() {
        $("#modalConfirmSuspend").modal('hide');
        var bannerRequest = {}
        var bannerId = $('#inputBannerId').val();
        bannerRequest["bannerId"] = bannerId;

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/suspend-banner",
            data : JSON.stringify(bannerRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    window.location.href = ctx + "/backend/list-banner?pageNumber=0";
                }
            },
            error: function(){
            }
        });
    });

</script>