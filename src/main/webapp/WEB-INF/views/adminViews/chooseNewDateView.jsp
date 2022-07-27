<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit an appointment</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<h3>Edit an appointment</h3>

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>

<form method="POST" action="chooseDate">
    <input type="hidden" name="id" value="${id}"/>
    Worker: ${worker.firstName} ${worker.lastName}
    <br><br>
    Working days:
    <ul>
        <c:forEach items="${worker.workingDays}" var="day">
            <li>
                    ${day}
            </li>
        </c:forEach>
    </ul>
    Choose new date:
    <input type="date" name="date" value="date" max="${maxDate}" min="${minDate}" required/><br><br>
    <button type="submit">Next</button>
    <button type="button" onclick="location.href='appointments'">Cancel</button>
</form>

</body>
</html>