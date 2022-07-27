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

@WebServlet(urlPatterns = { "/client/replenishBalance" })
public class ReplenishBalanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(ReplenishBalanceServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isError = false;
        int id = MyUtils.getLoggedInUser(request.getSession()).getId();
        LOG.info("Replenishing balance...");
        try {
            DBManager.getInstance().updateUserBalance(id,100);
        } catch (DBException e) {
            isError = true;
            e.printStackTrace();
            LOG.error("Failed to replenish balance");
        }
        if(!isError) {
            LOG.info(String.format("Balance of user №%d replenished successfully",id));
            try {
                MyUtils.storeLoggedInUser(request.getSession(), DBManager.getInstance().findUser(id));
            } catch (DBException e) {
                e.printStackTrace();
                LOG.error("Failed to refresh currently logged in user");
            }
        }
        response.sendRedirect("home");
    }


}
