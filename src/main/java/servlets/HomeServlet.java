package servlets;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/client/home", "/admin/home", "/worker/home", "/home"})
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(HomeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.info("Home page");
        if (request.getServletPath().equals("/home")) {
            MyUtils.deleteLoggedInUser(request.getSession());
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/homeView.jsp").
                forward(request, response);

    }

}