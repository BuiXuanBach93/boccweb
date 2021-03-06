<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
    <li><a href="<%= request.getContextPath()%>/backend/list-admin-user">管理者一覧</a></li>
    <li><a href="#">管理者編集</a></li>
</ul>

<form:form action="update-admin" modelAttribute="ad" method="post">
    <div class="box box-default" style="margin-top: 20px">
        <div class="box-body" style="margin-top: 20px">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-12" style="color: red;">
                        ${errorMsg}
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label"> 名前</label>
                    <div class="col-md-5">
                        <form:input type="text" path="adminName" class="form-control" value="${shmAdmin.adminName}" required=""/>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-2"></div>
                    <div class="col-md-5">
                        <span style="color: red;">${errorAdminName}</span>
                        <form:errors path="adminName" class="control-label" style="color:red;" />
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">メール</label>
                    <div class="col-md-5">
                        <form:input type="text" path="adminEmail" class="form-control" value="${shmAdmin.adminEmail}" required=""/>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-2"></div>
                    <div class="col-md-5">
                        <span style="color: red;">${errorAdminEmail}</span>
                        <form:errors path="adminEmail" class="control-label" style="color:red;" />
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">新しいパスワード</label>
                    <div class="col-md-5">
                        <form:input type="password" path="pwdFresh" class="form-control"/>
                        <form:errors path="pwdFresh" class="control-label" style="color:red;" />
                    </div>
                    <div class="col-md-5">
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2"></label>
                    <div class="col-md-5">
                        <span style="color: red;">${errorFreshPwd}</span>
                        <form:errors path="pwdFresh" class="control-label" style="color:red;" />
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">ベリファイパスワード</label>
                    <div class="col-md-5">
                        <form:input type="password" path="confirmAdminPwd" class="form-control"/>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2"></label>
                    <div class="col-md-5">
                        <span style="color: red;">${errorConfirmPwd}</span>
                        <form:errors path="confirmAdminPwd" class="control-label" style="color:red;" />
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <label class="col-md-2 control-label">ステータス</label>
                    <div class="col-md-5">
                        <form:select path="adminRole" class=" form-control select-button">
                            <form:option value="${shmAdmin.adminRole}" label="選択する"/>
                            <form:options items="${roleList}" />
                        </form:select>
                    </div>
                    <div class="col-md-5"></div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <label class="col-md-2"></label>
                    <div class="col-md-5">
                        <span style="color: red;">${errorAdminRole}</span>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-2"></div>
                <div class="col-md-5" style="padding-left: 140px !important;">
                    <button type="button" class="btn btn-info btn-modal" data-toggle="modal" data-target=" .bg-primary-modal">
                        <i class="fa fa-floppy-o" aria-hidden="true"></i>
                        保存
                    </button>
                    <a href="<%= request.getContextPath()%>/backend/list-admin-user" class="btn btn-danger" role="button">
                        <span class="glyphicon glyphicon-remove"></span>
                        キャンセル
                    </a>
                </div>
                <div class="col-md-5"></div>
            </div>
        </div>
    </div>

    <div class="modal fade modal-sm bg-primary-modal " role="document" style="margin: 30px auto; background: none;">
        <div class="modal-content">
            <div class="modal-header bg-primary" style="border-top-left-radius: 0px; border-top-right-radius: 0px;">
                <h4 class="modal-title"><i class="glyphicon glyphicon-question-sign"></i> 確認する</h4>
            </div>
            <div class="modal-body">変更を保存しますか。</div>
            <div class="modal-footer">
                <button type="submit" class="btn btn-primary" data-confirm="Ok">保存</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">キャンセル</button>
            </div>
        </div>
    </div>
</form:form>