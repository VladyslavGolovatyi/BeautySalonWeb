package servlets.adminServlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import entity.Service;
import entity.User;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;
import utils.MyUtils;

@WebServlet(urlPatterns = { "/admin/editWorker" })
public class EditWorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(EditServiceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        User worker = null;
        String errorString = null;
        List<Service> services = null;
        List<String> days = Arrays.asList("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");

        try {
            services = DBManager.getInstance().queryService();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            worker = DBManager.getInstance().findUser(email);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = "Worker not found";
        }

        if (errorString != null) {
            response.sendRedirect(request.getServletPath() + "/admin/workerList");
            return;
        }

        request.setAttribute("worker", worker);
        assert worker != null;
        request.setAttribute("workerServices", worker.getServices());
        request.setAttribute("serviceList", services);
        request.setAttribute("days", days);
        request.setAttribute("workingDays", worker.getWorkingDays());

        LOG.info("Edit worker page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/editWorkerView.jsp").
                forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String phone_number = request.getParameter("phone_number");
        String[] services = request.getParameterValues("service");
        String[] workingDays = request.getParameterValues("workingDay");
        String errorString = null;
        LOG.info("Editing worker with email "+email);

        if(services == null) {
            LOG.error("Required at least one service");
            errorString = "Required at least one service";
        }

        if(workingDays == null) {
            LOG.error("Required at least one working day");
            errorString = "Required at least one working day";
        }

        if (errorString == null) {
            try {
                DBManager.getInstance().updateWorker(email, phone_number, services, workingDays);
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/editWorkerView.jsp").
                    forward(request, response);
        }
        else {
            LOG.info(String.format("Worker with email %s successfully edited",email));
            response.sendRedirect(request.getContextPath() + "/admin/workerList");
        }
    }

}