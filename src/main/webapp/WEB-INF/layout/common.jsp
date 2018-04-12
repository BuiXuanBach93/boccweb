<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="./taglib.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title><tiles:getAsString name="title" /></title>
        <!-- Tell the browser to be responsive to screen width -->
        <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
        <!-- Bootstrap 3.3.6 -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.min.css">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
        <!-- Ionicons -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/ionicons/2.0.1/css/ionicons.min.css">
        <!-- jvectormap -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jquery/jquery-ui.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/plugins/jvectormap/jquery-jvectormap-1.2.2.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/jquery/jquery.datetimepicker.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/plugins/simditor/simditor.css"/>
        <link rel='shortcut icon' type='image/x-icon' href='${pageContext.request.contextPath}/resources/images/bocc_favicon.png'/>

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->

        <tilesx:useAttribute id="stylesheets" name="stylesheets" classname="java.util.List" />
        <tilesx:useAttribute id="javascripts" name="javascripts" classname="java.util.List" />
        <c:forEach var="css" items="${stylesheets}">
            <link rel="stylesheet" href="<c:url value="${css}"/>">
        </c:forEach>
        <style type="text/css">
            .main-sidebar{
                position: relative;
            }
        </style>
        <!-- jQuery 2.2.3 -->
        <script src="${pageContext.request.contextPath}/resources/plugins/jQuery/jquery-2.2.3.min.js"></script>
        <script src="${pageContext.request.contextPath}/resources/plugins/jQueryUI/jquery-ui.js"></script>
        <!-- Bootstrap 3.3.6 -->
        <script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.min.js"></script>
        <!-- FastClick -->
        <script src="${pageContext.request.contextPath}/resources/plugins/fastclick/fastclick.js"></script>
        <!-- Sparkline -->
        <script src="${pageContext.request.contextPath}/resources/plugins/sparkline/jquery.sparkline.min.js"></script>

        <!-- jvectormap -->
        <script src="${pageContext.request.contextPath}/resources/plugins/jvectormap/jquery-jvectormap-1.2.2.min.js"></script>

        <script src="${pageContext.request.contextPath}/resources/plugins/jvectormap/jquery-jvectormap-world-mill-en.js"></script>
        <!-- SlimScroll 1.3.0 -->
        <script src="${pageContext.request.contextPath}/resources/plugins/slimScroll/jquery.slimscroll.min.js"></script>

        <!-- ChartJS 1.0.1 -->
        <script src="${pageContext.request.contextPath}/resources/plugins/chartjs/Chart.min.js"></script>
        <%--DateTime picker--%>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.9.0/moment-with-locales.js"></script>
        <script src="${pageContext.request.contextPath}/resources/js/datepicker-ja.js"></script>
        <script src="${pageContext.request.contextPath}/resources/js/app.js"></script>
        <script src="${pageContext.request.contextPath}/resources/jquery/jquery.datetimepicker.full.js"></script>

        <!-- Simditor -->
        <script src="${pageContext.request.contextPath}/resources/plugins/simditor/module.js"></script>
        <script src="${pageContext.request.contextPath}/resources/plugins/simditor/hotkeys.js"></script>
        <script src="${pageContext.request.contextPath}/resources/plugins/simditor/uploader.js"></script>
        <script src="${pageContext.request.contextPath}/resources/plugins/simditor/simditor.js"></script>

        <!-- 	All JS Files -->  
        <c:forEach var="script" items="${javascripts}">
            <script src="<c:url value="${script}"/>"></script>
        </c:forEach>
        <script>
            $(document).ready(function(){
                $('[data-toggle="tooltip"]').tooltip();
            });
        </script>
    </head>
    <body style="min-height: 1200px; background-color: #222d32;">
        <tilesx:useAttribute name="body_template" />
        <jsp:include page="${body_template}" />

    </body>
</html>
