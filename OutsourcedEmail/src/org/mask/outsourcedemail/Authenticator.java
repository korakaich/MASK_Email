package org.mask.outsourcedemail;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

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
//@WebServlet("/AuthenticatorPath")
public class Authenticator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;  
	private Statement st;  
	private ResultSet rs;       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticator() {
        super();
        // TODO Auto-generated constructor stub
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
		writer.println("<h4>hellooo..."+userName+"(gigiti gigiti) eh eh</h4>");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		ServletContext context = session.getServletContext();
		
	    if(session == null || session.isNew()){
	       //valid session doesn't exist
	       //do something like send the user to a login screen
	    	System.out.println("WHY session null or new!");
	    }
	    else{
	    	String userName=(String)session.getAttribute("name");
	    	
	    	  
	    	String url = "jdbc:mysql://localhost:3306/emailServer";
	 	    String dbName = "root";  
	 	    String dbPassword = "";
	 	    
	 	    //System.out.println("URL::"+url);
	 	    //System.out.println("username::"+userName);
	 	    String rpasswd="";
	 	    try {  
	 	    	Class.forName("com.mysql.jdbc.Driver");
	 	    	con = DriverManager.getConnection(url, dbName, dbPassword);
	 	        st = con.createStatement();
	 	        st = con.createStatement();
	 		    rs = st.executeQuery("SELECT * FROM user where uname='"+userName+"'");
	 		    while(rs.next()){
	 		    	rpasswd=rs.getString(3);
	 		    }
	 		   System.out.println("DB is"+rpasswd);
	 	    } catch (Exception e) {
	 			e.printStackTrace();
	 		}	    	    
	 	    finally{	 	    	
	 	    	try {
	 	    		if(rs != null) {
	 	    			rs.close();
	 	    			rs = null;
	 	    		}
	 	    		if(st != null) {
	 	    			st.close();
	 	    			st = null;
	 	    		}
	 	    		if(con != null) {
	 	    			con.close();
	 	    			con = null;
	 	    		}
	 	    	} catch (SQLException e) {}
	 	    }
	 	    
	        
	    	if(userName == null){
	    		//no username in session..this should never happen ... validation check by javascript 	    
	    		//user probably hasn't logged in properly
	    		System.out.println("WHY U no give username");
	    	}	    	
	    	PrintWriter writer= response.getWriter();
	    	
	    	if( true) {
	    		String passwd=request.getParameter("hashedvalue");
	    		String salt=request.getParameter("Salt");
	    		System.out.println(salt);
	    		System.out.println("Entered pwd::"+passwd);
	    		if(passwd.equals(rpasswd)){	    			    			  
	    			response.sendRedirect("https://localhost:8443/OutsourcedEmail/home.jsp");	    			
	                /*if(context.getAttribute("connection")==null){
	                	System.out.println("Null hai");
	                }*/	                	
	    			session.setAttribute("logged", true);
	    			session.setMaxInactiveInterval(1);
	    		}
	    		else{
	    			writer.println("<h4>hellooo..."+userName+"wtf!</h4>");
	    		}
	    	}
	    	
	    }
	}
}
