package servlets.workerServlets;

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

@WebServlet(urlPatterns = {"/worker/markAppointmentDone"})
public class MarkAppointmentDoneServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(MarkAppointmentDoneServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        int id = Integer.parseInt(request.getParameter("id"));
        LOG.info(String.format("Marking appointment %d status done",id));

        try {
            DBManager.getInstance().updateAppointmentStatusDone(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if(errorString == null) {
            LOG.info(String.format("Appointment %d status marked done",id));
        }
        request.setAttribute("errorString", errorString);
        response.sendRedirect(request.getContextPath() + "/worker/appointments");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

}