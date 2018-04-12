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
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/migration-msg">システム構成</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <input type="hidden" id="migrationTalkpurchaseFlag" value="${migrationTalkpurchaseFlag}">
    <input type="hidden" id="migrationQaFlag" value="${migrationQaFlag}">
    <div class="search-posts">
        <form:form class="" id="migrationMsgTalkPurchase" action="/backend/process/migration-msg-tarkpurchase" method="post">
            <div class="row">
                <div class="col-md-8">
                    <label id="migrationTalkpurchaseMsg">All talk purchase messages have not migrated yet.</label>
                </div>
                <div class="col-md-4 ">
                    <input type="submit" id="migrationTalkPurchaseBtn" class="btn btn-primary" value="Process">
                    </input>
                </div>
            </div>
        </form:form>
    </div>
    <div class="search-posts">
        <form:form class="" id="migrationMsgQA" action="/backend/process/migration-msg-qa" method="post">
            <div class="row">
                <div class="col-md-8">
                    <label id="migrationQaMsg">All Questions/Answers messages have not migrated yet.</label>
                </div>
                <div class="col-md-4 ">
                    <input type="submit" id="migrationQaBtn" class="btn btn-primary" value="Process">
                    </input>
                </div>
            </div>
        </form:form>
    </div>
</div>


<script>
 $(document).ready(function () {
    var migrationTalkpurchaseFlagValue = $("#migrationTalkpurchaseFlag").val();
        if (migrationTalkpurchaseFlagValue == 'true'){
            $("#migrationTalkPurchaseBtn").prop("disabled", true);
            $("#migrationTalkpurchaseMsg").text("All talk purchase messages were already migrated.");
        }

     var migrationQaFlag = $("#migrationQaFlag").val();
     if (migrationQaFlag == 'true'){
         $("#migrationQaMsg").text("All Questions/Answers messages were already migrated.");
         $("#migrationQaBtn").prop("disabled", true);
     }

 })
</script>