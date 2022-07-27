package servlets.adminServlets;

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
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/editWorker"})
public class EditWorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(EditServiceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        User worker = null;
        String errorString = null;
        List<Service> services = null;
        List<String> days = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        try {
            services = DBManager.getInstance().queryService();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            worker = DBManager.getInstance().findUser(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = "Worker not found";
        }

        if (errorString != null) {
            request.getSession().setAttribute("errorString",errorString);
            response.sendRedirect("workerList");
            return;
        }

        request.setAttribute("worker", worker);
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
        int id = Integer.parseInt(request.getParameter("id"));
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String[] services = request.getParameterValues("service");
        String[] workingDays = request.getParameterValues("workingDay");
        String errorString = null;
        LOG.info("Editing worker №" + id);

        if (services == null) {
            LOG.error("Required at least one service");
            errorString = "Required at least one service";
        }

        if (workingDays == null) {
            LOG.error("Required at least one working day");
            errorString = "Required at least one working day";
        }

        if (errorString == null) {
            try {
                DBManager.getInstance().updateWorker(id, email, phoneNumber, Arrays.asList(services), Arrays.asList(workingDays));
            } catch (DBException e) {
                e.printStackTrace();
                errorString = e.getMessage();
            }
        }

        if (errorString != null) {
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("editWorker?id=" + id);
        } else {
            LOG.info(String.format("Worker with email %s successfully edited", email));
            response.sendRedirect("workerList");
        }
    }

}