package org.mask.outsourcedemail;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;


/**
 * Servlet implementation class PasswordRecover
 */
public class PasswordRecover extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;  
	private Statement st1;  
	private ResultSet rs; 
	private static final String emailUrl ="152.14.230.113:8443" ;

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasswordRecover() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer= response.getWriter();
		String userName=request.getParameter("username");
		//userName has xxx@xxx.xxx
		System.out.println("User: "+userName);
		//parse to get uname only
		String domain=null;
		String delims = "@";    	
    	String[] tokens = userName.split(delims);
    	userName=tokens[0];
    	if(tokens.length>1){
    		domain=tokens[1];
    	}
    	System.out.println("domain is "+domain);
    	//TODO
    	
    	//check domain and send uname to org server
    	//get the random pwd and the email from org server
    	//send mail to the received email
    	System.out.println("In pwd rec: Username "+userName+" : Domain " +domain );
		String url = "jdbc:mysql://localhost:3306/emailserver";
		String dbName = "root";  
		String dbPassword = "shor86bani";	 	    	 	   
		String secEmail=null;
		//String rpasswd="";
		//DbBean dbb=new DbBean();
		try {  
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, dbName, dbPassword);
			st1 = con.createStatement();
			//get salt from database::			
			String query="SELECT secondemail FROM user where uname='"+userName+"'";	 	        
			rs = st1.executeQuery(query);
			while(rs.next()){
				secEmail=rs.getString(1);
			}
		}		
		catch (Exception e){
			System.out.println(e);
		}	 
		System.out.println("Secondary email: "+secEmail);
		SendMailSSL mailer=new SendMailSSL();
		String temp_psw=mailer.sendMail(secEmail);
		//store in db
		
		//sendMail(secEmail);
		
		writer.println("<html><body> An email containing a temporary password has been sent to your secondary email.<br/> " +
				"Please <a href=\"https://"+emailUrl+"/OutsourcedEmail/login.jsp\">login</a> using the temporary password within 30 minutes and reset your password");
	
	

	}

}
