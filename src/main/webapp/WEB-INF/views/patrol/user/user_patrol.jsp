<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 3/29/2017
  Time: 5:23 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/patrol/handle-user">サイト パトロール ( ユーザー )</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<section class="home-page">
    <div class="search-posts">
        <form:form id="user_patrol_search_form" method="GET" modelAttribute="userPatrolRequest" action="${pageContext.request.contextPath}/backend/patrol/handle-user/search">
            <div class="row">
                <div class="form-group user-checkbox">
                    <div class="col-md-2">
                        <label style="margin-top: 10px; margin-left: 30px;">ステータス</label>
                    </div>

                    <div class="checkbox-item checkbox inline-block">
                        <label style="padding-left: 0px">
                            <form:radiobutton checked="true" id="processStatusNotYet" onclick="isCheckedCtrlStt(0)" path="processStatus" value="UNCENSORED"/>未対応
                        </label>
                    </div>

                    <div class="checkbox-item checkbox inline-block">
                        <label>
                            <form:radiobutton id="processStatusDoing" path="processStatus" onclick="isCheckedCtrlStt(1)" value="CENSORING"/>対応中
                        </label>
                    </div>

                    <div class="checkbox-item checkbox inline-block">
                        <label>
                            <form:radiobutton id="processStatusDone" path="processStatus" onclick="isCheckedCtrlStt(2)" value="CENSORED"/>対応完了
                        </label>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px; line-height: 34px">最終更新日時</label>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="updatedAtFrom" id="updatedAtFrom" class="form-control pull-right datepicker" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 10px;">
                                <span id="updatedAtFromErr"></span>
                            </div>
                        </div>
                        <span class="add-tilde"></span>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="updatedAtTo" id="updatedAtTo" class="form-control pull-right datepicker" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 10px;">
                                <span id = "updatedAtToErr"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2"></div>
                <div class="col-md-6"><span id="errorMsg" style="color: red; margin-bottom: 5px">${errorMsg}</span></div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px; line-height: 34px">オペレーター名</label>
                    </div>
                    <div class="col-md-3">
                        <form:input path="patrolAdminNames" cssClass="form-control"/>
                    </div>
                </div>
            </div>

            <div class="row" id="usRepairCplDate" style="margin-top: 10px; display: none">
                <div class="form-group">
                    <div class="col-md-2">
                        <label id ="datePatrol" style="margin-left: 30px; line-height: 34px;">対応中変更日時</label>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="censoredFrom" id="censoredFrom" class="form-control pull-right datepicker" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 10px;">
                                <span id = "censoredFromErr"></span>
                            </div>
                        </div>
                        <span class="add-tilde"></span>
                    </div>

                    <div class="col-md-3">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="censoredTo" id="censoredTo" class="form-control pull-right datepicker" placeholder="YYYY/MM/DD"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-left: 10px;">
                                <span id = "censoredToErr"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-2"></div>
                <div class="col-md-6"><span id="errorMsg2" style="color: red; margin-bottom: 5px">${errorMsg2}</span></div>
            </div>

            <div class="row">
                <div class="form-group">
                    <div class="col-md-2">
                        <label style="margin-left: 30px; line-height: 34px; margin-top: 3px">絞り込み</label>
                    </div>
                    <div class="col-md-6">
                        <div class="checkbox-item checkbox inline-block">
                            <label style="padding-left: 0px">
                                <form:checkbox id="pending" path="pending" value="PENDING"/>保留中
                            </label>
                        </div>

                        <%--<div class="checkbox-item checkbox inline-block">
                            <label>
                                <form:checkbox id="ng" path="ng" value="SUSPENDED"/>NG
                            </label>
                        </div>--%>
                        <div class="checkbox-item checkbox inline-block" id="updatedCheckbox">
                            <label>
                                <form:checkbox id="userUpdatedAt" path="userUpdatedAt" value="UPDATED"/>修正完了
                            </label>
                        </div>
                        <form:input type="number" hidden="true" path="pageIndex" value="${curPage}"></form:input>
                    </div>
                    <div class="col-md-2">
                        <button id="btn-search" type="button" onclick="submitBtnSearch(0)" class="btn btn-primary" style="width: 80px;">
                            検索
                        </button>
                    </div>
                </div>
            </div>
        </form:form>
    </div>

    <div class="" id="paginationUserPatrol">
        <div style="float: right;margin-right: 15px;">
            <jsp:include page="../../common/page.jsp"/>
        </div>
        <div>
            <table class=" table table-striped table-hover" id="tableBody">
                <thead style="background-color: #FFFFFF;">
                <tr>
                    <th class="textCenter">プロフィール画像</th>
                    <th class="textCenter">投稿者ニックネーム</th>
                    <th class="textCenter">ステータス</th>
                    <th class="textCenter">最終更新日時</th>
                    <th class="textCenter" id="completedSttCol">修正完了</th>
                    <th class="textCenter" id="pendingSttCol">保留 ステータス</th>
                    <%--<th class="textCenter">オペレータID</th>--%>
                    <th class="textCenter">オペレーター</th>
                </tr>
                </thead>
            </table>

        </div>
    </div>
</section>

<script src="${pageContext.request.contextPath}/resources/js/user_patrol.js"></script>

<script>
    function isCheckedCtrlStt(ctrlStt) {
        if (ctrlStt == 0) {
            $('#usRepairCplDate').hide().css("visibility", "hide");
            $('#censoredFrom').datepicker('setDate', null);
            $('#censoredTo').datepicker('setDate', null);

        } else {
            $('#usRepairCplDate').show().css("visibility", "visible");
        }
    }

    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }
</script>