<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User appointments</title>
    <style>
        <%@include file="../../style.css" %>
        .appointments {
            background-color: #b4b2b2;
        }
    </style>
</head>
<body>

<jsp:include page="_header.jsp"/>
<jsp:include page="_menu.jsp"/>

<h3>Appointments:</h3>

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>
<table>
    <thead>
    <tr>
        <th>Service</th>
        <th>DateTime</th>
        <th>Status</th>
        <c:if test="${loggedInUser.role=='client'}">
            <th>Worker</th>
            <th>To pay</th>
            <th>Cancel</th>
            <th>Leave response</th>
        </c:if>
        <c:if test="${loggedInUser.role=='admin'}">
            <th>Worker</th>
            <th>Client</th>
            <th>Client email</th>
            <th>Client phone number</th>
            <th>Edit</th>
            <th>Cancel</th>
        </c:if>
        <c:if test="${loggedInUser.role=='worker'}">
            <th>Client</th>
            <th>Mark as done</th>
        </c:if>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${appointmentList}" var="appointment">
        <tr>
            <td>${appointment.service.name}</td>
            <td>${appointment.timeslot}</td>
            <td>${appointment.status}</td>
            <c:if test="${loggedInUser.role=='client'}">
                <td>${appointment.worker.firstName} ${appointment.worker.lastName}</td>
                <td>
                    <button onclick="location.href='payForAppointment?id=${appointment.id}'" type="submit">Pay</button>
                </td>
                <td>
                    <button onclick="location.href='deleteAppointment?id=${appointment.id}'" type="submit">Cancel</button>
                </td>
                <td>
                    <button onclick="location.href='leaveFeedback?id=${appointment.id}'" type="submit">Response</button>
                </td>
            </c:if>
            <c:if test="${loggedInUser.role=='admin'}">
                <td>${appointment.worker.firstName} ${appointment.worker.lastName}</td>
                <td>${appointment.client.firstName} ${appointment.client.lastName}</td>
                <td>${appointment.client.email}</td>
                <td>${appointment.client.phoneNumber}</td>
                <td>
                    <button onclick="location.href='chooseDate?id=${appointment.id}'" type="submit">Edit</button>
                </td>
                <td>
                    <button onclick="location.href='deleteAppointment?id=${appointment.id}'" type="submit">Cancel</button>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>