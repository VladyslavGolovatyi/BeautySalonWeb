<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Feedback</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>
<%
    int[] arr = new int[]{-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5};
%>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>

<form method="POST" action="leaveFeedback">
    <input type="hidden" name="id" value="${id}"/>
    <table>
        <thead>
        <tr>
            <th colspan="2">Leave your feedback</th>
        </tr>
        </thead>
        <tr>
            <td>Service</td>
            <td>${appointment.service.name}</td>
        </tr>
        <tr>
            <td>Datetime</td>
            <td>${appointment.timeslot}</td>
        </tr>
        <tr>
            <td>Worker</td>
            <td>${appointment.worker.firstName} ${appointment.worker.lastName}</td>
        </tr>
        <tr>
            <td>Rate worker</td>
            <td>
                <c:forEach items="<%=arr%>" var="a">
                    <input type="radio" id="${a}" name="rating" value="${a}">
                    <label for=name="rating""${a}">${a}</label>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td>Your response</td>
            <td>
                <label>
<textarea rows="7" cols="60" name="message">
</textarea>
                </label>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit" onclick="${errorString = null}">Submit</button>
                <button type="button" onclick="location.href='appointments'">Cancel</button>
            </td>
        </tr>
    </table>
</form>

</body>
</html>