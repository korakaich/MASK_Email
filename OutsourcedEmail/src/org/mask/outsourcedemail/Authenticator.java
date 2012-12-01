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


import java.net.*;
import java.io.*;

import javax.net.ssl.*;
/**
 * Servlet implementation class Authenticator
 */

public class Authenticator extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_UNSUCCESSFUL_LOGINS=5;
	private static final long TIME_DIFF_LOGINS=2;
	private static final long TEMP_PWD_EXPIRED=1;
	private Connection con,con2;  
	private Statement st1, st2;  
	private ResultSet rs;       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticator() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //Function to send data to organisation server
    

	public void myHostVerifier()
	{
		HostnameVerifier hv = new HostnameVerifier()
		{
		      public boolean verify(String arg0, SSLSession arg1)	
		      {
		    	  return true;
		      }
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
	public String authFromOrg(String username,String password, String orgURL) {
		// TODO Auto-generated method stub
		String response="";
		try
		{
			myHostVerifier();			
			String urlParameters = new StringBuffer().append("username=").append(username).append("&password=").append(password).toString();
			URL u =new URL(orgURL);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST"); 
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes(urlParameters);
			wr.flush();
			
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String temp="";
            while((temp=br.readLine())!=null)
            {
            	response +=temp;
            }
            br.close();
            //System.out.println(response);	
            connection.disconnect();
            //PrintWriter writer= response.getWriter();
		}
		catch(Exception ee)
		{
			 ee.printStackTrace();
		}
		return response;

	}
    public String[] getDomain(String username){    	
    	String delims = "@";    	
    	String[] tokens = username.split(delims);
    	return tokens;
    }
    public void init(ServletConfig config) throws ServletException {
    	System.setProperty("javax.net.ssl.trustStore", "/home/korak/orgCert.keystore");
		System.setProperty("javax.net.ssl.trustStorePassword", "shorsen");	        
     } 
  
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userName=request.getParameter("name");
		PrintWriter writer= response.getWriter();
		writer.println("<html><body><h4>Good try "+userName+".</h4> No More Lies");
		writer.println("<a href=\"login.jsp\">Login here please</a></body></html>");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
				
		HttpSession session = request.getSession(true);//NOT NEEDED
		String session_user=(String)session.getAttribute("user_name");//NOT NEEDED
			
	    //check session 
		if(session_user!=null){
	       //user already logged in
	       //do something like send the user to a login screen	    
	       System.out.println("You are already logged in");
	    }
		//get the form parameters
		String userName=request.getParameter("username");
		String[] tokens=getDomain(userName);
		String domain=null;
		userName=tokens[0];
		if(tokens.length>1){
			domain=tokens[1];
		}	    	
		String password=request.getParameter("password");
		
		//make url 
		//print and return;
		//set the session variables
		session.setAttribute("user_name", userName);
		
		//database variables
		String url = "jdbc:mysql://localhost:3306/emailServer";
		String dbName = "root";  
		String dbPassword = "";	 	    	 	   							
		//shorbani
		String orgResponse=null;
		PrintWriter writer= response.getWriter();
		
		
		//FOR ORG DOMAIN
		if(!domain.equals("kmail.com"))
		{
			String orgAuthPath="https://";
			String changePwdPath="https://";
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(url, dbName, dbPassword);
				st1 = con.createStatement();								
				String domainPathAuth="select ip_port, authPath, changePwdPath from domainMap where name='"+domain+"'";
				rs=st1.executeQuery(domainPathAuth);
				
				if(rs==null){					
					System.out.println("Could not find any entry in domainMap for "+domain);
					writer.println("We dont have the domain registered.");
					return;
				}
				else{					
					while(rs.next()){
						orgAuthPath+=rs.getString(1);						
						orgAuthPath+="/";
						orgAuthPath+=rs.getString(2);
						changePwdPath+=rs.getString(1);						
						changePwdPath+="/";
						changePwdPath+=rs.getString(3);
						
					}
				}				
			}catch(Exception e){
				System.out.println(e);
			}
			if(orgAuthPath.equals("https://")){
				System.out.println("Could not find any entry in domainMap for "+domain);
				writer.println("<html><body>We dont have the domain registered.");
				writer.println("Click <a href=\"login.jsp\">here</a> to try again</body></html>");
				return;
			}
			orgResponse=authFromOrg(userName,password, orgAuthPath);
			System.out.println("ORG response is:"+ orgResponse);
			if(orgResponse.equals("1") || orgResponse.equals("2")){
				System.out.println("Came here if normal pass is true");
				session.setAttribute("logged", "true");
				session.setAttribute("domain_name", domain);
				session.setMaxInactiveInterval(-1);
				if(orgResponse.equals("1")){
					session.setAttribute("tempUsed", "false");//temp password not used
					//response.sendRedirect("ChangePwd.jsp");
					//return
				}
				else if (orgResponse.equals("2")){
					session.setAttribute("tempUsed", "true");//temp password used
					System.out.println("Redirecting to "+changePwdPath);
					response.sendRedirect(changePwdPath);
					return;
				}
				
				//set time of this login #TODO?
				response.sendRedirect("home.jsp");
			}
			else if (orgResponse.equals("0")){
				writer.println("<html><body><h4>Incorrect username and/or password.</h4>");
				writer.println("Click <a href=\"login.jsp\">here</a> to try again</body></html>");
			}
			else if(orgResponse.equals("3")){
				writer.println("<html><body>You have tried too many unsuccessful logins. Your account has been locked " +
						"for a certain period.<br/> Please Click <a href=\"login.jsp\">login</a> after sometime.</body></html>");
			}
			else if(orgResponse.equals("4")){
				writer.println("<html><body>Your temp password has expired. Click <a href=\"login.jsp\">here</a> to login again."+ 
						"</body></html>");			
			}
		}//For domain ends
		else{
			String salt=null;						
			//variables populated from db
			
			String rpasswd=null;
			String rTempPwd=null;
			int rNoLoginAttempts=0;
			Timestamp rTSLasLogin=null;
			Timestamp rTStampPwdGen=null;
			
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
				
				rs=null;			
				//st3 = con.createStatement();
				rs = st2.executeQuery("Select tempPwd FROM user where uname='"+userName+"'");
				if(rs!=null){
					while(rs.next()){
						rTempPwd=rs.getString(1);
					}
					System.out.println("temp password "+rTempPwd);
				}
				
				
				rs=null;
				//st4 = con.createStatement();
				rs= st2.executeQuery("Select loginAttempts FROM user where uname='"+userName+"'");
				while(rs.next()){
					rNoLoginAttempts=rs.getInt(1);
				}
				System.out.println("login attempts "+rNoLoginAttempts);
				
				rs=null;
				//st2 = con.createStatement();
				rs=st2.executeQuery("Select TSlastLogin FROM user where uname='"+userName+"'");
				while(rs.next()){
					rTSLasLogin=rs.getTimestamp(1);
				}
				System.out.println("Time since last login "+rTSLasLogin);
				
				rs=null;
				//st2 = con.createStatement();
				rs=st2.executeQuery("Select TStampPwdGen FROM user where uname='"+userName+"'");
				while(rs.next()){
					rTStampPwdGen=rs.getTimestamp(1);
				}
				System.out.println("Time last pwd gen "+rTSLasLogin);
				
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

			if(userName == null){//NOT NEEDED
				//no username in session..this should never happen ... validation check by javascript 	    
				//user probably hasn't logged in properly
				System.out.println("WHY U no give username");
			}	    	
			
			
			//check for Online pwd attacks
			if(rNoLoginAttempts >=MAX_UNSUCCESSFUL_LOGINS){
				//get current time stamp
				java.util.Date date= new java.util.Date();
				Timestamp ts = new Timestamp(date.getTime());
				
				//get last login timestamp se difference
				long diffLoginTime=(ts.getTime()-rTSLasLogin.getTime())/(60*1000);
				System.out.println( "Time diff in minutes"+(ts.getTime()-rTSLasLogin.getTime())/(60*1000));
				if(diffLoginTime<TIME_DIFF_LOGINS){
					System.out.println( "Time diff in minutes"+(ts.getTime()-rTSLasLogin.getTime())/(60*1000));
					writer.println("<html><body>You have tried too many unsuccessful logins. Your account has been locked for a certain period.<br/> Please login after sometime.</body></html>");
					return;
				}						
			}
			int tempTrueFlag=0;
			if(password.equals(rTempPwd)){
				//get current time stamp
				java.util.Date date= new java.util.Date();
				Timestamp ts = new Timestamp(date.getTime());
				long diffTmpPwdGen=(ts.getTime()-rTStampPwdGen.getTime())/(60*1000);
				System.out.println( "Temp passwd Time diff in minutes"+diffTmpPwdGen);
				tempTrueFlag=0;
				try {
					st1 = con.createStatement();
					String updateTmpPwd="update user set tempPwd =\"\" where uname='"+userName+"';";
					st1.executeUpdate(updateTmpPwd);					
				}
				catch(Exception e){
					System.out.println(e);			
				}
				if(diffTmpPwdGen>TEMP_PWD_EXPIRED){
					writer.println("<html><body>Your temp password has expired. Click <a href=\"login.jsp\">here</a> to login again."+ 
							"</body></html>");		
					return;
				}
				
				 								
			}
			
			if(password.equals(rpasswd) || password.equals(rTempPwd)){	    
				//set session attributes
				//System.out.println("Came here if temp pass is true");
				
				
				session.setAttribute("logged", "true");
				session.setAttribute("domain_name", domain);
				session.setMaxInactiveInterval(-1);
				//set time of this login
				
				try {	    	 	    		    				
					
					java.util.Date date= new java.util.Date();
					Timestamp ts = new Timestamp(date.getTime());
					
					st1 = con.createStatement();
					String updateTSLastLogin="update user set TSlastLogin ='"+ts+"' where uname='"+userName+"';";
					st1.executeUpdate(updateTSLastLogin);
					st2 = con.createStatement();
					String loginAttemptsQuery="update user set loginAttempts =0 where uname='"+userName+"';";
					st2.executeUpdate(loginAttemptsQuery);
					if(tempTrueFlag==1){
						response.sendRedirect("ChangePwd.jsp");	
						return;
					}
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
					String loginAttemptsQuery="update user set loginAttempts =loginAttempts+1 where uname='"+userName+"';";
					st2.executeUpdate(loginAttemptsQuery);
					java.util.Date date= new java.util.Date();
					Timestamp ts = new Timestamp(date.getTime());
					String loginTimeUpdate="update user set TSlastLogin ='"+ts+"' where uname='"+userName+"';";
					st2.executeUpdate(loginTimeUpdate);
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
			//System.out.println(rTemp)
			
		}																
		
	}
}
