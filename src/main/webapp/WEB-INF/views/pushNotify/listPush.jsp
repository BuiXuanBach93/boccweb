<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:url var="firstUrl" value="${baseUrlPagination}&pageNumber=1" />
<c:url var="lastUrl" value="${baseUrlPagination}&pageNumber=${deploymentLog.totalPages}" />
<c:url var="prevUrl" value="${baseUrlPagination}&pageNumber=${currentIndex - 1}" />
<c:url var="nextUrl" value="${baseUrlPagination}&pageNumber=${currentIndex + 1}" />

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-push?pageNumber=0">プッシュトップ</a></li>
</ul>

<script>
    var ctx = "${pageContext.request.contextPath}";
</script>


<div class="push" style="margin-top: -31px; margin-bottom: 2%">
    <div>
        <div class="box-body">
            <div class="form-group" >
                <div class="row" style="width: 50%; float: left">
                    <div class="col-md-5">
                        <label style="margin-left: 20px; margin-top: 5px"> PUSH一覧 </label>
                    </div>
                    <div class="col-md-1" style="float: right; width: auto">
                        <a href="<%= request.getContextPath()%>/backend/create-push" role="button" class="btn btn-primary"> 新規PUSHを作成</a>
                    </div>
                    <div class="col-md-2"></div>
                </div>
            </div>
        </div>
    </div>

    <div class="box box-default clearfix">
        <div class="box list-box-left" style="width: 55%; float: left">
            <c:if test="${totalElements > 0}">
                <div class="row">
                    <div class="col-md-12" style="padding-left: 75px; margin-top: 20px; margin-bottom: -35px;">
                        <div class="form-group pull-left ">
                            <select class="selectpicker" id="sortPush">
                                <c:choose>
                                    <c:when test="${sortType == 'DESC'}">
                                        <option value="DESC">新しいものからソート</option>
                                        <option value="ASC">古いものからソート</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="ASC">古いものからソート</option>
                                        <option value="DESC">新しいものからソート</option>
                                    </c:otherwise>
                                </c:choose>
                            </select>
                        </div>
                        <div class="dataTables_paginate paging_simple_numbers nav-height" id="example1_paginate">
                            <ul class="pagination" style="margin: 0px !important;">
                                <div class="col-md-6">
                                    <h5><b style="margin-right: 40px">${startElement} - ${curElements} の ${totalElements}</b></h5>
                                </div>
                                <c:choose>
                                    <c:when test="${curPage == 0}">
                                        <li class="paginate_button disabled"><a href="#">&lt;</a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="#" class="search-paging2" data-page="${curPage - 1}">&lt;</a></li>
                                    </c:otherwise>
                                </c:choose>

                                <c:choose>
                                    <c:when test="${curPage+1 == totalPage}">
                                        <li class="paginate_button disabled"><a href="#">&gt;</a></li>
                                    </c:when>
                                    <c:otherwise>
                                        <li><a href="#" class="search-paging2" data-page="${curPage + 1}">&gt;</a></li>
                                    </c:otherwise>
                                </c:choose>
                            </ul>
                        </div>
                    </div>
                </div>
            </c:if>
            <div >
                <div class="box-body">
                    <div class="form-group">
                        <div class="row text-center">
                            <c:if test="${sizeResult != 0}">
                                <div class="col-md-12">
                                    <div class="row" style="position:relative;">
                                        <div class="col-sm-12" style="overflow-y: auto; max-height: 600px; min-height: 350px;  ">
                                            <table id="example1" class="table table-bordered table-hover dataTable text-center table-wrapper" role="grid" aria-describedby="example1_info">
                                                <tbody>
                                                <c:forEach var="push" items="${listPush}"  varStatus="loop" >
                                                    <tr role="row">
                                                        <a class="row pushItem" data-push-status="${push.pushStatus}"  data-push-title="${fn:escapeXml(push.pushTitle)}"
                                                           data-push-content="${fn:escapeXml(push.pushContent)}"  data-push-date="${push.sendDate}" data-push-updatedat="${push.updatedAt}"  data-push-pushnumber="${push.pushNumber}"
                                                           data-push-androidnumber="${push.androidNumber}" data-push-iosnumber="${push.iosNumber}" data-push-readnumber="${push.readNumber}"
                                                           data-push-androidreadnumber="${push.androidReadNumber}" data-push-iosreadnumber="${push.iosReadNumber}" data-push-pushid="${push.pushId}">
                                                                <div style="width: 100%">
                                                                    <div style="width: 40px; float: left">
                                                                        <img class="direct-chat-img" src="${pageContext.request.contextPath}/resources/images/ic-push.png" alt="Push" style="margin-left: 5px;">
                                                                    </div>
                                                                    <div class="row memo-row" style="width: 88%; float: left; margin-right:10px; margin-left: 10px; margin-bottom: 10px">
                                                                        <div class="direct-chat-info clearfix" style="margin-bottom: -5px">
                                                                            <label class="label label-default" style="float: left; font-size: 13px">
                                                                                <c:choose>
                                                                                    <c:when test="${push.pushStatus == 'SENT'}">
                                                                                        配信済み
                                                                                    </c:when>
                                                                                    <c:when test="${push.pushStatus == 'WAITING'}">
                                                                                        配信予約
                                                                                    </c:when>
                                                                                    <c:when test="${push.pushStatus == 'SUSPENDED'}">
                                                                                        配信予約停止
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </label>
                                                                            <c:choose>
                                                                                <c:when test="${push.pushActionType == 'JUST_PUSH'}">
                                                                                    <label class="label label-info" style="float: left; font-size: 13px; margin-left: 10px">
                                                                                        プッシュ
                                                                                    </label>
                                                                                </c:when>
                                                                                <c:when test="${push.pushActionType == 'JUST_MESSAGE'}">
                                                                                    <label class="label label-danger" style="float: left; font-size: 13px; margin-left: 10px">
                                                                                        運営から
                                                                                    </label>
                                                                                </c:when>
                                                                                <c:when test="${push.pushActionType == 'PUSH_AND_MESSAGE'}">
                                                                                    <label class="label label-info" style="float: left; font-size: 13px; margin-left: 10px">
                                                                                        プッシュ
                                                                                    </label>
                                                                                    <label class="label label-danger" style="float: left; font-size: 13px; margin-left: 10px">
                                                                                        運営から
                                                                                    </label>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                </c:otherwise>
                                                                            </c:choose>

                                                                            <span style="float: right">
                                                                                 <c:choose>
                                                                                     <c:when test="${push.pushStatus == 'SENT'}">
                                                                                         <label>【送信日】</label>
                                                                                     </c:when>
                                                                                     <c:when test="${push.pushStatus == 'WAITING'}">
                                                                                         <label>【配信予定日時】</label>
                                                                                     </c:when>
                                                                                     <c:when test="${push.pushStatus == 'SUSPENDED'}">
                                                                                         <label>【配信予定日時】</label>
                                                                                     </c:when>
                                                                                     <c:otherwise>
                                                                                     </c:otherwise>
                                                                                 </c:choose>
                                                                            <span class="direct-chat-timestamp mem-time">${push.sendDate}</span>
                                                                        </span>
                                                                        </div>
                                                                        <div style="width: 100%; display: flex">
                                                                            <div class="direct-chat-text mem-content" style="text-align: left; width: 100%; margin-left: 0px; margin-top: 0px;">
                                                                                    ${fn:escapeXml(push.pushTitle)}
                                                                            </div>
                                                                            <div style="width: 70%; float: right; text-align: right; font-size: 12px;">
                                                                                <div>
                                                                                    <label>最終更新日時</label>
                                                                                    <span class="direct-chat-timestamp mem-time">${push.updatedAt}</span>
                                                                                </div>
                                                                                <div>
                                                                                    <label>${push.adminSender.adminName}</label>
                                                                                    <label class="label label-default" style="font-size: 13px">${push.pushNumber}端末</label>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                        </a>
                                                    </tr>
                                                </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>

                            </c:if>

                            <span style="color: red;" >${nullResult}</span>
                        </div>
                    </div>

                    <div class="modal fade modal-sm bg-danger-modal " role="document" style="margin: 30px auto; background: none;">
                        <div class="modal-content">
                            <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                                <h4 class="modal-title">
                                    <i class="glyphicon glyphicon-question-sign"></i> 確認する
                                </h4>
                            </div>
                            <div class="modal-body">この言葉を削除しますか。</div>
                            <div class="modal-footer">
                                <a href="#" class="btn btn-primary btn-modal-confirm">削除する</a>
                                <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                            </div>
                        </div>
                    </div>

                </div>
                <div style="width: 50%"></div>
            </div>
        </div>
        <div class="box push-info" style="width: 45%; height: 450px; float: right; border-style: ridge;">
            <div id="push-none-select-div" style="text-align: center; margin-top: 100px;">
                プッシュ通知が選択されていません
            </div>
            <div id="push-info-panel" style="display: none">
                <input type="hidden" id="push-data-pushId">
                <div class="info-header" style="width: 100%; margin-top: 10px; padding-left: 10px; padding-right: 10px; height: 50px;">
                    <div class="pull-left" style="display: inline-block;">
                        <label id="selected_push_status"></label>
                        <button id="btn-edit-push" class="btn btn-default" style="margin-left: 10px">
                            編集
                        </button>
                        <input value="停止" type="button" id="btn-suspend-push" class="btn btn-default" style="margin-left: 10px;" data-toggle="modal"  data-target=".bg-primary-modal-push-suspend"></input>
                    </div>
                    <div class="pull-right">
                        <button id="btn-copy-push" class="btn btn-default" style="border: 1px solid; border-radius: 10px;">
                            複製
                        </button>
                        <input value="削除" type="button" id="btn-delete-push" class="btn btn-default" style="border: 1px solid; border-radius: 10px;" data-toggle="modal"  data-target=".bg-primary-modal-push-delete"></input>
                    </div>
                </div>
                <div style="margin-top: 10px; padding-left: 40px;">
                    <div class="text-content">
                        <label>
                            タイトル
                        </label>
                        <div id="selected_push_title" style="padding-left: 20px; width: 80%">

                        </div>
                    </div>
                    <div class="text-content" style="margin-top: 20px;">
                        <label>
                            メッセージ
                        </label>
                        <div id="selected_push_content" class="direct-chat-text mem-content" style="padding-left: 10px; width: 80%; margin-left: 20px; max-height: 150px;overflow-y: auto;">

                        </div>
                    </div>
                    <div class="text-content" style="margin-top: 20px;">
                        <div style="display: flex">
                            <label id="selected_push_date_title" class="pull-left" style="width: 20%;">配信日時</label>
                            <div id="selected_push_sendDate"></div>
                        </div>
                        <div style="display: flex">
                            <label class="pull-left" style="width: 20%;">最終更新日時</label>
                            <div id="selected_push_updatedAt"></div>
                        </div>
                        <div style="display: flex">
                            <label  class="pull-left" style="width: 20%;">配信数</label>
                            <div id="selected_push_pushNumber"></div>
                        </div>
                        <div>
                            <label  class="pull-left" style="width: 20%;">開封数</label>
                            <div id="selected_push_readNumber"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-push-suspend" role="document" style="margin: 30px auto; background: none;">
    <div class="modal-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
        </div>
        <div class="modal-body">この配信予約を停止しますか？</div>
        <div class="modal-footer">
            <button id="btn-suspend-push-confirmed" type="submit" class="btn btn-primary" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<div class="modal fade modal-sm bg-primary-modal-push-delete" role="document" style="margin: 30px auto; background: none;">
    <div class="modal-content">
        <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
            <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
        </div>
        <div class="modal-body">このプッシュ通知を削除しますか？</div>
        <div class="modal-footer">
            <button id="btn-delete-push-confirmed" type="submit" class="btn btn-primary" data-confirm="Ok">はい </button>
            <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
        </div>
    </div>
</div>

<script>
    $('#sortPush').change(function () {
        var url = ctx + "/backend/list-push?pageNumber=" + ${currentIndex} + "&sortType=" + $("#sortPush").val();
        window.location = url;
    });

    $('.pushItem').click(function () {
        document.getElementById('push-none-select-div').style.display = 'none';
        document.getElementById('push-info-panel').style.display = 'block';

        var title = $(this).data('push-title');
        var content = $(this).data('push-content');
        var status = $(this).data('push-status');
        var sendDate = $(this).data('push-date');
        var updatedAt = $(this).data('push-updatedat');
        var pushNumber = $(this).data('push-pushnumber');
        var androidNumber = $(this).data('push-androidnumber');
        var iosNumber = $(this).data('push-iosnumber');
        var readNumber = $(this).data('push-readnumber');
        var androidReadNumber = $(this).data('push-androidreadnumber');
        var iosReadNumber = $(this).data('push-iosreadnumber');
        var pushId = $(this).data('push-pushid');
        var pushDateTitle = "";
        document.getElementById('push-data-pushId').value = pushId;

        if(status == 'WAITING'){
            status = '配信予約';
            pushDateTitle = "配信予定日時";
            document.getElementById('btn-edit-push').style.display = 'inline-block';
            document.getElementById('btn-suspend-push').style.display = 'inline-block';
        }else if(status == 'SENT'){
            status = '配信済み';
            pushDateTitle = "配信日時";
            document.getElementById('btn-edit-push').style.display = 'none';
            document.getElementById('btn-suspend-push').style.display = 'none';
        }else if(status == 'SUSPENDED'){
            status = '配信予約停止';
            pushDateTitle = "配信予定日時";
            document.getElementById('btn-edit-push').style.display = 'none';
            document.getElementById('btn-suspend-push').style.display = 'none';
        }
        var pushNumberText = pushNumber + ' 端末 (IOS '+iosNumber + '/ Android '+ androidNumber +')';
        var readNumberText = readNumber + ' 端末 (IOS '+iosReadNumber + '/ Android ' + androidReadNumber + ')';
        $("#selected_push_status").text(status);
        $("#selected_push_title").text(title);
        $("#selected_push_content").text(content);
        $("#selected_push_sendDate").text(sendDate);
        $("#selected_push_updatedAt").text(updatedAt);
        $("#selected_push_pushNumber").text(pushNumberText);
        $("#selected_push_readNumber").text(readNumberText);
        $("#selected_push_date_title").text(pushDateTitle);
    });

    $("#btn-suspend-push-confirmed").click(function() {
        var pushRequest = {}
        pushRequest["pushId"] = $('#push-data-pushId').val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend/push/suspend",
            data : JSON.stringify(pushRequest),
            timeout : 100000,
            success: function(response) {
                window.location.href = ctx + "/backend/list-push?pageNumber=" + ${curPage};
            },
            error: function(){
            }
        });
    });
        $("#btn-delete-push-confirmed").click(function() {
            var pushRequest = {}
            pushRequest["pushId"] = $('#push-data-pushId').val();

            var token = $("meta[name='_csrf']").attr("content");
            var header = $("meta[name='_csrf_header']").attr("content");
            $(document).ajaxSend(function(e, xhr, options) {
                xhr.setRequestHeader(header, token);
            });

            $.ajax({
                type : "PUT",
                contentType : "application/json",
                url : ctx + "/backend/push/detele-push",
                data : JSON.stringify(pushRequest),
                timeout : 100000,
                success: function(response) {
                    window.location.href = ctx + "/backend/list-push?pageNumber=" + ${curPage};
                },
                error: function(){
                }
            });
        });

    $("#btn-edit-push").click(function() {
        var pushId = $('#push-data-pushId').val();
        window.location.href = ctx + "/backend/edit-push?pushId=" + pushId;
    });

    $("#btn-copy-push").click(function() {
        var pushId = $('#push-data-pushId').val();
        window.location.href = ctx + "/backend/replicate-push?pushId=" + pushId;
    });
</script>