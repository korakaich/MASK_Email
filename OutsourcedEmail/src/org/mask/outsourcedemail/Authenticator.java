/* Korak Aich, kaich
 * 11/18/2012
 */
package org.mask.outsourcedemail;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.MessageDigest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Authenticator
 */

public class Authenticator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;  
	private Statement st1, st2;  
	private ResultSet rs;       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticator() {
        super();
        // TODO Auto-generated constructor stub
    }
    public String[] getDomain(String username){    	
    	String delims = "@";    	
    	String[] tokens = username.split(delims);
    	return tokens;
    }
    /*public void init(ServletConfig config) throws ServletException {
    	String url = "jdbc:mysql://localhost:3306/emailServer";
 	    String dbName = "root";  
 	    String dbPassword = "";
 	    
      	String driver = config.getInitParameter("Driver");  
        String url = config.getInitParameter("URL");  
        String userName = config.getInitParameter("UserName");  
        String password = config.getInitParameter("Password");
    	  
        String url = "jdbc:mysql://localhost:3306/emailServer";  
        String userName = "root"; 
        String password = "";
    	
        
        try {  
        	Class.forName("com.mysql.jdbc.Driver");
        	con = DriverManager.getConnection(url, userName, password);
            st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}        
        ServletContext context = getServletContext();
        context.setAttribute("connection", con); // Save DB Connection as an attribute                       
     }  */
  
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userName=request.getParameter("name");
		PrintWriter writer= response.getWriter();
		writer.println("<html><body><h4>Good try "+userName+".</h4> No More Lies");
		writer.println("<a href=\"login.html\">Login here please</a></body></html>");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		HttpSession session = request.getSession(true);
		String session_user=(String)session.getAttribute("user_name");
			
	    //if(session == null || session.isNew()){
		if(session_user!=null){// || !session_user.equals("")){
	       //user already logged in
	       //do something like send the user to a login screen	    
	       System.out.println("You are already logged in");
	    }
	    //else{
		//get the form parameters
		String userName=request.getParameter("username");
		String[] tokens=getDomain(userName);
		String domain=null;
		userName=tokens[0];
		if(tokens.length>1){
			domain=tokens[1];
		}	    	
		String password=request.getParameter("password");	
		//set the session variable
		session.setAttribute("user_name", userName);

		String url = "jdbc:mysql://localhost:3306/emailServer";
		String dbName = "root";  
		String dbPassword = "";	 	    	 	   
		String salt=null;
		String rpasswd="";
		//DbBean dbb=new DbBean();
		try {


			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, dbName, dbPassword);
			st1 = con.createStatement();
			//get salt from database::

			String query="SELECT salt FROM user where uname='"+userName+"'";	 	        
			rs = st1.executeQuery(query);
			while(rs.next()){
				salt=rs.getString(1);
			}
			//append the salt in the entered password::
			password=password+salt;
			//hash the password:
			MessageDigest md = MessageDigest.getInstance("SHA-256");        
			md.reset();
			md.update(password.getBytes("UTF-8")); 
			byte[] digest = md.digest();
			//password = new String(digest);
			//salt = new String(saltarray);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toHexString(0xFF & digest[i]));
			}
			password=sb.toString();
			System.out.println("Entered pwd: "+sb.toString());


			//get password from db
			rs=null;
			st2 = con.createStatement();
			rs = st2.executeQuery("SELECT password FROM user where uname='"+userName+"'");
			while(rs.next()){
				rpasswd=rs.getString(1);
			}
			System.out.println("DB password"+rpasswd);

		} catch (Exception e) {
			e.printStackTrace();
		}	    	    
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
				if(st2 != null) {
					st2.close();
					st2 = null;
				}
				
			} catch (SQLException e) {}
		}

		if(userName == null){
			//no username in session..this should never happen ... validation check by javascript 	    
			//user probably hasn't logged in properly
			System.out.println("WHY U no give username");
		}	    	
		PrintWriter writer= response.getWriter();


		if(password.equals(rpasswd)){	    
			//set session attributes
			session.setAttribute("logged", "true");
			session.setAttribute("domain_name", domain);
			session.setMaxInactiveInterval(-1);
			//set time of this login
			try {	    	 	    		    				
				if(con==null){
					System.out.println("whoopsie");
				}
				java.util.Date date= new java.util.Date();
				Timestamp ts = new Timestamp(date.getTime());
				st1 = con.createStatement();
				String updateTSLastLogin="update user set TSlastLogin ='"+ts+"' where uname='"+userName+"';";
				st1.executeUpdate(updateTSLastLogin);
			}
			catch(Exception e){
				System.out.println(e);
			}
			finally{
				try{
					if(st2 != null) {
						st2.close();
						st2 = null;
					}
					if(con!=null){
						con.close();
						con = null;
					}	    						
				}
				catch(Exception e){
					System.out.println(e);
				}
			}

			response.sendRedirect("home.jsp");
		}
		else{
			writer.println("<html><body><h4>Incorrect username and/or password.</h4>");
			writer.println("Click <a href=\"login.jsp\">here</a> to try again</body></html>");
			//login attempts ++
			try {	    	 	    		    				
				if(con==null){
					System.out.println("whoopsie");
				}
				st2 = con.createStatement();
				String loginAttemptsQuery="update user set loginAttempts =loginAttempts+1 where uname=\"kaich\";";
				st2.executeUpdate(loginAttemptsQuery);
			}
			catch(Exception e){
				System.out.println(e);
			}
			finally{
				try{
					if(st2 != null) {
						st2.close();
						st2 = null;
					}
					if(con!=null){
						con.close();
						con = null;
					}	    						
				}
				catch(Exception e){
					System.out.println(e);
				}
			}	    				    			
		}
	}
}
