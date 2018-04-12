<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-qa?page=0">お問合わせー覧</a></li>
    <li><a href="#">問い合わせ ID: ${qa.qaId}</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<div class="box box-default" style="margin-top: 20px">
    <div class="box-body">
        <div class="form-group">
            <div class="row">
                <div class="col-sm-12">
                    <table style="text-align: center; border: 2px solid #f4f4f4" id="example2" class="table table-bordered table-hover dataTable" role="grid" aria-describedby="example2_info">
                        <tbody>
                        <tr role="row" class="odd" style="text-align: left; border: 2px solid #cab3b3; border-radius: 5px;">
                            <td>
                                <div class="row form-group">
                                    <div class="inline-block left20">ステータス：
                                        <c:choose>
                                            <c:when test="${qa.qaStatus == 'NO_RESPONSE'}">
                                                未対応
                                            </c:when>
                                            <c:when test="${qa.qaStatus == 'INPROGRESS'}">
                                                対応中
                                            </c:when>
                                            <c:otherwise>
                                                対応完了
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <input id="qaDetailId" type="hidden" value="${qa.qaId}">
                                    <div class="inline-block left20">発信者 : ${qa.shmUser.nickName}</div>
                                    <div class="inline-block left20">問合せ番号 : ${qa.qaId}</div>
                                    <div class="inline-block left20">初回受信日時 :
                                        <javatime:format value="${qa.createdAt}" pattern="Y年MM月dd日 HH:mm" var="parsedEmpDate"/>
                                        <c:out value="${parsedEmpDate}"/>
                                    </div>
                                </div>
                                <div class="row form-group">
                                    <div class="inline-block left20">タイトル :
                                        <c:choose>
                                        <c:when test="${qa.qaType == 'ACCOUNT_PROBLEM'}">
                                        アカウント 登録 · ログイン について
                                        </c:when>
                                        <c:when test="${qa.qaType == 'POST_PROBLEM'}">
                                        投稿方法 · 投稿 ルール について
                                        </c:when>
                                        <c:when test="${qa.qaType == 'USAGE_PROBLEM'}">
                                        トラブル · 詐欺 について
                                        </c:when>
                                        <c:when test="${qa.qaType == 'HELP'}">
                                        お 問い合わ · やりとり について
                                        </c:when>
                                        <c:otherwise>
                                        退会について
                                        </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <%--<div class="inline-block left20">メールアドレス：${qa.shmUser.email}</div>--%>
                                    <button id="memoQaBtn" class="inline-block left20 btn btn-primary">受付 メモ</button>
                                    <c:if test="${qa.qaStatus != 'RESOLVED'}">
                                        <input type="button"
                                               class="inline-block left20 btn btn-primary" value="完了" data-toggle="modal"  data-target=".bg-primary-modal-qa-ok"></input>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                        <c:forEach var="msg" items="${talkQas}">
                            <tr role="row" class="odd modal-confirm" style="text-align: left; border: 2px solid #cab3b3; border-radius: 5px;">
                                <td>
                                    <div class="row form-group">
                                        <div class="inline-block left20">
                                            <c:choose>
                                                <c:when test="${msg.fromAdmin == false}">
                                                    ${qa.shmUser.nickName}
                                                </c:when>
                                                <c:otherwise>
                                                    ${msg.shmAdmin.adminName}
                                                </c:otherwise>
                                            </c:choose>
                                            &nbsp;
                                            <javatime:format value="${msg.createdAt}" pattern="Y年MM月dd日 HH:mm" var="parsedEmpDate"/>
                                            <c:out value="${parsedEmpDate}"/>
                                        </div>
                                    </div>
                                    <div class="row form-group">
                                        <div class="inline-block left40" style="white-space: pre-wrap">${msg.talkQaMsg}</div>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <tr role="row" class="odd" style="text-align: left; border: 2px solid #cab3b3; border-radius: 5px;">
                            <td>
                                <div class="row form-group">
                                    <c:choose>
                                        <c:when test="${qa.qaStatus == 'RESOLVED'}">
                                            <textarea id="msgContentInput" class="inline-block left20" disabled="true" style="width: 600px; height: 100px;" maxlength="300"></textarea>
                                            <button type="reset" class="inline-block pull-right btn btn-primary" disabled="true" style="margin-right: 850px;" onclick="showPreviewPopup()">プレビュー</button>
                                        </c:when>
                                        <c:otherwise>
                                            <textarea id="msgContentInput" class="inline-block left20" style="width: 600px; height: 100px;" maxlength="300"></textarea>
                                            <button type="reset" class="inline-block pull-right btn btn-primary" style="margin-right: 850px;" onclick="showPreviewPopup()">プレビュー</button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                        <tfoot></tfoot>
                    </table></div>
            </div>
        </div>
    </div>
    <div id="previewMsgPopup" class="modal fade" role="dialog" data-backdrop="static" data-keyboard="false">
        <div class="modal-dialog" role="document">
            <div class="modal-content" style="width: 650px">
                <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> プレビュー</h4>
                </div>
                <div class="modal-body">
            <textarea id="msgPreviewContent" maxlength="300" style="width: 620px; height: 100px ">
            </textarea>
                </div>
                <div class="modal-footer">
                    <input type="button" id ="confirmAfterEditBtn" class="btn btn-primary" onclick="confirmQaAgain()" style="display:none" value="確定"/>
                    <input type="button" id ="btnQaSendMsg" class="btn btn-primary" value="送信"/>
                    <button type="button" id="editQaBtn" class="btn btn-default" onclick="showEditConfirmModal()">編集</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal fade modal-sm bg-primary-modal-ok" id="sendConfModal" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
        <div class="modal-content modal-confirm" data-backdrop="static" data-keyboard="false" style="margin-top: 70px">
            <div class="modal-header bg-primary">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
            </div>
            <div class="modal-body" style="border-bottom: 0.5px solid #cab3b3;">メッセージをユーザーに送信しますか？</div>
            <div class="modal-footer">
                <input type="button" class="btn btn-primary save-post-memo" data-dismiss="modal" id="sendQaConfirmModalBtn" data-confirm="Ok" value="はい"></input>
                <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
            </div>
        </div>
    </div>

    <div class="modal fade modal-sm bg-primary-modal-ok" id="editConfModal" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
        <div class="modal-content modal-confirm" data-backdrop="static" data-keyboard="false" style="margin-top: 70px">
            <div class="modal-header bg-primary">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
            </div>
            <div class="modal-body" style="border-bottom: 0.5px solid #cab3b3;"> 編集を行いますか？</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary save-post-memo" data-dismiss="modal" onclick="enableEditMsgContent()" data-confirm="Ok">はい</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
            </div>
        </div>
    </div>

    <div class="modal fade modal-sm bg-primary-modal-ok" id="confirmAgainModal" role="document" style="margin: 30px auto; background: none; z-index: 1900000">
        <div class="modal-content modal-confirm" data-backdrop="static" data-keyboard="false" style="margin-top: 70px">
            <div class="modal-header bg-primary">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 最終確認</h4>
            </div>
            <div class="modal-body" style="border-bottom: 0.5px solid #cab3b3;"> 編集を確定しますか？</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary save-post-memo" id="finalEditConfirmBtn" data-dismiss="modal" onclick="agreeToModifyQAMsg()" data-confirm="Ok">はい</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
            </div>
        </div>
    </div>

    <div id="qaMemoModal" class="modal fade" role="dialog">
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
                                <textarea class="form-control us-mem-content" name="qaMemCont" id="qaMemCont"
                                          style="width:523px; height:35px; resize: vertical"></textarea>
                                <span class="input-group-btn">
                                <button type="button" class="btn btn-primary btn-flat save-qa-memo">保存</button>
                            </span>
                            </div>
                        </form>
                    </div>
                    <div style="color: red; margin-left: 10px; padding-bottom: 10px;" id="errorUserMemo" class="form-group"></div>
                </div>
            </div>
        </div>
    </div>
    <div class="modal fade modal-sm bg-primary-modal-qa-ok " role="document" style="margin: 30px auto; background: none;">
        <div class="modal-content">
            <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
            </div>
            <div class="modal-body">対応完了としますか。</div>
            <div class="modal-footer">
                <button id="btnResolvedQa" type="submit" class="btn btn-primary" data-confirm="Ok">はい </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
            </div>
        </div>
    </div>
</div>



<script>

    function showPreviewPopup() {
        var msgInput = $('#msgContentInput').val();
        if (!isEmpty(msgInput)) {
            $('#msgPreviewContent').val(msgInput);
            $("#msgPreviewContent").attr("disabled",true);
            $("#previewMsgPopup").modal("show");
            document.getElementById('confirmAfterEditBtn').style.display = 'none';
            $('#btnQaSendMsg').show().css("visibility", "visible");
            $('#editQaBtn').show().css("visibility", "visible");
        } else {

        }
    }

    function confirmQaAgain() {
        $("#confirmAgainModal").modal("show");
    }

    function showEditConfirmModal() {
        $('#editConfModal').modal("show");
    }

    function agreeToModifyQAMsg() {
        $("#msgPreviewContent").attr("disabled", true);
        document.getElementById('confirmAfterEditBtn').style.display = 'none';
        $('#btnQaSendMsg').show().css("visibility", "visible");
        $('#editQaBtn').show().css("visibility", "visible");
    }

    function enableEditMsgContent() {
        $("#msgPreviewContent").attr("disabled",false);
        document.getElementById('confirmAfterEditBtn').style.display = 'block';
        $('#btnQaSendMsg').hide().css("visibility", "hide");
        $('#editQaBtn').hide().css("visibility", "hide");
    }

    $('#btnQaSendMsg').click(function () {
        $('#sendConfModal').modal("show");
    });



    $("#sendQaConfirmModalBtn").click(function() {
        var talkQa = {}

        talkQa["qaContent"] = $('#msgPreviewContent').val();
        talkQa["qaId"] = $('#qaDetailId').val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/web/qa/create-msg",
            data : JSON.stringify(talkQa),
            timeout : 100000,
            success: function(response) {
                location.reload();
            },
            error: function(){

            }
        });

    })

    $("#btnResolvedQa").click(function() {
        var talkQa = {}
        talkQa["qaId"] = $('#qaDetailId').val();

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "PUT",
            contentType : "application/json",
            url : ctx + "/backend//web/qa/resolved-qa",
            data : JSON.stringify(talkQa),
            timeout : 100000,
            success: function(response) {
                window.location.href = ctx + "/backend/list-qa?page=0";
            },
            error: function(){
            }
        });

    })

</script>