<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-admin-ng">NGワードマスタ</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/create-admin-ng">NG ワード 登録</a></li>
</ul>

<form:form action="add-admin-ng" modelAttribute="adminNg" method="POST">
    <div class="box box-default" style="margin-top: 20px;">
        <div class="box-body" style="margin-top: 10px; padding: 10px">
            <div class="box-tools clearfix">
                <div class="form-group pull-left ">
                    <label>NG ワード 登録</label>
                </div>
            </div>

            <div class="row">
                <div class="col-md-1"><label style="line-height: 34px">言葉</label></div>
            </div>

            <div class="row">
                <div class="col-md-6">
                    <form:input type="text" path="adminNgContent" class="form-control" maxlength="100"/>
                    <span style="color: red;">${errorAdminNgContent}</span>
                    <form:errors path="adminNgContent" class="control-label" style="color:red;" />
                </div>
            </div>

            <div class="row" style="margin-top: 20px; margin-bottom: 20px">
                <div class="col-md-4">
                    <button type="button" class="btn btn-success" data-toggle="modal"  data-target=".bg-primary-modal-ok">
                        <i class="fa fa-floppy-o" aria-hidden="true"></i>
                        保存
                    </button>
                    <button type="button" class="btn btn-danger" data-toggle="modal"  data-target=".bg-primary-modal-cancel">
                        <i class="fa fa-times" aria-hidden="true"></i>
                        キャンセル
                    </button>
                </div>

                <div class="col-md-7"></div>
            </div>

            <div class="modal fade modal-sm bg-primary-modal-ok " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                    </div>
                    <div class="modal-body">この言葉を作成しますか。</div>
                    <div class="modal-footer">
                        <button type="submit" class="btn btn-primary" data-confirm="Ok">OK</button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

            <div class="modal fade modal-sm bg-primary-modal-cancel " role="document" style="margin: 30px auto; background: none;">
                <div class="modal-content">
                    <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                        <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
                    </div>
                    <div class="modal-body">キャンセルしますか。</div>
                    <div class="modal-footer">
                        <button type="button" id="btnOkCancelNG" class="btn btn-primary"><a href="<%= request.getContextPath()%>/backend/list-admin-ng?pageNumber=0" style="color: white">OK</a></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
                    </div>
                </div>
            </div>

        </div>
    </div>
</form:form>
<script>
    $('#btnOkCancelNG').click(function() {
        var url = document.URL;
        var contextPath = "<%= request.getContextPath()%>";
        url = url.substring(0, url.indexOf(contextPath));
        window.location = url + contextPath + "/backend/list-admin-ng";
    });
</script>