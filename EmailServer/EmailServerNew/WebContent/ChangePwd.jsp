<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Password</title>
<script language="JavaScript" type="text/javascript">
	function login_validate(form)  
	{  
		if (form.uname.value  == "")
		{
		alert("Please enter email address!")
		form.uname.focus();
		return false;
		}
		
		if (form.new_password.value == "")
		{
		alert("Please enter new password!")
		form.new_password.focus();
		return false;
		}
		if (form.cfrm_new_password.value == "")
		{
		alert("Please confirm new password!")
		form.cfrm_new_password.focus();
		return false;
		}
		validRegExp = /^[^@]+@[^@]+.[a-z]{2,}$/i;
		if (form.uname.value.search(validRegExp)== -1)
		{
		alert("Invalid email address!\nPlease amend and retry.")
		form.uname.focus();
		return false;
		}
		if ((form.new_password.value.length < 6) || (form.new_password.value.length > 15)) 
		{
		alert("Password length should be minimum 6 and maximum 15 chars.\nPlease amend and retry.")
		form.new_password.focus();
		return false;
		}
		if (form.new_password.value != form.cfrm_new_password.value)
		{
		alert("New password and password confirmation don't match!\nPlease amend and retry.")
		form.new_password.focus();
		return false;
		}
		return true;
		
	}  
  </script>
</head>
<body>
	
  <h3>Change Password</h3>
  <form method ="Post" action = "/EmailServerNew/ChangedPassword.jsp" onsubmit="return login_validate(this)" >
  Enter email address<input name="uname" type="text" />
  <br/> 
  <br/> 
  <br/> 
  Enter new password <input name ="new_password" type="password">  
  <br/>
  <br/>
  Confirm new password <input name ="cfrm_new_password" type="password">  
  <br/>
  <br/>
  <input name="Change_Password" type="submit" value="Change Password" />
  <br/> 
  
  </form>
</body>
</html>