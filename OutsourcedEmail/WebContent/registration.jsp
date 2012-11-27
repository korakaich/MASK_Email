<%@ page import="java.util.*,  java.io.*, java.net.*, java.security.SecureRandom, java.math.BigInteger, java.security.MessageDigest" %>
<%@ page import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
  <% 
    response.setHeader("Cache-Control","no-store"); 
    response.setHeader("Pragma","no-cache"); 
    response.setDateHeader ("Expires", 0);   
    %>
    
    
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Kmail</title>

<body>


<jsp:declaration>

//Statement st;
Connection con;
String url = "jdbc:mysql://localhost:3306/emailServer";
int errorFlag=0;// 0 means no error
//ResultSet rs;
//String salt="";
</jsp:declaration>

<jsp:scriptlet><![CDATA[
try {
	Class.forName("com.mysql.jdbc.Driver");
	con = DriverManager.getConnection(url, "root", ""); 
	String firstname=request.getParameter("first_name");
	String lastname=request.getParameter("last_name");
	String uname = request.getParameter("reg_email");
	String delims = "@";
	String[] tokens = uname.split(delims);
	uname=tokens[0];
	String domain=tokens[0];
	String secondemail = request.getParameter("sec_email");
	String psw = request.getParameter("password");
	session.setAttribute("name", uname);
	
	String password=null;
	String salt=null;
	String line=null;
    SecureRandom r = new SecureRandom();
    //byte[] saltarray = new byte[20];
    //r.nextBytes(saltarray);
    salt=new BigInteger(130, r).toString(32);
    //append salt to entered password
    psw=psw+salt;
    try
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");        
        md.reset();
        //md.update(saltarray);
        md.update(psw.getBytes("UTF-8")); 
        byte[] digest = md.digest();
        //password = new String(digest);
        //salt = new String(saltarray);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
          sb.append(Integer.toHexString(0xFF & digest[i]));
        }
 
        System.out.println("Storing pwd: "+psw +":" + sb.toString());
        password=sb.toString();
    }
    catch(Exception e)
    {
        System.out.println(e);
    }
    
    //st = con.createStatement();
    PreparedStatement insertInfo = con.prepareStatement("INSERT INTO user (uname, firstname, lastname, password, salt, secondemail, tempPwd, TStampPwdGen, TSlastLogin, loginAttempts) VALUES(?,?,?,?,?,?,?,?,?,0)");
    insertInfo.setString(1, uname);
    insertInfo.setString(2, firstname);
    insertInfo.setString(3, lastname);
    insertInfo.setString(4, password);
    insertInfo.setString(5, salt);
    insertInfo.setString(6, secondemail);
    insertInfo.setString(7, null);
    insertInfo.setString(8, null);
    insertInfo.setString(9, null);
	//String query="INSERT into user values('uname, firstname, lastname, password, salt, secondemail')";
	//rs = st.executeQuery(query);
	
	System.out.println(uname+" "+ firstname+" "+ lastname+" "+ password+" "+ salt+" "+ secondemail);
	insertInfo.executeUpdate();		

} catch (Exception e) {
	e.printStackTrace();
	errorFlag=1;
}	    
finally{	 	    	
	try {
				
		if(con != null) {
			con.close();
			con = null;
		}
	} catch (SQLException e) {}
}

]]></jsp:scriptlet>

<%if (errorFlag==0){
	%> You have been registered. Click <a href="login.jsp">here</a> to login.
	<%}else{ %>There has been some error while registering.
	<%} %>

    
</body>
</html>