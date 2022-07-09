package servlets.adminServlets;

import entity.Service;
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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servlet for adding new service(only for admin)
 */
@WebServlet(urlPatterns = {"/admin/addWorker"})
public class AddWorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AddWorkerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Service> list = null;
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
        String errorString = null;

        try {
            list = DBManager.getInstance().queryService();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }
        request.setAttribute("serviceList", list);
        request.setAttribute("days", days);
        request.setAttribute("errorString", errorString);

        LOG.info("Add worker page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/addWorkerView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOG.info("Adding new worker");
        String first_name = request.getParameter("first_name");
        String last_name = request.getParameter("last_name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone_number = request.getParameter("phone_number");


        String errorString = null;
        List<Service> list = null;
        List<String> workerServices = null;
        List<String> workingDays = null;
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        try {
            workerServices = new ArrayList<>(Arrays.asList(request.getParameterValues("service")));

        } catch (NullPointerException e) {
            LOG.error("Worker must have at least one service");
            errorString = "Worker must have at least one service";
        }

        try {
            workingDays = new ArrayList<>(Arrays.asList(request.getParameterValues("workingDay")));
        } catch (NullPointerException e) {
            LOG.error("Worker must have at least one working day");
            errorString = "Worker must have at least one working day";
        }

        try {
            list = DBManager.getInstance().queryService();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (password.length() == 0) {
            LOG.error("Password is invalid!");
            errorString = "Password is invalid!";
        }

        if (email.length() == 0) {
            LOG.error("Email is invalid!");
            errorString = "Email is invalid!";
        }

        if (first_name.length() == 0 || last_name.length() == 0) {
            LOG.error("Name is invalid!");
            errorString = "Name is invalid!";
        }

        if (errorString == null) {
            try {
                DBManager.getInstance().addWorker(email, password, "worker", first_name, last_name, phone_number,
                        workerServices.toArray(new String[0]), workingDays.toArray(new String[0]));
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            request.setAttribute("serviceList", list);
            request.setAttribute("days", days);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/addWorkerView.jsp").
                    forward(request, response);
        } else {
            LOG.info(String.format("New worker %s successfully added",first_name+" "+last_name));
            response.sendRedirect(request.getContextPath() + "/admin/workerList");
        }
    }

}