package org.mask.outsourcedemail;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.util.Random;         // Contains the 'Random'
import java.math.BigInteger;

public class SendMailSSL 
{
    // extract email address and store it in some variable x.
    public void sendMail(String secEmail, String bodyMessage) 
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
 
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
            message.setText(bodyMessage +"\n"+ temp_psw);
            //store in database
            Transport.send(message);
            
            System.out.println("Done");
            
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}