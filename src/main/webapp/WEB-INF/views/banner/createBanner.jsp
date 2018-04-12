<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<script src="${pageContext.request.contextPath}/resources/plugins/nic-editor/nicEdit.js"></script>
<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-banner?pageNumber=0">特集バナー管理</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/create-banner"> 特集バナー追加・編集</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<style>
    .fileUpload {
        position: relative;
        overflow: hidden;
        margin: 10px;
    }
    .fileUpload input.upload {
        position: absolute;
        top: 0;
        right: 0;
        margin: 0;
        padding: 0;
        font-size: 20px;
        cursor: pointer;
        opacity: 0;
        filter: alpha(opacity=0);
    }
</style>
<form:form action="add-banner" id="bannerCreateForm" modelAttribute="banner" method="POST" enctype="multipart/form-data">
    <div class="box box-default" style="margin-top: 20px;">
        <div class="box-body" style="margin-top: 10px; padding: 10px">
            <div class="row">
                <div class="col-md-2"><label style="line-height: 34px">特集名（*）
                </label></div>
                <div class="col-md-2">
                    <form:input id="inputBannerName" type="text" path="bannerName" class="form-control"/>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2">
                    <div>
                        <img src="#" alt=""/>
                    </div>
                    <div>
                        <label style="line-height: 34px">バナー画像（*）
                        </label>
                    </div>
                </div>
                <div class="col-md-4">
                    <div>
                        <img id="bannerImage" src="#" alt=""/>
                        <input type="hidden" id="inputSelectedImage"/>
                    </div>
                    <div class="fileUpload btn btn-primary" style="margin-left: 0px;">
                        <span>アップロード</span>
                        <input type="file" name="imageBanner" id="fileImageBanner" class="upload" onchange="readURL(this);" accept="image/png,image/jpg,image/gif"/>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">特集方法選択
                </label></div>
                <div action="" class="col-md-6" id="group-filter-type" style="padding-top: 5px;">
                    <input id="inputDesTypeWebUrl" type="radio" name="desType" value="0" checked onclick="handleClickDesTypeRadio(this);"> <label for="inputDesTypeWebUrl">WebView</label>
                    <input id="inputDesTypeCategoryId" type="radio" name="desType" value="1" style="margin-left: 20px;" onclick="handleClickDesTypeRadio(this);"><label for="inputDesTypeCategoryId">カテゴリID</label>
                    <input id="inputDesTypePostId" type="radio" name="desType" value="2" style="margin-left: 20px;" onclick="handleClickDesTypeRadio(this);"><label for="inputDesTypePostId">投稿ID</label>
                    <input id="inputDesTypeKeyword" type="radio" name="desType" value="3" style="margin-left: 20px;" onclick="handleClickDesTypeRadio(this);"><label for="inputDesTypeKeyword">キーワード検索結果</label>
                </div>
            </div>
            <div class="row" style="margin-top: 20px; display: block" id="groupRadioUrlTypePanel">
                <div class="col-md-2"><label style="line-height: 34px">遷移先
                </label></div>
                <div action="" class="col-md-6" id="group-url-type" style="padding-top: 5px;">
                    <input id="inputUrlFillType" type="radio" name="urlType" value="0" onclick="handleClickUrlTypeRadio(this);" checked> <label for="inputUrlFillType">URL入力</label>
                    <input id="inputUrlBuildType" type="radio" name="urlType" value="1" onclick="handleClickUrlTypeRadio(this);" style="margin-left: 20px;"><label for="inputUrlBuildType">キャンペーンページ作成</label>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"></div>
                <div class="col-md-4">
                    <input id="inputDesText" name="destinationText" type="text" class="form-control"/>
                </div>
            </div>
            <div id="bannerPageBuildPanel" class="row" style="margin-top: 20px; display: none">
                <div class="col-md-2"></div>
                <div class="col-md-10" style="padding-top: 10px; padding-bottom: 10px;">
                    <form:input type="hidden" id="inputPageId" path="pageId"/>
                    <div class="row">
                        <div class="col-md-2"><label style="line-height: 34px">タイトル
                        </label></div>
                        <div class="col-md-6">
                            <%--<form:input id="inputPageTitle" type="text" path="pageTitle" class="form-control"/>--%>
                            <form:textarea class="form-control" id="inputPageTitle" path="pageTitle" rows="2" cssStyle="width: 700px; height: 100px;"></form:textarea>
                        </div>
                        <%--<div class="col-md-2">--%>
                            <%--<form:input id="inputTitleColor" type="text" path="titleColor" class="form-control"/>--%>
                        <%--</div>--%>
                        <%--<div class="col-md-1">--%>
                            <%--<div id="divColorTitle"  style="background: black; width: 34px; height: 34px;">--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    </div>
                    <div class="row" style="margin-top: 20px;">
                        <div class="col-md-2"><label style="line-height: 34px">背景色（*）
                        </label></div>
                        <div class="col-md-2">
                            <form:input id="inputBackgroundColor" type="text" path="backgroundColor" class="form-control"/>
                        </div>
                        <div  class="col-md-1">
                            <div id="divColorBackground"  style="background: white; width: 34px; height: 34px;">
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 20px;">
                        <div class="col-md-2">
                            <div>
                                <img src="#" alt=""/>
                            </div>
                            <div>
                                <label style="line-height: 34px">メイン画像（*）
                                </label>
                            </div>
                        </div>
                        <div class="col-md-10">
                            <div>
                                <img id="pageImage" src="#" alt=""/>
                                <input type="hidden" id="inputSelectedPageImage"/>
                            </div>
                            <div class="fileUpload btn btn-primary" style="margin-left: 0px;">
                                <span>アップロード</span>
                                <input type="file" name="imagePage" id="fileImagePage" class="upload" onchange="readImagePageURL(this);" accept="image/png,image/jpg,image/gif"/>
                                <form:input type="hidden" id="inputIsChangeImagePage" path="isChangeImagePage"/>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 20px;">
                        <div class="col-md-2"><label style="line-height: 34px">本文（*）
                        </label></div>
                        <div class="col-md-10">
                            <form:textarea id="inputPageContent" path="pageContent" rows="10" cssStyle="width: 700px; height: 300px;"></form:textarea>
                        </div>
                    </div>
                    <div class="row" style="    margin-bottom: 10px;">
                        <div class="col-md-2"></div>
                        <div class="col-md-6">
                            <span style="color: red; padding-top: 10px;" id="errMsgPage"></span>
                        </div>
                    </div>
                    <div class="row" style="margin-top: 20px;">
                        <div class="col-md-2"><label style="line-height: 34px">
                        </label></div>
                        <div class="col-md-2">
                            <button id="btnPreviewPage" type="submit" class="btn btn-success">
                                プレビュー
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">表示開始日</label>
                </div>
                <div class="col-md-4">
                    <div class="inline-block">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="fromDate" class="form-control pull-right form_datetime"
                                            id="inputFromDate" placeholder="YYYY/MM/DD HH:MM"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                <span style="color: red" id="msgFromDate">${msgFromDate}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">表示終了日</label>
                </div>
                <div class="col-md-4">
                    <div class="inline-block">
                        <div class="form-group">
                            <div class="input-group date">
                                <div class="input-group-addon">
                                    <i class="fa fa-calendar"></i>
                                </div>
                                <form:input type="text" path="toDate" class="form-control pull-right form_datetime"
                                            id="inputToDate" placeholder="YYYY/MM/DD HH:MM"/>
                            </div>
                            <div class="row form-group" style="color: red; margin-top: 10px; margin-left: 0px;">
                                <span style="color: red" id="msgToDate">${msgToDate}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" style="    margin-bottom: 10px;">
            <div class="col-md-2"></div>
            <div class="col-md-6">
                <span style="color: red; padding-top: 10px;" id="errMsg"></span>
            </div>
        </div>
        <div class="row" style="padding-bottom: 20px">
            <div class="col-md-2">

            </div>
            <div class="col-md-4">
                <button id="btnSaveBanner" type="button" class="btn btn-success">
                    <i class="fa fa-floppy-o" aria-hidden="true"></i>
                    登録
                </button>
                <button type="button" class="btn btn-danger" data-toggle="modal"  data-target=".bg-primary-modal-cancel">
                    <i class="fa fa-times" aria-hidden="true"></i>
                    キャンセル
                </button>
            </div>
        </div>

        <div id="createConfirmModal" class="modal fade modal-sm bg-primary-modal-ok " role="document" style="margin: 30px auto; background: none;">
            <div class="modal-content">
                <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                    <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                </div>
                <div class="modal-body">特集を追加しますか</div>
                <div class="modal-footer">
                    <button type="button" id="btnOkCreateBanner" class="btn btn-primary" data-confirm="Ok">はい</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
                </div>
            </div>
        </div>

        <div class="modal fade modal-sm bg-primary-modal-cancel " role="document" style="margin: 30px auto; background: none;">
            <div class="modal-content">
                <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                    <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                </div>
                <div class="modal-body">キャンセルしますか？</div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary"><a href="<%= request.getContextPath()%>/backend/list-banner?pageNumber=0" style="color: white">はい</a></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
                </div>
            </div>
        </div>

    </div>

</form:form>
<script>
    function handleClickUrlTypeRadio(urlTypeRadio) {
        var filterValue = urlTypeRadio.value;
        if(filterValue == 0){
            document.getElementById('bannerPageBuildPanel').style.display = 'none';
            document.getElementById('inputDesText').disabled = false;
        }else{
            document.getElementById('bannerPageBuildPanel').style.display = 'block';
            document.getElementById('inputDesText').disabled = true;
            document.getElementById('inputDesText').value = '';
        }
    }

    function handleClickDesTypeRadio(desTypeRadio) {
        var filterValue = desTypeRadio.value;
        if(filterValue == 0){
            document.getElementById('groupRadioUrlTypePanel').style.display = 'block';
            var desTypeValue = $('input[name=urlType]:checked').val();
            if(desTypeValue == 1){
                document.getElementById('inputDesText').disabled = true;
                document.getElementById('bannerPageBuildPanel').style.display = 'block';
            }else{
                document.getElementById('inputDesText').disabled = false;
                document.getElementById('bannerPageBuildPanel').style.display = 'none';
            }
        }else{
            document.getElementById('inputDesText').disabled = false;
            document.getElementById('groupRadioUrlTypePanel').style.display = 'none';
            document.getElementById('bannerPageBuildPanel').style.display = 'none';
        }
    }

//    $('#inputTitleColor').focusout(function(){
//        var titleColor = $('#inputTitleColor').val();
//        $('#divColorTitle').css("background-color", titleColor);
//    });

    $('#inputBackgroundColor').focusout(function(){
        var backgroundColor = $('#inputBackgroundColor').val();
        $('#divColorBackground').css("background-color", backgroundColor);
    });

    $(document).ready(function() {
        bkLib.onDomLoaded(function() { nicEditors.allTextAreas() });
    });

    $.datetimepicker.setLocale('ja');
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

    function readURL(input) {
        var _URL = window.URL || window.webkitURL;
        if (input.files && input.files[0]) {
            var fileType = input.files[0]["type"];
            var ValidImageTypes = ["image/gif", "image/jpeg", "image/png", "image/jpg"];
            if ($.inArray(fileType, ValidImageTypes) < 0) {
                document.getElementById("errMsg").innerHTML= "正しい画像形式をアップロードしてください。 形式はPNG/JPG/GIFだけでございます。";
                return;
            }
            var file = input.files[0];
            var img = new Image();
            img.onload = function () {
                if(this.width != 715 && this.height != 100){
                    document.getElementById("errMsg").innerHTML= "画像のサイズが間違いました。再度アップロードしてください。";
                    return;
                }else {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        $('#bannerImage')
                            .attr('src', e.target.result)
                            .width(428)
                            .height(60);
                    };
                    reader.readAsDataURL(input.files[0]);
                    document.getElementById("inputSelectedImage").value = 1;
                }
            };
            img.src = _URL.createObjectURL(file);
        }
    }

    function readImagePageURL(input) {
        var _URL = window.URL || window.webkitURL;
        if (input.files && input.files[0]) {
            var fileType = input.files[0]["type"];
            var ValidImageTypes = ["image/gif", "image/jpeg", "image/png", "image/jpg"];
            if ($.inArray(fileType, ValidImageTypes) < 0) {
                document.getElementById("errMsgPage").innerHTML= "PNG,JPG,GIFの画像をアップロードしてください";
                return;
            }
            var file = input.files[0];
            var img = new Image();
            img.onload = function () {
                var reader = new FileReader();
                reader.onload = function (e) {
                    $('#pageImage')
                        .attr('src', e.target.result)
                        .width(300);
//                      .height(60);
                };
                reader.readAsDataURL(input.files[0]);
                document.getElementById("inputSelectedPageImage").value = 1;
                var isCreatedPage = $('#inputPageId').val();
                if(isCreatedPage && isCreatedPage > 0){
                    document.getElementById("inputIsChangeImagePage").value = 1;
                }
            };
            img.src = _URL.createObjectURL(file);
        }
    }

    $('#bannerCreateForm').on('submit',(function(e) {
        e.preventDefault();

        var title = document.getElementsByClassName('nicEdit-main')[0].innerHTML;
        var content = document.getElementsByClassName('nicEdit-main')[1].innerHTML;
        var backgroundColor = $('#inputBackgroundColor').val();
        var titleText = document.getElementsByClassName('nicEdit-main')[0].innerText;
        if(titleText && titleText.trim().length > 0){
            document.getElementById('inputPageTitle').value = title;
        }else{
            document.getElementById('inputPageTitle').value = '';
        }
        var contentText = document.getElementsByClassName('nicEdit-main')[1].innerText;
        if(!contentText || !contentText.trim()){
            document.getElementById("errMsgPage").innerHTML= '本文を入力してください';
            return;
        }else{
            document.getElementById('inputPageContent').value = content;
        }

        if(!backgroundColor || !backgroundColor.trim()){
            document.getElementById("errMsgPage").innerHTML= '背景色コードを入力してください';
            return;
        }

        var isOkBackgroundColor  = /^#[0-9A-F]{6}$/i.test(backgroundColor);
        if(!isOkBackgroundColor){
            document.getElementById("errMsgPage").innerHTML= '正しい色コードを入力してください';
            return;
        }

        var isSelectedImage = $('#inputSelectedPageImage').val();
        if(isSelectedImage != 1){
            document.getElementById("errMsgPage").innerHTML= 'メイン画像をアップロードしてください';
            return;
        }

        var formData = new FormData(this);
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type:'POST',
            url: ctx + "/backend/add-banner-page",
            data:formData,
            cache:false,
            contentType: false,
            processData: false,
            success:function(data){
                document.getElementById("errMsgPage").innerHTML= '';
                document.getElementById("inputPageId").value = data.pageId;

                var getUrl = window.location + "";
                var index = getUrl.indexOf("/backend");
                var desUrl = getUrl.substring(0,index) + '/backend/preview-banner-page?pageId=' + data.pageId;
                window.open(desUrl,'_blank');
            },
            error: function(data){
            }
        });
    }));

    $('#btnSaveBanner').click(function() {
        var bannerRequest = {}
        bannerRequest["bannerName"] = $('#inputBannerName').val();

        var desTypeValue = $('input[name=desType]:checked').val();
        if(desTypeValue == 0){
            bannerRequest["desTypeWebUrl"] = true;
        }
        if(desTypeValue == 1){
            bannerRequest["desTypeCategoryId"] = true;
        }
        if(desTypeValue == 2){
            bannerRequest["desTypePostId"] = true;
        }
        if(desTypeValue == 3){
            bannerRequest["desTypeKeyword"] = true;
        }

        var isSelectedImage = $('#inputSelectedImage').val();
        if(isSelectedImage != 1){
            document.getElementById("errMsg").innerHTML= 'バナー画像をアップロードしてください。';
            return;
        }

        var urlType = $('input[name=urlType]:checked').val();
        if(desTypeValue == 0 && urlType == 1){
            var title = document.getElementsByClassName('nicEdit-main')[0].innerHTML;
            var content = document.getElementsByClassName('nicEdit-main')[1].innerHTML;
            var backgroundColor = $('#inputBackgroundColor').val();
            var titleText = document.getElementsByClassName('nicEdit-main')[0].innerText;
            if(titleText && titleText.trim().length > 0){
                document.getElementById('inputPageTitle').value = title;
            }else{
                document.getElementById('inputPageTitle').value = '';
            }

            if(!content || !content.trim()){
                document.getElementById("errMsgPage").innerHTML= '本文を入力してください';
                return;
            }else {
                document.getElementById('inputPageContent').value = content;
            }
            var isSelectedImage = $('#inputSelectedPageImage').val();
            if(isSelectedImage != 1){
                document.getElementById("errMsgPage").innerHTML= 'メイン画像をアップロードしてください';
                return;
            }
//            if(!titleColor || !titleColor.trim()){
//                document.getElementById("errMsgPage").innerHTML= 'タイトルの色コードを入力してください';
//                return;
//            }
            if(!backgroundColor || !backgroundColor.trim()){
                document.getElementById("errMsgPage").innerHTML= '背景色コードを入力してください';
                return;
            }

//            var isOkTitleColor  = /^#[0-9A-F]{6}$/i.test(titleColor);
//            if(!isOkTitleColor){
//                document.getElementById("errMsgPage").innerHTML= '正しい色コードを入力してください';
//                return;
//            }

            var isOkBackgroundColor  = /^#[0-9A-F]{6}$/i.test(backgroundColor);
            if(!isOkBackgroundColor){
                document.getElementById("errMsgPage").innerHTML= '正しい色コードを入力してください';
                return;
            }
        }

        if(urlType == 0){
            bannerRequest["urlFillType"] = true;
        }else {
            bannerRequest["urlBuildType"] = true;
        }
        bannerRequest["destinationText"] = $('#inputDesText').val();
        bannerRequest["fromDate"] = $('#inputFromDate').val();
        bannerRequest["toDate"] = $('#inputToDate').val();
        bannerRequest["urlType"] = $('input[name=urlType]:checked').val();
        bannerRequest["pageTitle"] = document.getElementsByClassName('nicEdit-main')[0].innerHTML;
        bannerRequest["pageContent"] = document.getElementsByClassName('nicEdit-main')[1].innerHTML;
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/validate-banner",
            data : JSON.stringify(bannerRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    document.getElementById("errMsg").innerHTML= '';
                    document.getElementById("errMsgPage").innerHTML= '';
                    $("#createConfirmModal").modal();
                }
            },
            error: function(){
            }
        });
    });

    $("#btnOkCreateBanner").click(function() {
        $("#createConfirmModal").modal('hide');
        document.getElementById("bannerCreateForm").submit();
    })

</script>