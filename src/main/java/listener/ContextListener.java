package listener;

import entity.Appointment;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import utils.DBManager;
import utils.MyTimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

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
        try {
            Timer timer = new Timer();
            MyTimerTask task = new MyTimerTask();

            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
			calendar.set(Calendar.HOUR_OF_DAY, 21);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date startTime = calendar.getTime();
            if(currentTime.after(startTime)) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                startTime = calendar.getTime();
            }

            timer.scheduleAtFixedRate(task, startTime, 1000 * 60 * 60 * 24);
        } catch (Exception e) {
            LOG.error("Problem initializing the task that was to run hourly: " + e.getMessage());
        }
    }

}
