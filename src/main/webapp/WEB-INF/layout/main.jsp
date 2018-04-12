<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./taglib.jsp" %>
<div class="hold-transition skin-blue sidebar-mini" style="height: 100%">

    <div class="wrapper" style="overflow: hidden">
        <!-- header -->
        <tiles:insertAttribute name="header" />

        <!-- nav-bar -->
        <tiles:insertAttribute name="nav-bar" />

        <!-- content -->
        <div class="content-wrapper" style="min-height: 600px">
            <%--<section class="content-header">
               管理者一覧
               <span class="glyphicon glyphicon-chevron-right"></span>
               管理者一覧
           </section>--%>

            <section class="content" style="min-height: 1200px;">
                <div class="row" id ="divLoading">
                </div>
                <tiles:insertAttribute name="content" />
            </section>

        </div>

        <!-- footer -->
        <tiles:insertAttribute name="footer" />
    </div>
    <!-- ./wrapper -->
</div>