<%--
  Created by IntelliJ IDEA.
  User: DonBach
  Date: 9/4/2017
  Time: 6:01 PM
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/sync-date-config">自動退会処理日設定</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <form:form class="" id="syncDateConfig" role="form" modelAttribute="syncDateConfigForm" action="update-sync-date" method="POST" style="margin-top: 20px">
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label>自動退会処理日</label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <form:input type="number" step="1" id="sysConfigValue" path="dayOfMonth" class="form-control"/>
                    <span id="sysConfigValuesError" style="color: red">${sysConfigValuesError}</span>
                    <span style="color: green"><b>${successMsg}</b></span>
                </div>
            </div>
            <div class="row">
            <div class="col-md-3 ">
                <input type="button" class="btn btn-primary" value="保存する" id="synButton">
                </input>
            </div>
                </div>
        </form:form>
    </div>
</div>
<style>
    #systemConfigValidVersion{
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
        $('#synButton').click(function () {
        var sysConfigValue = $('#sysConfigValue').val();
            if (sysConfigValue <1 || sysConfigValue >31) {
                $('#sysConfigValuesError').html("1~31の値で入力してください");
                $('#sysConfigValue').focus();
            }else {
                $('#sysConfigValuesError').html("");
                $('#syncDateConfig').submit();
            }
        })
    });
</script>