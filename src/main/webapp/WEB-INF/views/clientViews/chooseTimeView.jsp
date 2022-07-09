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

<c:if test="${not empty name}">
    <form method="POST" action="${pageContext.request.contextPath}/client/chooseTime">
        <input type="hidden" name="name" value="${name}"/>
        <input type="hidden" name="date" value="${date}"/>
        <input type="hidden" name="workerEmail" value="${worker.email}"/>
        <input type="hidden" name="clientEmail" value="${loggedInUser.email}"/>
        Service: ${name}<br><br>
        Date: ${date}<br><br>
        Specialist: ${worker.first_name} ${worker.last_name}<br><br>
        Choose time:
        <select name="slots" id ="slots">
            <c:forEach items="${slotList}" var="slot">
                <option value="${slot}">${slot}</option>
            </c:forEach>
        </select><br><br>
        <button type="submit">Submit</button>
        <button type="button" onclick="location.href='${pageContext.request.contextPath}/serviceList'">Cancel</button>
    </form>
</c:if>

</body>
</html>