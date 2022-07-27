<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Worker</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>

<form method="POST" action="addWorker">
    <table>
        <thead>
        <tr>
            <th colspan="2">Add Worker</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>First name</td>
            <td><input type="text" name="firstName" value="${worker.firstName}"/></td>
        </tr>
        <tr>
            <td>Last name</td>
            <td><input type="text" name="lastName" value="${worker.lastName}"/></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type="email" name="email" value="${worker.email}"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="text" name="password" value="${worker.password}"/></td>
        </tr>
        <tr>
            <td>Phone number</td>
            <td><input type="text" name="phoneNumber" value="${worker.phoneNumber}"/></td>
        </tr>
        <tr>
            <td colspan="2">Services</td>
        </tr>
        <c:forEach items="${serviceList}" var="service">
            <tr>
                <td>${service.name}</td>
                <td><input type="checkbox" name="service" value="${service.name}"
                           <c:if test="${workerServices.contains(service.name)}">checked</c:if>/></td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">Working days</td>
        </tr>
        <c:forEach items="${days}" var="day">
            <tr>
                <td>${day}</td>
                <td><input type="checkbox" name="workingDay" value=${day}
                        <c:if test="${worker.workingDays.contains(day)}">checked</c:if>/></td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">
                <button type="submit">Submit</button>
                <button type="button" onclick="location.href='workerList'">Cancel</button>
            </td>
        </tr>
        </tbody>
    </table>
</form>
<%session.removeAttribute("errorString");%>
<%session.removeAttribute("worker");%>
<%session.removeAttribute("workerServices");%>
</body>
</html>