package servlets;

import entity.Service;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = {"/workerList", "/admin/workerList", "/client/workerList", "/worker/workerList"})
public class WorkerListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(WorkerListServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        List<User> workerList = null;
        List<Service> serviceList = null;

        String sorting = request.getSession().getAttribute("isSortedByRating") == null ||
                !(boolean) request.getSession().getAttribute("isSortedByRating") ? "first_name" : "rating";
        List<String> filter = request.getSession().getAttribute("filterListForWorkers") == null ? new ArrayList<>() :
                (List<String>) request.getSession().getAttribute("filterListForWorkers");
        try {
            workerList = DBManager.getInstance().queryWorker(sorting, filter, null);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            serviceList = DBManager.getInstance().queryService();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        request.setAttribute("errorString", errorString);
        request.setAttribute("workerList", workerList);
        request.setAttribute("serviceList", serviceList);

        LOG.info("Worker list page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/workerListView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("sorting").equals("rating")) {
            request.getSession().setAttribute("isSortedByRating", true);
        } else {
            request.getSession().setAttribute("isSortedByRating", false);
        }
        if (request.getParameterValues("filter") == null) {
            request.getSession().setAttribute("filterListForWorkers", new ArrayList<>());
        } else {
            request.getSession().setAttribute("filterListForWorkers", Arrays.asList(request.getParameterValues("filter")));
        }
        boolean isGuest = MyUtils.getLoggedInUser(request.getSession()) == null;
        response.sendRedirect(request.getContextPath() + (!isGuest ? "/" + MyUtils.getLoggedInUser(request.getSession()).getRole() : "") + "/workerList");
    }

}