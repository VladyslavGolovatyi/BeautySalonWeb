
package listener;

import exception.DBException;
import filters.EncodingFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
	private static final Logger LOG = LogManager.getLogger(ContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		LOG.info("ContextListener");
		try {
			DBManager.getInstance();
		} catch (DBException e) {
			throw new IllegalStateException(e);
		}
	}

}
