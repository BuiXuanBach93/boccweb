<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 6/30/2017
  Time: 6:01 PM
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
    <li><a href="<%= request.getContextPath()%>/backend/maintain-system">メンテナンス設定</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <form:form class="" id="maintainMode" role="form" modelAttribute="maintainForm" action="/backend/process/maintain-system" method="POST" style="margin-top: 20px">
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    メンテナンス設定:
                </div>
                <div class="col-md-3">
                    <div class="input-group date">
                        <form:radiobutton path="maintain" value="false"/>OFF
                        <form:radiobutton path="maintain" style="margin-left: 20px" value="true"/>ON
                    </div>

                </div>
                </div>
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label>メッセージ内容</label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <form:input type="text" id="sysConfigMsg" path="sysConfigMsg" class="form-control"/>
                    <span id="sysConfigMsgError" style="color: red">${sysConfigMsgError}</span>
                    <span style="color: green"><b>${successMsg}</b></span>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label>許可IPリスト</label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <label style="max-width: 700px; word-wrap: break-word;"><b>${ipRestrict}</b></label>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label></label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <span style="color: #00a7d0">許可IPの設定はapp.properties ファイルに直接設定をお願いします</span>
                </div>
            </div>
            <div class="row">
            <div class="col-md-3 ">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label></label>
                </div>
                <input type="submit" class="btn btn-primary" value="設定">
                </input>
            </div>
                </div>
        </form:form>
    </div>
</div>
<style>
    #systemConfigMaintain{
        color: white;
        position: relative;
        display: block;
        background-color: transparent;
        box-sizing: border-box;
    }
</style>
<script>
    $(document).ready(function() {
        $(".subMenu").toggle("fast");
    });
//        var sysConfigMsg = $('#sysConfigMsg').val();
//        jQuery(document).ready(function ($) {
//
//            if (isEmpty(sysConfigMsg)) {
//                $('#sysConfigMsgError').html("メッセージ内容を入力してください。");
//                $('#sysConfigMsg').focus();
//            } else {
//            $( "#maintainMode" ).submit(function( event ) {
//                event.preventDefault();
//            }); }
//
//        });
//
//        function submitMaintain() {
//            if (isEmpty(sysConfigMsg)) {
//                $('#sysConfigMsgError').html("メッセージ内容を入力してください。");
//                $('#sysConfigMsg').focus();
//            } else {
//                $('#maintainMode').submit();
//            }
//        }

</script>