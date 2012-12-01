package org.mask.outsourcedemail;

import java.io.*;
import java.util.*;
import com.mysql.jdbc.PreparedStatement;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class StoreMail
 */
@WebServlet("/StoreMail")
public class StoreMail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection con;  
	private Statement st1, st2;  
	private ResultSet rs;  
	public static byte[] ivBytes = new byte[] { 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00 };
	public static byte[] keyBytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef };   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoreMail() {
        super();
    }
    public static String encryption(String textToEncrypt)
   	{
   		        
   	    byte[] input = textToEncrypt.getBytes();

   	    try{
   	    SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
   	    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
   	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS7Padding", "BC");



   	    // encryption pass

   	    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

   	    byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

   	    int ctLength = cipher.update(input, 0, input.length, cipherText, 0);

   	    ctLength += cipher.doFinal(cipherText, ctLength);

   	    return (new String(cipherText) );

            }
   	    catch(Exception e){
   	    	return null;
   	    }
   	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	String userName=request.getParameter("name");
    	PrintWriter writer= response.getWriter();
    	writer.println("<html><body><h4>Good try "+userName+".</h4> No More Lies");
    	writer.println("<a href=\"login.jsp\">Login here please</a></body></html>");

    }
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter wr = response.getWriter();
		wr.println("Hello Shorbani");
		HttpSession session =request.getSession(false);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		if(session==null){
			wr.println("You should not come here .. get out");
			return;
		}
		//remove from
		//session.setAttribute("user_name","shorbani");
		//session.setAttribute("domain_name","ncsu.edu");
		//remove to
		
		String from =(String)session.getAttribute("user_name")+'@'+(String)session.getAttribute("domain_name");		
		String sendTo=request.getParameter("to");		
		String subject=request.getParameter("subject");
		String content=request.getParameter("body");
		
		
		//encrypt ##TODO
		//String enSendTo = Crypt.encryption(sendTo);
		
		System.out.println("THIS IS STORE MAIL.");
		String enSendTo =sendTo;
		byte[] eSubject =null;
		byte[] eContent =null;
		try{
			eSubject=LocalEncrypter.encrypt(subject);
			eContent=LocalEncrypter.encrypt(content);
			System.out.println("THIS IS STORE MAIL. eSubjectis " +eSubject);
			
		}
		catch(Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
		}
		
		/*
		String enSubject = Crypt.encryption(subject);
		String enContent = Crypt.encryption(content);
		*/
		String enFrom =from;
		
		String url = "jdbc:mysql://localhost:3306/emailServer";
				
		System.out.println("Mail from "+from+" Sent to "+enSendTo+" with sub: "+eSubject+ " Content is: "+eContent);
		try
		{
			/* calling function instead
		    MessageDigest md = MessageDigest.getInstance("SHA-256");        
	        md.reset();
	        md.update(keyBytes);
	        md.update(content.getBytes("UTF-8")); 
	        //md.update(eContent);
	        byte[] digest = md.digest();
	        
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < digest.length; i++) 
	        {
	          sb.append(Integer.toHexString(0xFF & digest[i]));
	        }
	 		*/
	        
	        
	        //String contentHash = sb.toString();
			String contentHash=LocalEncrypter.calcHash(content);
	        
	        // get Timestamp of the message sent
	        java.util.Date date= new java.util.Date();
	        Timestamp timeStampMsg = new Timestamp(date.getTime());
	        
	        Class.forName("com.mysql.jdbc.Driver");
	    	con = DriverManager.getConnection(url, "root", "");
	    	//st1 = con.createStatement();
	        //String query="INSERT INTO emailStore (enSendTo, enFrom, enSubject, enContent, contentHash, timeStampMsg) VALUES(?,?,?,?,?,?) ";	 	        
		  //  rs = st1.executeQuery(query);
     
        
		   PreparedStatement insertInfo =(PreparedStatement)con.prepareStatement("INSERT INTO emailStore (enSendTo, enFrom, enSubject, enContent, contentHash,timeStampMsg) VALUES(?,?,?,?,?,?)");
			  
		    insertInfo.setString(1, enSendTo);
			insertInfo.setString(2, enFrom);
			insertInfo.setBytes(3, eSubject);
			insertInfo.setBytes(4, eContent);
			insertInfo.setString(5, contentHash);
			insertInfo.setTimestamp(6, timeStampMsg);					
			insertInfo.executeUpdate();			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		finally{	 	    	
			try {
						
				if(con != null) {
					con.close();
					con = null;
				}
			}
			catch (Exception e) {}
			
		}
		response.sendRedirect("home.jsp");
	}

}
