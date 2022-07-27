package servlets.guestServlets;

import entity.User;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;
import utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet implementation class NewAccountServlet
 */
@WebServlet("/newAccount")
public class NewAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(NewAccountServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.info("Create account page");
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/guestViews/newAccountView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String first_name = request.getParameter("firstName");
        String last_name = request.getParameter("lastName");
        String phone_number = request.getParameter("phoneNumber");
        String errorString = null;

        try {
            if(DBManager.getInstance().findUser(email) != null) {
                LOG.error("User with this email already existing");
                errorString = "User with this email already existing";
            }
        } catch (DBException ex) {
            errorString = ex.getMessage();
        }

        if (email.length() == 0 || password.length() == 0 || first_name.length() == 0 || last_name.length() == 0 || phone_number.length() == 0) {
            LOG.error("Fill in all the fields!");
            errorString = "Fill in all the fields!";
        } else if(errorString == null){
            try {
                DBManager.getInstance().addClient(email, password, "client", first_name, last_name, phone_number);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        User user = null;
        try {
            user = DBManager.getInstance().findUser(email);
        } catch (DBException e) {
            LOG.error("Cannot find user");
            errorString = "Cannot find user";
        }

        if (errorString != null) {
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("newAccount");
        } else {
            LOG.info("New account successfully created");
            HttpSession session = request.getSession();
            MyUtils.storeLoggedInUser(session, user);
            LOG.info("Successfully logged in");
            response.sendRedirect( "client/home");
        }
    }

}
