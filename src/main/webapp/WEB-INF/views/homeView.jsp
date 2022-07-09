<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
     <meta charset="UTF-8">
     <title>Home Page</title>
     <style><%@include file="../../style.css" %></style>
  </head>
  <body>

     <jsp:include page="_header.jsp"/>
     <jsp:include page="_menu.jsp"/>
     <p style="color: red;">${errorString}</p>
     <pre>
     "Louise" is a highly qualified specialist, the latest equipment, professional and high-quality cosmetics. 
     Today, many salons offer hairdressing and aesthetic cosmetology services. 
     But not all salons offer a service that meets modern requirements. 
     In our salon exclusively the most daring, modern and miraculous procedures for healing and rejuvenation of hair, 
     face and body are exclusively presented.
     
     <b>
     Quality guaranteed!
     </b>
     The salon is responsible for all services and guarantees quality.
	 
     <b>
     Where?
     </b>
     Lviv, st.Franka 46
     
     <b>
     When?
     </b>
     Every day except Sunday from 10 to 22
     
     <b>
     Our contacts
     </b>
     Contact phone number: 0661234567
     Email: beautysalonlouise@gmail.com
     
	 </pre>
     <c:if test = "${loggedInUser.getRole() == 'client'}">
        <jsp:include page="_footer.jsp"/>
     </c:if>

  </body>
</html>