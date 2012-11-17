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
 * Servlet implementation class SaltDigger
 */
@WebServlet(description = "Gives the salt", urlPatterns = { "/SaltDiggerPath" })
public class SaltDigger extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
		//get salt here.. and set hidden field in response html .. 
		writer.println("<html>" +
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
				"<title>Kmail</title>" +
				"</head>" +
				"<body> <h3> Hi "+session.getAttribute("name")+"</h3> " +
				"<form method =\"Post\" action = \"AuthenticatorPath\">  " +				
				"<br/> Password <input name =\"password\" type=\"password\"> </br> " +
				"<input name=\"Submit\" type=\"submit\" value=\"Submit\" /> " +
				"</form></body></html>");				
	}
	

}
