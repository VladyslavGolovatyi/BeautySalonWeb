package servlets.clientServlets;

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

@WebServlet(urlPatterns = {"/client/payForAppointment"})
public class PayingForAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(PayingForAppointmentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        int id = Integer.parseInt(request.getParameter("id"));
        LOG.info(String.format("Paying for the appointment %d",id));
        try {
            DBManager.getInstance().payForAppointment(id);
            DBManager.getInstance().updateAppointmentStatusPaid(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if(errorString == null) {
            LOG.info(String.format("Appointment %d successfully paid",id));
            try {
                MyUtils.storeLoggedInUser(request.getSession(), DBManager.getInstance().findUser(MyUtils.getLoggedInUser(request.getSession()).getEmail()));
            } catch (DBException e) {
                e.printStackTrace();
                LOG.error("Failed to refresh currently logged in user");
            }
            response.sendRedirect(request.getContextPath() + "/client/appointments");
        } else {
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/homeView.jsp").
                    forward(request, response);
        }
    }

}