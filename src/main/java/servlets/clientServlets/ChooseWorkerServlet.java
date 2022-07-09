package servlets.clientServlets;

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
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = {"/client/chooseWorker"})
public class ChooseWorkerServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(ChooseWorkerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String date = request.getParameter("date");
        List<User> workers = null;
        String errorString = null;

        try {
            workers = DBManager.getInstance().queryWorker("first_name", Collections.singletonList(name), date);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            response.sendRedirect(request.getServletPath() + "/client/chooseDate");
            return;
        }

        request.setAttribute("name", name);
        request.setAttribute("date", date);
        request.setAttribute("workerList", workers);

        LOG.info("Choose worker for appointment page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/chooseWorkerView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String service = request.getParameter("name");
        String date = request.getParameter("date");
        String email = request.getParameter("workers");
        LOG.info(String.format("Chosen worker for appointment, service - %s, date - %s, worker email - %s", service, date, email));
        response.sendRedirect(request.getContextPath() + "/client/chooseTime?name=" + service + "&date=" + date + "&workerEmail=" + email);
    }
}
