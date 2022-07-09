package servlets.adminServlets;

import entity.Service;
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

/**
 * Servlet for adding new service(only for admin)
 */
@WebServlet(urlPatterns = {"/admin/addService"})
public class AddServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AddServiceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOG.info("Add service page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/addServiceView.jsp").
                forward(request, response);
    }

    // Коли адмін вводить інформацію про послугу, і натискає Submit цей метод буде викликаний.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOG.info("Adding new service");
        String errorString = null;
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        int price = 0;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException | NullPointerException e) {
            LOG.error("Service price invalid");
            errorString = "Service price invalid";
        }

        if (name.length() == 0) {
            LOG.error("Service name invalid");
            errorString = "Service name invalid!";
        }

        Service service = new Service(name, price);

        if (errorString == null) {
            try {
                DBManager.getInstance().addService(service);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        request.setAttribute("errorString", errorString);

        if (errorString != null) {
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/addServiceView.jsp").
                    forward(request, response);
        } else {
            LOG.info(String.format("New service %s successfully added",name));
            response.sendRedirect(request.getContextPath() + "/admin/serviceList");
        }
    }

}