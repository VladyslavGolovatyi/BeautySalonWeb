<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Worker</title>
    <style><%@include file="../../../style.css" %></style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<h3>Add Worker</h3>

<p style="color: red;">${errorString}</p>

<form method="POST" action="${pageContext.request.contextPath}/admin/addWorker">
    <table>
        <tr>
            <td>First name</td>
            <td><input type="text" name="first_name" value= "${worker.first_name}"/></td>
        </tr>
        <tr>
            <td>Last name</td>
            <td><input type="text" name="last_name" value= "${worker.last_name}"/></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type="text" name="email" value= "${worker.email}"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="text" name="password" value= "${worker.password}"/></td>
        </tr>
        <tr>
            <td>Phone number</td>
            <td><input type="text" name="phone_number" value= "${worker.phone_number}"/></td>
        </tr>
        <tr>
            <td></td>
            <td>Services</td>
        </tr>
        <c:forEach items="${serviceList}" var="service" >
            <tr>
                <td>${service.name}</td>
                <td><input type = "checkbox" name = "service" value = "${service.name}"/></td>
            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td>Working days</td>
        </tr>
        <c:forEach items="${days}" var="day">
            <tr>
                <td>${day}</td>
                <td><input type = "checkbox" name = "workingDay" value = ${day}></td>
            </tr>
        </c:forEach>
        <tr>
            <td>
                <button type="submit">Submit</button>
            </td>
            <td>
                <button type="button" onclick="location.href='${pageContext.request.contextPath}/workerList'">Cancel</button>
            </td>
        </tr>
    </table>
</form>

</body>
</html>