package org.mask.outsourcedemail;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Authenticator
 */
@WebServlet("/AuthenticatorPath")
public class Authenticator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authenticator() {
        super();
        // TODO Auto-generated constructor stub
    }

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
	    if(session == null || session.isNew()){
	       //valid session doesn't exist
	       //do something like send the user to a login screen
	    	System.out.println("WHY session null or new!");
	    }
	    else{
	    	String userName=(String)session.getAttribute("name");
	    	System.out.println(session.getId());
	    	if(userName == null){
	    		//no username in session..this should never happen ... validation check by javascript 	    
	    		//user probably hasn't logged in properly
	    		System.out.println("WHY U no give username");
	    	}	    	
	    	PrintWriter writer= response.getWriter();
	    	if( userName.equals("korak") ){
	    		String passwd=request.getParameter("password");
	    		if(passwd.equals("letmein")){	    		
	    			writer.println("<h4>hellooo..."+userName+"You are in!</h4>");
	    			session.setAttribute("logged", true);
	    			session.setMaxInactiveInterval(1);
	    		}
	    		else{
	    			writer.println("<h4>hellooo..."+userName+"wtf!</h4>");
	    		}
	    	}
	    	else{
	    		writer.println("<h4>wtf!</h4>");
	    	}
	    }
	}
}
