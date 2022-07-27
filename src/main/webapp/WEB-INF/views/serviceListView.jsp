<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Service List</title>
    <style>
        <%@include file="../../style.css" %>
        .services {
            background-color: #b4b2b2;
        }
    </style>
</head>
<body>

<jsp:include page="_header.jsp"/>
<jsp:include page="_menu.jsp"/>

<h3>Service List</h3>

<p style="color: red;">${errorString}</p>

<form method="POST" action="serviceList">
    <i>Filter by worker</i>
    <br>
    <c:forEach items="${workerList}" var="worker">
        <input type="radio" name="filter" value="${worker.email}" onclick="this.form.submit()"
               <c:if test="${worker.email.equals(filterForServices)}">checked</c:if>
        />${worker.firstName} ${worker.lastName}
    </c:forEach>
    <br>
</form>
<form>
    <button type="submit" onclick="${filterForServices = null}">Reset</button>
</form>
<table>
    <thead>
    <tr>
        <th>Name</th>
        <th>Price in UAH</th>
        <c:if test="${loggedInUser.getRole() == 'admin'}">
            <th>Edit</th>
            <th>Delete</th>
        </c:if>
        <c:if test="${loggedInUser.getRole() == 'client'}">
            <th>To make an appointment</th>
        </c:if>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${serviceList}" var="service">
        <tr>
            <td>${service.name}</td>
            <td>${service.price}</td>
            <c:if test="${loggedInUser.getRole() == 'admin'}">
                <td>
                    <button onclick="location.href='editService?name=${service.name}'" type="submit">Edit</button>
                </td>
                <td>
                    <button onclick="location.href='deleteService?name=${service.name}'" type="submit">Delete</button>
                </td>
            </c:if>
            <c:if test="${loggedInUser.getRole() == 'client'}">
                <td>
                    <button onclick="location.href='chooseDate?name=${service.name}'" type="submit">Appointment</button>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${loggedInUser.getRole() == 'admin'}">
    <button onclick="location.href='addService'" type="submit">Add Service</button>
</c:if>

</body>
</html>