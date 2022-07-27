<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        <%@include file="../../../style.css" %>
        .newAccount {
            background-color: #b4b2b2;
        }
    </style>
    <c:if test="${errorString.equals(\"User with this email already existing\")}">
        <script>
            window.alert("${errorString}")
        </script>
    </c:if>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>


<form method="POST" action="newAccount">
    <table>
        <thead>
            <tr>
                <th colspan="2">New Account</th>
            </tr>
        </thead>
        <tr>
            <td>First name</td>
            <td><input type="text" name="firstName" value="${user.firstName}"/></td>
        </tr>
        <tr>
            <td>Last name</td>
            <td><input type="text" name="lastName" value="${user.lastName}"/></td>
        </tr>
        <tr>
            <td>Phone number</td>
            <td><input type="text" name="phoneNumber" value="${user.phoneNumber}"/></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type="email" name="email" value="${user.email}"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="text" name="password" value="${user.password}"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit" onclick="${errorString = null}">Submit</button>
                <button type="button" onclick="location.href='home'">Cancel</button>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
 