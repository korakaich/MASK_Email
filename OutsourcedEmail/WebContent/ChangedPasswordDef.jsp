<%@ page import="java.util.*,java.io.*,java.net.*,java.security.SecureRandom,java.math.BigInteger,java.security.MessageDigest" %>
<%@ page import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

<jsp:declaration>
    Connection con;
	String url = "jdbc:mysql://localhost:3306/emailServer";
</jsp:declaration>


<% //not to be handled for org..from here
	if (session.isNew()) {
		out.println("Session says new");
	}
	String uname = (String) session.getAttribute("user_name");
	String logged = (String) session.getAttribute("logged");
	String domain = (String) session.getAttribute("domain_name");
	if (uname == null || logged != "true") {
		out.println("You are not logged in.<br/>");
		out.println("Click <a href=\"login.jsp\">here</a> to login again.");

	} else {//not to be handled for org..till here
		String currPwd = request.getParameter("password");
		String newPwd = request.getParameter("new_password");
		//String unHashedCurrPwd=currPwd;	
		//also get uname
		try {
			//required for db connection
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, "root", "");

			ResultSet rs = null;
			Statement st = con.createStatement();
			//required for db connection
			

			String salt = null;

			String query = "SELECT salt FROM user where uname='"
					+ uname + "'";
			rs = st.executeQuery(query);
			while (rs.next()) {
				salt = rs.getString(1);
			}
		    currPwd=currPwd+salt;//now added
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();		    					
			md.reset();
			md.update(currPwd.getBytes("UTF-8"));
			digest = md.digest();
			sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toHexString(0xFF & digest[i]));
			}
			currPwd = sb.toString();
			System.out.println("Changing pwd::Entered pwd after hash: " + sb.toString());
			
			//now get the stored password;
			
			String rpasswd = null;
			String tempPwd=null;
			//get password from db
			rs = null;
			rs = st.executeQuery("SELECT password FROM user where uname='"
					+ uname + "'");
			while (rs.next()) {
				rpasswd = rs.getString(1);				
				
			}
			System.out.println("Changing pwd:: DB password" + rpasswd);
			
			if (rpasswd.equals(currPwd)){ //if passwords match
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
				
				//sb = new StringBuffer();
				sb = new StringBuffer();
				for (int i = 0; i < digest.length; i++) {
					sb.append(Integer.toHexString(0xFF & digest[i]));
				}
				System.out.println("Storing pwd: " + newPwd + ":"
						+ sb.toString());
				newPwd = sb.toString();
				
				//store salt,pwd in db
				String updateSaltPwd = "UPDATE user SET salt='"
						+ salt + "', password='" + newPwd
						+ "' where uname='" + uname + "';";
				st.executeUpdate(updateSaltPwd);
				out.println("Your password has been changed. Click <a href=\"login.jsp\">here </a>to login.");
				session.setAttribute("logged", "false");
				session.invalidate();				
				//store pwd in db
		}
		else {
				out.println("Current password does not match records. Try resetting your password.Click <a href=\"login.jsp\">here </a>to login.");
		}
	} catch (Exception e) {
		}
	}
%>

</body>
</html>