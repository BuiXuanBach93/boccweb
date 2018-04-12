<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 4/13/2017
  Time: 1:54 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="<c:url value="/resources/css/styles.css" />" rel="stylesheet">

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/post-patrol?page=0">サイト パトロール(投稿)</a></li>
    <c:choose>
        <c:when test="${isPatrolSequent == 1}">
            <li><a>${sequentNumber} 件目 / ${maxSequentNumber} 件中</a></li>
        </c:when>
        <c:otherwise>
            <li><a>投稿 : ${countPost}&nbsp; <span style="font-size: 22px">ID</span> : ${postDetail.postId}</a></li>
        </c:otherwise>
    </c:choose>
</ul>
<jsp:include page="../../common/userAvatarModel.jsp"/>
<script>
    var ctx = "${pageContext.request.contextPath}";
</script>

<%--<c:forEach var="post" items="${postDetail}" >--%>
<div class="box box-default" style="margin-top: 20px">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group margin-bottom">
            <div class="row" style="font-size: 20px;">
                <div class="title-text col-md-3 text-center" style="font-size: 20px; color: red;">${errorMsg}</div>
            </div>
        </div>
        <div class="form-group margin-bottom">
            <div class="row" style="font-size: 20px;">
                <div class="title-text col-md-3 text-center">
                    <span>投稿者</span>
                </div>
                <div class="title-text col-md-4">
                    <span>投稿名 : </span>${fn:escapeXml(postDetail.postName)}
                </div>
                <div class="title-text col-md-3">
                    <span>投稿<span style="font-size: 20px">ID</span> : </span>${postDetail.postId}
                </div>
                <input id="postPatrolId" name="postPatrolId" type="hidden" value="${postDetail.postId}">
                <input id="isPatrolSequent" name="isPatrolSequent" type="hidden" value="${isPatrolSequent}">
                <input id="currentPatrolNumber" name="currentPatrolNumber" type="hidden" value="${sequentNumber}">
                <input id="maxSequentNumber" name="maxSequentNumber" type="hidden" value="${maxSequentNumber}">
            </div>
        </div>
        <div class="form-group">
            <div class="row">
                <div class="col-md-3">
                    <div class="box" style="margin-bottom: 0px; border-top: none">
                        <div class="box-body" style="border: 2px solid #d2d6de !important;">
                            <div class="divImgUserProfile">
                                <img class="profile-user-img img-responsive img-circle" data-toggle="modal" data-target="#userAvatarModel"
                                     src="${postOwnerAvatarPath}"
                                     alt="User profile picture"
                                     onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                            </div>
                            <h3 class="profile-username text-center" style="color: #5F9EA0; word-wrap: break-word;">${postDetail.shmUser.nickName}</h3>

                            <ul class="listgit -group list-group-unbordered">
                                <li class="list-group-item" style="color: #6495ED">
                                    <i class="fa fa-home"></i> &nbsp;<span> ${postDetail.shmUser.address.fullAreaName} <c:choose>
                                    <c:when test="${postDetail.shmUser.gender == 'MALE'}">
                                        ( 男性 )
                                    </c:when>
                                    <c:when test="${postDetail.shmUser.gender == 'FEMALE'}">
                                        ( 女性 )
                                    </c:when>
                                    <c:otherwise>

                                    </c:otherwise>
                                </c:choose></span>
                                </li>
                            </ul>

                            <c:choose>
                                <c:when test="${postDetail.postReportTimes == 0}">
                                    &nbsp;
                                </c:when>
                                <c:otherwise>

                                    <div class="" style="background-color: #e26d52; margin: 10%">
                                            <%--<span class="info-box-icon bg-red"><i class="glyphicon glyphicon-bell"></i></span>--%>

                                        <span class="info-box-number" style="margin-left: 10%">通報あり</span>
                                        <span class="info-box-number" style="margin-left: 10%">通報回数 : ${postDetail.postReportTimes} </span>

                                        <!-- /.info-box-content -->
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <!-- /.box-body -->
                    </div>
                    <div>
                        <div style="text-align: center; margin-bottom: 20px; margin-top: 10px">
                            投稿数 : ${countPost}
                        </div>
                        <div>
                            <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${postDetail.shmUser.id}" class="btn btn-primary btn-block"><b>ユーザー詳細へ</b></a>
                        </div>
                    </div>
                </div>
                <%--<div class="col-md-1" style=""></div>--%>
                <div class="col-md-9" style="font-size: 15px; float: right">
                    <table class="table borderless " style="table-layout:fixed;">
                        <%--<tbody>--%>
                        <tr>
                            <c:forEach var="file" items="${postDetail.postThumbnailImagePaths}" varStatus="loop">
                                <td>
                                    <a href="#" class="showImageClick" data-src-link="${file}"
                                       data-link-index="${loop.index}"
                                       data-list-link="${postDetail.postOriginalImagePaths}"
                                       data-text-1="${fn:escapeXml(postDetail.postName)}"
                                       data-text-2="${postDetail.shmUser.nickName}">
                                        <img class="image-post profile-user-img img-responsive img-rounded"
                                             src="${file}"
                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';"
                                             width="100" height="100"  style="border: none">
                                    </a>
                                </td>
                            </c:forEach>
                        </tr>
                        <tr>
                            <td colspan="5" class="title-text"><span>投稿内容説明 : ${fn:escapeXml(postDetail.postDescription)}</span></td>
                        </tr>
                    </table>
                    <table class="table borderless " style="table-layout:fixed; margin-top: 5px;">
                        <%--<tbody>--%>
                        <tr>
                            <td class="title-text">
                                <span>カテゴリ :</span>
                                <span>&nbsp;${postDetail.postCategoryParent.categoryName}&nbsp;</span>
                                <span class="category-symbol">&nbsp;${postDetail.postCategory.categoryName}&nbsp;</span>
                            </td>
                        </tr>
                        <tr>
                            <td class="title-text"><span>価格 : </span> <fmt:formatNumber pattern="#,##0" value="${postDetail.postPrice}"/>円</td>
                        </tr>
                        <tr>
                            <td class="title-text"><span>ハッシュ タグ : </span>${postDetail.postHashTagVal}</td>
                        </tr>
                        <tr>
                            <td class="title-text"><span>お気に入り 登録数 : </span>${postDetail.postLikeTimes}</td>
                        </tr>

                        <tr>
                            <td class="title-text"><span>違反受取回数 : </span>${postDetail.postReportTimes}</td>
                        </tr>

                        <tr>
                            <td class="title-text"><span>受け渡し エリア : </span>${postDetail.postAddrTxt}</td>
                        </tr>

                        <tr>
                            <td class="title-text">
                                <span>新規投稿日時 : </span><javatime:format value="${postDetail.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                <c:out value="${createdAt}"/>
                            </td>
                        </tr>

                        <tr>
                            <td class="title-text"><span>投稿更新日時 : </span><javatime:format value="${postDetail.userUpdateAt}" pattern="Y年MM月dd日 HH:mm" var="updateAt"/>
                                <c:out value="${updateAt}"/>
                            </td>
                        </tr>

                        <tr>
                            <td class="title-text"><span>投稿停止 フラグ : </span>
                                <c:choose>
                                    <c:when test="${postDetail.postCtrlStatus.toString() == 'SUSPENDED'}">
                                        停止中
                                    </c:when>
                                    <c:otherwise>
                                        &nbsp;
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>

                        <tr>
                            <td class="title-text"><span>投稿削除 フラグ : </span>
                                <c:choose>
                                    <c:when test="${postDetail.postSellStatus.toString() == 'DELETED'}">
                                        削除済
                                    </c:when>
                                    <c:otherwise>
                                        &nbsp;
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>

                        <%--</tbody>--%>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="col-md-3"></div>
                <div class="col-md-9">
                    <a data-post-patrol-id="${postDetail.postId}" class="btn btn-primary patrol-button" id="post-oke">
                        OK
                    </a>
                    <a class="btn btn-primary pending-pop patrol-button" style="margin-left: 50px;">
                        保留
                    </a>
                    <a class="btn btn-primary ng-pop patrol-button" style="margin-left: 50px;">
                        NG
                    </a>
                    <a class="btn btn-primary all-mem-pop patrol-button" style="margin-left: 50px;">
                        対応履歴 · メモ
                    </a>
                    <c:if test="${postDetail.postReportTimes > 0}">
                    <a class="btn btn-primary all-report-pop patrol-button" style="margin-left: 50px;">
                        違反通報履歴
                    </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="ngConfModal" data-backdrop="static" data-keyboard="false" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
        </div>
        <div class="modal-body" id="postBodyConfModal"> 更新を行いますか？</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary save-ng-memo" data-dismiss="modal" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="postOkeConfirmModal" data-backdrop="static" data-keyboard="false" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
        </div>
        <div class="modal-body"> 対応完了としますか？</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary" data-dismiss="modal" data-confirm="Ok" id="postOkeConfirmBtn">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="memoConfModal" data-backdrop="static" data-keyboard="false" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content modal-confirm patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
        </div>
        <div class="modal-body"> 更新しますか</div>
        <div class="modal-footer">
            <button type="submit" class="btn btn-primary save-post-memo" data-dismiss="modal" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" id="conflictProcess" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
    <div class="modal-content modal-confirm patrol-popup-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i>  警告する</h4>
        </div>
        <div class="modal-body">この投稿は対応されました！</div>
        <div class="modal-footer">
            <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-ok patrol-popup-modal" data-toggle="modal" data-backdrop="static" data-keyboard="false" id="chooseOneReasonModal" role="document"
     style="margin: 30px auto; background: none; z-index: 1900000;width: 100%;height: 100%">
    <div class="modal-content patrol-popup-content" style="width: 300px; margin: auto;">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i>  警告する</h4>
        </div>
        <div class="modal-body">少なくとも１つの理由を選択してください！</div>
        <div class="modal-footer">
            <button type="button" class="btn btn-primary" data-dismiss="modal">OK</button>
        </div>
    </div>
</div>
<div id="patrolPostDetail">
<div id="allMemPostOfUser" data-userid="${postDetail.shmUser.id}" data-backdrop="static" data-keyboard="false" class="modal fade" role="dialog">
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
                                <div class="box-body">
                                    <div class="direct-chat-messages" style="min-height: 270px">
                                        <div class="direct-chat-msg left" id="memoPostHis">
                                        </div>
                                    </div>
                                </div>
                                <div class="box-footer">
                                    <form>
                                        <div class="input-group">
                                            <%--<input type="text" name="usMemCont" placeholder="Type Message ..." class="form-control us-mem-content" id="usMemCont">--%>
                                            <textarea class="form-control" name="memoCont" id="memoCont"
                                                      style="width:580px; height:35px; resize: vertical; margin-left: 6px"></textarea>
                                        </div>
                                        <div class="error" style="color: red; margin-left: 20px;">
                                            <span id = "memContErr"></span>
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
                            <button type="button" class="btn btn-primary" onclick="showMemoConfirmModal()">更新</button>
                            <button type="button" class="btn btn-danger" data-dismiss="modal" style="margin-left: 10px">キャンセル</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</div>



<div id="allReportOfPost" data-userid="${postDetail.shmUser.id}" data-backdrop="static" data-keyboard="false" class="modal fade" role="dialog">
    <div class="modal-dialog" role="document">
            <div class="modal-content" style="width: 650px">
                <div class="box box-primary direct-chat direct-chat-primary" style="border-top-color:#3c8dbc">
                    <div class="box-header with-border" style="position: static; background-color: #3c8dbc">
                        <h3 class="box-title"><b>【違反通報履歴】</b></h3>
                    </div>
                    <div class="box-body" style="min-height: 290px">
                        <div class="direct-chat-messages" style="min-height: 420px">
                            <div class="row" style="margin-right:10px; margin-left: 5px; margin-top: 10px">
                                <div class="box box-primary box-solid direct-chat direct-chat-primary">
                                    <div class="box-header" style="padding-bottom: 2px; padding-top: 2px;">
                                        <h6>▼ 違反通報履歴</h6>
                                    </div>
                                    <div class="box-body">
                                        <div class="direct-chat-messages" style="min-height: 270px" id="reportPostList">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="box-footer">
                        <form>
                            <div class="row">
                                <div class="col-md-5"></div>
                                <button type="button" class="btn btn-primary" data-dismiss="modal" style="margin-left: 10px">キャンセル</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>


<div id="ngPopup" class="modal fade" data-backdrop="static" data-keyboard="false" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content popup-patrol-content">
            <div class="box box-primary direct-chat direct-chat-primary">
                <div class="box-header with-border" style="position: static; background-color: #3c8dbc">
                    <h3 class="box-title popup-patrol-title" id="ngPopTitle"></h3>
                </div>
                <div class="box-body" style="min-height: 290px">
                    <div class="direct-chat-messages popup-patrol-messages" style="min-height: 360px;">
                        <div class="row" style="margin-right:10px; margin-left: 5px; margin-bottom: 10px">
                            <div class="col-md-6 popup-patrol-list-choose">
                                <label style="font-size: 18px">違反理由</label>
                                <div class="form-group">
                                    <div class="popup-patrol-text-choose">
                                        <label><input type="checkbox" name="notSuitable" id="notSuitable" value="0">&nbsp;&nbsp;画像・商品名が不適切である<br></label>
                                        <label><input type="checkbox" name="sensitiveInf" id="sensitiveInf" value="1">&nbsp;&nbsp;個人を特定できる内容が含まれている<br></label>
                                        <label><input type="checkbox" name="slander" id="slander" value="2">&nbsp;&nbsp;誹謗中傷の内容が含まれている<br></label>
                                        <label><input type="checkbox" name="cheat" id="cheat" value="3">&nbsp;&nbsp;詐欺の疑いがある<br></label>
                                        <label><input type="checkbox" name="other" id="other" value="4" onclick="isCheckOtherReason()">&nbsp;&nbsp;その他利用規約に反する内容がある<br></label>
                                        <textarea class="form-control us-mem-content" name="difReason" id="difReason"
                                                  style="height:70px; resize: vertical; margin-top: 10px" disabled="true" placeholder="その他 . . ."></textarea>
                                        <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 5px; height: 20px;">
                                            <span id = "difReasonErr"></span>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-3"></div>
                                        <button type="submit" class="btn btn-primary" data-patrol-type="0" onclick="showConfirmModal()">更新</button>
                                        <a data-dismiss="modal" type="button" class="btn btn-danger" style="margin-left: 10px">キャンセル</a>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6 popup-patrol-direct-chat">
                                <div class="box box-primary box-solid direct-chat direct-chat-primary">
                                    <div class="box-header" style="padding-bottom: 2px; padding-top: 2px;">
                                        <h6>▼対応履歴 ※ ユーザー に は 通知 さ れ ませ ん </h6>
                                    </div>
                                    <div class="box-body">
                                        <div class="direct-chat-messages" id="postPatrolMemoId" style="max-height: 193px">
                                        </div>
                                    </div>
                                    <div class="box-footer">
                                        <form>
                                            <div class="input-group">
                                                <textarea class="form-control us-mem-content" name="postMemCont" id="postMemCont"
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
                    <p id="footerWarningTxt"></p>
                </div>
            </div>
        </div>
    </div>
</div>



<script>
    function validateOtherReason() {
        if($("#other").is(':checked') && isEmpty($("#difReason").val())) {
            $('#difReasonErr').html("<spring:message code="SH_E100092"/>");
            return false;
        }
        if(!($("#other").is(':checked')) && !($("#notSuitable").is(':checked')) && !($("#sensitiveInf").is(':checked')) && !($("#slander").is(':checked'))
            && !($("#cheat").is(':checked')) && isEmpty($("#postMemCont").val())) {
            return false;
        }
        return true;
    }

    function notCheckedReasons() {
        if(!($("#other").is(':checked')) && !($("#notSuitable").is(':checked')) && !($("#sensitiveInf").is(':checked')) && !($("#slander").is(':checked')) && !($("#cheat").is(':checked'))) {
            return false;
        }
        return true;
    }

    function isCheckOtherReason() {
        if($("#other").is(':checked')) {
            $('#difReason').prop('disabled', false);
        } else {
            $('#difReason').prop('disabled', true);
            $('#difReasonErr').html("");
        }
    }

    function validatePostMemo() {
        if (isEmpty($('#memoCont').val())) {
            $('#memContErr').html("<spring:message code="SH_E100093"/>");
            return false;
        }
        return true;
    }

    function showMemoConfirmModal() {
        if (validatePostMemo()) {
            $('#memContErr').html("");
            $("#memoConfModal").modal("show");
        }
    }

    function showConfirmModal() {
        if (validateOtherReason()) {
            $('#difReasonErr').html("");
            if (!notCheckedReasons() && $('#ngPopup').attr('data-patrol-type') == 2) {
                $("#chooseOneReasonModal").modal("show");
            } else {
                $("#ngConfModal").modal("show");
            }
        }
    }

    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }

    $('.all-report-pop').click(function () {
        $.ajax({
            type: "GET",
            url: ctx + "/backend/web/post/report-hostory",
            cache: false,
            data:'postId=' + $("#postPatrolId").val(),
            success: function(response){
                $('#reportPostList').html("");
                for (var i = 0; i < response.length; i++) {
                    fetchDataIntoReportedModal(response[i], '#allReportOfPost', '#reportPostList');
                }
                $("#allReportOfPost").modal("show");
            },
            error: function(e){
                console.log('Error while request..' + e);
            }
        });
    });

    function fetchDataIntoReportedModal(response, el_parent, el_child) {
        if (response) {
            var reportedRow = $(userReportRow);
            var content = response.userRprtCont;
            var nickName = response.shmUser.nickName;
            var createAt = response.createdAt;
            if(+ createAt[4] < 10){
                createAt[4] = '0' + createAt[4];
            }
            var title = response.reportTitle;
            var status = response.reportStatusStr;

            $(reportedRow).find(".nick-name").text(nickName);
            $(reportedRow).find(".report-time").text(createAt[0] + '/' + createAt[1] + '/' + createAt[2] + '   ' + createAt[3] + ':' + createAt[4]);
            $(reportedRow).find(".report-title").text(title);
            $(reportedRow).find(".report-status").text(status);
            $(reportedRow).find(".report-content").text(content);
            var el = el_parent + ' ' + el_child;
            $(el).append($(reportedRow).clone());
        }
    }


</script>

<style>
    .borderless td, .borderless th {
        border: none !important;
    }
</style>
