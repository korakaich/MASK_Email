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
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class Authenticate
 */
public class Authenticate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int MAX_UNSUCCESSFUL_LOGINS=5;
    private static final int TEMP_PWD_EXPIRED=2;
	private static final long TIME_DIFF_LOGINS=2;	
	private Connection con;  
	private Statement st1, st2;  
	private ResultSet rs; 

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticate() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    public String[] getDomain(String username){    	
    	String delims = "@";    	
    	String[] tokens = username.split(delims);
    	return tokens;
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userName=request.getParameter("name");
		PrintWriter writer= response.getWriter();
		//writer.println("<html><body><h4>Good try "+userName+".</h4> No More Lies");
		//writer.println("<a href=\"login.html\">Login here please</a></body></html>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void init(ServletConfig config) throws ServletException  {
	    super.init(config);
	    ServletContext context = getServletContext();
	    System.setProperty("javax.net.ssl.trustStore", "S:/MyCert/mykeystore.cert");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
	  }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
				//if(session_user!=null){// || !session_user.equals("")){
				       //user already logged in
				       //do something like send the user to a login screen	    
				       //System.out.println("You are already logged in");
				  //  }

				        String userName=request.getParameter("username");
					//	String[] tokens=getDomain(userName);
					//	String domain=null;
					//	userName=tokens[0];
					//	if(tokens.length>1){
					//		domain=tokens[1];
					//	}	    	
						String password=request.getParameter("password");	
						String url = "jdbc:mysql://localhost:3306/emailserver";
						String dbName = "root";  
						String dbPassword = "shor86bani";	 	    	 	   
						String salt=null;
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
							
							//get temp password from db
							rs = st2.executeQuery("Select tempPwd FROM user where uname='"+userName+"'");
							if(rs!=null){
								while(rs.next()){
									rTempPwd=rs.getString(1);
								}
								System.out.println("temp password "+rTempPwd);
							}

							//get number of loging attempts from db
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
						PrintWriter writer= response.getWriter();
						int tempUsed=0;
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
								writer.println("3");
							    //Use enumeration
								//writer.println("<html><body>You have tried too many unsuccessful logins. Your account has been locked for a certain period.<br/> Please login after sometime.</body></html>");
								return;
							}						
						}
						if(password.equals(rTempPwd)){
							//get current time stamp
							java.util.Date date= new java.util.Date();
							Timestamp ts = new Timestamp(date.getTime());
							long diffTmpPwdGen=(ts.getTime()-rTStampPwdGen.getTime())/(60*1000);
							System.out.println( "Temp passwd Time diff in minutes"+diffTmpPwdGen);
							try {
								st1 = con.createStatement();
								String updateTmpPwd="update user set tempPwd =\"\" where uname='"+userName+"';";
								st1.executeUpdate(updateTmpPwd);
							}
							catch(Exception e){
								System.out.println(e);			
							}
							if(diffTmpPwdGen>TEMP_PWD_EXPIRED){
								writer.println("4");		
								return;
							}
							else{
								//can redirect to change pwd page.. or set session var so that msg can be disp in home
								tempUsed=1;
							}
													
						}


						if(password.equals(rpasswd)|| password.equals(rTempPwd)){
							//set time of this login
							try {	    	 	    		    				
								
								
								System.out.println("Any of them equal");
								java.util.Date date= new java.util.Date();
								Timestamp ts = new Timestamp(date.getTime());
								st1 = con.createStatement();
								String updateTSLastLogin="update user set TSlastLogin ='"+ts+"' where uname='"+userName+"';";
								st1.executeUpdate(updateTSLastLogin);
								st2 = con.createStatement();
								String loginAttemptsQuery="update user set loginAttempts =0 where uname='"+userName+"';";
								st2.executeUpdate(loginAttemptsQuery);
								if(tempUsed==0)
									writer.println("1");//1
								else if(tempUsed==1)
									writer.println("2");//2
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

							//response.sendRedirect("home.jsp");
						}
						else{
							
							//login attempts ++
							try {	
								
								System.out.println("None of them equal");
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
		                        writer.println("0");//0
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
