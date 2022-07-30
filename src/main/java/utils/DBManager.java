package utils;

import entity.Appointment;
import entity.Response;
import entity.Service;
import entity.User;
import exception.DBException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The DAO-layer class
 */
public class DBManager {

    private final static String INSERT_INTO_SERVICES = "INSERT INTO Services VALUES (?,?)";
    private final static String INSERT_INTO_WORKERS_SERVICES = "INSERT INTO workers_services VALUES ";
    private final static String INSERT_INTO_WORKERS_WORKING_DAYS = "INSERT INTO workers_workingDays VALUES ";
    private final static String INSERT_INTO_USERS = "INSERT INTO users VALUES(null,?,?,?,?,?,?,0,0)";
    private final static String INSERT_INTO_APPOINTMENTS = "INSERT INTO appointments VALUES (null,?,?,false,false,?,?)";
    private final static String INSERT_INTO_RESPONSES = "INSERT INTO responses VALUES(null,?,?,?)";
    private final static String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
    private final static String SELECT_APPOINTMENT = "SELECT * FROM appointments WHERE id=?";
    private final static String SELECT_CLIENT_APPOINTMENTS = "SELECT * FROM appointments WHERE appointments.timeslot>? && client_id=? order by timeslot";
    private final static String SELECT_WORKER_APPOINTMENTS = "SELECT * FROM appointments WHERE appointments.timeslot>? && worker_id=? order by timeslot";
    private final static String SELECT_ALL_APPOINTMENTS = "SELECT * FROM appointments WHERE timeslot>? order by timeslot";
    private final static String SELECT_ALL_PAST_APPOINTMENTS = "SELECT * FROM appointments WHERE timeslot<=? order by timeslot";
    private final static String SELECT_SERVICE = "SELECT * FROM Services WHERE service_name=?";
    private final static String SELECT_ALL_SERVICES = "SELECT * FROM Services";
    private final static String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private final static String SELECT_USER_BY_EMAIL_AND_PASSWORD = "SELECT * FROM users WHERE email = ? AND password = ?";
    private final static String SELECT_USER_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private final static String SELECT_ADMIN = "SELECT * FROM users WHERE role = 'admin'";
    private final static String SELECT_ALL_WORKERS_ORDERED_BY = "SELECT * FROM users WHERE role = 'worker' ORDER BY ";
    private final static String SELECT_WORKER_SERVICES = "SELECT service FROM workers_services WHERE worker_id = ?";
    private final static String SELECT_WORKER_WORKING_DAYS = "SELECT working_day FROM workers_workingdays WHERE worker_id = ?";
    private final static String SELECT_WORKER_RESPONSES = "SELECT * FROM responses INNER JOIN appointments ON responses.appointment_id=appointments.id WHERE worker_id=?";
    private final static String SELECT_APPOINTMENT_RESPONSE = "SELECT * FROM responses WHERE appointment_id=?";
    private final static String UPDATE_SERVICE = "UPDATE services SET price=? WHERE service_name=?";
    private final static String UPDATE_USER = "UPDATE users SET phone_number=?, email=? WHERE id=?";
    private final static String UPDATE_USER_RATING = "UPDATE users SET rating = rating+? WHERE id=?";
    private final static String UPDATE_CLIENT_BALANCE = "UPDATE users SET money_balance=money_balance+? where id = ?";
    private final static String UPDATE_APPOINTMENT_STATUS_PAID = "UPDATE appointments SET is_paid=true where id=?";
    private final static String UPDATE_APPOINTMENT_STATUS_DONE = "UPDATE appointments SET is_done=true where id=?";
    private final static String UPDATE_APPOINTMENT_TIMESLOT = "UPDATE appointments SET timeslot=? where id=?";
    private final static String DELETE_FROM_WORKERS_SERVICES = "DELETE FROM workers_services WHERE worker_id = ?";
    private final static String DELETE_FROM_WORKERS_WORKING_DAYS = "DELETE FROM workers_workingdays WHERE worker_id = ?";
    private final static String DELETE_SERVICE = "DELETE FROM Services WHERE service_name= ?";
    private final static String DELETE_USER = "DELETE FROM users WHERE id= ?";
    private final static String DELETE_APPOINTMENT = "DELETE FROM appointments WHERE id= ?";
    private static final Logger LOG = LogManager.getLogger(DBManager.class);
    private static DBManager instance;
    private final DataSource ds;

    /**
     * Private singleton constructor
     */
    private DBManager() throws DBException {
        try {
            ds = getDataSource();
            LOG.info("Data source ==> " + ds);
        } catch (NamingException ex) {
            LOG.error("Cannot obtain the data source", ex);
            throw new DBException("Cannot obtain the data source", ex);
        }
    }

    private DBManager(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @throws DBException the db exception
     */
    public static synchronized DBManager getInstance() throws DBException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Gets instance.
     *
     * @param ds the ds
     * @return the instance
     * @throws DBException the db exception
     */
    public static synchronized DBManager getInstance(DataSource ds) throws DBException {
        if (instance == null) {
            instance = new DBManager(ds);
        }
        return instance;
    }

    private DataSource getDataSource() throws NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        return (DataSource) envContext.lookup("jdbc/beautysalondb");
    }

    /**
     * Returns a DB connection from the Pool Connections.
     *
     * @return DB connection.
     */
    private Connection getConnection() throws DBException {
        Connection con;
        try {
            con = ds.getConnection();
        } catch (SQLException ex) {
            LOG.error("Cannot obtain a connection from the pool", ex);
            throw new DBException("Cannot obtain a connection from the pool", ex);
        }
        return con;
    }

    /**
     * Rollbacks a connection.
     */
    private void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                LOG.error("Cannot rollback transaction", ex);
            }
        }
    }

    /**
     * Closes a connection.
     */
    private void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                LOG.error("Cannot close a connection", ex);
            }
        }
    }

    /**
     * Closes a statement object.
     */
    private void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                LOG.error("Cannot close a statement", ex);
            }
        }
    }

    /**
     * Closes a result set object.
     */
    private void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.error("Cannot close a result set", ex);
            }
        }
    }


    /**
     * Add service.
     *
     * @param name  the name
     * @param price the price
     * @throws DBException the db exception
     */
    public void addService(String name, int price) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(INSERT_INTO_SERVICES);
            pstm.setString(1, name);
            pstm.setInt(2, price);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot add service", ex);
            throw new DBException("Cannot add service", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Add client.
     *
     * @param email        the email
     * @param password     the password
     * @param role         the role
     * @param first_name   the first name
     * @param last_name    the last name
     * @param phone_number the phone number
     * @throws DBException the db exception
     */
    public void addClient(String email, String password, String role, String first_name, String last_name, String phone_number) throws DBException {
        Connection conn = null;
        try {
            conn = getConnection();
            addUser(conn, email, password, role, first_name, last_name, phone_number);
            conn.commit();
        } catch (DBException | SQLException ex) {
            rollback(conn);
            LOG.error("Cannot add client", ex);
            throw new DBException("Cannot add client", ex);
        } finally {
            close(conn);
        }
    }

    /**
     * Add worker.
     *
     * @param email        the email
     * @param password     the password
     * @param role         the role
     * @param first_name   the first name
     * @param last_name    the last name
     * @param phone_number the phone number
     * @param services     the services
     * @param workingDays  the working days
     * @throws DBException the db exception
     */
    public void addWorker(String email, String password, String role, String first_name, String last_name, String phone_number,
                          List<String> services, List<String> workingDays) throws DBException {
        Connection conn = null;
        try {
            conn = getConnection();
            int id = addUser(conn, email, password, role, first_name, last_name, phone_number);
            addWorkerServices(conn, id, services);
            addWorkerWorkingDays(conn, id, workingDays);
            conn.commit();
        } catch (DBException | SQLException ex) {
            rollback(conn);
            LOG.error("Cannot add worker", ex);
            throw new DBException("Cannot add worker", ex);
        } finally {
            close(conn);
        }

    }

    private void addWorkerWorkingDays(Connection conn, int id, List<String> workingDays) throws DBException {
        StringBuilder sql = new StringBuilder(INSERT_INTO_WORKERS_WORKING_DAYS);
        for (int i = 0; i < workingDays.size(); ++i) {
            sql.append("(?,?)");
            if (i != workingDays.size() - 1) {
                sql.append(",");
            }
        }
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql.toString());
            int i = 1;
            for (String day : workingDays) {
                pstm.setInt(i++, id);
                pstm.setString(i++, day);
            }
            if (workingDays.size() != 0) {
                pstm.executeUpdate();
            }
        } catch (SQLException ex) {
            LOG.error("Cannot add working days for worker", ex);
            throw new DBException("Cannot add working days for worker", ex);
        } finally {
            close(pstm);
        }

    }

    private void addWorkerServices(Connection conn, int id, List<String> services) throws DBException {
        StringBuilder sql = new StringBuilder(INSERT_INTO_WORKERS_SERVICES);
        for (int i = 0; i < services.size(); ++i) {
            sql.append("(?,?)");
            if (i != services.size() - 1) {
                sql.append(",");
            }
        }
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql.toString());
            int i = 1;
            for (String service : services) {
                pstm.setInt(i++, id);
                pstm.setString(i++, service);
            }
            if (services.size() != 0) {
                pstm.executeUpdate();
            }
        } catch (SQLException ex) {
            LOG.error("Cannot add services for worker", ex);
            throw new DBException("Cannot add services for worker", ex);
        } finally {
            close(pstm);
        }

    }

    private int addUser(Connection conn, String email, String password, String role,
                        String first_name, String last_name, String phone_number) throws DBException {
        PreparedStatement pstm = null;
        PreparedStatement pstm1 = null;
        try {
            pstm = conn.prepareStatement(INSERT_INTO_USERS);
            pstm.setString(1, email);
            pstm.setString(2, password);
            pstm.setString(3, role);
            pstm.setString(4, first_name);
            pstm.setString(5, last_name);
            pstm.setString(6, phone_number);
            pstm.executeUpdate();

            pstm1 = conn.prepareStatement(SELECT_LAST_INSERT_ID);
            try (ResultSet rs = pstm1.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("last_insert_id()");
                }
            }

        } catch (SQLException ex) {
            LOG.error("Cannot add user", ex);
            throw new DBException("Cannot add user", ex);
        } finally {
            close(pstm1);
            close(pstm);
        }
        return 0;
    }

    /**
     * Add appointment.
     *
     * @param workerId the worker id
     * @param clientId the client id
     * @param datetime the datetime
     * @param service  the service
     * @throws DBException the db exception
     */
    public void addAppointment(int workerId, int clientId, String datetime, String service) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(INSERT_INTO_APPOINTMENTS);

            pstm.setInt(1, workerId);
            pstm.setInt(2, clientId);
            pstm.setString(3, datetime);
            pstm.setString(4, service);

            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot add appointment", ex);
            throw new DBException("Cannot add appointment", ex);
        } finally {
            close(pstm);
            close(conn);
        }

    }

    /**
     * Leave feedback.
     *
     * @param id       the id
     * @param rating   the rating
     * @param response the response
     * @throws DBException the db exception
     */
    public void leaveFeedback(int id, int rating, String response) throws DBException {
        Connection conn = null;
        PreparedStatement pstm1 = null;
        PreparedStatement pstm2 = null;

        try {
            Appointment appointment = findAppointment(id);
            conn = getConnection();

            pstm1 = conn.prepareStatement(UPDATE_USER_RATING);
            pstm1.setInt(1, rating);
            pstm1.setInt(2, appointment.getWorker().getId());
            pstm1.executeUpdate();

            pstm2 = conn.prepareStatement(INSERT_INTO_RESPONSES);
            pstm2.setString(1, response);
            pstm2.setInt(2, rating);
            pstm2.setInt(3, appointment.getId());
            pstm2.executeUpdate();

            conn.commit();
        } catch (SQLException | DBException ex) {
            rollback(conn);
            LOG.error("Cannot leave feedback", ex);
            throw new DBException("Cannot leave feedback", ex);
        } finally {
            close(pstm1);
            close(pstm2);
            close(conn);
        }
    }

    private boolean hasResponse(Connection conn, int appointmentId) throws DBException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(SELECT_APPOINTMENT_RESPONSE);
            pstm.setInt(1, appointmentId);
            rs = pstm.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            LOG.error("Cannot find whether appointment has response", ex);
            throw new DBException("Cannot find whether appointment has response", ex);
        } finally {
            close(rs);
            close(pstm);
        }
    }

    /**
     * Find appointment appointment.
     *
     * @param id the id
     * @return the appointment
     * @throws DBException the db exception
     */
    public Appointment findAppointment(int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_APPOINTMENT);

            pstm.setInt(1, id);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setClient(findUser(rs.getInt("client_id")));
                    appointment.setWorker(findUser(rs.getInt("worker_id")));
                    appointment.setId(id);
                    appointment.setTimeslot(rs.getString("timeslot"));
                    appointment.setService(findService(rs.getString("service")));
                    appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                    appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
                    appointment.setHasResponse(hasResponse(conn, id));
                    return appointment;
                }
                return null;
            }

        } catch (SQLException ex) {
            LOG.error("Cannot find appointment", ex);
            throw new DBException("Cannot find appointment", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Find service.
     *
     * @param name the name
     * @return the service
     * @throws DBException the db exception
     */
    public Service findService(String name) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_SERVICE);

            pstm.setString(1, name);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String serviceName = rs.getString("service_name");
                    int price = rs.getInt("Price");
                    return new Service(serviceName, price);
                }
                return new Service();
            }

        } catch (SQLException ex) {
            LOG.error("Cannot find service", ex);
            throw new DBException("Cannot find service", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    private User findAdmin() throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_ADMIN);

            rs = pstm.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getString("role"),
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone_number"), rs.getInt("money_balance"),
                        rs.getInt("rating"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
            }
            return new User();
        } catch (SQLException ex) {
            LOG.error("Cannot find admin", ex);
            throw new DBException("Cannot find admin", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }
    }

    private User findUser(String sql, int id, String email, String password) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(sql);
            switch (sql) {
                case SELECT_USER_BY_EMAIL:
                    pstm.setString(1, email);
                    break;
                case SELECT_USER_BY_EMAIL_AND_PASSWORD:
                    pstm.setString(1, email);
                    pstm.setString(2, password);
                    break;
                case SELECT_USER_BY_ID:
                    pstm.setInt(1, id);
                    break;
            }

            rs = pstm.executeQuery();

            if (rs.next()) {
                User user = new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getString("role"),
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone_number"), rs.getInt("money_balance"),
                        rs.getInt("rating"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());

                queryWorkerServices(conn, user);
                queryWorkerWorkingDays(conn, user);

                return user;
            }
            return new User();
        } catch (SQLException ex) {
            LOG.error("Cannot find user", ex);
            throw new DBException("Cannot find user", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }
    }

    /**
     * Find user.
     *
     * @param email the email
     * @return the user
     * @throws DBException the db exception
     */
    public User findUser(String email) throws DBException {
        return findUser(SELECT_USER_BY_EMAIL, 0, email, null);
    }

    /**
     * Find user.
     *
     * @param email    the email
     * @param password the password
     * @return the user
     * @throws DBException the db exception
     */
    public User findUser(String email, String password) throws DBException {
        return findUser(SELECT_USER_BY_EMAIL_AND_PASSWORD, 0, email, password);
    }

    /**
     * Find user.
     *
     * @param id the id
     * @return the user
     * @throws DBException the db exception
     */
    public User findUser(int id) throws DBException {
        return findUser(SELECT_USER_BY_ID, id, null, null);
    }

    /**
     * Update user balance.
     *
     * @param userId the user id
     * @param amount the amount
     * @throws DBException the db exception
     */
    public void updateUserBalance(int userId, int amount) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_CLIENT_BALANCE);

            pstm.setInt(1, amount);
            pstm.setInt(2, userId);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update balance", ex);
            throw new DBException("Cannot update balance", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Update user balance.
     *
     * @param conn   connection
     * @param userId the user id
     * @param amount the amount
     * @throws DBException the db exception
     */
    public void updateUserBalance(Connection conn, int userId, int amount) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(UPDATE_CLIENT_BALANCE);

            pstm.setInt(1, amount);
            pstm.setInt(2, userId);
            pstm.executeUpdate();

        } catch (SQLException ex) {
            LOG.error("Cannot update balance", ex);
            throw new DBException("Cannot update balance", ex);
        } finally {
            close(pstm);
        }
    }

    /**
     * Update service.
     *
     * @param name  the name
     * @param price the price
     * @throws DBException the db exception
     */
    public void updateService(String name, int price) throws DBException {

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_SERVICE);

            pstm.setInt(1, price);
            pstm.setString(2, name);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update service", ex);
            throw new DBException("Cannot update service", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Update appointment.
     *
     * @param timeslot the timeslot
     * @param id       the id
     * @throws DBException the db exception
     */
    public void updateAppointment(String timeslot, int id) throws DBException {

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_APPOINTMENT_TIMESLOT);

            pstm.setString(1, timeslot);
            pstm.setInt(2, id);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update appointment", ex);
            throw new DBException("Cannot update appointment", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Update appointment status paid.
     *
     * @param id the id
     * @throws DBException the db exception
     */
    public void updateAppointmentStatusPaid(int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_APPOINTMENT_STATUS_PAID);
            pstm.setInt(1, id);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update appointment status to paid", ex);
            throw new DBException("Cannot update appointment status to paid", ex);
        } finally {
            close(pstm);
            close(conn);
        }

    }

    /**
     * Update appointment status done.
     *
     * @param id the id
     * @throws DBException the db exception
     */
    public void updateAppointmentStatusDone(int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_APPOINTMENT_STATUS_DONE);
            pstm.setInt(1, id);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update Appointment status to done", ex);
            throw new DBException("Cannot update Appointment status to done", ex);
        } finally {
            close(pstm);
            close(conn);
        }

    }

    /**
     * Update worker.
     *
     * @param id          the id
     * @param email       the email
     * @param phoneNumber the phone number
     * @param services    the services
     * @param workingDays the working days
     * @throws DBException the db exception
     */
    public void updateWorker(int id, String email, String phoneNumber, List<String> services, List<String> workingDays) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_USER);
            pstm.setString(1, phoneNumber);
            pstm.setString(2, email);
            pstm.setInt(3, id);
            pstm.executeUpdate();

            deleteFromWorkersServices(conn, id);
            deleteFromWorkersWorkingDays(conn, id);
            addWorkerServices(conn, id, services);
            addWorkerWorkingDays(conn, id, workingDays);
            conn.commit();
        } catch (DBException | SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update worker");
            throw new DBException("Cannot update worker", ex);
        } finally {
            close(pstm);
            close(conn);
        }

    }

    /**
     * Pay for appointment.
     *
     * @param id the id
     * @throws DBException the db exception
     */
    public void payForAppointment(int id) throws DBException {
        Appointment appointment = findAppointment(id);
        int price = appointment.getService().getPrice();
        int balance = appointment.getClient().getMoneyBalance();
        Connection conn = null;
        if (appointment.getStatus().startsWith("Paid")) {
            return;
        }
        if (price > balance) {
            LOG.error("There is not enough money on balance");
            throw new DBException("There is not enough money on balance");
        } else {
            try {
                conn = getConnection();
                updateUserBalance(conn, appointment.getClient().getId(), -price);
                updateUserBalance(conn, findAdmin().getId(), price);
                conn.commit();
            } catch (DBException | SQLException ex) {
                rollback(conn);
                LOG.error("Payment rejected");
                throw new DBException("Payment rejected");
            } finally {
                close(conn);
            }
        }
    }

    private void deleteFromWorkersServices(Connection conn, int id) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(DELETE_FROM_WORKERS_SERVICES);
            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot delete worker's service", ex);
            throw new DBException("Cannot delete worker's service", ex);
        } finally {
            close(pstm);
        }
    }

    private void deleteFromWorkersWorkingDays(Connection conn, int id) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(DELETE_FROM_WORKERS_WORKING_DAYS);
            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot delete worker's working day", ex);
            throw new DBException("Cannot delete worker's working day", ex);
        } finally {
            close(pstm);
        }
    }

    /**
     * Delete service.
     *
     * @param name the name
     * @throws DBException the db exception
     */
    public void deleteService(String name) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(DELETE_SERVICE);
            pstm.setString(1, name);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot delete service", ex);
            throw new DBException("Cannot delete service", ex);
        } finally {
            close(pstm);
            close(conn);
        }

    }

    /**
     * Delete worker.
     *
     * @param id the id
     * @throws DBException the db exception
     */
    public void deleteWorker(int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(DELETE_USER);
            pstm.setInt(1, id);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            LOG.error("Cannot delete worker", ex);
            rollback(conn);
            throw new DBException("Cannot delete worker", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Delete appointment.
     *
     * @param id the id
     * @throws DBException the db exception
     */
    public void deleteAppointment(int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(DELETE_APPOINTMENT);
            pstm.setInt(1, id);
            pstm.executeUpdate();
            conn.commit();
        } catch (SQLException ex) {
            rollback(conn);
            LOG.error("Cannot delete appointment", ex);
            throw new DBException("Cannot delete appointment", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    /**
     * Query service list.
     *
     * @return List of all services which are available in beauty salon now
     * @throws DBException the db exception
     */
    public List<Service> queryService() throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_ALL_SERVICES);

            rs = pstm.executeQuery();
            List<Service> list = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("service_name");
                int price = rs.getInt("Price");
                Service service = new Service();
                service.setName(name);
                service.setPrice(price);
                list.add(service);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get all services", ex);
            throw new DBException("Cannot get all services", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }

    }

    /**
     * Query worker list.
     *
     * @param sorting the sorting
     * @param filter  the filter
     * @param date    the date
     * @return List of workers who are registered in beauty salon now ordered by second parameter
     * @throws DBException the db exception
     */
    public List<User> queryWorker(String sorting, List<String> filter, String date) throws DBException {
        String sql = SELECT_ALL_WORKERS_ORDERED_BY;
        sql += sorting;
        if (sorting.equals("rating")) sql += " DESC";

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();

            List<User> list = new ArrayList<>();
            while (rs.next()) {
                User worker = new User();
                worker.setId(rs.getInt("id"));
                worker.setFirstName(rs.getString("first_name"));
                worker.setLastName(rs.getString("last_name"));
                worker.setEmail(rs.getString("email"));
                worker.setPhoneNumber(rs.getString("phone_number"));
                worker.setPassword(rs.getString("password"));
                worker.setRating(rs.getInt("rating"));
                worker.setServices(new ArrayList<>());
                worker.setWorkingDays(new ArrayList<>());

                queryWorkerServices(conn, worker);
                queryWorkerWorkingDays(conn, worker);

                if (new HashSet<>(worker.getServices().stream().map(Service::getName).collect(Collectors.toList())).containsAll(filter))
                    if (date == null || worker.getWorkingDays().stream().anyMatch(LocalDate.parse(date).getDayOfWeek().name()::equalsIgnoreCase))
                        list.add(worker);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get all workers", ex);
            throw new DBException("Cannot get all workers", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }
    }

    private void queryWorkerServices(Connection conn, User worker) throws DBException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(SELECT_WORKER_SERVICES);
            pstm.setInt(1, worker.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                String name = rs.getString("service");
                worker.getServices().add(new Service(name, findService(name).getPrice()));
            }
        } catch (SQLException ex) {
            LOG.error("Cannot get all worker's services", ex);
            throw new DBException("Cannot get all worker's services", ex);
        } finally {
            close(rs);
            close(pstm);
        }
    }

    private void queryWorkerWorkingDays(Connection conn, User worker) throws DBException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement(SELECT_WORKER_WORKING_DAYS);
            pstm.setInt(1, worker.getId());
            rs = pstm.executeQuery();
            while (rs.next()) {
                worker.getWorkingDays().add(rs.getString("working_day"));
            }
        } catch (SQLException ex) {
            LOG.error("Cannot get all worker's working days", ex);
            throw new DBException("Cannot get all worker's working days", ex);
        } finally {
            close(rs);
            close(pstm);
        }
    }

    /**
     * Query available slots list.
     *
     * @param workerId the worker id
     * @param date     the date
     * @return the list
     * @throws DBException the db exception
     */
    public List<String> queryAvailableSlots(int workerId, String date) throws DBException {
        List<String> allSlots = new ArrayList<>();
        List<Appointment> workerAppointments = queryWorkerAppointments(workerId);
        List<String> occupiedSlots = new ArrayList<>();
        for (Appointment appointment : workerAppointments) {
            if (appointment.getTimeslot().startsWith(date)) {
                occupiedSlots.add(appointment.getTimeslot().split(" ")[1]);
            }
        }
        for (int i = 8; i < 20; ++i) {
            if (i == 13) {
                continue;
            }
            if (!occupiedSlots.contains(i + ":00")) {
                allSlots.add(i + ":00");
            }
            if (!occupiedSlots.contains(i + ":30")) {
                allSlots.add(i + ":30");
            }
        }
        return allSlots;
    }

    /**
     * Query client appointments list.
     *
     * @param clientId the client id
     * @return the list
     * @throws DBException the db exception
     */
    public List<Appointment> queryClientAppointments(int clientId) throws DBException {
        return queryAppointments(SELECT_CLIENT_APPOINTMENTS, clientId);
    }

    /**
     * Query worker appointments list.
     *
     * @param workerId the worker id
     * @return the list
     * @throws DBException the db exception
     */
    public List<Appointment> queryWorkerAppointments(int workerId) throws DBException {
        return queryAppointments(SELECT_WORKER_APPOINTMENTS, workerId);
    }

    /**
     * Query all appointments list.
     *
     * @return the list
     * @throws DBException the db exception
     */
    public List<Appointment> queryAllAppointments() throws DBException {
        return queryAppointments(SELECT_ALL_APPOINTMENTS, 0);
    }

    /**
     * Query past appointments list.
     *
     * @return the list
     * @throws DBException the db exception
     */
    public List<Appointment> queryPastAppointments() throws DBException {
        return queryAppointments(SELECT_ALL_PAST_APPOINTMENTS, 0);
    }

    private List<Appointment> queryAppointments(String sql, int id) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(sql);
            pstm.setString(1, LocalDate.now().toString());
            if (sql.equals(SELECT_WORKER_APPOINTMENTS) || sql.equals(SELECT_CLIENT_APPOINTMENTS)) {
                pstm.setInt(2, id);
            }
            rs = pstm.executeQuery();
            List<Appointment> list = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setWorker(findUser(rs.getInt("worker_id")));
                appointment.setClient(findUser(rs.getInt("client_id")));
                appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
                appointment.setTimeslot(rs.getString("timeslot"));
                appointment.setService(findService(rs.getString("service")));
                list.add(appointment);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get appointments", ex);
            throw new DBException("Cannot get appointments", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }

    }

    /**
     * Query worker responses list.
     *
     * @param workerId the worker id
     * @return the list
     * @throws DBException the db exception
     */
    public List<Response> queryWorkerResponses(int workerId) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_WORKER_RESPONSES);
            pstm.setInt(1, workerId);
            rs = pstm.executeQuery();
            List<Response> list = new ArrayList<>();
            while (rs.next()) {
                Response response = new Response();
                response.setId(rs.getInt("appointments.id"));
                response.setRating(rs.getInt("rating"));
                response.setWorker(findUser(rs.getInt("worker_id")));
                response.setClient(findUser(rs.getInt("client_id")));
                response.setDate(rs.getString("timeslot"));
                response.setService(findService(rs.getString("service")));
                response.setMessage(rs.getString("message"));
                list.add(response);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get all worker's appointments", ex);
            throw new DBException("Cannot get all worker's appointments", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }

    }

}
