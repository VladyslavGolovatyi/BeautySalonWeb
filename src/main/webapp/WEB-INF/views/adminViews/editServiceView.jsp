<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>Edit Service</title>
      <style><%@include file="../../../style.css" %></style>
   </head>
   <body>

      <jsp:include page="../_header.jsp"/>
      <jsp:include page="../_menu.jsp"/>

      <p style="color: red;">${errorString}</p>
      <%session.removeAttribute("errorString");%>

      <c:if test="${not empty service}">
         <form method="POST" action="editService">
            <input type="hidden" name="name" value="${service.name}" />
            <table>
               <thead>
               <tr>
                  <th colspan="2">Edit Service</th>
               </tr>
               </thead>
               <tr>
                  <td colspan="2">${service.name}</td>
               </tr>
               <tr>
                  <td>Price</td>
                  <td><input type="text" name="price" value="${service.price}" /></td>
               </tr>
               <tr>
                  <td colspan ="2">
                     <button type="submit">Submit</button>
                     <button type="button" onclick="location.href='serviceList'">Cancel</button>
                  </td>
               </tr>
            </table>
         </form>
      </c:if>

   </body>
</html>