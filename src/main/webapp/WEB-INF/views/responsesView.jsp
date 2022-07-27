<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Responses</title>
    <style>
        <%@include file="../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="_header.jsp"/>
<jsp:include page="_menu.jsp"/>

<h3>Responses about ${worker.firstName} ${worker.lastName}:</h3>

<table>
    <thead>
    <tr>
        <th>Service</th>
        <th>Rating</th>
        <th>Message</th>
        <th>Client</th>
        <th>Date</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${responseList}" var="response">
        <tr>
            <td>${response.service.name}</td>
            <td>${response.rating}</td>
            <td>${response.message}</td>
            <td>${response.client.firstName} ${response.client.lastName}</td>
            <td>${response.date}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>