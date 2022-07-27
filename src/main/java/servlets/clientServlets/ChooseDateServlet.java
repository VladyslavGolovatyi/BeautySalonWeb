package servlets.clientServlets;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/client/chooseDate"})
public class ChooseDateServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(servlets.clientServlets.ChooseDateServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("name", request.getParameter("name"));
        request.setAttribute("minDate", LocalDate.now().plusDays(1));
        request.setAttribute("maxDate", LocalDate.now().plusMonths(1));
        LOG.info("Choose date for appointment page");
        request.getServletContext().getRequestDispatcher("/WEB-INF/views/clientViews/chooseNewDateView.jsp").
                forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String service = request.getParameter("name");
        String date = request.getParameter("date");
        LOG.info(String.format("Chosen date of appointment, service - %s, date - %s", service, date));
        response.sendRedirect("chooseWorker?name=" + service + "&date=" + date);
    }
}
