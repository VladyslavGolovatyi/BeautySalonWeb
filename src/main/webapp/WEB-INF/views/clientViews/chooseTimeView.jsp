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

<form method="POST" action="chooseTime">
    <input type="hidden" name="name" value="${name}"/>
    <input type="hidden" name="date" value="${date}"/>
    <input type="hidden" name="workerId" value="${worker.id}"/>
    <input type="hidden" name="clientId" value="${loggedInUser.id}"/>
    Service: ${name}<br><br>
    Date: ${date}<br><br>
    Specialist: ${worker.firstName} ${worker.lastName}<br><br>
    Choose time:
    <select name="slots" id="slots">
        <c:forEach items="${slotList}" var="slot">
            <option value="${slot}">${slot}</option>
        </c:forEach>
    </select><br><br>
    <button type="submit">Submit</button>
    <button type="button" onclick="location.href='serviceList'">Cancel</button>
</form>

</body>
</html>