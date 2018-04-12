<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/kpi-daily">KPI集計</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <div class="col-md-4" style="padding-left: 0px; margin-top: -10px; margin-left: -9px;">
            <a type="button" class="btn btn-primary" style="width: 100px;">
                日次
            </a>
            <a type="button" class="btn btn-default" style="width: 100px;" href="<%= request.getContextPath()%>/backend/kpi-monthly">
                月次
            </a>
        </div>
        <div class="row"><div style="color: red" class="col-md-offset-1" id="errorMsg">
        </div> </div>
        <form:form class="" id="kpi_form_search" role="form" modelAttribute="kpiRequest" action="kpi-daily" method="GET" style="margin-top: 20px">
            <div class="row">
                <div class="col-md-1" style="text-align: center; line-height: 34px; font-weight: bold">
                    日付
                </div>
                <div class="col-md-3">
                    <div class="input-group date">
                        <div class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </div>
                        <form:input type="text" path="fromDate" class="form-control pull-right" id="kpiFromDate" placeholder="YYYY/MM/DD"/>
                    </div>
                    <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                        <span id="msgStartDate"></span>
                    </div>
                    <span class="add-tilde"></span>
                </div>
                <div class="col-md-3">
                    <div class="input-group date">
                        <div class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </div>
                        <form:input type="text" path="toDate" class="form-control pull-right" id="kpiToDate" placeholder="YYYY/MM/DD"/>
                    </div>
                    <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                        <span id="msgEndDate"></span>
                    </div>
                </div>
                <div class="col-md-3">
                    <input id="btnSearchKpiDay" type="button" class="btn btn-primary" value="検索する">
                    </input>
                </div>
            </div>
        </form:form>
    </div>

</div>
<div class="row">
    <c:if test="${not empty kpiDailys}">
        <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
            <ul class="pagination" style="margin: 0px !important;">
                <div class="col-md-2 pagination-button" style="margin-right: 20px; margin-top: 10px;">
                    <input type="button" id="btnExportExcel" class="btn btn-primary" value="Excel出力"></input>
                </div>
            </ul>
        </div>
    </c:if>
</div>

<div class="box box-default">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="col-md-12">
                <div class="pull-right" style="margin-bottom: 10px;">
                    DL数＝Google AnalyticsのNewUser数、利用者数＝期間中に投稿もしくは購入したUU数
                </div>
            </div>
            <div class="col-sm-12">
                <table id="example2" class="table table-bordered table-hover dataTable text-center table-striped" role="grid" aria-describedby="example2_info" style="table-layout: fixed; width: 100%">
                    <thead >
                    <tr role="row">
                        <th class="text-center" style="width: 80px" rowspan="2">日付</th>
                        <th class="text-center" colspan="4">会員系</th>
                        <th class="text-center" colspan="3">投稿系</th>
                        <th class="text-center" colspan="3">購入系</th>
                    </tr>
                    <tr role="row">
                        <th class="text-center">DL数</th>
                        <th class="text-center">会員数</th>
                        <th class="text-center">会員登録率</th>
                        <th class="text-center">利用者数</th>
                        <th class="text-center">投稿者数</th>
                        <th class="text-center">投稿数</th>
                        <th class="text-center">平均投稿回数</th>
                        <th class="text-center">購入者数</th>
                        <th class="text-center">購入回数</th>
                        <th class="text-center">平均購入回数</th>
                    </tr>
                    </thead>
                    <tbody>
                    <col width="130">
                    <c:forEach var="kpiDay" items="${kpiDailys}">
                        <tr role="row" style="text-align: center; word-wrap: break-word">
                            <td>
                                    ${kpiDay.queryTime}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.dlNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.regNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.regRatio}%
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.actorNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.ownerNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.postNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.postRatio}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.partnerNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.transNumber}
                            </td>
                            <td class="sorting_1">
                                    ${kpiDay.transRatio}
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

<script>
    jQuery(document).ready(function ($) {
        $("#kpiFromDate").datepicker({
            changeMonth: true,
            changeYear: true,
            dateFormat: 'yy/mm/dd'
        }).keydown(false);

        $("#kpiToDate").datepicker({
            changeMonth: true,
            changeYear: true,
            dateFormat: 'yy/mm/dd'
        }).keydown(false);

        function validateSearch() {
            var flag = true;
            var startDate = $('#kpiFromDate').val();
            var endDate = $('#kpiToDate').val();
            if (!isEmpty(startDate) && !isEmpty(endDate)) {
                var startTime = new Date(startDate);
                var endTime = new Date(endDate);
                if (startTime.getTime() > endTime.getTime()) {
                    $('#msgStartDate').html("<spring:message code="SH_E100048"/>")
                    flag = false;
                }
            }else{
                flag = false;
            }
            return flag;
        }

        $('#btnExportExcel').click(function () {
            if (validateSearch()) {
                $('#kpi_form_search').attr('action', 'export/kpi-daily');
                $('#kpi_form_search').submit();
                $('#kpi_form_search').attr('action', 'kpi-daily');
            }
        });

        $('#btnSearchKpiDay').click(function () {
            if (validateSearch()) {
                $('#kpi_form_search').submit();
            }
        });
    });
</script>
