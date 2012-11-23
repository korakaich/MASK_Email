<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Kmail</title>
</head>
<body>
 <h3> Log in</h3>
 <%
session.invalidate();
%>
 <form method ="Post" action = "/OutsourcedEmail/AuthenticatorPath">
  Username<input name="name" size="15" type="text" />
  <br/>  
  Password <input name ="password" type="password">  
  <br/>
  <input name="Submit" type="submit" value="Submit" />
 </form>
</body>
</html>