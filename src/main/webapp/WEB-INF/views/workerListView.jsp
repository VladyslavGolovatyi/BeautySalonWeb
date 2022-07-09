<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Worker List</title>
    <style>
        <%@include file="../../style.css" %>
    </style>
</head>

<jsp:include page="_header.jsp"/>
<jsp:include page="_menu.jsp"/>

<h3>Worker List</h3>

<p style="color: red;">${errorString}</p>

<form method="POST" action="${pageContext.request.contextPath}/workerList">
    Sorted by
    <input type="radio" name="sorting" value="first_name" checked/>First name
    <input type="radio" name="sorting" value="rating" <c:if test="${isSortedByRating}">checked</c:if>>Rating
    <br>
    <br>
    Filter by services
    <br>
    <c:forEach items="${serviceList}" var="service">
        <input type="checkbox" name="filter" value="${service.name}"
               <c:if test = "${filterListForWorkers.contains(service.name)}">checked</c:if>
        />${service.name}
    </c:forEach>
    <br>
    <button type="submit">Apply</button>
</form>
<form>
    <button type="submit" onclick="${filterListForWorkers = null}">Reset</button>
</form>

<table>
    <thead>
    <tr>
        <th>First name</th>
        <th>Last name</th>
        <c:if test="${loggedInUser.getRole() == 'admin'}">
            <th>Email</th>
            <th>Phone number</th>
            <th>Password</th>
        </c:if>
        <th>Services</th>
        <th>Rating</th>
        <th>Working days</th>
        <c:if test="${loggedInUser.getRole() == 'admin'}">
            <th>Edit</th>
            <th>Delete</th>
        </c:if>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${workerList}" var="worker">
        <tr>
            <td>${worker.first_name}</td>
            <td>${worker.last_name}</td>
            <c:if test="${loggedInUser.getRole() == 'admin'}">
                <td>${worker.email}</td>
                <td>${worker.phone_number}</td>
                <td>${worker.password}</td>
            </c:if>
            <td>
                <ul>
                    <c:forEach items="${worker.services}" var="service">
                        <li>${service.name}</li>
                    </c:forEach>
                </ul>
            </td>
            <td>${worker.rating}</td>
            <td>
                <ul>
                    <c:forEach items="${worker.workingDays}" var="day">
                        <li>${day}</li>
                    </c:forEach>
                </ul>
            </td>
            <c:if test="${loggedInUser.getRole() == 'admin'}">
                <td>
                    <button onclick="location.href='editWorker?email=${worker.email}'" type="submit">Edit</button>
                </td>
                <td>
                    <button onclick="location.href='deleteWorker?email=${worker.email}'" type="submit">Delete</button>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${loggedInUser.getRole() == 'admin'}">
    <button onclick="location.href='addWorker'" type="submit">Add Worker</button>
</c:if>

</body>
</html>