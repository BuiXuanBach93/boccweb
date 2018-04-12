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
    <li><a href="<%= request.getContextPath()%>/backend/torino-csv">基幹システム 取り込み用 CSV 出力</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" style="margin-top: 20px">
    <div class="search-posts">
        <div class="row"><div style="color: red" class="col-md-offset-1" id="errorMsg">
        </div> </div>
        <form:form class="" id="torino_form_search" role="form" modelAttribute="torinoFormSearch" action="torino-csv" method="GET" style="margin-top: 20px">
            <div class="row">
                <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                    指定年月
                </div>
                <div class="col-md-3">
                    <div class="input-group date">
                        <div class="input-group-addon">
                            <i class="fa fa-calendar"></i>
                        </div>
                        <form:input type="text" path="periodDate" class="form-control pull-right" id="exportCsvDate" placeholder="YYYY/MM"/>
                    </div>
                </div>
                <div class="col-md-3">
                    <input type="button" class="btn btn-primary" id="exportTorinoBtn" value="CSV 出力">
                    </input>
                </div>
            </div>
        </form:form>
    </div>

</div>

<script>
    jQuery(document).ready(function ($) {
        $("#exportCsvDate").datepicker({
            changeMonth: true,
            changeYear: true,
            dateFormat: 'yy/mm',

            onClose: function() {
                var iMonth = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
                var iYear = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
                $(this).datepicker('setDate', new Date(iYear, iMonth, 1));
            },

            beforeShow: function() {
                if ((selDate = $(this).val()).length > 0)
                {
                    iYear = selDate.substring(selDate.length - 4, selDate.length);
                    iMonth = jQuery.inArray(selDate.substring(0, selDate.length - 5), $(this).datepicker('option', 'monthNames'));
                    $(this).datepicker('option', 'defaultDate', new Date(iYear, iMonth, 1));
                    $(this).datepicker('setDate', new Date(iYear, iMonth, 1));
                }
            }
        }).keydown(false);

        $('#exportTorinoBtn').click(function () {
            if (!isEmpty($("#exportCsvDate").val())) {
                $('#torino_form_search').attr('action', 'export/torino-csv');
                $('#torino_form_search').submit();
                $('#torino_form_search').attr('action', 'torino-csv');
            }else {
                $('#errorMsg').html("月を選択してください。")
                $('#exportCsvDate').focus();
            }
        });
    });
</script>
