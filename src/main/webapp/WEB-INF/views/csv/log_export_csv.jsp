<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/log-export-csv?page=0">CSV出力ログ</a></li>
</ul>

<div class="row">
    <c:if test="${not empty adminCsvHsts}">
        <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
            <ul class="pagination" style="margin: 0px !important;">
                <div class="col-md-6">
                    <h5><b style="margin-right: 50px">${startElement} - ${curElements} の ${totalElements}</b></h5>
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
    </c:if>
</div>

<div class="box box-default">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="col-sm-12">
                <table id="example2" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example2_info" style="table-layout: fixed; width: 100%">
                    <thead >
                    <tr role="row">
                        <th class="text-center" style="width: 80px">管理者ID</th>
                        <th class="text-center">管理者名</th>
                        <th class="text-center minwidth150">CSV 出力時間</th>
                        <th class="text-center minwidth150">CSV 出力内容</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="adminCsvHst" items="${adminCsvHsts}">
                        <tr role="row" style="text-align: center; word-wrap: break-word">
                            <td class="sorting_1">
                                    ${adminCsvHst.admin.adminId}
                            </td>
                            <td class="sorting_1">
                                    ${adminCsvHst.admin.adminName}
                            </td>
                            <td class="minwidth150">
                                    <javatime:format value="${adminCsvHst.createdAt}" pattern="Y年MM月dd日 HH:mm:ss" var="createdAt"/><c:out value="${createdAt}"/>
                            </td>
                            <td class="minwidth150">
                                <c:choose>
                                    <c:when test="${adminCsvHst.adminCsvHstType == 'USER'}">
                                        ユーザー一覧CSV
                                    </c:when>
                                    <c:when test="${adminCsvHst.adminCsvHstType == 'POST'}">
                                        投稿一覧CSV
                                    </c:when>
                                    <c:when test="${adminCsvHst.adminCsvHstType == 'USER_PATROL'}">
                                        ユーザー一覧サイトパトロールCSV
                                    </c:when>
                                    <c:when test="${adminCsvHst.adminCsvHstType == 'POST_PATROL'}">
                                        投稿一覧サイトパトロールCSV
                                    </c:when>
                                    <c:otherwise>
                                        基幹出力用CSV
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

