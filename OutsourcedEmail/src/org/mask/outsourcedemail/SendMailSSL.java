package org.mask.outsourcedemail;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Random;         // Contains the 'Random'
import java.math.BigInteger;

public class SendMailSSL 
{
    // extract email address and store it in some variable x.
	public SendMailSSL(){
		
	}
    public String sendMail(String secEmail) 
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        Connection con=null;  
		Statement st=null;
 
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() { protected PasswordAuthentication getPasswordAuthentication() {  return new PasswordAuthentication("csc574.mask","csc574project"); } }); 
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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("csc574.mask@gmail.com"));            
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(secEmail));  // substitute the extracted email id here.
            message.setSubject("Testing Subject");
            message.setText("Your temp password is " +"\n"+ temp_psw);            
    		//store in database
            String url = "jdbc:mysql://localhost:3306/emailServer";
    		String dbName = "root";  
    		String dbPassword = "";
    		try {
    			Class.forName("com.mysql.jdbc.Driver");
    			con = DriverManager.getConnection(url, dbName, dbPassword);
    			st = con.createStatement();    		
    			String salt = null;
    			ResultSet rs=null;
    			String query="SELECT salt FROM user where secondemail='"+secEmail+"'";
    		    rs = st.executeQuery(query);
    		    while(rs.next()){
    		    	salt=rs.getString(1);	
    		    }
    		    temp_psw=temp_psw+salt;
    			//hash the password:
    			MessageDigest md = MessageDigest.getInstance("SHA-256");        
    			md.reset();
    			md.update(temp_psw.getBytes("UTF-8")); 
    			byte[] digest = md.digest();
    			//password = new String(digest);
    			//salt = new String(saltarray);
    			StringBuffer sb = new StringBuffer();
    			for (int i = 0; i < digest.length; i++) {
    				sb.append(Integer.toHexString(0xFF & digest[i]));
    			}
    			temp_psw=sb.toString();
    		    
    			  
    			String tempPassUpdate="update user set tempPwd='"+temp_psw+"' where secondemail='"+secEmail+"'";
    			st.executeUpdate(tempPassUpdate);
    			java.util.Date date= new java.util.Date();
				Timestamp ts = new Timestamp(date.getTime());
				String updateTSTempPsw="update user set TStampPwdGen ='"+ts+"' where secondemail='"+secEmail+"';";
				st.executeUpdate(updateTSTempPsw);
			}
			catch(Exception e){
				System.out.println(e);
			}
			finally{
				try{
					if(st != null) {
						st.close();
						st = null;
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
            Transport.send(message);            
            System.out.println("Mail sent");
            return temp_psw;
            
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}