<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        <%@include file="../../../style.css" %>
        .login {
            background-color: #b4b2b2;
        }
    </style>
</head>
<body>
<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<p style="color: red;">${errorString}</p>
<%session.removeAttribute("errorString");%>

<form method="POST" action="login">
    <table>
        <thead>
        <tr>
            <th colspan="2">Login Page</th>
        </tr>
        </thead>
        <tr>
            <td>Email</td>
            <td><input type="email" name="email" value="${user.email}"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="password" name="password" value="${user.password}"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Submit</button>
                <button type="button" onclick="location.href='home'">Cancel</button>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
 