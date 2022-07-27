<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home Page</title>
    <style>
        .home {
            background-color: #b4b2b2;
        }

        <%@include file="../../style.css" %>
    </style>
</head>
<body>

<jsp:include page="_header.jsp"/>
<jsp:include page="_menu.jsp"/>
<p style="color: red;">${errorString}</p>

<div>
    "Louise" is a highly qualified specialist, the latest equipment, professional and high-quality cosmetics.
    Today, many salons offer hairdressing and aesthetic cosmetology services.<br>
    But not all salons offer a service that meets modern requirements.<br>
    In our salon exclusively the most daring, modern and miraculous procedures for healing and rejuvenation of hair,
    face and body are exclusively presented.
</div>
<div>
    <h3>
        Quality guaranteed!
    </h3>
    The salon is responsible for all services and guarantees quality.
</div>

<div>
    <h3>
        Where?
    </h3>
    Lviv, st.Franka 46
</div>

<div>
    <h3>
        When?
    </h3>
    Every day except Sunday from 8 to 20 (13-14 lunch break)
</div>

<div>
    <h3>
        Our contacts
    </h3>
    Contact phone number: +380661234567<br>
    Email: beautysalonlouise@gmail.com
</div>

</body>
</html>