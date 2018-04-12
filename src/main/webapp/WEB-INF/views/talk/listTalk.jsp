<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/posts">投稿内容</a></li>
    <li><a href="#">トークー覧</a></li>
</ul>


<div class="box box-default" style="margin-top: 20px">
    <div class="box-body" style="margin-top: 30px">
        <div class="form-group">
            <div class="row " style="color: red;"><span style="padding-left: 40px;">${errorMsg}</span> </div>
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
                            <th style="text-align: center; vertical-align: text-top;" class="minwidth150" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >投稿登録日<br>最新更新日</th>
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
                                            <a href="#">
                                                <img src="${pageContext.request.contextPath}/resources/images/noimage.png" width="40" height="40">
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                        <c:forEach var="file" items="${post.postThumbnailImagePaths}" varStatus="loop">

                                            <c:choose>
                                                <c:when test="${loop.index == '0'}">
                                                    <a href="#" class="showImageClick"  data-src-link="${file}" data-link-index="${loop.index}" data-list-link="${post.postOriginalImagePaths}"  data-text-1="${post.postName}" data-text-2="${post.shmUser.nickName}">
                                                        <img src="${file}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';" width="40" height="40">
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="showImageClick" data-src-link="${file}" data-link-index="${loop.index}" data-list-link="${post.postOriginalImagePaths}" data-text-1="${post.postName}" data-text-2="${post.shmUser.nickName}">${loop.index + 1}</a>
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
                                <td>${user.firstName} ${user.lastName}</td>
                                <td class="table-td-center minwidth150">
                                    <javatime:format value="${post.createdAt}" pattern="Y年MM月dd日 HH:mm" var="createdAt"/>
                                    <c:out value="${createdAt}"/>
                                    <br>
                                    <javatime:format value="${post.userUpdateAt == null ? post.createdAt : post.userUpdateAt}" pattern="Y年MM月dd日 HH:mm" var="updateddAt"/>
                                    <c:out value="${updateddAt}"/>
                                </td>
                                <td class="table-td-center"> <fmt:formatNumber pattern="#,##0" value="${post.postPrice}"/>円</td>
                                <td class="table-td-center">
                                    <a href="<%= request.getContextPath()%>/backend/list-talk?id=${post.postId}">
                                        <c:choose>
                                            <c:when test="${post.postSellStatus == 'PUBLIC' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                公開中
                                            </c:when>
                                            <c:when test="${post.postSellStatus == 'IN_CONVERSATION' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                取引中
                                            </c:when>
                                            <c:when test="${post.postSellStatus == 'TEND_TO_SELL' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                取引意思確認中
                                            </c:when>
                                            <c:when test="${post.postSellStatus == 'SOLD' && post.postCtrlStatus != 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                取引完了
                                            </c:when>
                                            <c:when test="${post.postCtrlStatus == 'SUSPENDED' && post.postSellStatus != 'DELETED'}">
                                                強制停止・違反
                                            </c:when>
                                            <c:when test="${post.postSellStatus == 'DELETED'}">
                                                削除済
                                            </c:when>
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
            <div class="row" style="font-size: 20px;">
                <div class="col-md-3 text-center">
                    投稿者
                </div>

                <div class="col-md-4">
                    通知者・ニックネーム絞込 :
                </div>

                <div class="col-md-3">
                    <div class="">
                        <input type="text" id="filterInput" onkeyup="filterFunction()" placeholder="絞込" class="form-control">
                    </div>
                </div>
            </div>
        </div>

        <div class="form-group">
            <div class="row">
                <div class="col-md-3">
                    <div class="box">
                        <div class="box-body" style="border: 3px solid #d2d6de !important;">
                            <a href="#" class="showImageAvatarClick"  data-src-link="${userAvatar}" data-link-index="${userAvatar}" data-list-link="${userAvatar}">
                                <img class=" profile-user-img img-responsive img-circle"
                                                           src="${userAvatar}" alt="User profile picture"  onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';" style="height: 100px"/>
                            </a>
                            <h3 class="profile-username text-center" style="color: #5F9EA0;  word-wrap: break-word;">${user.firstName} ${user.lastName}</h3>

                            <ul class="list-group list-group-unbordered">
                                <li class="list-group-item" style="color: #6495ED">
                                    <i class="fa fa-home"></i> &nbsp;<span> ${user.address.fullAreaName}</span>
                                    <span>
                                        &nbsp;
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
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-user"></i> &nbsp;<span> ${user.nickName}</span>
                                </li>
                                <li class="list-group-item">
                                    <i class="fa fa-envelope-o"></i> &nbsp;<span> ${user.email}</span>
                                </li>
                                <li class="list-group-item">
                                    生年月日:&nbsp;
                                    <javatime:format value="${user.dateOfBirth}" pattern="Y年MM月dd日" var="dateOfBirth"/><c:out value="${dateOfBirth}"/>
                                </li>
                            </ul>
                        </div>
                        <a href="<%= request.getContextPath()%>/backend/user-detail?userId=${user.id}" class="btn btn-primary btn-block"><b>ユーザー詳細へ</b></a>
                        <!-- /.box-body -->
                    </div>
                </div>
                <div class="col-md-9">
                    <div style="margin-bottom: 20px;">だれ から 通知</div>
                    <div class="row">
                        <div class="col-sm-12">
                                <c:if test="${not empty talks}">
                                    <table id="talkTable" class="table table-bordered table-hover dataTable table-striped" role="grid" aria-describedby="example2_info">
                                        <thead>
                                            <tr role="row">
                                                <th style="text-align: center" class="sorting_asc" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" aria-sort="ascending">画像</th>
                                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >通知者</th>
                                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >ニックネーム</th>
                                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >取引決定者</th>
                                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >ステータス</th>
                                                <th style="text-align: center" class="sorting" tabindex="0" aria-controls="example2" rowspan="1" colspan="1" >&nbsp</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="talk" items="${talks}">
                                                <tr role="row" class="odd" style="text-align: center" >
                                                    <td>
                                                        <a href="#" class="showImageAvatarClick"  data-src-link="${talk.partnerAvatar}" data-link-index="${talk.partnerAvatar}" data-list-link="${talk.partnerAvatar}">
                                                        <img style="width: 60px; height: 60px" class="profile-user-img img-responsive img-circle"
                                                                 src="${talk.partnerAvatar}"
                                                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/resources/images/noimage.png';"
                                                                 alt="partner image">
                                                        </a>
                                                    </td>
                                                    <td class="sorting_1">
                                                            ${talk.shmUser.firstName} ${talk.shmUser.lastName}
                                                    </td>
                                                    <td>
                                                            ${talk.shmUser.nickName}
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${talk.talkPurcStatus == 'TEND_TO_SELL'}">
                                                                &Omicron;
                                                            </c:when>
                                                            <c:otherwise>

                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${talk.shmUser.ctrlStatus == 'SUSPENDED'}">
                                                                停止
                                                            </c:when>
                                                            <c:when test="${talk.shmUser.status == 'ACTIVATED'}">
                                                                通常
                                                            </c:when>
                                                            <c:when test="${talk.shmUser.status == 'TEND_TO_LEAVE'}">
                                                                退会予定
                                                            </c:when>
                                                            <c:when test="${talk.shmUser.status == 'LEFT'}">
                                                                退会
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <a href="<%= request.getContextPath()%>/backend/talk-detail?talkId=${talk.talkPurcId}"><button type="submit" class="btn btn-info">トーク確認</button></a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</div>

<style>
    table,td,th{
        border: 1px solid #d4d4d4 !important;
    }
</style>
<script>

    function filterFunction() {
        var input, filter, table, tr, tdNotifier, tdNickname, i;
        input = document.getElementById("filterInput");
        filter = input.value.toUpperCase();
        table = document.getElementById("talkTable");
        tr = table.getElementsByTagName("tr");
        for (i = 0; i < tr.length; i++) {
            tdNotifier = tr[i].getElementsByTagName("td")[1];
            tdNickname = tr[i].getElementsByTagName("td")[2];

            if (tdNotifier && tdNickname) {
                if (tdNotifier.innerText.toUpperCase().indexOf(filter) > -1 ||tdNickname.innerText.toUpperCase().indexOf(filter) > -1 ) {
                    tr[i].style.display = "";
                } else {
                    tr[i].style.display = "none";
                }
            }
        }
    }

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
