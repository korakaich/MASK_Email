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
<title>Log Out</title>
</head>
<body>
<% session.setAttribute("user_name", null);
session.setAttribute("logged_in",null);
session.invalidate();%>
You have successfully logged out.
Click <a href="login.jsp">here</a> to login again.
</body>
</html>

