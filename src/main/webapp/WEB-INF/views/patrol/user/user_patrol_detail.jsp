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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="../../common/userAvatarModel.jsp"/>
<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/patrol/handle-user">サイト パトロール ( ユーザー )</a></li>
    <c:choose>
        <c:when test="${isPatrolSequent == 1}">
            <li><a>${sequentNumber} 件目 / ${maxSequentNumber} 件中</a></li>
        </c:when>
        <c:otherwise>
            <li><a href="#">${user.userNickName} (${user.userId})</a></li>
        </c:otherwise>
    </c:choose>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<script src="${pageContext.request.contextPath}/resources/js/user_patrol.js"></script>
<div class="box box-default" style="margin-top: 20px">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="col-offset-md-5">
                <label style="color: red">${errorMsg}</label>
            </div>
        </div>
        <div class="form-group" style="margin-bottom: 5px">
            <div class="row" style="font-size: 20px;">
                <div class="title-text col-md-3 text-center">
                    <span>投稿者</span>
                </div>
            </div>
        </div>
        <div class="form-group">
            <div class="row">
                <div class="col-md-3">
                    <div class="box" style="margin-bottom: 0px; border-top: none">
                        <div class="box-body" style="border: 2px solid #d2d6de !important;">
                            <div class="hidden" id="userPatrolId">${user.userId}</div>
                            <input id="userCurrentPatrolNumber" name="currentPatrolNumber" type="hidden" value="${sequentNumber}">
                            <input id="userMaxSequentNumber" name="maxSequentNumber" type="hidden" value="${maxSequentNumber}">
                            <input id="isUserPatrolSequent" name="isPatrolSequent" type="hidden" value="${isPatrolSequent}">
                            <div class="divImgUserProfile">
                                <img class="profile-user-img img-responsive img-circle" data-toggle="modal" data-target="#userAvatarModel"
                                     src="${user.userAvatarPath}" alt="User profile picture"
                                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                            </div>
                            <h3 class="profile-username text-center" style="color: #5F9EA0;">${user.userNickName}</h3>

                        </div>
                        <!-- /.box-body -->
                    </div>
                    <div>
                        <div style="text-align: center; margin-top: 20px">
                            投稿数: ${user.totalPost}
                        </div>
                    </div>
                </div>
                <%--<div class="col-md-1" style=""></div>--%>
                <div class="col-md-9" style="font-size: 15px; float: right;">
                    <table class="table borderless " style="table-layout:fixed; margin-left: 40px;">
                        <%--<tbody>--%>
                        <tr>
                            <td class="title-text"><span>住所 ：</span>${user.province.areaName}&nbsp;${user.district.areaName}</td>
                        </tr>
                        <tr>
                            <c:choose>
                            <c:when test="${user.age > '0'}">
                                <td class="title-text"><span>年齢 : </span>${user.age}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="title-text"><span>年齢 : </span></td>
                            </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <td class="title-text"><span>職業 : </span>${user.career}</td>
                        </tr>
                            <tr>
                                <td class="title-text">
                                    <div style="    word-wrap: break-word;
    margin-right: 20px;">自己紹介 : ${user.userDescr}</div>
                                </td>
                            </tr>
                        <%--</tbody>--%>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-9">
                    <a class="btn btn-primary patrol-button" id="userOkBtn">
                        OK
                    </a>
                    <a class="btn btn-primary patrol-button" id="userPendingBtn" style="margin-left: 50px">
                        保留
                    </a>
                    <a class="btn btn-primary patrol-button" id="userNgBtn" style="margin-left: 50px">
                        NG
                    </a>
                    <a class="btn btn-primary patrol-button" id="userAllMemoBtn" style="margin-left: 50px">
                        対応履歴
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="userOkeConfirmModal" data-backdrop="static" data-keyboard="false" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
        </div>
        <div class="modal-body"> 対応完了としますか？</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary" data-dismiss="modal" data-confirm="Ok" id="userOkeConfirmBtn">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="userNgConfModal" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content modal-confirm patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
        </div>
        <div class="modal-body" id="userBodyConfModal">更新しますか</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary" id="save-user-ng" data-dismiss="modal" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="userConflictProcess" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content modal-confirm patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-warning-sign"></i>  警告する</h4>
        </div>
        <div class="modal-body">このユーザーは対応されました！</div>
        <div class="modal-footer">
            <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="userMemoConfModal" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content modal-confirm patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
        </div>
        <div class="modal-body">更新しますか</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary" id="save-user-memo" data-dismiss="modal" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" data-backdrop="static" data-keyboard="false" id="chooseOneReasonUserModal" role="document"
     style="margin: 30px auto; background: none; z-index: 1900000;width: 100%; height: 100%">
    <div class="modal-content patrol-popup-content" style="width: 300px; margin: auto;">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-warning-sign"></i>  警告する</h4>
        </div>
        <div class="modal-body">少なくとも１つの理由を選択してください！</div>
        <div class="modal-footer">
            <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
        </div>
    </div>
</div>
<div id="patrolUser">
<div id="allMemoOfUser" class="modal fade" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content" style="width: 650px">
            <div class="box box-primary direct-chat direct-chat-primary" style="border-top-color:#3c8dbc">
                <div class="box-header with-border" style="position: static; background-color: #3c8dbc">
                    <h3 class="box-title"><b>【対応履歴 · メモ】</b></h3>
                </div>
                <div class="box-body" style="min-height: 290px">
                    <div class="direct-chat-messages" style="min-height: 420px">
                        <div class="row" style="margin-right:10px; margin-left: 5px; margin-top: 10px">
                            <div class="box box-primary box-solid direct-chat direct-chat-primary">
                                <div class="box-header" style="padding-bottom: 2px; padding-top: 2px;">
                                    <h6>▼ 対応履歴 ※ ユーザー に は 通知 さ れ ませ ん</h6>
                                </div>
                                <div class="box-body" style="min-height: 270px">
                                    <div class="direct-chat-messages">
                                        <div class="direct-chat-msg left" id="memoUserHis">
                                        </div>
                                    </div>
                                </div>
                                <div class="box-footer">
                                    <form>
                                        <div class="input-group">
                                            <%--<input type="text" name="usMemCont" placeholder="Type Message ..." class="form-control us-mem-content" id="usMemCont">--%>
                                            <textarea class="form-control" id="userMemoCont"
                                                      style="width:590px; height:35px; resize: vertical"></textarea>
                                        </div>
                                        <div class="error" style="color: red; margin-left: 20px;">
                                            <span id = "userMemContErr"></span>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="box-footer">
                    <form>
                        <div class="row">
                            <div class="col-md-5"></div>
                            <button type="button" class="btn btn-primary" onclick="showUserMemoConfirmModal()">更新</button>
                            <button type="button" class="btn btn-danger" data-dismiss="modal" style="margin-left: 10px">キャンセル</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</div>

<div id="ngPopupUser" class="modal fade" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content popup-patrol-content">
            <div class="box box-primary direct-chat direct-chat-primary">
                <div class="box-header with-border" style="position: static; background-color: #3c8dbc">
                    <h3 class="box-title popup-patrol-title" id="userNgPopTitle"></h3>
                </div>
                <div class="box-body" style="min-height: 290px">
                    <div class="direct-chat-messages popup-patrol-messages" style="min-height: 360px;">
                        <div class="row" style="margin-right:10px; margin-left: 5px; margin-bottom: 10px">
                            <div class="col-md-6 popup-patrol-list-choose">
                                <label style="font-size: 18px">違反理由</label>
                                <div class="form-group">
                                    <div class="popup-patrol-text-choose">
                                        <label><input type="checkbox" name="notSuitable" id="userNotSuitable" value="0">&nbsp;&nbsp;画像・商品名が不適切である<br></label>
                                        <label><input type="checkbox" name="sensitiveInf" id="userSensitiveInf" value="1">&nbsp;&nbsp;個人を特定できる内容が含まれている<br></label>
                                        <label><input type="checkbox" name="slander" id="userSlander" value="2">&nbsp;&nbsp;誹謗中傷の内容が含まれている<br></label>
                                        <label><input type="checkbox" name="cheat" id="userCheat" value="3">&nbsp;&nbsp;詐欺の疑いがある<br></label>
                                        <label><input type="checkbox" name="other" id="userOtherReason" onclick="isCheckOtherReasonForUser()" value="4">&nbsp;&nbsp;その他利用規約に反する内容がある<br></label>
                                        <textarea class="form-control us-mem-content" name="difReason" disabled="true" id="userDifReason"
                                                  style="height:70px; resize: vertical; margin-top: 10px" placeholder="その他 . . ."></textarea>
                                        <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 5px; height: 20px;">
                                            <span id = "userDifReasonErr"></span>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-3"></div>
                                        <button type="submit" class="btn btn-primary" data-user-patrol-type="0" onclick="showUserConfirmModal()">更新</button>
                                        <button type="button" class="btn btn-danger" data-dismiss="modal" style="margin-left: 10px">キャンセル</button>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6 popup-patrol-direct-chat">
                                <div class="box box-primary box-solid direct-chat direct-chat-primary">
                                    <div class="box-header" style="padding-bottom: 2px; padding-top: 2px;">
                                        <h6>▼対応履歴 ※ ユーザー に は 通知 さ れ ませ ん </h6>
                                    </div>
                                    <div class="box-body">
                                        <div class="direct-chat-messages" id="userPatrolMemoId" style="max-height: 193px">
                                        </div>
                                    </div>
                                    <div class="box-footer">
                                        <form>
                                            <div class="input-group">
                                                <textarea class="form-control us-mem-content" id="userNGMemoCont"
                                                          style="width:250px; height:70px; resize: vertical" placeholder="新しいメモ . . ."></textarea>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="box-footer post-patrol-text-footer">
                    <p id="userFooterWarningTxt"></p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function validateOtherReason() {
        if($("#userOtherReason").is(':checked') && isEmpty($("#userDifReason").val())) {
            $('#userDifReasonErr').html("<spring:message code="SH_E100092"/>");
            return false;
        }
        if(!($("#userOtherReason").is(':checked')) && !($("#userNotSuitable").is(':checked')) && !($("#userSensitiveInf").is(':checked')) && !($("#userSlander").is(':checked'))
            && !($("#userCheat").is(':checked')) && isEmpty($("#userNGMemoCont").val())) {
            return false;
        }
        return true;
    }

    function notCheckedReasons() {
        if(!($("#userOtherReason").is(':checked')) && !($("#userNotSuitable").is(':checked')) && !($("#userSensitiveInf").is(':checked')) && !($("#userSlander").is(':checked')) && !($("#userCheat").is(':checked'))) {
            return false;
        }
        return true;
    }

    function isCheckOtherReasonForUser() {
        if($("#userOtherReason").is(':checked')) {
            $('#userDifReason').prop('disabled', false);
        } else {
            $('#userDifReason').prop('disabled', true);
            $('#userDifReasonErr').html("");
        }
    }

    function validatePostMemo() {
        if (isEmpty($('#userMemoCont').val())) {
            $('#userMemContErr').html("<spring:message code="SH_E100093"/>");
            return false;
        }
        return true;
    }

    function showUserMemoConfirmModal() {
        if (validatePostMemo()) {
            $('#userMemContErr').html("");
            $("#userMemoConfModal").modal("show");
        }
    }

    function showUserConfirmModal() {
        if (validateOtherReason()) {
            $('#userDifReasonErr').html("");
            if (!notCheckedReasons() && $('#ngPopupUser').attr('data-user-patrol-type') == 'SUSPENDED') {
                $("#chooseOneReasonUserModal").modal("show");
            }
            else {
                $("#userNgConfModal").modal("show");
            }
        }
    }

    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }
</script>

<style>
    .borderless td, .borderless th {
        border: none !important;
    }
</style>