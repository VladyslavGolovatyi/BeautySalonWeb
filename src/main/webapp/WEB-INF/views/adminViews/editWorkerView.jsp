<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Worker</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<h3>Edit Worker</h3>

<p style="color: red;">${errorString}</p>

<c:if test="${not empty worker}">
    <form method="POST" action="${pageContext.request.contextPath}/admin/editWorker">
        <input type="hidden" name="email" value="${worker.email}"/>
            ${worker.first_name} ${worker.last_name}
        <br>
        ${worker.email}
        <table>
            <tr>
                <td>Phone number</td>
                <td><input type="text" name="phone_number" value="${worker.phone_number}"/></td>
            </tr>
            <tr>
                <td></td>
                <td>Services</td>
            </tr>
            <c:forEach items="${serviceList}" var="service">
                <tr>
                    <td>${service.name}</td>
                    <td><input type="checkbox" name="service" value="${service.name}"
                               <c:if test="${workerServices.contains(service.name)}">checked</c:if>/> </td>
                </tr>
            </c:forEach>
            <tr>
                <td></td>
                <td>Working days</td>
            </tr>
            <c:forEach items="${days}" var="day">
                <tr>
                    <td>${day}</td>
                    <td><input type="checkbox" name="workingDay" value=${day}
                            <c:if test="${workingDays.contains(day)}">checked</c:if>
                    /></td>
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
</c:if>

</body>
</html>