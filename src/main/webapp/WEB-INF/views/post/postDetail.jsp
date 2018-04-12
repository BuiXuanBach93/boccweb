<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/posts?page=0">投稿一覧 · 検索</a></li>
    <li><a href="#">投稿名 : ${fn:escapeXml(postDetail.postName)}&nbsp;投稿 ID : ${postDetail.postId}</a></li>
</ul>
<jsp:include page="../common/userAvatarModel.jsp"/>

<div class="home-page" style="margin-top: 20px">
    <div class="box box-default" style="margin-top: 20px">
        <div class="box-body" style="margin-top: 30px">
            <div class="form-group margin-bottom">
                <div class="title-text text-center" style="font-size: 20px; color: red;"><c:out value="${errorMsg}"/> </div>
                <div class="row" style="font-size: 20px;">
                    <div class="title-text col-md-3 text-center">
                        <span>投稿者</span>
                    </div>
                    <div class="title-text col-md-5">
                        <span>投稿名 : </span>${fn:escapeXml(postDetail.postName)}
                    </div>
                    <div class="title-text col-md-3">
                        <span>投稿 ID : </span>${postDetail.postId}
                    </div>
                    <input id="postPatrolId" name="postPatrolId" type="hidden" value="${postDetail.postId}">
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-3">
                        <div class="box" style="border-top: none;">
                            <div class="box-body" style="border: 2px solid #d2d6de !important;">
                                <div class="divImgUserProfile">
                                <img class="profile-user-img img-responsive img-circle" data-toggle="modal" data-target="#userAvatarModel"
                                     src="${postOwnerAvatarPath}"
                                     alt="User profile picture" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';">
                                </div>
                                <h3 class="profile-username text-center"
                                    style="color: #5F9EA0; word-wrap: break-word;">${postDetail.shmUser.firstName} ${postDetail.shmUser.lastName}</h3>

                                <ul class="list-group list-group-unbordered">
                                    <li class="list-group-item" style="color: #6495ED">
                                        <div style="display: inline-block;">
                                            <span>
                                                <p class="glyphicon glyphicon-home" style="width: 20px;display: inline-block;"></p> ${postDetail.shmUser.address.fullAreaName}
                                            </span>
                                            <span>
                                                &nbsp;
                                                <c:choose>
                                                    <c:when test="${postDetail.shmUser.gender == 'MALE'}">
                                                        ( 男性 )
                                                    </c:when>
                                                    <c:when test="${postDetail.shmUser.gender == 'FEMALE'}">
                                                        ( 女性 )
                                                    </c:when>
                                                    <c:otherwise>

                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </li>
                                    <li class="list-group-item">
                                        <i class="fa fa-user"></i> <span style="margin-left: 10px"> ${postDetail.shmUser.nickName}</span>
                                    </li>
                                    <li class="list-group-item">
                                        <i class="fa fa-envelope-o"></i> <span style="margin-left: 10px"> ${postDetail.shmUser.email}</span>
                                    </li>
                                    <li class="list-group-item">
                                        生年月日 :&nbsp;
                                        <javatime:format value="${postDetail.shmUser.dateOfBirth}" pattern="Y年MM月dd日" var="dateOfBirth"/><c:out value="${dateOfBirth}"/>
                                    </li>
                                </ul>
                            </div>
                            <!-- /.box-body -->
                        </div>
                        <div>
                            <div style="text-align: center; margin-bottom: 16px">
                                <b>投稿数 : ${countPost}</b>
                            </div>
                            <div>
                                <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${postDetail.shmUser.id}" class="btn btn-primary btn-block"><b>ユーザー詳細へ</b></a>
                            </div>
                        </div>
                    </div>
                    <%--<div class="col-md-1" style=""></div>--%>
                    <div class="col-md-9" style="font-size: 15px; float: right">
                        <table class="table borderless " style="table-layout:fixed; margin-bottom: 0px;">
                            <%--<tbody>--%>
                            <tr>
                                <c:forEach var="file" items="${postDetail.postOriginalImagePaths}" varStatus="loop">
                                    <td>
                                        <a href="#" class="showImageClick" data-src-link="${file}"
                                           data-link-index="${loop.index}"
                                           data-list-link="${postDetail.postOriginalImagePaths}"
                                           data-text-1="${fn:escapeXml(postDetail.postName)}"
                                           data-text-2="${postDetail.shmUser.nickName}">
                                            <img class="image-post profile-user-img img-responsive img-rounded"
                                                 src="${file}"
                                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';"
                                                 width="100" height="100" style="border: none">
                                        </a>
                                    </td>
                                </c:forEach>
                            </tr>
                            <tr>
                                <td colspan="5" class="title-text"><span>投稿内容説明: ${fn:escapeXml(postDetail.postDescription)}</span></td>
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
                                <td class="title-text"><span>価格 : </span>
                                    <fmt:formatNumber pattern="#,##0" value="${postDetail.postPrice}"/>円
                                </td>
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
                                <td class="title-text minwidth150"><span>新規投稿日時 : </span>
                                    <javatime:format value="${postDetail.createdAt}" pattern="Y年MM月dd日" var="createdAt"/>
                                    <c:out value="${createdAt}"/>
                                </td>
                            </tr>

                            <tr>
                                <td class="title-text minwidth150"><span>投稿更新日時 : </span>
                                    <javatime:format value="${postDetail.userUpdateAt}" pattern="Y年MM月dd日" var="updatedAt"/>
                                    <c:out value="${updatedAt}"/>
                                </td>
                            </tr>

                            <tr>
                                <td class="title-text"><span>投稿停止 フラグ :
                                    <c:set var="postCtrlStatus">${postDetail.postCtrlStatus}</c:set>
                                <c:choose>
                                    <c:when test="${postCtrlStatus == 'SUSPENDED'}">
                                        <c:out value="停止中"/>
                                    </c:when>
                                </c:choose>
                            </span></td>
                            </tr>

                            <tr>
                                <td class="title-text"><span>投稿削除 フラグ :
                                <c:set var="postSellStatus">${postDetail.postSellStatus}</c:set>
                            <c:choose>
                                <c:when test="${postSellStatus == 'DELETED'}">
                                    <c:out value="削除済"/>
                                </c:when>
                            </c:choose>
                            </span></td>
                            </tr>

                            <%--</tbody>--%>
                        </table>
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


<style>
    .borderless td, .borderless th {
        border: none !important;
    }
</style>
<script>
    var ctx = "${pageContext.request.contextPath}";
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