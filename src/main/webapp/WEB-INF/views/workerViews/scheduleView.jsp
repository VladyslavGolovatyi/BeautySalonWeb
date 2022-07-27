<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Schedule</title>
    <style>
        <%@include file="../../../style.css" %>
        .schedule {
            background-color: #b4b2b2;
        }
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>
<c:set var="totalCount" scope="session" value="${workingDaysInMonth.size()}"/>
<c:set var="perPage" scope="session" value="5"/>
<c:set var="pageStart" value="${param.page}"/>
<c:if test="${empty pageStart}">
    <c:set var="pageStart" value="1"/>
</c:if>

<div class="pagination">
        <a class="a" href="?page=${pageStart > 1?pageStart - 1:pageStart}"><<</a>
        <a class="text"><b>${pageStart}</b></a>
        <a class="a" href="?page=${(pageStart-1)*5+5 < totalCount?pageStart + 1:pageStart}">>></a>
</div>

<table>
    <thead>
    <tr>
        <th></th>
        <c:forEach items="${workingDaysInMonth}" var="day" varStatus="letterCounter"
                   begin="${(pageStart-1)*5}" end="${(pageStart-1)*5 + perPage - 1}">
            <th>${day}</th>
        </c:forEach>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${timeslots}" var="timeslot">
        <tr>
            <td>${timeslot}</td>
            <c:forEach items="${workingDaysInMonth}" var="day" varStatus="letterCounter"
                       begin="${(pageStart-1)*5}" end="${(pageStart-1)*5 + perPage - 1}">
                <c:choose>
                    <c:when test="${appointmentMap.containsKey(day.concat(\" \").concat(timeslot.split(\"-\")[0])) and
                    appointmentMap.get(day.concat(\" \").concat(timeslot.split(\"-\")[0])).status.contains(\"not done\")}">
                        <td class="red btn-flip" onclick="location.href='markAppointmentDone?id=${appointmentMap.get(day.concat(" ").concat(timeslot.split("-")[0])).id}'"
                            data-front="${appointmentMap.get(day.concat(" ").concat(timeslot.split("-")[0])).service.name}"
                            data-back="Done">
                        </td>
                    </c:when>
                    <c:when test="${appointmentMap.containsKey(day.concat(\" \").concat(timeslot.split(\"-\")[0]))}">
                        <td class="green">
                                ${appointmentMap.get(day.concat(" ").concat(timeslot.split("-")[0])).service.name}
                        </td>
                    </c:when>
                    <c:otherwise>
                        <td>
                            ---
                        </td>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>