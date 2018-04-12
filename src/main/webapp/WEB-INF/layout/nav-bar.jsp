<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%--
    Document   : menu
    Created on : Feb 20, 2017, 12:14:55 AM
    Author     : harunaga
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<aside class="main-sidebar sidebar-nav" style="position: absolute;">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar" style="height: 130%;">
        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">
            <li class="treeview navMenu">
                <a href="<%= request.getContextPath()%>/backend/">
                    <i class="fa fa-dashboard"></i>
                    <span>ダッシュボード</span>
                </a>
            </li>

            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/kpi-daily">
                        <i class="fa fa-bar-chart"></i>
                        <span>KPI 統計</span>
                    </a>
                </li>
            </security:authorize>

            <security:authorize access="isAuthenticated() and hasAuthority('SUPPER_ADMIN')">
                <li class="treeview navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-admin-user?">
                        <i class="fa fa-user-secret"></i>
                        <span>ユーザー権限設定マスタ</span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                <li class="treeview navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-admin-ng?pageNumber=0">
                        <i class="fa fa-warning"></i>
                        <span> NGワードマスタ</span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN,CUSTOMER_SUPPORT')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/posts?page=0">
                        <i class="fa fa-files-o"></i>
                        <span>投稿検索</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/users?page=0">
                        <i class="fa fa-user"></i> <span>ユーザー検索</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-qa?page=0">
                        <i class="fa fa-comments"></i>
                        <span>お問合わせ</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN,SITE_PATROL')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/post-patrol?page=0">
                        <i class="fa fa-sticky-note"></i>
                        <span>サイト パトロール (投稿)</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/post-patrol-sequent?sequentNumber=1">
                        <i class="fa fa-object-ungroup"></i> <span> 連続 パトロール( 投稿 )</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <c:url value="/backend/patrol/handle-user" var="patrolHandleUser"/>
                    <a href="${patrolHandleUser}">
                        <i class="fa fa-users"></i>
                        <span>サイト パトロール (ユーザー)</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/user-patrol-sequent?sequentNumber=1">
                        <i class="fa fa-user-circle-o"></i>
                        <span>連続 パトロール (ユーザー)</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/torino-csv">
                        <i class="fa fa-download"></i>
                        <span>基幹 システム 取り込み用 CSV</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/log-export-csv?page=0">
                        <i class="fa fa-sign-out"></i>
                        <span>CSV 出力 ログ</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-group-publish?pageNumber=0">
                        <i class="fa fa-files-o"></i>
                        <span style="white-space: normal">法人グループ設定</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/post-sample-upload">
                        <i class="glyphicon glyphicon-cloud-upload"></i>
                        <span style="white-space: normal">サンプルの投稿をアップロードしてください。</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>
            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-push?pageNumber=0">
                        <i class="glyphicon glyphicon-bullhorn"></i>
                        <span style="white-space: normal">PUSH通知送信</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>

            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-category-setting?pageNumber=0">
                        <i class="glyphicon glyphicon-filter"></i>
                        <span style="white-space: normal">カテゴリ管理・追加</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>

            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu">
                    <a href="<%= request.getContextPath()%>/backend/list-banner?pageNumber=0">
                        <i class="fa fa-flag-o"></i>
                        <span style="white-space: normal">特集バナー管理</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
            </security:authorize>

            <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN')">
                <li class="navMenu" id="systemConfig" style="padding-left: 15px;">
                    <i class="fa fa-cogs" style="margin-right: 5px;"></i>
                    <span >システム構成</span>
                <ul class="subMenu" style="display: none">
                <li>
                    <a id="systemConfigMaintain" href="<%= request.getContextPath()%>/backend/maintain-system">
                        <i class="glyphicon glyphicon-wrench"></i>
                        <span>メンテナンス設定</span>
                        <span class="pull-right-container">
                        </span>
                    </a>
                </li>
                    <li>
                        <a id="systemConfigValidVersion" href="<%= request.getContextPath()%>/backend/valid-version">
                            <i class="glyphicon glyphicon-wrench"></i>
                            <span>有効なバージョン</span>
                            <span class="pull-right-container">
                        </span>
                        </a>
                    </li>
                    <li>
                        <a href="<%= request.getContextPath()%>/backend/topic-migration">
                            <i class="glyphicon glyphicon-wrench"></i>
                            <span>PUSH通知配信用移行</span>
                            <span class="pull-right-container">
                        </span>
                        </a>
                    </li>
                    <li>
                        <a id="systemConfigKpiSync" href="<%= request.getContextPath()%>/backend/kpi-sync">
                            <i class="glyphicon glyphicon-wrench"></i>
                            <span>KPIの過去データ同期</span>
                            <span class="pull-right-container">
                        </span>
                        </a>
                    </li>
                    <li>
                        <a id="systemConfigSyncDate" href="<%= request.getContextPath()%>/backend/sync-date-config">
                            <i class="glyphicon glyphicon-wrench"></i>
                            <span>自動退会処理日設定</span>
                            <span class="pull-right-container">
                        </span>
                        </a>
                    </li>
                    <li>
                        <a id="systemUserLeftSync" href="<%= request.getContextPath()%>/backend/user-left-sync">
                            <i class="glyphicon glyphicon-wrench"></i>
                            <span>手動退会処理</span>
                            <span class="pull-right-container">
                        </span>
                        </a>
                    </li>
                </ul>
                </li>
            </security:authorize>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<script>
   // $(".navMenu .subMnu").hide();
    $(document).ready(function() {
        $("#systemConfig").click(function () {
            $(".subMenu").toggle("fast");
        })
    });
</script>
<style type="text/css">
    #systemConfig {
        border-left: 3px solid transparent;
        padding: 6px 5px 6px 15px;
    }

    #systemConfig:hover{
        border-left: 3px solid #367fa9;
        color: white;
        position: relative;
        display: block;
        background-color: transparent;
        box-sizing: border-box;
    }
    .subMenu:hover {
         color: white;
     }
    .subMenu li {
        padding: 3px 3px 6px 8px;
    }
</style>
