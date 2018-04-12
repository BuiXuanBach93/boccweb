<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="../../layout/taglib.jsp" %>
    <div class="hold-transition login-page wrapper" style="background-color: #3c8dbc; height: 100%">
        <div class="login-box">
            <div class="login-logo">
                <a href="#l" style="color: #FFFFFF"><b>BOCC</b>ログイン</a><br>
            </div>
            <!-- /.login-logo -->
            <div class="login-box-body" style="padding: 50px">
                <c:if test="${param.changepwd != null}">
                    <div class="alert alert-info">
                        <strong>再度新しいパスワードでログインをしてください</strong>
                    </div>
                </c:if>
                <form class="" role="form" action="<spring:url value='/backend/login' />" method="POST">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger" id="wrongAdmInf">
                            <strong>指定したログインID、またはパスワードが間違っています。もう一度入力しなおしてください。</strong>
                        </div>
                        <br>
                    </c:if>
                    <div class="form-group has-feedback">
                        <input type="text" name="username" class="form-control" id="userId" placeholder="ログインID"
                               required="" autofocus>
                        <span class="glyphicon glyphicon-user form-control-feedback"></span>
                    </div>
                    <div class="form-group has-feedback">
                        <input type="password" name="password" class="form-control" id="password" placeholder="パスワード"
                               required=""/>
                        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                    </div>
                    <!-- /.col -->
                    <div class="form-group has-feedback">
                        <button type="submit" class="btn btn-primary btn-block btn-flat">ログイン</button>
                    </div>
                    <!-- /.col -->
                </form>
                <div class="form-group has-feedback" hidden>
                    <a href="#">ログイン状態を保存する？</a>
                </div>
            </div>
            <!-- /.login-box-body -->
        </div>
        <!-- /.login-box -->

    </div>
<script>
    $(".alert-info").delay(3000).slideUp(200, function () {
        $(this).alert('close');
    });
</script>