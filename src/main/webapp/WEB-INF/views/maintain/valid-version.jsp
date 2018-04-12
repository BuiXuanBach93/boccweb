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
    <li><a href="<%= request.getContextPath()%>/backend/valid-version">有効なバージョン</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <form:form class="" id="validVersion" role="form" modelAttribute="validVersionForm" action="update-valid-version" method="POST" style="margin-top: 20px">
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label>有効なバージョン</label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <form:input type="text" step="0.01" id="sysConfigValues" path="sysConfigValues" class="form-control"/>
                    <span id="sysConfigValuesError" style="color: red">${sysConfigValuesError}</span>
                    <span style="color: green"><b>${successMsg}</b></span>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    <label>メッセージ内容</label>
                </div>
                <div class="input-group" style="width: 500px;">
                    <form:textarea id="sysConfigMsg" path="sysConfigMsg" class="form-control"/>
                    <span id="sysConfigMsgError" style="color: red">${sysConfigMsgError}</span>
                    <span style="color: green"><b>${successMsg}</b></span>
                </div>
            </div>
            <div class="row">
            <div class="col-md-3 ">
                <input type="submit" class="btn btn-primary" value="Save">
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
    });
</script>