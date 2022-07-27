package servlets.adminServlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;
import utils.MyUtils;

@WebServlet(urlPatterns = { "/admin/deleteWorker" })
public class DeleteWorkerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(DeleteWorkerServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        LOG.info("Deleting worker with id "+id);
        String errorString = null;

        try {
            DBManager.getInstance().deleteWorker(id);
        } catch (DBException e) {
            e.printStackTrace();
            errorString = e.getMessage();
        }

        if (errorString != null) {
            request.setAttribute("errorString", errorString);
            request.getServletContext().getRequestDispatcher("/WEB-INF/views/serviceListView.jsp").
                    forward(request, response);
        }
        else {
            LOG.info(String.format("Worker №%s successfully deleted",id));
            response.sendRedirect(request.getContextPath() + "/admin/workerList");
        }

    }

}