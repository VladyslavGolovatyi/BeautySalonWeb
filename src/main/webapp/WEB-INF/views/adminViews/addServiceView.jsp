<%@ page language="java" contentType="text/html; charset=UTF-8"
   pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
   <head>
      <meta charset="UTF-8">
      <title>Add Service</title>
      <style><%@include file="../../../style.css" %></style>
   </head>
   <body>
   
      <jsp:include page="../_header.jsp"/>
      <jsp:include page="../_menu.jsp"/>
      
      <h3>Add Service</h3>
      
      <p style="color: red;">${errorString}</p>
      
      <form method="POST" action="${pageContext.request.contextPath}/admin/addService">
         <table>
            <tr>
               <td>Name</td>
               <td><input type="text" name="name"/></td>
            </tr>
            <tr>
               <td>Price in UAH</td>
               <td><input type="text" name="price" /></td>
            </tr>
            <tr>
               <td>
                   <button type="submit">Submit</button>
                   <button type="button" onclick="location.href='${pageContext.request.contextPath}/serviceList'">Cancel</button>
               </td>
            </tr>
         </table>
      </form>
      
   </body>
</html>