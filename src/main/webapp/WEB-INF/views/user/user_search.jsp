<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 3/14/2017
  Time: 3:51 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/users?page=0">ユーザー検索</a></li>
</ul>


<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <div class="form-group">
            <p style="color: #ff0000">${errorMsg}</p>
        </div>
        <form:form class="" id="condition_form" role="form" modelAttribute="userSearch" action="users" method="GET" style="margin-top: 20px">
            <div class="row">
                <div class="form-group">
                    <div class="col-md-2">
                        <label>性</label>
                        <form:input path="firstName" type="text" class="form-control"/>
                        <div class="error">
                            <span style="color: red;">${firstName}</span>
                            <form:errors path="firstName" class="control-label" style="color:red;" />
                        </div>
                    </div>
                    <div class="col-md-2">
                        <label>名</label>
                        <form:input path="lastName" type="text" class="form-control"/>
                        <div class="error">
                            <span style="color: red;">${lastName}</span>
                            <form:errors path="lastName" class="control-label" style="color:red;" />
                        </div>
                    </div>
                    <div class="col-md-2">
                        <label>性別</label>
                        <div class="">
                            <div class="checkbox inline-block">
                                <label>
                                    <form:checkbox path="male" value="1"/>
                                    男
                                </label>
                            </div>

                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="female" value="0"/>
                                    女
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2">
                    <div>
                        <label>生年月日</label></div>
                        <div class="inline-block">
                            <div class="form-group">
                                <div class="input-group date">
                                    <div class="input-group-addon">
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                    <form:input type="text"
                                           path="birthDay"
                                           class="form-control pull-right datepicker" id="datepicker2" placeholder = "YYYY/MM/DD"/>
                                </div>
                                <div class="row form-group" style="color: red; margin-left: 10px;">
                                    <span id = "msg1"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <label>法人ID</label>
                        <form:input type="text" path="legalId" class="form-control" maxlength="6"/>
                        <div class="row form-group" style="color: red; margin-left: 10px;">
                            <span id = "msg2"></span>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <label>BSID</label>
                        <form:input type="text" path="bsid" class="form-control"  maxlength="15"/>
                        <div class="error">
                            <span style="color: red;">${bsid}</span>
                            <form:errors path="bsid" class="control-label" style="color:red;" />
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <div class="form-group user-checkbox">
                        <label>ステータス</label>
                        <div>
                            <div class="checkbox inline-block">
                                <label>
                                    <form:checkbox path="activeStt" value="4"/>
                                    有効
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="reservedStt" value="2"/>
                                    停止中
                                </label>
                            </div>
                            <div class="checkbox-item checkbox inline-block">
                                <label>
                                    <form:checkbox path="leftStt" value="6"/>
                                    退会中
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-2">
                    <label>電話番号</label>
                    <form:input path="phoneNumber" type="text" class="form-control" maxlength="11" onkeypress=" return isNumberKey(event)"/>
                </div>
                <div class="col-md-2">
                    <label>住所①</label>
                    <div class="form-group">
                        <select class="form-control select-button" name="provinceId" id="province">
                            <option value="">すべて</option>
                        </select>
                    </div>
                </div>
                <div class="col-md-2">
                    <label>住所②</label>
                    <div class="form-group">
                        <select class="form-control select-button" name="districtId" id="district">
                            <option value="">すべて</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-2">
                    <label>
                        メールアドレス
                    </label>
                </div>
            </div>
            <div class="row">
                <div class="form-group">
                    <div class="col-md-4">
                        <form:input path="email" type="email" class="form-control"/>
                        <div class="error">
                            <span style="color: red;">${email}</span>
                            <form:errors path="email" class="control-label" style="color:red;" />
                        </div>
                    </div>
                    <div class="col-md-2">
                        <div class="form-group">
                            <form:select class="form-control select-button" path="userType">
                                <form:option value="0">登録日が</form:option>
                                <form:option value="1">停止日が</form:option>
                                <form:option value="2">退会日が</form:option>
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
                                    <form:input type="text" path="fromDate" class="form-control pull-right datepicker" id="startDate" placeholder = "YYYY/MM/DD"/>
                                </div>
                                <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                    <span id = "msg3"></span>
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
                                    <form:input type="text" path="toDate" class="form-control pull-right datepicker" id="endDate" placeholder = "YYYY/MM/DD"/>
                                    <form:input type="number" hidden="true" path="page" value="${curPage}"></form:input>
                                </div>
                                <div class="row form-group" style="color: red; margin-left: 10px;">
                                    <span id = "msg4"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2">
                        <input type="button" onclick="searchUserForm()" class="btn btn-primary" value="検索"/>
                    </div>
                </div>

            </div>
        </form:form>
    </div>

    <div class="row">
        <c:if test="${not empty users}">
            <div class="col-md-12">
                <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
                    <ul class="pagination">
                        <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                            <div class="col-md-2 pagination-button" style="margin-right: 20px">
                                <input type="button" id="userExportCSVBtn" class="btn btn-primary" value="CSV出力"></input>
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
        </c:if>
    </div>

    <div class="result-posts">
        <div class="row">
            <div class="form-group col-md-12">
                <div class="row">
                    <div class="col-sm-12">
                        <c:if test="${not empty users}">
                            <table id="example2" class="table table-bordered table-hover dataTable table-striped" role="grid" aria-describedby="example2_info" style="table-layout: fixed; width: 100%">
                                <thead>
                                <tr role="row">
                                    <th style="text-align: center" class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending">ID</th>
                                    <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >名前</th>
                                    <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >Eメール</th>
                                    <th style="text-align: center" class="sorting minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >登録日時</th>
                                    <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >ステータス</th>
                                    <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >編集</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="user" items="${users}">
                                    <tr role="row" class="odd" style="text-align: center; word-wrap: break-word">
                                        <td class="sorting_1">
                                            <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${user.id}">${user.id}</a>
                                        </td>
                                        <td>
                                            <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${user.id}"><c:out value="${user.firstName}  ${user.lastName}" /></a>
                                        </td>
                                        <td>${user.email}</td>
                                        <td class="minwidth150">
                                            <javatime:format value="${user.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                            <c:out value="${createdAt}"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${user.status == 'LEFT'}">
                                                    退会中
                                                </c:when>
                                                <c:when test="${user.ctrlStatus == 'SUSPENDED'}">
                                                    停止中
                                                </c:when>
                                                <c:when test="${user.status == 'ACTIVATED'}">
                                                    有効
                                                </c:when>
                                                <c:when test="${user.status == 'TEND_TO_LEAVE'}">
                                                    退会中
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${user.id}">編集</a>
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
</div>

<script>
    jQuery(document).ready(function($){
        $.ajax({
            type: "GET",
            url: "api/addresses",
            success: function (response) {
                response.forEach(function (provice) {
                    $('#province').append('<option value=' + provice.addressId + '>' + provice.areaName + '</option>');

                });
                $('#province').val('<c:out value="${param.provinceId}" />');
                $('#province').trigger('change');
            },
            error: function (e) {
                console.log('Error while request..' + e);
            }
        });
        $("#province").on('change', function () {
            $('#district').val('').html('<option value="">すべて</option>');
            var parentId = $(this).val();
            if (parentId) {
                var districtId = '<c:out value="${param.districtId}" />';
                var update = false;
                $.ajax({
                    type: "GET",
                    url: "api/addresses?parentId=" + parentId,
                    success: function (response) {
                        response.forEach(function (district) {
                            $('#district').append('<option value=' + district.addressId + '>' + district.areaName + '</option>');
                            !update ? update = district.addressId == districtId : '';
                        });
                        update ? $('#district').val(districtId) : '';
                    },
                    error: function (e) {
                        console.log('Error while request..' + e);
                    }, done: function (e) {
                        console.log("DONE");
                    }
                });
            }
        });

    });

    $('#userExportCSVBtn').click(function () {
        if (validateSearch()) {
            $('#condition_form').attr('action', 'users/csv');
            $('#condition_form').submit();
            $('#condition_form').attr('action', 'users');
        }
    });

    function isNumberKey(evt){
        var charCode = (evt.which) ? evt.which : event.keyCode
        if (charCode > 31 && (charCode < 48 || charCode > 57)){
            return false;
        }
        return true;
    };

    function validateSearch() {
        var legalId = $('#legalId').val();
        var phoneNumber = $('#phoneNumber').val();
        if(!isNumberInt(legalId)) {
            $('#msg2').html("法人ID " + "<spring:message code="SH_E100045"/>")
            $('#id').focus();
            return false;
        }
        if(!isNumberInt(phoneNumber)) {
            $('#msg').html("電話番号 " + "<spring:message code="SH_E100045"/>")
            $('#phoneNumber').focus();
            return false;
        }

        var birthday = $('#datepicker2').val();
        if(!isEmpty(birthday) && !validateDateFormat(birthday)){
            $('#msg1').html("入力フォーマットが違います。");
          //  $('#datepicker2').focus();
            return false;
        }
        var startDate = $('#startDate').val();
        if(!isEmpty(startDate) && !validateDateFormat(startDate)){
            $('#msg3').html("入力フォーマットが違います。");
          //  $('#startDate').focus();
            return false;
        }

        var endDate = $('#endDate').val();
        if(!isEmpty(endDate) && !validateDateFormat(endDate)){
            $('#msg4').html("入力フォーマットが違います。");
           // $('#endDate').focus();
            return false;
        }
        if(!isEmpty(startDate) && !isEmpty(endDate)&& !isEmpty(endDate) && validateDateFormat(startDate) && validateDateFormat(endDate)) {
            var startTime = new Date(startDate);
            var endTime = new Date(endDate);
            if (startTime.getTime() > endTime.getTime()) {
                $('#msgStartDate').html("<spring:message code="SH_E100048"/>")
                flag = false;
            }
        }else{
            $('#msg3').html("");
        }
        return true;
    }
    function searchUserForm(){
        if (validateSearch()){
//            loadingContent();
            $('#condition_form').submit();
        }
    }

</script>
