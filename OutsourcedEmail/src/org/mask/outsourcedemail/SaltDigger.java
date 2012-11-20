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
 * Servlet implementation class SaltDigger
 */
//@WebServlet(description = "Gives the salt", urlPatterns = { "/SaltDiggerPath" })
public class SaltDigger extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Connection con;  
	private Statement st;  
	private ResultSet rs; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaltDigger() {
        super();
        // TODO Auto-generated constructor stub
    }              
    
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
		String userName=request.getParameter("name");		
		//create session
		HttpSession session=request.getSession(true);
		if(!session.isNew()){
			session.invalidate();
		    session = request.getSession();
		}
		session.setAttribute("name", userName);			
		PrintWriter writer= response.getWriter();
		
	    String url = "jdbc:mysql://localhost:3306/emailServer";
	    String dbName = "root";  
	    String dbPassword = "";
	    String salt="";
	    
	    try {  
	    	Class.forName("com.mysql.jdbc.Driver");
	    	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/emailServer", dbName, dbPassword);
	        st = con.createStatement();
	        
	        String query="SELECT salt FROM user where uname='"+userName+"'";
	        
		    rs = st.executeQuery(query);
		    while(rs.next()){
		    	salt=rs.getString(1);
		    }
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
 	    
	    //System.out.println("Salt is"+salt);
		//get salt here.. and set hidden field in response html .. 
		writer.println("<html>" +
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
				"<title>Kmail</title>" +
				"<script src=\"http://crypto-js.googlecode.com/svn/tags/3.0.2/build/rollups/sha256.js\"> </script>" +
				"<script>" +
				"function hashFunction(form)" +
				"{" +
				"	var pass=form.password.value;" +
				"	var salt=form.Salt.value;	" +
				"   var message=pass.concat(salt);"+
				//"	form.hashedvalue.value=CryptoJS.SHA256(pass.concat(salt));	" +				
				"	form.hashedvalue.value=message;	" +
				"}" +				
				"</script>"+				
				"</head>" +
				"<body> <h3> Hi "+session.getAttribute("name")+"</h3> " +
				"<form id=\"password_form\" method =\"Post\" action = \"AuthenticatorPath\">  " +				
				"Password <input name =\"password\" type=\"password\"> </br> " +
				"<input type=\"hidden\" name=\"Salt\" value="+ salt+"> "+
				"<input type=\"hidden\" name=\"hashedvalue\" value=\"rand\">" +
				"<input name=\"Submit\" type=\"submit\" value=\"Submit\" onclick=\"return hashFunction(this.form)\"/> " +
				"</form></body></html>");				
	}	
}
