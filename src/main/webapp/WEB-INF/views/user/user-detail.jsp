<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 4/4/2017
  Time: 4:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script>
    var ctx = "${pageContext.request.contextPath}";
</script>

<jsp:include page="../common/userAvatarModel.jsp"/>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/users?page=0">ユーザー検索</a></li>
    <li><a href="#">ユーザー詳細</a></li>
</ul>

<div class="box box-default" style="margin-top: 20px">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="row">
                <label style="color: red; margin-left: 50px;">${errorMsg}</label>
            </div>
            <div class="row">
                <div class="col-md-3">
                    <div class="box" style="border-top: none">
                        <div class="box-body" style="border: 2px solid #d2d6de !important;">
                            <div class="divImgUserProfile">
                            <img class="profile-user-img img-responsive img-circle" data-toggle="modal" data-target="#userAvatarModel"
                                 src="${userAvatarPath}"
                                 alt="User profile picture" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';" style="height: 100px;"/>
                            </div>

                            <h3 class="profile-username text-center"
                                style="color: #5F9EA0; word-wrap: break-word;">${user.firstName} ${user.lastName}</h3>

                            <ul class="list-group list-group-unbordered">
                                <li class="list-group-item" style="color: #6495ED">
                                    <div style="display: inline-block;">
                                        <span><p class="glyphicon glyphicon-home" style="width: 20px;display: inline-block;"></p> ${user.address.fullAreaName }
                                        <c:choose>
                                            <c:when test="${user.gender == 'FEMALE'}">
                                                ( 女性 )
                                            </c:when>
                                            <c:when test="${user.gender == 'MALE'}">
                                                ( 男性 )
                                            </c:when>
                                            <c:otherwise>

                                            </c:otherwise>
                                        </c:choose>
                                        </span>
                                    </div>
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-user"></i> <span>&nbsp; ${user.nickName}</span>
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-envelope-o"></i> <span>&nbsp; ${user.email}</span>
                                </li>
                                <li class="list-group-item">
                                    生年月日 :&nbsp;
                                    <javatime:format value="${user.dateOfBirth}" pattern="Y年MM月dd日" var="dateOfBirth"/><c:out value="${dateOfBirth}"/>
                                </li>
                            </ul>
                            <input id="userDetailId" name="userDetailId" type="hidden" value="${user.id}">
                        </div>
                        <a href="${pageContext.request.contextPath}/backend/patrol/handle-user/${user.id}" class="btn btn-primary btn-block" style="margin-top: 20px"><b>ステータス 変更</b></a>
                        <!-- /.box-body -->
                    </div>
                </div>
                <%--<div class="col-md-1" style=""></div>--%>
                <div class="col-md-9" style="font-size: 15px; float: right">
                    <div class="box box-primary" style="border: 2px solid #d2d6de; height: 407px">
                        <ul>
                            <table class="table borderless " style="table-layout:fixed; margin-top: 5px;">
                                <%--<tbody>--%>
                                <tr>
                                    <td>
                                        <li>
                                            <div>住所 ：${user.address.fullAreaName}</div>
                                        </li>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <li style="width: auto">
                                            <div>電話番号 : ${user.phone}</div>
                                        </li>
                                    </td>
                                    <td>
                                        <li>
                                            <div> 職業 : ${user.career}</div>
                                        </li>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="title-text">
                                        <li>
                                            <div>自己紹介 :</div>
                                        </li>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="2">${user.description}</td>
                                </tr>
                                <tr>
                                    <td class="title-text">
                                        <li>
                                            <div>法人ID : ${fn:substring(user.bsid, 0, 6)}</div>
                                        </li>
                                    </td>
                                    <td>
                                        <li>
                                            <div>BSID : ${user.bsid}</div>
                                        </li>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="title-text">
                                        <li>
                                            <div>出品投稿数 : ${buyTotal} </div>
                                        </li>
                                    </td>
                                    <td>
                                        <li>
                                            <div>リクエスト数 : ${sellTotal}</div>
                                        </li>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="2" class="title-text">
                                        <li>
                                            <div>評価受け取り数 : ${reviewedTotal}</div>
                                        </li>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="title-text">
                                        <li>
                                            ユーザー 退会日 :
                                            <javatime:format value="${user.leftDate}" pattern="Y年MM月dd日" var="leftDate"/><c:out value="${leftDate}"/>
                                        </li>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="title-text">
                                        <div>ユーザー メモ ( 運営用 )</div>
                                    </td>
                                    <td>
                                    <button class="btn btn-primary btn-block get-user-memo" style="width: 120px;">メモ
                                    </button>
                                    </td>
                                </tr>

                            </table>
                        </ul>
                    </div>
                </div>

            </div>
        </div>

    </div>
</div>

<div id="memoModal" class="modal fade" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="box box-primary direct-chat direct-chat-primary">
                <div class="box-header with-border" style="position: static; background-color: #3c8dbc">
                    <h3 class="box-title" style="color: #FFFFFF">メモ</h3>
                    <div class="box-tools col-md-1 pull-right">
                        <button class="btn close" data-dismiss="modal" aria-label="Close"><i class="fa fa-times"></i>
                        </button>
                    </div>
                </div>
                <div class="box-body">
                    <div class="direct-chat-messages">
                        <div class="direct-chat-msg left">
                        </div>
                    </div>
                </div>
                <div class="box-footer">
                    <form>
                        <div class="input-group">
                            <%--<input type="text" name="usMemCont" placeholder="Type Message ..." class="form-control us-mem-content" id="usMemCont">--%>
                            <textarea class="form-control us-mem-content" name="usMemCont" id="usMemCont"
                                      style="width:523px; height:35px; resize: vertical"></textarea>
                            <span class="input-group-btn">
                                <button type="button" class="btn btn-primary btn-flat save-user-memo"
                                        data-userid="${user.id}">保存</button>
                            </span>
                        </div>
                    </form>
                </div>
                <div style="color: red; margin-left: 10px; padding-bottom: 10px;" id="errorUserMemo" class="form-group"></div>
            </div>
        </div>
    </div>
</div>
<style>
    .borderless td, .borderless th {
        border: none !important;
    }
</style>
<script>
    jQuery(document).ready(function ($) {
        var gender = '${user.gender}';
        if (!isEmpty(gender)) {
            $('#gender').html('(' + gender + ')');
        }
    });

    var contextPath = '${pageContext.request.contextPath}';
    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }
</script>
