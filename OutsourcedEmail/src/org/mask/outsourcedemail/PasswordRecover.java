package org.mask.outsourcedemail;

import java.io.IOException;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PasswordRecover
 */
@WebServlet(description = "class to generate randome password and send email to sec email", urlPatterns = { "/PasswordRecover" })
public class PasswordRecover extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String orgURL="152.14.236.237:8443";
	private Connection con;  
	private Statement st1;  
	private ResultSet rs;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PasswordRecover() {
        super();
        // TODO Auto-generated constructor stub
    }
   /* public void sendMail(String secEmail) 
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { protected PasswordAuthentication getPasswordAuthentication() {  return new PasswordAuthentication("csc574.mask","csc574project"); } });
        System.out.println("comes here1");
        try 
        {
            String temp_psw;                            // has to be stored in the database
            SecureRandom r = new SecureRandom();
            byte[] temp = new byte[4];
            r.nextBytes(temp);
            BigInteger bi = new BigInteger(1, temp);
            String hex = bi.toString(16);
            int paddingLength = (temp.length * 2) - hex.length();
            if(paddingLength > 0)
                temp_psw = String.format("%0" + paddingLength + "d", 0) + hex;
            else
                temp_psw = hex;
            System.out.println("comes here1");
            Message message = new MimeMessage(session);            
            message.setFrom(new InternetAddress("csc574.mask@gmail.com"));
            
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(secEmail));  // substitute the extracted email id here.
            message.setSubject("Testing Subject");
            message.setText("Your temp password is " +"\n"+ temp_psw);
            //store in database
            System.out.println("comes here3");
            Transport.send(message);            
            System.out.println("Mail sent");
            
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    */
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
    	if(domain.equals("ncsu.edu")){
    		writer.println("<html><body>You need to go to the <a href=\"https://"+orgURL+"/EmailServer/Forgot_password.html\">ncsu page</a>" +
    				" to change your password.</body></html>");
    		return;
    	}
    	//check domain and send uname to org server
    	//get the random pwd and the email from org server
    	//send mail to the received email
    	System.out.println("In pwd rec: Username "+userName+" : Domain " +domain );
		String url = "jdbc:mysql://localhost:3306/emailServer";
		String dbName = "root";  
		String dbPassword = "";	 	    	 	   
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
				"Please <a href=\"login.jsp\">login</a> using the temporary password within 30 minutes and reset your password");
	
	}
}
