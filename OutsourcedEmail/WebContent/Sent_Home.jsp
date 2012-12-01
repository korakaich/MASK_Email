<!-- 
Korak Aich, kaich
11/21/2012
-->
<%@ page import="java.util.*,java.io.*,java.net.*,java.security.SecureRandom,java.math.BigInteger,java.security.MessageDigest, javax.crypto.*" %>
<%@ page import="java.sql.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <jsp:useBean id="Cryptography" class="org.mask.outsourcedemail.LocalEncrypter" scope="session" ></jsp:useBean>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Home</title>
</head>
<body> 
<jsp:declaration>
    Connection con;
    Statement st1;  
	ResultSet rs;   
</jsp:declaration>
<% 
if(session.isNew()){
	out.println("Session says new");
	response.sendRedirect("login.jsp");
}
String uname=(String)session.getAttribute("user_name");
String logged=(String)session.getAttribute("logged");
String domain=(String)session.getAttribute("domain_name");

String domainURL=null;
//basic session logged in checks..


if(uname==null|| logged!="true"){%>
	You are not logged in.<br/>
	Click <a href="login.jsp">here</a> to login again.
	
<%}


else{				
	String url = "jdbc:mysql://localhost:3306/emailServer";
	String dbName = "root";  
	String dbPassword = "";
	//basic checks cont.
	
	
	
	
	Cryptography.setUp();
	String ip_port=null;
	String changePwdPath1=null;
	String pwdRstPath1=null;
	String changePwdPath2=null;
	String pwdRstPath2=null;
	if(domain!=null){
		
		if(!domain.equals("kmail.com")){				 		
			try {
				//Class.forName("com.mysql.jdbc.Driver");
				//con = DriverManager.getConnection(url, dbName, dbPassword);
				st1 = con.createStatement();
				String query="SELECT ip_port, changePwdPath, pwdRstPath FROM domainMap where name ='"+domain+"'";	 	        
				rs = st1.executeQuery(query);
				while(rs.next()){
					ip_port=rs.getString(1);
					changePwdPath1=rs.getString(2);
					pwdRstPath1=rs.getString(3);
				}
				changePwdPath2="https://"+ip_port+"/"+changePwdPath1+"";
				pwdRstPath2="https://"+ip_port+"/"+pwdRstPath1+"";
				
	
			}//try closes
			catch (Exception e) {
					e.printStackTrace();
			}//catch closes	    	    
			finally{
				try {
					if(rs != null) {
						rs.close();
						rs = null;
					}
					if(st1 != null) {
						st1.close();
						st1 = null;
					}
					
			}catch (SQLException e) {}
				System.out.println();
			}					
		}//finally closes
		
	}//domain!=kmail closes
%>	
	
	Hi <%out.println(uname); %> 
	You are at <%out.println(domain);%><br/>
	This is your inbox. <br/>
	
	</br>
	<a href="Compose.jsp">Compose</a>
	<%
	
	String me =(String)session.getAttribute("user_name")+'@'+(String)session.getAttribute("domain_name");	
	String enSubject,enContent,contentHash,Time=null;
	byte[] eSubject=null, eContent=null;
	String mailFrom =null ,mailSubject=null ,mailContent=null ,mailHash=null;
	//me=Cryptography.encryption(me);
	System.out.println("IN HOME: ME:"+me);
	//Now connect to emailstore table to display all emails for that user.
	try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, dbName, dbPassword);
				st1 = con.createStatement();
				String query="SELECT ensendTo, enSubject, enContent, contentHash, timeStampMsg FROM emailStore where enFrom ='"+me+"'";
				System.out.println(query);
				rs = st1.executeQuery(query);
				%>
				<TABLE cellpadding="15" border="1" style="background-color: #ffffcc;">
				<TR>
					<TD>To</TD>
					<TD>Subject</TD>
					<TD>Content</TD>
					<TD>Time</TD>
				</TR>
				
				<%
				while (rs.next()) {
					mailFrom=rs.getString(1);
					eSubject=rs.getBytes(2);
					eContent=rs.getBytes(3);
					contentHash=rs.getString(4);
					
					try{
						mailSubject=Cryptography.decrypt(eSubject);
						//System.out.println("subject was:: "+mailSubject);// TOCHECK
						mailContent=Cryptography.decrypt(eContent);														
					}
					catch(Exception e){
						out.println("Your mailBox MIGHT be compromised.... Contact the system admin");
					}
					
					mailHash=Cryptography.calcHash(mailContent);
					if(!mailHash.equals(contentHash))
					{
						out.println("Your mailbox is compromised.... Contact the system admin ASAP");
						
					}
					
				%>
				
				<TR>
				<TD><%=mailFrom%></TD>
				<TD><%=mailSubject%></TD>
				<TD><%=mailContent%></TD>				
				<TD><%=rs.getTimestamp(5)%></TD>
				</TR>
				
				<% 
				
				} 				
				// close all the connections.
				rs.close();
				st1.close();
				con.close();
				} catch (Exception ex) {			
				out.println("Unable to connect to database.");
				System.out.println(ex);
				}
				%>
				</TABLE>	
	</br>
	</br>
	</br>
	</br>
	<a href="home.jsp">Inbox</a>
	<% if (!domain.equals("kmail.com")){		
		out.println("Click <a href=\'"+changePwdPath2+"'>here </a> to change password.");
	}
	else{
	%>
	<a href="ChangePwd.jsp">Change password</a>
	<%} %>
	</br>
Click <a href="logout.jsp">here</a> to logout.

<%} //else closes%>
</body>
</html>