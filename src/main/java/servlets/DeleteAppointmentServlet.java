package servlets;

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
import java.util.Objects;

@WebServlet(urlPatterns = {"/client/deleteAppointment", "/admin/deleteAppointment"})
public class DeleteAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(DeleteAppointmentServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String errorString = null;
        int id = Integer.parseInt(request.getParameter("id"));
        try {
            if (!(MyUtils.getLoggedInUser(request.getSession()).getRole().equals("admin")) &&
                    !DBManager.getInstance().findAppointment(id).getClient().equals(MyUtils.getLoggedInUser(request.getSession()))) {
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

        LOG.info("Deleting appointment " + id);
        try {
            DBManager.getInstance().deleteAppointment(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }
        request.setAttribute("errorString", errorString);
        if (errorString == null) {
            LOG.info(String.format("Appointment %d successfully deleted", id));
        }
        response.sendRedirect(request.getContextPath() + "/" + MyUtils.getLoggedInUser(request.getSession()).getRole() + "/appointments");
    }

}