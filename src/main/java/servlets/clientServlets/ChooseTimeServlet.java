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
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/client/chooseTime"})
public class ChooseTimeServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(ChooseTimeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String date = request.getParameter("date");
        String workerEmail = request.getParameter("workerEmail");
        User worker = null;
        String errorString = null;
        List<String> slotList = null;


        try {
            worker = DBManager.getInstance().findUser(workerEmail);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            slotList = DBManager.getInstance().queryAvailableSlots(workerEmail, date);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            response.sendRedirect(request.getServletPath() + "/client/chooseWorker");
            return;
        }

        request.setAttribute("name", name);
        request.setAttribute("date", date);
        request.setAttribute("worker", worker);
        request.setAttribute("slotList", slotList);

        LOG.info("Choose time for appointment page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/chooseTimeView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String workerEmail = request.getParameter("workerEmail");
        String clientEmail = request.getParameter("clientEmail");
        String service = request.getParameter("name");
        String datetime = request.getParameter("date") + " " + request.getParameter("slots");
        String errorString = null;

        try {
            DBManager.getInstance().addAppointment(workerEmail, clientEmail, datetime, service);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/chooseTimeView.jsp").
                    forward(request, response);
        } else {
            LOG.info(String.format("Appointment successfully created, service - %s, datetime - %s, worker email - %s, " +
                    "client email - %s",service,datetime,workerEmail,clientEmail));
            response.sendRedirect(request.getContextPath() + "/client/serviceList");
        }
    }
}
