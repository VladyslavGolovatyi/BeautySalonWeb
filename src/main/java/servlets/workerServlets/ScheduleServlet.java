package servlets.workerServlets;

import entity.Appointment;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/worker/schedule"})
public class ScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(ScheduleServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Appointment> appointments = new ArrayList<>();
        List<String> workingDays = new ArrayList<>();
        List<String> workingDaysInMonth = new ArrayList<>();
        List<String> timeslots = new ArrayList<>();
        Map<String,Appointment> appointmentMap = new HashMap<>();
        HttpSession httpSession = request.getSession();
        int userId = MyUtils.getLoggedInUser(httpSession).getId();
        String errorString = null;

        for (int i = 8; i < 19; ++i) {
            if (i == 13) {
                continue;
            }
            timeslots.add(i + ":00-"+i+":30");
            timeslots.add(i + ":30-"+(i+1)+":00");
        }

        try {
            workingDays = DBManager.getInstance().findUser(userId).getWorkingDays();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        for (LocalDate date = LocalDate.now(); !date.equals(LocalDate.now().plusMonths(1)); date = date.plusDays(1)) {
            if (workingDays.stream().anyMatch(date.getDayOfWeek().name()::equalsIgnoreCase)) {
                workingDaysInMonth.add(String.valueOf(date));
            }
        }

        try {
            appointments = DBManager.getInstance().queryWorkerAppointments(userId);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        for (Appointment appointment:appointments) {
            appointmentMap.put(appointment.getTimeslot(),appointment);
        }

        request.setAttribute("errorString", errorString);
        request.setAttribute("appointmentMap", appointmentMap);
        request.setAttribute("workingDaysInMonth", workingDaysInMonth);
        request.setAttribute("timeslots", timeslots);

        LOG.info("Schedule page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/workerViews/scheduleView.jsp").
                forward(request, response);
    }

}