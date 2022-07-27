<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Make an appointment</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<h3>Make an appointment</h3>

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>

<c:if test="${not empty name}">
    <form method="POST" action="${pageContext.request.contextPath}/client/chooseDate">
        <input type="hidden" name="name" value="${name}"/>
        Service: ${name}<br><br>
        Choose date:
        <input type="date" name="date" value="date" max = "${maxDate}" min = "${minDate}" required/><br><br>
        <button type="submit">Next</button>
        <button type="button" onclick="location.href='serviceList'">Cancel</button>
    </form>
</c:if>

</body>
</html>