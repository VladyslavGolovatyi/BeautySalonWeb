package utils;

import entity.Appointment;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MyTimerTask extends TimerTask {
    private static final Logger LOG = LogManager.getLogger(MyTimerTask.class);

    public void run() {
        List<Appointment> appointments = new ArrayList<>();
        try {
            appointments = DBManager.getInstance().queryAllAppointments().stream().
                    filter(a -> a.getTimeslot().startsWith(LocalDate.now().minusDays(1).toString())).
                    filter(a -> a.getStatus().contains(",done")).
                    collect(Collectors.toList());
        } catch (DBException ex) {
            ex.printStackTrace();
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("beautysalonlouise@gmail.com", "cwztdouonaxiffer");

            }

        });
        session.setDebug(true);
        for (Appointment appointment : appointments) {
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("beautysalonlouise@gmail.com"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(appointment.getClient().getEmail()));
                message.setSubject("Response about your yesterday appointment");
                message.setText(String.format("Please, leave response about appointment.\n Service - %s.\n Datetime - %s.\n Worker - %s\n" +
                                "Leave your feedback here - http://localhost:8080/BeautySalonLouise/client/leaveFeedback?id=%d",
                        appointment.getService().getName(), appointment.getTimeslot(),
                        appointment.getWorker().getFirstName() +" "+ appointment.getWorker().getLastName(),appointment.getId()));
                LOG.info("Sending email");
                Transport.send(message);
                LOG.info("Sent email successfully....");
            } catch (MessagingException ex) {
                LOG.error("Cannot send email message");
                ex.printStackTrace();
            }

        }
    }
}