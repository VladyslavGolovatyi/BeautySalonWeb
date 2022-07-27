<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Service</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>

<form method="POST" action="addService">
    <table>
        <thead>
        <tr>
            <th colspan="2">Add Service</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>Name</td>
            <td><input type="text" name="name"/></td>
        </tr>
        <tr>
            <td>Price in UAH</td>
            <td><input type="text" name="price"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Submit</button>
                <button type="button" onclick="location.href='serviceList'">Cancel</button>
            </td>
        </tr>
        </tbody>
    </table>
</form>

</body>
</html>