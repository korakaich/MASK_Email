<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Kmail</title>
</head>
<body>
 <h3> Log in</h3>
 <%session.invalidate();%>
 <form method ="Post" action = "/OutsourcedEmail/AuthenticatorPath" onsubmit="return login_validate(this);">
  Email address<input name="username" size="15" type="text">
  <br/>  
  Password <input name ="password" type="password">  
  <br/>
  <input name="Submit" type="submit" value="Login">
  <br/> 
  </br>
  <a href="Forgot_password.html">Forgot password?</a>
  </br>
  </br>
  New user? Click <a href="Registration.html">here</a> to register.
  <script language="JavaScript" type="text/javascript">
	function login_validate(form)  
	{  
		if (form.username.value  == "")
		{
		alert("Please enter username!")
		form.username.focus();
		return false;
		}
		if (form.password.value == "")
		{
		alert("Please enter password!")
		form.password.focus();
		return false;
		}
		validRegExp = /^[^@]+@[^@]+.[a-z]{2,}$/i;
		if (form.username.value.search(validRegExp)== -1)
		{
		alert("Invalid email address!\nPlease amend and retry.")
		form.username.focus();
		return false;
		}
		return true;
		
	}  
  </script>
 </form>
</body>
</html>