<%@ taglib prefix="for" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Namlong
  Date: 4/16/2017
  Time: 12:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="row rowCus" style="display: inline-block">
    <nav aria-label="Pages">
        <ul class="pagination">
            <li style="display: inline-block; float: left;">
                <security:authorize access="isAuthenticated() and hasAnyAuthority('SUPPER_ADMIN,ADMIN')">
                    <input type="button" style="margin-right: 10px" id="exportUserPatrolCsvBtn" class="btn btn-primary" value="CSV出力"></input>
                </security:authorize>
            </li>
            <li style="display: inline-block; margin-top: 9px; font-weight: bold; float: left; margin-right: 10px;" id="totalElements"></li>
            <li id="prevPageButton" onclick="prevPage(this);"><a class="page-link"> &lt; </a></li>
            <li id="nextPageButton" onclick="nextPage(this);"><a class="page-link"> &gt; </a></li>
        </ul>
    </nav>
</div>
