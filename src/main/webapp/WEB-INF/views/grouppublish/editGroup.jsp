<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-group-publish?pageNumber=0">法人グループ設定</a></li>
    <li><a href="#">法人グループ編集</a></li>
</ul>
<script>var ctx = "${pageContext.request.contextPath}";</script>
<form:form action="update-group-publish" modelAttribute="groupPublish" method="POST">
    <div class="box box-default" style="margin-top: 20px;">
        <div class="box-body" style="margin-top: 10px; padding: 10px">
            <div class="box-tools clearfix">
                <div class="form-group pull-left ">
                    <label>法人グループ編集</label>
                </div>
            </div>

            <div class="row">
                <div class="col-md-2"><label style="line-height: 34px">法人グループ名
                </label></div>
                <div class="col-md-2">
                    <form:input id="inputGroupName" type="text" path="groupName" class="form-control" maxlength="14"/>
                    <form:input type="hidden" id="inputGroupId" path="groupId"/>
                </div>
            </div>
        </div>
    </div>
    <div class="box box-default">
        <div style="padding-left: 10px"><label style="line-height: 34px">所属法人ID一覧
        </label></div>
        <div>
            <div class="col-md-2">
            </div>
            <div class="col-md-4">
                <div class="group-detail" style="margin-bottom: 10px;">
                    <input type="button" class="btn btn-success" id="addGroupDetail" style="margin-top: 10px" value="法人ID新規登録" onclick="insRow()"/>
                    <table id="groupDetailTable" border="1" style="margin-top: 10px">
                        <tr>
                            <td style="text-align: center">法人ID</td>
                            <td>削除</td>
                        </tr>
                        <c:forEach var="list" items="${groupPblDetails}"  varStatus="loop" >
                            <tr role="row">
                                <td><input maxlength="6" style="padding-left: 10px;" size=25 type="text" value="${list.legalId}"/></td>
                                <td><a href="#" onclick="deleteRow(this)" style="padding: 10px">
                                    <span class="glyphicon glyphicon-trash"></span>
                                </a></td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
                <span style="color: red; padding-top: 10px;" id="errMsg"></span>
                <div style="margin-top: 50px;">
                    <button type="button" class="btn btn-success" id="btnSavePublishGroup">
                        <i class="fa fa-floppy-o" aria-hidden="true"></i>
                        保存
                    </button>
                    <button type="button" class="btn btn-danger" data-toggle="modal"  data-target=".bg-primary-modal-cancel">
                        <i class="fa fa-times" aria-hidden="true"></i>
                        キャンセル
                    </button>
                </div>
            </div>
        </div>

        <div class="row" style="margin-left: 135px; margin-bottom: 20px; padding-bottom: 10px;">


            <div class="col-md-7"></div>
        </div>

        <div id="createConfirmModal" class="modal fade modal-sm bg-primary-modal-ok " role="document" style="margin: 30px auto; background: none;">
            <div class="modal-content">
                <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                    <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                </div>
                <div class="modal-body">法人グループを登録しますか？</div>
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
                    <button type="button" class="btn btn-primary"><a href="<%= request.getContextPath()%>/backend/list-group-publish?pageNumber=0" style="color: white">はい</a></button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">いいえ</button>
                </div>
            </div>
        </div>

    </div>
</form:form>
<script>

    function deleteRow(row)
    {
        var i=row.parentNode.parentNode.rowIndex;
        var rowCount = document.getElementById('groupDetailTable').rows.length;
        if(rowCount > 3){
            document.getElementById('groupDetailTable').deleteRow(i);
        }
    }


    function insRow()
    {
        var x=document.getElementById('groupDetailTable');
        var new_row = x.rows[1].cloneNode(true);
        var inp1 = new_row.cells[0].getElementsByTagName('input')[0];
        inp1.value = '';
        $("#groupDetailTable tr:first").after(new_row);
    }

    $('#btnSavePublishGroup').click(function() {
        var groupPublishRequest = {}
        groupPublishRequest["groupName"] = $('#inputGroupName').val();
        var groupId = $('#inputGroupId').val();
        var groupDetails = [];
        var rows = document.getElementById("groupDetailTable").rows;
        var leng = rows.length;
        var index = 0;
        for (i = 1; i < leng ; i++) {
            var legal = rows[i].cells[0].getElementsByTagName('input')[0].value;
            if(legal && legal.length > 0){
                groupDetails[index] = legal;
                index++;
            }
        }
        groupPublishRequest["groupDetails"] = groupDetails;
        groupPublishRequest["groupId"] = groupId;
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/validate-group-publish",
            data : JSON.stringify(groupPublishRequest),
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
        var groupPublishRequest = {}
        groupPublishRequest["groupName"] = $('#inputGroupName').val();
        var groupId = $('#inputGroupId').val();
        groupPublishRequest["groupId"] = groupId;
        var groupDetails = [];
        var rows = document.getElementById("groupDetailTable").rows;
        var leng = rows.length;
        var index = 0;
        for (i = 1; i < leng ; i++) {
            var legal = rows[i].cells[0].getElementsByTagName('input')[0].value;
            if(legal && legal.length > 0){
                groupDetails[index] = legal;
                index++;
            }
        }
        groupPublishRequest["groupDetails"] = groupDetails;

        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        $.ajax({
            type : "POST",
            contentType : "application/json",
            url : ctx + "/backend/update-group-publish",
            data : JSON.stringify(groupPublishRequest),
            timeout : 100000,
            success: function(response) {
                if(response.error == 1){
                    document.getElementById("errMsg").innerHTML= response.errorMsg;
                }else{
                    window.location.href = ctx + "/backend/list-group-publish?pageNumber=0";
                }
            },
            error: function(){
            }
        });

    })

</script>