package servlets.adminServlets;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/chooseTime"})
public class ChooseNewTimeServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(ChooseNewTimeServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String date = request.getParameter("date");
        String workerEmail = null;
        String errorString = null;
        List<String> slotList = null;

        try {
            workerEmail = DBManager.getInstance().findAppointment(id).getWorker().getEmail();
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

        String dayOfTheWeek = LocalDate.parse(date).getDayOfWeek().name().toLowerCase();
        try {
            if(DBManager.getInstance().findUser(workerEmail).getWorkingDays().stream().noneMatch(dayOfTheWeek::equalsIgnoreCase)) {
                slotList = new ArrayList<>();
            }
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            response.sendRedirect(request.getServletPath() + "/admin/chooseDate");
            return;
        }

        request.setAttribute("id", id);
        request.setAttribute("date", date);
        request.setAttribute("slotList", slotList);

        LOG.info("Choose new time for appointment page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/chooseTimeView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String datetime = request.getParameter("date") + " " + request.getParameter("slots");
        String errorString = null;

        try {
            DBManager.getInstance().updateAppointment(datetime, id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/chooseTimeView.jsp").
                    forward(request, response);
        } else {
            LOG.info(String.format("Appointment №%d successfully updated, new datetime - %s", id, datetime));
            response.sendRedirect(request.getContextPath() + "/admin/appointments");
        }
    }
}
