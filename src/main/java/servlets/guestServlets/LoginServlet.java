package servlets.guestServlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import entity.User;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;
import utils.MyUtils;

@WebServlet(urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(LoginServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info("Login page");
		this.getServletContext().getRequestDispatcher("/WEB-INF/views/guestViews/loginView.jsp").
				forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		User user = null;
		String errorString = null;

		if (email.length() == 0 || password.length() == 0) {
			LOG.error("Required email and password!");
			errorString = "Required email and password!";
		} else {
			try {
				user = DBManager.getInstance().findUser(email, password);
				if(user == null) {
					errorString = "Wrong email or password";
				}
			} catch (DBException e) {
				e.printStackTrace();
				errorString = e.getMessage();
			}
		}
		if (errorString != null) {
			request.getSession().setAttribute("errorString", errorString);
			response.sendRedirect("login");
		}
		else {
			HttpSession session = request.getSession();
			MyUtils.storeLoggedInUser(session, user);
			LOG.info("Successfully logged in");
			response.sendRedirect(request.getContextPath() + "/"+user.getRole()+"/home");
		}
	}

}