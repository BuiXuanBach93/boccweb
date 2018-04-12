<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>


<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-category-setting?pageNumber=0">カテゴリ管理・追加</a></li>
    <li><a href="">カテゴリ追加・編集</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<form:form action="update-category-setting" modelAttribute="categorySetting" method="POST">
    <div class="box box-default" style="margin-top: 20px;">
        <div class="box-body" style="margin-top: 10px; padding: 10px">
            <div class="row">
                <div class="col-md-2"><label style="line-height: 34px">カテゴリ名
                </label></div>
                <div class="col-md-2">
                    <form:input id="inputCategoryName" type="text" path="categoryName" class="form-control" maxlength="8"/>
                    <form:input type="hidden" id="inputCategorySettingId" path="categorySettingId"/>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">表示方法
                </label></div>
                <div action="" class="col-md-6" id="group-filter-type">
                    <c:choose>
                        <c:when test="${categorySetting.filterTypePrivate == true}">
                            <input id="inputFilterTypePrivate" type="radio" name="filterType" checked value="0" onclick="handleClickFilterTypeRadio(this);">  <label for="inputFilterTypePrivate">社内限定 </label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputFilterTypePrivate" type="radio" name="filterType" value="0" onclick="handleClickFilterTypeRadio(this);"> <label for="inputFilterTypePrivate">社内限定 </label>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${categorySetting.filterTypeKeyword == true}">
                            <input id="inputFilterTypeKeyword"  type="radio" name="filterType" value="1" onclick="handleClickFilterTypeRadio(this);" checked style="margin-left: 20px;"> <label for="inputFilterTypeKeyword">キーワード絞込</label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputFilterTypeKeyword"  type="radio" name="filterType" value="1" onclick="handleClickFilterTypeRadio(this);" style="margin-left: 20px;"> <label for="inputFilterTypeKeyword">キーワード絞込</label>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${categorySetting.filterTypePostId == true}">
                            <input id="inputFilterTypePostId"  type="radio" name="filterType" value="2" onclick="handleClickFilterTypeRadio(this);" checked style="margin-left: 20px;"> <label for="inputFilterTypePostId">投稿ID指定</label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputFilterTypePostId"  type="radio" name="filterType" value="2" onclick="handleClickFilterTypeRadio(this);" style="margin-left: 20px;"> <label for="inputFilterTypePostId">投稿ID指定</label>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="row" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">商品絞込
                </label></div>
                <div class="col-md-4">
                    <form:input id="inputFilterText" path="filterText" type="text" class="form-control"/>
                </div>
            </div>
            <div class="row" id="divInputPublishType" style="margin-top: 20px;">
                <div class="col-md-2"><label style="line-height: 34px">絞込ジャンル
                </label></div>
                <div class="col-md-6" id="group-publish-type">
                    <c:choose>
                        <c:when test="${categorySetting.publishTypeSell == true}">
                            <input id="inputPublishTypeSell" type="radio" name="publishType" checked value="0" ><label for="inputPublishTypeSell">出品投稿のみ</label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputPublishTypeSell" type="radio" name="publishType" value="0" ><label for="inputPublishTypeSell">出品投稿のみ</label>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${categorySetting.publishTypeBuy == true}">
                            <input id="inputPublishTypeBuy" type="radio" name="publishType" checked value="1" style="margin-left: 20px;"> <label for="inputPublishTypeBuy">リクエスト投稿のみ</label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputPublishTypeBuy" type="radio" name="publishType" value="1" style="margin-left: 20px;"><label for="inputPublishTypeBuy">リクエスト投稿のみ</label>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${categorySetting.publishTypeAll == true}">
                            <input id="inputPublishTypeAll" type="radio" name="publishType" value="2" checked style="margin-left: 20px;"><label for="inputPublishTypeAll">出品･リクエスト両方</label>
                        </c:when>
                        <c:otherwise>
                            <input id="inputPublishTypeAll" type="radio" name="publishType" value="2" style="margin-left: 20px;"><label for="inputPublishTypeAll">出品･リクエスト両方</label>
                        </c:otherwise>
                    </c:choose>
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
                <button id="btnSaveCategorySetting" type="button" class="btn btn-success">
                    <i class="fa fa-floppy-o" aria-hidden="true"></i>
                    保存
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
                <div class="modal-body">カテゴリを登録・編集しますか？</div>
                <div class="modal-footer">
                    <button type="button" id="btnOkCreateGroup" class="btn btn-primary" data-confirm="Ok">はい</button>
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
                    <button type="button" class="btn btn-primary"><a href="<%= request.getContextPath()%>/backend/list-category-setting?pageNumber=0" style="color: white">はい</a></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
                </div>
            </div>
        </div>

    </div>

</form:form>
<script>

    var filterValue = $('input[name=filterType]:checked').val();
    if(filterValue == 2){
        document.getElementById('divInputPublishType').style.display = 'none';
    }else if(filterValue == 0){
        document.getElementById('inputFilterText').disabled = true;
    }

    function handleClickFilterTypeRadio(filterRadio) {
        var filterValue = filterRadio.value;
        if(filterValue == 0){
            document.getElementById('inputFilterText').disabled = true;
        }else{
            document.getElementById('inputFilterText').disabled = false;
        }
        if(filterValue == 2){
            document.getElementById('divInputPublishType').style.display = 'none';
        }else{
            document.getElementById('divInputPublishType').style.display = 'block';
        }
    }

    $('#btnSaveCategorySetting').click(function() {
        var categorySettingRequest = {}
        categorySettingRequest["categoryName"] = $('#inputCategoryName').val();
        var categorySettingId = $('#inputCategorySettingId').val();
        categorySettingRequest["categorySettingId"] = categorySettingId;

        var filterTypeValue = $('input[name=filterType]:checked').val();
        if(filterTypeValue == 0){
            categorySettingRequest["filterTypePrivate"] = true;
        }
        if(filterTypeValue == 1){
            categorySettingRequest["filterTypeKeyword"] = true;
        }
        if(filterTypeValue == 2){
            categorySettingRequest["filterTypePostId"] = true;
        }

        categorySettingRequest["filterText"] = $('#inputFilterText').val();

        var publishTypeValue = $('input[name=publishType]:checked').val();
        if(publishTypeValue == 0){
            categorySettingRequest["publishTypeSell"] = true;
        }
        if(publishTypeValue == 1){
            categorySettingRequest["publishTypeBuy"] = true;
        }
        if(publishTypeValue == 2){
            categorySettingRequest["publishTypeAll"] = true;
        }

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/validate-category-setting",
            data : JSON.stringify(categorySettingRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    $("#createConfirmModal").modal();
                }
            },
            error: function(){
            }
        });
    });

    $("#btnOkCreateGroup").click(function() {
        $("#createConfirmModal").modal('hide');
        var categorySettingRequest = {}
        categorySettingRequest["categoryName"] = $('#inputCategoryName').val();
        var categorySettingId = $('#inputCategorySettingId').val();
        categorySettingRequest["categorySettingId"] = categorySettingId;

        var filterTypeValue = $('input[name=filterType]:checked').val();
        if(filterTypeValue == 0){
            categorySettingRequest["filterTypePrivate"] = true;
        }
        if(filterTypeValue == 1){
            categorySettingRequest["filterTypeKeyword"] = true;
        }
        if(filterTypeValue == 2){
            categorySettingRequest["filterTypePostId"] = true;
        }

        categorySettingRequest["filterText"] = $('#inputFilterText').val();

        var publishTypeValue = $('input[name=publishType]:checked').val();
        if(publishTypeValue == 0){
            categorySettingRequest["publishTypeSell"] = true;
        }
        if(publishTypeValue == 1){
            categorySettingRequest["publishTypeBuy"] = true;
        }
        if(publishTypeValue == 2){
            categorySettingRequest["publishTypeAll"] = true;
        }

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/update-category-setting",
            data : JSON.stringify(categorySettingRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    window.location.href = ctx + "/backend/list-category-setting?pageNumber=0";
                }
            },
            error: function(){
            }
        });

    })

</script>