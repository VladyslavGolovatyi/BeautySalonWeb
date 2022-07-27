package servlets;

import entity.Appointment;
import entity.Response;
import entity.User;
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

@WebServlet(urlPatterns = {"/client/responses", "/admin/responses", "/worker/responses", "/responses"})
public class ResponsesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(ResponsesServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Response> responses = null;
        String errorString = null;
        int workerId = Integer.parseInt(request.getParameter("id"));
        User worker = null;

        try {
            responses = DBManager.getInstance().queryWorkerResponses(workerId);
        } catch (DBException ex) {
            errorString = ex.getMessage();
            ex.printStackTrace();
        }

        try {
            worker = DBManager.getInstance().findUser(workerId);
        } catch (DBException ex) {
            errorString = ex.getMessage();
            ex.printStackTrace();
        }

        request.setAttribute("errorString",errorString);
        request.setAttribute("responseList",responses);
        request.setAttribute("worker",worker);

        LOG.info("Responses page");
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/responsesView.jsp").
                forward(request, response);

    }

}