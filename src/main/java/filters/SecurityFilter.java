package filters;

import entity.User;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.MyUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "securityFilter", urlPatterns = {"/*"})
public class SecurityFilter implements Filter {
    private static final Logger LOG = LogManager.getLogger(SecurityFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOG.info("Security filter starts");

        if (accessAllowed(request)) {
            LOG.info("Security filter finished");
            chain.doFilter(request, response);
        } else {
            LOG.info("Access denied");
            String errorString = "You do not have permission to access the requested resource";
            request.setAttribute("errorString", errorString);
            request.getRequestDispatcher("/WEB-INF/views/homeView.jsp")
                    .forward(request, response);
        }
    }

    private boolean accessAllowed(ServletRequest request) {
        List<String> roles = Arrays.asList("admin", "client", "worker");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        User loggedInUser = MyUtils.getLoggedInUser(httpRequest.getSession());
        String loggedInUserRole = loggedInUser == null ? "guest" : loggedInUser.getRole();

        LOG.info(String.format("Currently logged in user role - %s, email - %s ", loggedInUserRole,
                loggedInUser == null ? "" : loggedInUser.getEmail()));
        String pageAccessLevel = httpRequest.getServletPath().split("/")[1];
        if (!roles.contains(pageAccessLevel)) {
            pageAccessLevel = "guest";
        }
        LOG.info(String.format("Page access level - %s", pageAccessLevel));
        return pageAccessLevel.equals("guest") || loggedInUserRole.equals(pageAccessLevel);
    }

}
