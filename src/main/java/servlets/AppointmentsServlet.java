package servlets;

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
import java.util.List;

@WebServlet(urlPatterns = {"/client/appointments", "/admin/appointments", "/worker/appointments"})
public class AppointmentsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(AppointmentsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Appointment> appointments = null;
        HttpSession httpSession = request.getSession();
        String userEmail = MyUtils.getLoggedInUser(httpSession).getEmail();
        String role = MyUtils.getLoggedInUser(httpSession).getRole();
        String errorString = null;

        try {
            if(role.equals("client"))
                appointments = DBManager.getInstance().queryClientAppointments(userEmail);
            if(role.equals("worker"))
                appointments = DBManager.getInstance().queryWorkerAppointments(userEmail);
            if(role.equals("admin"))
                appointments = DBManager.getInstance().queryAllAppointments();
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        request.setAttribute("errorString",errorString);
        request.setAttribute("appointmentList",appointments);

        LOG.info("Appointments page");
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/appointmentsView.jsp").
                forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}