<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<ul class="ul">
    <li class="li home"><a
            href="${pageContext.request.contextPath}<c:if test="${loggedInUser!=null}">/</c:if>${loggedInUser.getRole()}/home">Home</a>
    </li>
    <li class="li services"><a
            href="${pageContext.request.contextPath}<c:if test="${loggedInUser!=null}">/</c:if>${loggedInUser.getRole()}/serviceList">Our
        services</a></li>
    <li class="li workers"><a
            href="${pageContext.request.contextPath}<c:if test="${loggedInUser!=null}">/</c:if>${loggedInUser.getRole()}/workerList">Our
        workers</a></li>
    <c:choose>
        <c:when test="${loggedInUser == null}">
            <li class="li login" style="float:right"><a href="${pageContext.request.contextPath}/login">Sign in</a></li>
            <li class="li newAccount" style="float:right"><a href="${pageContext.request.contextPath}/newAccount">New account</a>
            </li>
        </c:when>
        <c:when test="${loggedInUser.getRole() == 'client'}">
            <li class="li appointments"><a href="${pageContext.request.contextPath}/client/appointments">Appointments</a></li>
            <li class="li">Balance = ${loggedInUser.getMoneyBalance()} ₴</li>
            <li class="li"><a href="${pageContext.request.contextPath}/client/replenishBalance">+100</a></li>
        </c:when>
        <c:when test="${loggedInUser.getRole() == 'admin'}">
            <li class="li appointments"><a href="${pageContext.request.contextPath}/admin/appointments">Appointments</a></li>
            <li class="li">Balance = ${loggedInUser.getMoneyBalance()} ₴</li>
        </c:when>
        <c:when test="${loggedInUser.getRole() == 'worker'}">
            <li class="li schedule"><a href="${pageContext.request.contextPath}/worker/schedule">Your schedule</a></li>
        </c:when>
    </c:choose>

    <c:if test="${loggedInUser != null}">
        <li class="li" style="float:right; vertical-align: middle">${loggedInUser.getEmail()}</li>
        <li class="li" style="float:right"><a href="${pageContext.request.contextPath}/home">Sign out</a></li>
    </c:if>

</ul>
