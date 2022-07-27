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
        int workerId = Integer.parseInt(request.getParameter("workerId"));
        User worker = null;
        String errorString = null;
        List<String> slotList = null;


        try {
            worker = DBManager.getInstance().findUser(workerId);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            slotList = DBManager.getInstance().queryAvailableSlots(workerId, date);
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
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/chooseNewTimeView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int workerId = Integer.parseInt(request.getParameter("workerId"));
        int clientId = Integer.parseInt(request.getParameter("clientId"));
        String service = request.getParameter("name");
        String date = request.getParameter("date");
        String datetime = date + " " + request.getParameter("slots");
        String errorString = null;

        try {
            DBManager.getInstance().addAppointment(workerId, clientId, datetime, service);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("chooseTime?name="+service+"&date="+date+"&workerId="+workerId);
        } else {
            LOG.info(String.format("Appointment successfully created, service - %s, datetime - %s, worker id - %s, " +
                    "client id - %s",service,datetime,workerId,clientId));
            response.sendRedirect(request.getContextPath() + "/client/serviceList");
        }
    }
}
