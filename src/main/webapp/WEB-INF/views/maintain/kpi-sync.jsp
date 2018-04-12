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
    <li><a href="<%= request.getContextPath()%>/backend/kpi-sync">KPIの過去データを取得する</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <form:form class="" id="kpiModel" role="form"
                   action="kpi-sync-process" method="POST" style="margin-top: 20px">
        <div class="row">
            <div class="col-md-12" style="text-align: left; line-height: 34px; font-weight: bold">
                KPIの過去データを取得する
            </div>
        </div>
            <div class="row">
                <div class="col-md-8" style="color: green"><b>${successMsg}</b></div>
            </div>
        <div class="row">
            <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                <label></label>
            </div>
        </div>
        <div class="row">
            <div class="col-md-3 ">
                <input type="button" id="migrationButton" class="btn btn-primary" data-toggle="modal"
                       data-target="#confirmModal" value="同期する">
                </input>
            </div>
        </div>
        </form:form>
        <!-- Modal -->
        <div class="modal fade" id="confirmModal" role="dialog">
            <div class="modal-dialog">
                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                    </div>
                    <div class="modal-body">
                        <p>KPIの過去データを同期しますか？</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="buttonContinue" class="btn btn-primary" data-dismiss="modal" onclick="doSyncData()">
                            実行する
                        </button>
                        <button type="button" id="buttonClose" class="btn btn-default" data-dismiss="modal">閉じる
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<style>
    #systemConfigKpiSync{
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
    function doSyncData() {
                $('#kpiModel').submit();
    }
</script>
