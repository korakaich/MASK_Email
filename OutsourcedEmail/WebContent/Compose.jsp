<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Compose</title>
<script language="JavaScript" type="text/javascript">
	function login_validate(form)  
	{  
		if (form.to.value  == "")
		{
		alert("Please fill To field!")
		form.to.focus();
		return false;
		}
		if (form.subject.value == "")
		{
		alert("Please fill Subject field!")
		form.subject.focus();
		return false;
		}
		if (form.body.value == "")
		{
		alert("Please fill Body field!")
		form.body.focus();
		return false;
		}
		validRegExp = /^[^@]+@[^@]+.[a-z]{2,}$/i;
		if (form.to.value.search(validRegExp)== -1)
		{
		alert("Invalid email address!\nPlease amend and retry.")
		form.to.focus();
		return false;
		}
		return true;
		
	}  
  </script>
</head>
<body>
<% 
if(session.isNew()){
	out.println("Session says new");	
}
String uname=(String)session.getAttribute("user_name");
String logged=(String)session.getAttribute("logged");
String domain=(String)session.getAttribute("domain_name");

String domainURL=null;

if(uname==null|| logged!="true"){  %>
	You are not logged in.<br/>
	Click <a href="login.jsp">here</a> to login again.
<%}
else{
%>	
<form method ="Post" action = "/OutsourcedEmail/StoreMail" onsubmit="return login_validate(this);">
  To:&nbsp; &nbsp; &nbsp; &nbsp;<input name="to" size="100" type="text">
  <br/>  
  Subject:<input name ="subject" size="100" type="text">  
  <br/>
  <textarea name="body" rows="30" cols="50">
  </textarea>
    
  <br/>
  <input name="send" type="submit" value="Send">
  <br/> 
  </form>
</body>
</html>
<% } %>