<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        <%@include file="../../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="../_header.jsp"/>
<jsp:include page="../_menu.jsp"/>

<h3>New Account</h3>
<p style="color: red;">${errorString}</p>


<form method="POST" action="${pageContext.request.contextPath}/newAccount">
    <table>
        <tr>
            <td>Email</td>
            <td><input type="email" name="email" value="${user.email}"/></td>
        </tr>
        <tr>
            <td>Password</td>
            <td><input type="password" name="password" value="${user.password}"/></td>
        </tr>
        <tr>
            <td>First name</td>
            <td><input type="text" name="first_name" value="${user.first_name}"/></td>
        </tr>
        <tr>
            <td>Last name</td>
            <td><input type="text" name="last_name" value="${user.last_name}"/></td>
        </tr>
        <tr>
            <td>Phone number</td>
            <td><input type="text" name="phone_number" value="${user.phone_number}"/></td>
        </tr>
        <tr>
            <td colspan="2">
                <button type="submit">Submit</button>
                <button type="button" onclick="location.href='${pageContext.request.contextPath}/home'">Cancel</button>
            </td>
        </tr>
    </table>
</form>

<jsp:include page="../_footer.jsp"/>
</body>
</html>
 