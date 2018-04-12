<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<ul class="breadcrumb">
    <li><a href="<%= request.getContextPath()%>/backend/">ダッシュ ボード</a></li>
</ul>

<div class="box box-default" style="margin-top: 20px; height: 600px;">
    <div class="box-body" style="margin-top: 30px">
        <security:authorize access="isAuthenticated() and hasAuthority('SUPPER_ADMIN')">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-admin-user?">ユーザー権限設定マスタ</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-admin-ng?pageNumber=0">NGワードマスタ</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/posts?page=0">投稿検索</a>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/users?page=0">ユーザー検索</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-qa?page=0"> お問合わせ</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol?page=0">サイト パトロール (投稿)</a>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol-sequent?sequentNumber=1"> 連続 パトロール( 投稿 )</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/patrol/handle-user">サイト パトロール (ユーザー)</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/user-patrol-sequent?sequentNumber=1">連続 パトロール (ユーザー)</a>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/torino-csv">基幹システム取込用CSV出力</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/maintain-system">メンテナンス設定</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-push?pageNumber=0">PUSH通知送信</a>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-group-publish?pageNumber=0">法人グループ設定</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-category-setting?pageNumber=0">カテゴリ管理・追加</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-banner?pageNumber=0">特集バナー管理</a>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/kpi-daily">KPI統計</a>
                    </div>
                </div>
            </div>

        </security:authorize>

        <security:authorize access="isAuthenticated() and hasAuthority('ADMIN')">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-admin-ng?pageNumber=0">NGワードマスタ</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/posts?page=0">投稿検索</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/users?page=0">ユーザー検索</a>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-qa?page=0"> お問合わせ</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol?page=0">サイト パトロール (投稿)</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol-sequent?sequentNumber=1"> 連続 パトロール( 投稿 )</a>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/patrol/handle-user">サイト パトロール (ユーザー)</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/user-patrol-sequent?sequentNumber=1">連続 パトロール (ユーザー)</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/torino-csv">基幹システム取込用CSV出力</a>
                    </div>
                </div>
            </div>
        </security:authorize>

        <security:authorize access="isAuthenticated() and hasAuthority('SITE_PATROL')">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol?page=0">サイト パトロール (投稿)</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/post-patrol-sequent?sequentNumber=1"> 連続 パトロール( 投稿 )</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/patrol/handle-user">サイト パトロール (ユーザー)</a>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/user-patrol-sequent?sequentNumber=1">連続 パトロール (ユーザー)</a>
                    </div>
                </div>
            </div>
        </security:authorize>

        <security:authorize access="isAuthenticated() and hasAuthority('CUSTOMER_SUPPORT')">
            <div class="form-group">
                <div class="row">
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/posts?page=0">投稿検索</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/users?page=0">ユーザー検索</a>
                    </div>
                    <div class="col-md-4">
                        <a type="button" class="btn btn-block btn-primary btn-lg"
                           href="<%= request.getContextPath()%>/backend/list-qa?page=0"> お問合わせ</a>
                    </div>
                </div>
            </div>
        </security:authorize>
    </div>
</div>