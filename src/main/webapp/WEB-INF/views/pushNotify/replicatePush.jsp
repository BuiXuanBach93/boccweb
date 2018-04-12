<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-push?pageNumber=0">新しいPUSHを作成</a></li>
</ul>

<form:form action="add-replicate-push" commandName="pushNotify" method="post" id="formAddRepPush">
    <div class="box box-default" style="margin-top: 20px">
        <div class="box-body" style="margin-top: 20px">
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">タイトル*</label>
                    <div class="col-md-5">
                        <form:input type="text" id="inputPushTitle" path="pushTitle" maxlength="150" class="form-control"/>
                    </div>
                    <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                        <span style="color: red" id="msgPushTitle">${msgPushTitle}</span>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">メッセージ*</label>
                    <div class="col-md-5" style="display: grid;">
                        <form:textarea id="inputPushContent" path="pushContent" rows="10"></form:textarea>
                        <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                            <span style="color: red" id="msgPushContent">${msgPushContent}</span>
                        </div>
                        <div style="text-align: right">
                            <label>Android残り</label><label id="count-android">4096</label><label>バイト iOS残り<label id="count-ios">4096</label>バイト</label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">PUSH種類</label>
                    <div class="col-md-2">
                        <form:select class="form-control select-button" path="pushActionType"
                                     id="choosePushActionType">
                            <form:option value="JUST_PUSH">PUSH通知のみ</form:option>
                            <form:option value="JUST_MESSAGE">運営から」のみ</form:option>
                            <form:option value="PUSH_AND_MESSAGE">PUSH通知かつ「運営から」へ</form:option>
                        </form:select>
                    </div>
                    <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                        <span style="color: red" id="msgPushActionType">${msgPushTitle}</span>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">配信日時</label>
                    <div class="col-md-2">
                        <form:select class="form-control select-button" path="pushImmediate"
                                     id="choosePushType">
                            <form:option value="IMMEDIATE">いますぐ</form:option>
                            <form:option value="WAIT">日時</form:option>
                        </form:select>
                    </div>
                    <div class="col-md-3" id="panel-choose-push-date" style="display: none;">
                        <label>日本時間*</label>
                        <div>
                            <div class="inline-block">
                                <div class="form-group">
                                    <div class="input-group date">
                                        <div class="input-group-addon">
                                            <i class="fa fa-calendar"></i>
                                        </div>
                                        <form:input type="text" path="timerDateStr" class="form-control pull-right form_datetime"
                                                    id="inputTimerDate" placeholder="YYYY/MM/DD HH:MM"/>
                                    </div>
                                    <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                        <span style="color: red" id="msgTimerDate">${msgTimerDate}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-2"></div>
                    <div class="col-md-2">
                        <div class="checkbox">
                            <label><form:checkbox id="cbPushAndroid" path="pushAndroid" value="0"/>Android端末に配信する</label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-2"></div>
                    <div class="col-md-2">
                        <div class="checkbox">
                            <label>
                                <form:checkbox id="cbPushIOS" path="pushIos" value="0"/>iOS端末に配信する
                            </label>
                        </div>
                    </div>
                    <div class="row" style="color: red; margin-top: 10px; margin-left: 0px;">
                        <span style="color: red" id="msgPushType">${msgPushType}</span>
                    </div>
                </div>
            </div>
            <form:input id="inputPushId" type="hidden" path="pushId"></form:input>
            <form:input id="inputIOSNumber" type="hidden" path="iosNumber"></form:input>
            <form:input id="inputAndroidNumber" type="hidden" path="androidNumber"></form:input>
            <form:input id="inputCurrentIOSNumber" type="hidden" path="currentIosNumber"></form:input>
            <form:input id="inputCurrentAndroidNumber" type="hidden" path="currentAndroidNumber"></form:input>
            <div class="row">
                <div class="col-md-2"></div>
                <div class="col-md-5" style="padding-left: 140px !important;">
                    <button type="button" class="btn btn-info" data-toggle="modal" onclick="validatePushRequest()">
                        <i class="fa fa-floppy-o" aria-hidden="true"></i>
                        PUSHを送信する
                    </button>
                    <a href="<%= request.getContextPath()%>/backend/list-push?pageNumber=0" class="btn btn-danger" role="button">
                        <span class="glyphicon glyphicon-remove"></span>
                        キャンセル
                    </a>
                </div>
                <div class="col-md-5"></div>
            </div>

            <div id="pushConfirmModal" class="modal fade modal-lg bg-primary-modal " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 下記の内容で送信しますか？</h4>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <div class="row">
                                <label class="col-md-3 control-label">タイトル</label>
                                <div class="col-md-5" style="padding-top: 5px;">
                                    <span id="modalPushTitle"></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <label class="col-md-3 control-label">メッセージ</label>
                                <div class="col-md-8" style="display: grid; padding-top: 5px;">
                                    <span id="modalPushContent"></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <label class="col-md-3 control-label">配信日時</label>
                                <div class="col-md-5" style="display: grid; padding-top: 5px;">
                                    <span id="modalPushSendDate"></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <label class="col-md-3 control-label">配信対象デバイス</label>
                                <div class="col-md-5" style="display: grid; padding-top: 5px;">
                                    <span id="modalPushType"></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="row">
                                <label class="col-md-3 control-label">配信対象端末数 (想定)</label>
                                <div class="col-md-5" style="display: grid; padding-top: 5px;">
                                    <span id="modalPushNumber"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button id="btnConfirmReplicatePush" type="submit" class="btn btn-primary" data-confirm="Ok">OK</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</form:form>

<script>
    $.datetimepicker.setLocale('ja');

    $('#choosePushActionType').change(function () {
        var type = $("#choosePushActionType").val();
        if(type === 'JUST_MESSAGE'){
            document.getElementById("cbPushAndroid").checked = true;
            document.getElementById("cbPushIOS").checked = true;
            document.getElementById("cbPushAndroid").disabled = true;
            document.getElementById("cbPushIOS").disabled = true;
        }else{
            document.getElementById("cbPushAndroid").disabled = false;
            document.getElementById("cbPushIOS").disabled = false;
        }
    });

    if($("#choosePushType").val()){
        var pushType = $("#choosePushType").val();
        if(pushType === 'WAIT'){
            document.getElementById('panel-choose-push-date').style.display = 'block';
        }else{
            document.getElementById('panel-choose-push-date').style.display = 'none';
        }
    }
    $('#choosePushType').change(function () {
        var type = $("#choosePushType").val();
        if(type === 'WAIT'){
            document.getElementById('panel-choose-push-date').style.display = 'block';
        }else{
            document.getElementById('panel-choose-push-date').style.display = 'none';
        }
    });

    $(".form_datetime").datetimepicker({
        dateFormat: 'dd/mm/yy H:m',
        changeMonth: true,
        changeYear: true,
        yearRange: '1900:2099',
        autoclose: true,
        todayBtn: true,
        pickerPosition: "bottom-left",
        language: 'ja'
    });
    var maxLength = 4096;
    $('textarea').keyup(function() {
        var sizeInByte = encodeURI($(this).val()).split(/%..|./).length - 1;
        var length = maxLength-sizeInByte;
        if(length >=0){
            $('#count-android').text(length);
            $('#count-ios').text(length);
        }else{
            var str = $(this).val();
            var countByte = 0;
            var endpoint = -1;
            for (var i = 0, len = str.length; i < len; i++) {
                countByte += encodeURI(str[i]).split(/%..|./).length - 1;
                if(countByte >= maxLength-3){
                    endpoint = i;
                    break;
                }
            }
            $(this).val($(this).val().substr(0, endpoint));
        }

    });

    function validatePushRequest() {
        $('#msgPushTitle').html("");
        $('#msgPushContent').html("");
        $('#msgTimerDate').html("");
        $('#msgPushType').html("");
        if(!$("#inputPushTitle").val() || !$("#inputPushTitle").val().trim()){
            $('#msgPushTitle').html("プッシュタイトルがありません。")
            return;
        }
        if(!$('#inputPushContent').val() || !$("#inputPushContent").val().trim()){
            $('#msgPushContent').html("プッシュの内容が入力されていません。")
            return;
        }
        if(!$('#choosePushActionType').val()){
            $('#msgPushActionType').html(" PUSH通知の種類を選択してください。")
            return;
        }
        if($('#choosePushType').val() && $('#choosePushType').val() === 'WAIT'){
            if(!$('#inputTimerDate').val()){
                $('#msgTimerDate').html("配信日時を設定してください。")
                return;
            }
            var today = new Date();
            var day = today.getDate();
            if(day < 10){
                day = "0"+day;
            }
            var month = today.getMonth() + 1;
            if(month < 10){
                month = "0"+month;
            }
            var year = today.getFullYear();
            var hours = today.getHours();
            if(hours < 10){
                hours = "0"+hours;
            }
            var min = today.getMinutes();
            if(min < 10){
                min = "0"+min;
            }
            var todayStr = year+"/"+month+"/"+day+" "+hours+":"+min;
            if(todayStr > $('#inputTimerDate').val()){
                $('#msgTimerDate').html("配信日時を現在時点の前に設定することはできません。")
                return;
            }
        }
        var androidChecked = document.getElementById("cbPushAndroid").checked;
        var iosChecked = document.getElementById("cbPushIOS").checked;
        if(!androidChecked && !iosChecked){
            $('#msgPushType').html("配信予約の端末を設定してください。")
            return;
        }
        var pushTypeStr = "";
        if(iosChecked && !androidChecked){
            pushTypeStr = "iOS";
        }
        if(!iosChecked && androidChecked){
            pushTypeStr = "Android";
        }
        if(iosChecked && androidChecked){
            pushTypeStr = "iOS/Android";
        }

        var pushAndroi = 0;
        var pushIos = 0;
        if($('#inputIOSNumber').val() >= 0 && iosChecked){
            pushIos = $('#inputCurrentIOSNumber').val();
        }
        if($('#inputAndroidNumber').val() >= 0 && androidChecked){
            pushAndroi = $('#inputCurrentAndroidNumber').val();
        }
        var pushNumber = +pushAndroi + +pushIos;
        var pushNumberText = pushNumber + "(iOS:" + pushIos + "/Android:"+pushAndroi+")";
        $('#modalPushTitle').html($("#inputPushTitle").val().replace(/</g, "&lt;").replace(/>/g, "&gt;"));
        $('#modalPushContent').html($('#inputPushContent').val().replace(/</g, "&lt;").replace(/>/g, "&gt;"));
        if($('#choosePushType').val() === 'WAIT'){
            $('#modalPushSendDate').html($('#inputTimerDate').val());
        }else{
            $('#modalPushSendDate').html("");
        }
        $('#modalPushType').html(pushTypeStr);
        $('#modalPushNumber').html(pushNumberText);
        $("#pushConfirmModal").modal();
    }

    $('#btnConfirmReplicatePush').click(function () {
        $("#pushConfirmModal").modal('hide');
    });

</script>