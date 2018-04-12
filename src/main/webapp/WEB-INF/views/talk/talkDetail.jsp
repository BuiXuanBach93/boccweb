<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link href="<c:url value="/resources/css/styles.css" />" rel="stylesheet">

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/posts">投稿内容</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-talk?id=${post.postId}">トークー覧</a></li>
    <li><a href="#">トーク 詳細</a></li>
</ul>
<jsp:include page="../common/userAvatarModel.jsp"/>

<style>
    .img_talk_image {
        width: 100%;
        height: auto;
        position: absolute;
        top: 50%;
        left: 50%;
        -webkit-transform: translate(-50%, -50%);
        -ms-transform: translate(-50%, -50%);
        transform: translate(-50%, -50%);
    }

    .div_talk_image_contain_owner{
        margin-bottom: 10px;
        margin-top: 10px;
        margin-left: 10px;
        width: 210px;
        height: 210px;
        overflow: hidden;
        position: relative;
        border: 1px solid #ddd;
    }
</style>

<div class="box box-default" style="margin-top: 20px">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="row">
                <div class="col-sm-12">
                    <table style="text-align: center;" id="example2" class="table table-bordered table-hover dataTable" role="grid" aria-describedby="example2_info">
                        <thead>
                        <tr role="row">
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿画像</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿ID<br>投稿名</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >大カテゴリ <br> 中カテゴリ</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >依頼</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿者名</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿登録日<br>最新更新日</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >価格</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >ステータス</th>
                            <th style="text-align: center; vertical-align: text-top;" class="" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >(通報)</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr role="row" class="odd">
                            <td>
                                <c:choose>
                                    <c:when test="${empty post.postThumbnailImagePaths}">
                                        <a href="#" class="showImageClick">
                                            <img src="${pageContext.request.contextPath}/resources/images/noimage.png" width="40" height="40">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                    <c:forEach var="file" items="${post.postThumbnailImagePaths}" varStatus="loop">
                                        <c:choose>
                                            <c:when test="${loop.index == '0'}">
                                                <a href="#" class="showImageClick"  data-src-link="${file}" data-link-index="${loop.index}" data-list-link="${post.postOriginalImagePaths}" data-text-1="${post.postName}" data-text-2="${post.shmUser.nickName}">
                                                    <img src="${file}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';" width="40" height="40">
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="showImageClick" data-src-link="${file}" data-text-1="${post.postName}" data-text-2="${post.shmUser.nickName}" data-link-index="${loop.index}" data-list-link="${post.postOriginalImagePaths}">${loop.index + 1}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a href="<%= request.getContextPath()%>/backend/post-detail?id=${post.postId}">${post.postId}</a><br>
                                <a href="<%= request.getContextPath()%>/backend/post-detail?id=${post.postId}">${post.postName}</a>
                            </td>
                            <td class="table-td-center">
                                ${post.postCategoryParent.categoryName}
                                <br>
                                ${post.postCategory.categoryName}
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${post.postType == 'SELL'}">
                                        出品
                                    </c:when>
                                    <c:otherwise>
                                        リクエスト
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${post.shmUser.firstName} ${post.shmUser.lastName}</td>
                            <td class="table-td-center">
                                <javatime:format value="${post.createdAt}"
                                                pattern="Y年MM月dd日 HH:mm " var="parsedEmpDate" />
                                <c:out value="${parsedEmpDate}" />
                                <br>
                                <javatime:format value="${post.updatedAt}"
                                                pattern="Y年MM月dd日 HH:mm " var="parsedEmpDate" />
                                <c:out value="${parsedEmpDate}" />
                            </td>
                            <td class="table-td-center">
                                <fmt:formatNumber pattern="#,##0" value="${post.postPrice}"/>円
                            </td>
                            <td class="table-td-center">
                                <a href="<%= request.getContextPath()%>/backend/list-talk?id=${post.postId}">
                                    <c:choose>
                                        <c:when test="${post.postSellStatus == 'PUBLIC'}">
                                            公開中
                                        </c:when>
                                        <c:when test="${post.postSellStatus == 'IN_CONVERSATION'}">
                                            取引中
                                        </c:when>
                                        <c:when test="${post.postSellStatus == 'TEND_TO_SELL'}">
                                            取引意思確認中
                                        </c:when>
                                        <c:when test="${post.postSellStatus == 'SOLD'}">
                                            取引完了
                                        </c:when>
                                        <c:otherwise>
                                            公開停止
                                        </c:otherwise>
                                    </c:choose>
                                </a>
                            </td>
                            <td>
                                【${post.postReportTimes}】
                            </td>
                        </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table></div>
            </div>
        </div>

        <div class="form-group margin-bottom">
            <div class="row" style="font-size: 18px;">
                <div class="col-md-3 text-center">
                    投稿者
                </div>
                <div class="col-md-1" style="min-width: 100px !important;">
                    通知者:
                </div>
                <div class="col-md-4">
                    ${partner.firstName} ${partner.lastName}&nbspさんとのトーク
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="row">
                <div class="col-md-3">
                    <div class="box">
                        <div class="box-body" style="border: 3px solid #d2d6de !important;">
                            <div class="divImgUserProfile">
                            <img class="profile-user-img img-responsive img-circle" data-toggle="modal" data-target="#userAvatarModel"
                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';"
                                 src="${userAvatar}" alt="User profile picture">
                            </div>
                            <h3 class="profile-username text-center" style="color: #5F9EA0; word-wrap: break-word;">${post.shmUser.firstName} ${post.shmUser.lastName}</h3>
                            <ul class="list-group list-group-unbordered">
                                <li class="list-group-item" style="color: #6495ED">
                                    <i class="fa fa-home"></i> &nbsp;<span> ${post.shmUser.address.fullAreaName} &nbsp;
                                    <c:choose>
                                        <c:when test="${post.shmUser.gender == 'FEMALE'}">
                                            ( 女性 )
                                        </c:when>
                                        <c:when test="${post.shmUser.gender == 'MALE'}">
                                            ( 男性 )
                                        </c:when>
                                        <c:otherwise>

                                        </c:otherwise>
                                    </c:choose>
                                    </span>
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-user"></i> &nbsp;<span> ${post.shmUser.nickName}</span>
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-envelope-o"></i> &nbsp;<span> ${post.shmUser.email}</span>
                                </li>
                                <li class="list-group-item">
                                    生年月日:&nbsp;
                                    <javatime:format value="${post.shmUser.dateOfBirth}" pattern="Y年MM月dd日" var="dateOfBirth"/><c:out value="${dateOfBirth}"/>
                                </li>
                            </ul>
                        </div>
                        <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${post.shmUser.id}" class="btn btn-primary btn-block"><b>ユーザー詳細へ</b></a>
                        <!-- /.box-body -->
                    </div>
                </div>
                <div class="col-md-9">
                    <table class="table table-bordered">
                        <tbody>
                        <c:forEach var="msg" items="${msgList}">
                            <tr role="row" class="odd">
                                <td>
                                    <%--<c:choose>
                                        <c:when test="${msg.talkPurcMsgStatus=='SENDING'}">
                                            ${post.shmUser.firstName} ${post.shmUser.lastName} &nbsp &nbsp &nbsp
                                            <javatime:format value="${msg.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                            <c:out value="${createdAt}"/>
                                        </c:when>
                                        <c:otherwise>
                                            ${partner.firstName} ${partner.lastName} &nbsp &nbsp &nbsp
                                            <javatime:format value="${msg.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                            <c:out value="${createdAt}"/>
                                        </c:otherwise>
                                    </c:choose>--%>
                                        ${msg.shmUserCreator.firstName} ${msg.shmUserCreator.lastName} &nbsp &nbsp &nbsp
                                        <javatime:format value="${msg.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                        <c:out value="${createdAt}"/>

                                        <br>
                                        <c:choose>
                                            <c:when test="${msg.talkPurcMsgType == 'REVIEW_FOR_OWNER'}">
                                                &nbsp&nbsp&nbsp&nbsp取引相手に評価をしました。
                                            </c:when>
                                            <c:when test="${msg.talkPurcMsgType == 'REVIEW_FOR_PARTNER'}">
                                                &nbsp&nbsp&nbsp&nbsp取引相手に評価をしました。
                                            </c:when>
                                            <c:when test="${msg.talkPurcMsgType == 'IMAGE'}">
                                                <c:forEach var="imgUrl" items="${fn:split(msg.talkPurcMsgCont,',')}">
                                                    <div class="div_talk_image_contain_owner divImgUserProfile">
                                                        <img data-toggle="modal" data-target="#userAvatarModel" class="img_talk_image"
                                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';"
                                                             src="${imgUrl}" alt="post image">
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                &nbsp&nbsp&nbsp&nbsp${msg.talkPurcMsgCont}
                                            </c:otherwise>
                                        </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div>
</div>

<script>
    function imgError(image) {
        image.onerror = "";
        image.src = "${pageContext.request.contextPath}/resources/images/noimage.png";
        return true;
    }
</script>

<style>

    table,td,th{
        border: 1px solid #d4d4d4 !important;
    }

</style>
