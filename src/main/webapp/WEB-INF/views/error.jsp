<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<header>
    <style>
        .center {
            text-align: center;
            margin-left: auto;
            margin-right: auto;
            margin-bottom: auto;
            margin-top: auto;
        }

        .backgroundError {
            background-color: white;
            /*background-repeat: no-repeat;*/
            /*background-position: center;*/
            height: 100%;
            width: 100%;
            /*background-size: cover;*/
        }

    </style>
</header>
<div class="container backgroundError">
        <div class="span12 ">
            <div class="hero-unit center">
                <h1>Page Not Found
                    <small><font face="Tahoma" color="red">Error 404</font></small>
                </h1>
                <h2>The page you requested could not be found, either contact your webmaster or try again.</h2>
                <%--<p><b>Or you could just press this neat little button:</b></p>--%>
                <%--<a href="#" class="btn btn-large btn-info"><i class="icon-home icon-white"></i> Take Me Home</a>--%>

                <img src="${pageContext.request.contextPath}/resources/images/bocc.png" class="img-circle" alt="worker's market" width="65%" height="70%" max-width: 100%;>
            </div>
    </div>
</div>

