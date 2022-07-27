package servlets;

import entity.Service;
import entity.User;
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
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = {"/serviceList", "/admin/serviceList", "/client/serviceList", "/worker/serviceList"})
public class ServiceListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(ServiceListServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String errorString = null;
        List<Service> serviceList = null;
        List<User> workerList = null;
        String filter = (String) request.getSession().getAttribute("filterForServices");
        try {
            workerList = DBManager.getInstance().queryWorker("first_name", Collections.emptyList(), null);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }
        try {
            if (filter == null) {
                serviceList = DBManager.getInstance().queryService();
            } else {
                serviceList = DBManager.getInstance().findUser(filter).getServices();
            }
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }
        request.setAttribute("errorString", errorString);
        request.setAttribute("serviceList", serviceList);
        request.setAttribute("workerList", workerList);

        LOG.info("Service list page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/serviceListView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession().setAttribute("filterForServices", request.getParameter("filter"));
        response.sendRedirect("serviceList");
    }

}