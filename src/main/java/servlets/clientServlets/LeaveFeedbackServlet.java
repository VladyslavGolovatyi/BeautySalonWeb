package servlets.clientServlets;

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
import java.io.IOException;

@WebServlet(urlPatterns = {"/client/leaveFeedback"})
public class LeaveFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(LeaveFeedbackServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        Appointment appointment = new Appointment();
        int id = Integer.parseInt(request.getParameter("id"));
        request.setAttribute("id", request.getParameter("id"));

        try {
            appointment = DBManager.getInstance().findAppointment(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        try {
            if (!DBManager.getInstance().findAppointment(id).getClient().getEmail().
                    equals(MyUtils.getLoggedInUser(request.getSession()).getEmail())) {
                errorString = "You do not have permission to access the requested resource";
                request.setAttribute("errorString", errorString);
                request.getRequestDispatcher("/WEB-INF/views/homeView.jsp")
                        .forward(request, response);
                return;
            }
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (appointment.getStatus().contains("not done")) {
            errorString = "Appointment has not been done yet";
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("appointments");
            return;
        }

        if(appointment.isHasResponse()) {
            errorString = "You have already left feedback at this appointment";
            request.getSession().setAttribute("errorString", errorString);
            response.sendRedirect("appointments");
            return;
        }


        LOG.info(String.format("Leaving feedback for the appointment %d", id));

        request.setAttribute("errorString", errorString);
        request.setAttribute("appointment", appointment);
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/leaveFeedbackView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        int id = Integer.parseInt(request.getParameter("id"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String message = request.getParameter("message");

        try {
            DBManager.getInstance().leaveFeedback(id, rating, message);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString == null) {
            LOG.info(String.format("Feedback for the appointment №%d successfully created", id));
            response.sendRedirect("appointments");
        } else {
            request.getSession().setAttribute("errorString", errorString);
            LOG.error("Cannot create feedback for appointment №" + id);
            response.sendRedirect("leaveFeedback?id=" + id);
        }


    }

}