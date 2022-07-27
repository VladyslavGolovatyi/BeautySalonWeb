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

@WebServlet(urlPatterns = {"/admin/editService"})
public class EditServiceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(EditServiceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        Service service = null;
        String errorString = null;

        try {
            service = DBManager.getInstance().findService(name);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = "Service not found";
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            response.sendRedirect(request.getServletPath() + "/admin/serviceList");
            return;
        }

        request.setAttribute("service", service);
        LOG.info("Edit service page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/editServiceView.jsp").
                forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        LOG.info("Editing service " + name);
        int price = 0;
        String errorString = null;

        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException | NullPointerException e) {
            LOG.error("Service price invalid");
            errorString = "Service price invalid";
        }

        Service service = new Service(name, price);

        if (errorString == null) {
            try {
                DBManager.getInstance().updateService(name,price);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        if (errorString != null) {
            try {
                service = DBManager.getInstance().findService(name);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = "Service not found";
            }
        }
        request.setAttribute("service", service);
        if (errorString != null) {
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("editService?name="+name);
        } else {
            LOG.info(String.format("Service %s successfully edited, new price - %d", name, price));
            response.sendRedirect("serviceList");
        }
    }

}