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

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>

<c:if test="${not empty worker}">
    <form method="POST" action="${pageContext.request.contextPath}/admin/editWorker">
        <input type="hidden" name="id" value="${worker.id}"/>
        <table>
            <thead>
            <tr>
                <th colspan="2">Edit Worker</th>
            </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Full name</td>
                    <td>${worker.firstName} ${worker.lastName}</td>
                </tr>
                <tr>
                    <td>Email</td>
                    <td><input type="text" name="email" value="${worker.email}"/></td>
                </tr>
                <tr>
                    <td>Phone number</td>
                    <td><input type="tel" name="phoneNumber" value="${worker.phoneNumber}" pattern="\+[0-9]{9,15}"
                               placeholder="+3801234567"/></td>
                </tr>
                <tr>
                    <td colspan="2">Services</td>
                </tr>
                <c:forEach items="${serviceList}" var="service">
                    <tr>
                        <td>${service.name}</td>
                        <td><input type="checkbox" name="service" value="${service.name}"
                                   <c:if test="${workerServices.contains(service)}">checked</c:if>/></td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="2">Working days</td>
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
                    <td colspan="2">
                        <button type="submit">Submit</button>
                        <button type="button" onclick="location.href='${pageContext.request.contextPath}/workerList'">
                            Cancel
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </form>
</c:if>

</body>
</html>