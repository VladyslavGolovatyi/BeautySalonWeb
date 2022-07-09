package servlets.guestServlets;

import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String phone_number = request.getParameter("phone_number");
        String errorString = null;

        if (email.length() == 0 || password.length() == 0 || first_name.length() == 0 || last_name.length() == 0 || phone_number.length() == 0) {
            LOG.error("Fill in all the fields!");
            errorString = "Fill in all the fields!";
        } else {
            try {
                DBManager.getInstance().addClient(email, password, "client", first_name, last_name, phone_number);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            this.getServletContext().getRequestDispatcher("/WEB-INF/views/guestViews/newAccountView.jsp").
                    forward(request, response);
        } else {
            LOG.info("New account successfully created");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

}
