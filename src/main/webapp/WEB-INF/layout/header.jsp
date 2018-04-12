<%-- 
    Document   : header
    Created on : Feb 20, 2017, 12:14:14 AM
    Author     : harunaga
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<header class="main-header">

    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>

    <!-- Logo -->
    <a href="${pageContext.request.contextPath}/backend/" class="logo">
        <!-- mini logo for sidebar mini 50x50 pixels -->
        <span class="logo-mini"><b>A</b>LT</span>
        <!-- logo for regular state and mobile devices -->
        <span class="logo-lg">Admin Worker Market</span>
    </a>

    <!-- Header Navbar: style can be found in header.less -->
    <nav class="navbar navbar-static-top">
        <!-- Sidebar toggle button-->
        <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
        </a>
        <!-- Navbar Right Menu -->
        <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
                <!-- User Account: style can be found in dropdown.less -->
                <li class="dropdown user user-menu">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown" style="margin-left: 50px">
                        <img src="${pageContext.request.contextPath}/resources/images/japan.png" class="user-image" alt="User Image">
                        <%--<span class="hidden-xs">Alexander Dastan</span>--%>
                        <security:authorize access="isAuthenticated()">
                            こんにちは : <security:authentication property="principal.username" />
                        </security:authorize>
                    </a>
                    <ul class="dropdown-menu logout-div">
                        <li class="user-footer change-pass-button" style="width: 60%; float:left;">
                            <div class="pull-left" class="btn btn-primary btn-flat">
                                <a class="btn btn-primary btn-flat" href="<%= request.getContextPath()%>/backend/change-admin-password">
                                    パスワード再設定
                                </a>
                            </div>
                        </li>
                        <li class="user-footer logout-button" style="width: 40%; float:right;">
                            <div class="pull-right" class="btn btn-primary btn-flat">
                                <%--<a href="<c:url value="/backend/logout" />" class="btn btn-primary btn-flat">ログアウト</a>--%>
                                    <spring:url var="logoutUrl" value="/backend/logout"/>
                                    <form action="${logoutUrl}"
                                          method="post" id ="logoutForm">
                                        <label class="btn btn-primary btn-flat" onclick="submitLogout()">ログアウト</label>
                                        <input type="hidden"
                                               name="${_csrf.parameterName}"
                                               value="${_csrf.token}"/>
                                    </form>
                                <script>
                                    function submitLogout() {
                                        document.getElementById("logoutForm").submit();
                                    }
                                </script>
                            </div>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>

    </nav>
</header>
