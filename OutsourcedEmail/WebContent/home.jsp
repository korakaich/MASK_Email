<!-- 
Korak Aich, kaich
11/21/2012
-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Home</title>
</head>
<body> 
<% 
if(session.isNew()){
	out.println("Session says new");	
}
String uname=(String)session.getAttribute("user_name");
String logged=(String)session.getAttribute("logged");
if(uname==null|| logged!="true"){  %>
	You are not logged in.<br/>
	Click <a href="login.jsp">here</a> to login again.
<%}
else{
%>	

Hi <% out.println(uname);  %>This is your inbox. <br/>
Click <a href="logout.jsp">here</a> to logout.
<%} %>
</body>
</html>