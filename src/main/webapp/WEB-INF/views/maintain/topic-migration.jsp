<%--
  Created by IntelliJ IDEA.
  User: NguyenThuong
  Date: 6/30/2017
  Time: 6:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<style type="text/css">
    #loading {
        width: 100%;
        height: 100%;
        margin: auto;
        padding: 10px;
        position: fixed;
        display: block;
        opacity: 0.7;
        background-color: #fff;
        z-index: 99;
        text-align: center;
    }

    #loading-image {
        position: absolute;
        top: 100px;
        left: 240px;
        z-index: 100;
    }
</style>
<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/topic-migration">PUSH通知配信用移行</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="home-page" id="home-page-id" style="margin-top: 20px">

    <div id="loading" style="display: none">
        <img id="loading-image" src='/backend/resources/images/loader.gif' alt="Loading..." />
    </div>

    <div class="search-posts">
        <%--<form:form class="" id="topicModel" role="form" modelAttribute="topicForm"
                   action="/backend/process/topic-migration" method="POST" style="margin-top: 20px">--%>
        <div class="row">
            <div class="col-xs-offset-5" style="color: green"><b>${successMsg}</b></div>
        </div>
        <div class="row">
            <div class="col-md-12" style="text-align: left; line-height: 34px; font-weight: bold">
                一回のみ実行するアクションである。「既存のデータの処理」ボタンが無効としたら、再度実行できません。
            </div>
        </div>
        <div class="row">
            <div class="col-md-2" style="text-align: center; line-height: 34px; font-weight: bold">
                <label></label>
            </div>
            <div class="input-group" style="width: 500px;">
                <input id="isMigration" type="hidden" value="${topicForm.subscribeTopicAll}">
            </div>
        </div>
        <div class="row">
            <div class="col-md-3 ">
                <input type="button" id="migrationButton" class="btn btn-primary" data-toggle="modal"
                       data-target="#migrateModal" value="既存のデータの処理">
                </input>
            </div>
        </div>
        <%--</form:form>--%>
        <!-- Modal -->
        <div class="modal fade" id="migrateModal" role="dialog">
            <div class="modal-dialog">
                <!-- Modal content-->
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                        <h4 class="modal-title">Modal Header</h4>
                    </div>
                    <div class="modal-body">
                        <p>既存データをサブスクライブするには時間がかかりますのでプロセスが完了するまでブラウザを閉じないでください。このプロセスを実行して続けたいでしょうか？</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" id="buttonContinue" class="btn btn-default" data-dismiss="modal">
                            実行する
                        </button>
                        <button type="button" id="buttonClose" class="btn btn-default" data-dismiss="modal">閉じる
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
    var isMigration = $('#isMigration').val();
    var data = {subscribeTopicAll: "true"};
    var json = JSON.stringify(data);
    $(document).ready(function () {
        if (isMigration == "true") {
            $("input").prop('disabled', true);
        }
        $('#buttonContinue').click(function (event) {
            $('#loading').css("display", "block");
            event.preventDefault();
            $("#migrationButton").prop("disabled", true);
            $.ajax({
                type: "POST",
                url: ctx+"/backend/process/topic-migration",
                data: json,
                contentType: "application/json; charset=utf-8",
                timeout : 100000,
                dataType: "json",
                success: function (data) {
                    console.log(data);
                    $('#loading').css("display", "none");
                },
                complete: function () {
                    $('#loading').css("display", "none");
                }
            });
        });
    });

    function continueMigrate() {
//                $('#topicModel').submit();
    }


</script>
