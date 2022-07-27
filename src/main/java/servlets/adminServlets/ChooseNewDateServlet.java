package servlets.adminServlets;

import entity.Appointment;
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

@WebServlet(urlPatterns = {"/admin/chooseDate"})
public class ChooseNewDateServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(ChooseNewDateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        request.setAttribute("id", id);
        request.setAttribute("minDate", LocalDate.now().plusDays(1));
        request.setAttribute("maxDate", LocalDate.now().plusMonths(1));
        Appointment appointment = null;
        String errorString = null;
        try {
            appointment = DBManager.getInstance().findAppointment(id);
        } catch (DBException e) {
            errorString = e.getMessage();
            e.printStackTrace();
        }
        request.setAttribute("errorString", errorString);
        if(appointment == null) {
            LOG.error(String.format("Appointment with id = %d doesn't exist",id));
            errorString = String.format("Appointment with id = %d doesn't exist",id);
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/homeView.jsp").
                    forward(request, response);
            return;
        }
        request.setAttribute("worker", appointment.getWorker());
        LOG.info("Choose new date for appointment page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/adminViews/chooseNewDateView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String date = request.getParameter("date");
        LOG.info(String.format("Chosen date of appointment, id - %s, date - %s", id, date));
        response.sendRedirect("chooseTime?id=" + id + "&date=" + date);
    }
}
