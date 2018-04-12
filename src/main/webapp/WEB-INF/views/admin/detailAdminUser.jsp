<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>


<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-admin-user">管理者一覧</a></li>
    <li><a href="#">管理者詳細</a></li>
</ul>

<div class="box box-default" style="margin-top: 30px">
    <div class="box-body">

        <div class="row">
            <div class="form-group col-md-12">
                <div class="row"><div class="col-sm-12">
                    <table id="example2" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example2_info">
                        <thead>
                        <tr role="row">
                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">ID</th>
                            <th class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending" aria-label="Rendering engine: activate to sort column descending">名前</th>
                            <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">メール</th>
                            <th class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">ステータス</th>
                            <th class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-label="Browser: activate to sort column ascending">登録日</th>
                        </tr>
                        </thead>
                        <tbody>


                        <c:forEach var="list" items="${listAdmin}"  varStatus="status">
                            <tr role="row">
                                <td ><c:out value="${list.adminId}"/></td>
                                <td ><c:out value="${list.adminName}"/></td>
                                <td>${list.adminEmail}</td>

                                <td>

                                    <span class="label label-primary">
                                            ${list.adminRoleTxt}
                                    </span>

                                </td>

                                <td class="minwidth150">
                                    <javatime:format value="${list.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                    <c:out value="${createdAt}"/>
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