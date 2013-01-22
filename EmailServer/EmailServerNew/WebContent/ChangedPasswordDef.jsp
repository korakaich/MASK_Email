<%@ page import="java.util.*,java.io.*,java.net.*,java.security.SecureRandom,java.math.BigInteger,java.security.MessageDigest" %>
<%@ page import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ChangedPassword</title>
</head>
<body>
<jsp:declaration>
    Connection con;
	String url = "jdbc:mysql://localhost:3306/emailserver";
	
</jsp:declaration>
<% 

String uname = request.getParameter("uname");
String delims = "@";    	
String[] tokens = uname.split(delims);
String domain=null;
String serverURL="https://152.14.230.113:8443/OutsourcedEmail";
uname=tokens[0];
if(tokens.length>1){
	domain=tokens[1];
}
String currPwd = request.getParameter("password");
String newPwd = request.getParameter("new_password");
try {
	//required for db connection
	Class.forName("com.mysql.jdbc.Driver");
	con = DriverManager.getConnection(url, "root", "shor86bani");

	ResultSet rs = null;
	Statement st = con.createStatement();
	//required for db connection
	

	String salt = null;

	String query = "SELECT salt FROM user where uname='" + uname + "'";
	rs = st.executeQuery(query);
	while (rs.next()) {
		salt = rs.getString(1);
	}

	//append the salt in the entered password::
	currPwd = currPwd + salt;
	//hash the password:
	MessageDigest md = MessageDigest.getInstance("SHA-256");
	md.reset();
	md.update(currPwd.getBytes("UTF-8"));
	byte[] digest = md.digest();
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < digest.length; i++) {
		sb.append(Integer.toHexString(0xFF & digest[i]));
	}
	currPwd = sb.toString();
	System.out.println("Entered pwd: " + sb.toString());

	//now get the stored password;
	String rpasswd = null;
	//get password from db
	rs = null;
	rs = st.executeQuery("SELECT password FROM user where uname='" + uname + "'");
	while (rs.next()) {
		rpasswd = rs.getString(1);
	}
	System.out.println("DB password:" + rpasswd);
	
	if (rpasswd.equals(currPwd)) {//if passwords match
		//gen new salt						
		
		SecureRandom r = new SecureRandom();
		salt = new BigInteger(130, r).toString(32);
		//append salt to entered password
		newPwd = newPwd + salt;
		
		//hash
		
		md = MessageDigest.getInstance("SHA-256");
		md.reset();
		md.update(newPwd.getBytes("UTF-8"));
		//byte[] digest = md.digest();
		digest = md.digest();
		
		//StringBuffer sb = new StringBuffer();
		sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			sb.append(Integer.toHexString(0xFF & digest[i]));
		}
		System.out.println("Storing pwd: " + newPwd + ":" + sb.toString());
		newPwd = sb.toString();
		
		//store salt,pwd in db
		String updateSaltPwd = "UPDATE user SET salt='" + salt + "', password='" + newPwd + "' where uname='" + uname + "';";
		st.executeUpdate(updateSaltPwd);
		out.println("Your password has been changed and you need to re-login. Click <a href=\""+serverURL+"/login.jsp\">here </a>to login."); //TODO Email Server's login.jsp 
	 }
	 else {
		out.println("Current password does not match records. Try resetting your password.Click <a href=\""+serverURL+"/login.jsp\">here </a>to login"); //TODO Email Server's login.jsp
	 }
 }
 catch (Exception e) {
	    }

		%>
</body>
</html>