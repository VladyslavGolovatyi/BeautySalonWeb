package utils;

import entity.Appointment;
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
import java.util.*;
import java.util.stream.Collectors;

public class DBManager {

    private final static String INSERT_INTO_SERVICES = "INSERT INTO Services(name,price) VALUES (?,?)";
    private final static String INSERT_INTO_WORKERS_SERVICES = "INSERT INTO workers_services VALUES ";
    private final static String INSERT_INTO_WORKERS_WORKING_DAYS = "INSERT INTO workers_workingDays VALUES ";
    private final static String INSERT_INTO_USERS = "INSERT INTO users VALUES(?,?,?,?,?,?,0)";
    private final static String INSERT_INTO_APPOINTMENTS = "INSERT INTO appointments(worker_email, client_email, is_paid,is_done, timeslot, service) VALUES (?,?,false,false,?,?)";
    private final static String SELECT_APPOINTMENT = "SELECT worker_email, client_email, is_paid, is_done, service, timeslot FROM appointments WHERE id=?";
    private final static String SELECT_CLIENT_APPOINTMENTS = "SELECT worker_email, is_paid, is_done, timeslot, service, id FROM appointments a where a.client_email=? order by timeslot";
    private final static String SELECT_WORKER_APPOINTMENTS = "SELECT client_email, is_paid, is_done, timeslot, service, id FROM appointments a where a.worker_email=? order by timeslot";
    private final static String SELECT_ALL_APPOINTMENTS = "SELECT * FROM appointments order by timeslot";
    private final static String SELECT_SERVICE = "SELECT a.Name, a.Price FROM Services a WHERE a.Name=?";
    private final static String SELECT_ALL_SERVICES = "SELECT a.Name, a.Price FROM Services a ";
    private final static String SELECT_USER = "SELECT a.password, a.role, first_name, last_name, money_balance, phone_number, rating FROM users a WHERE a.email = ?";
    private final static String SELECT_ADMIN = "SELECT a.email, a.password, a.role, first_name, last_name, money_balance, phone_number, rating FROM users a WHERE a.role = 'admin'";
    private final static String SELECT_ALL_WORKERS_ORDERED_BY = "SELECT a.first_name, a.last_name, a.email, a.phone_number, a.password, a.rating FROM users a WHERE a.role = 'worker' ORDER BY ";
    private final static String SELECT_WORKER_SERVICES = "SELECT a.service FROM workers_services a WHERE worker_email = ?";
    private final static String SELECT_WORKER_WORKING_DAYS = "SELECT a.working_day FROM workers_workingdays a WHERE worker_email = ?";
    private final static String UPDATE_SERVICE = "UPDATE services SET price=? WHERE name=? ";
    private final static String UPDATE_USER_PHONE_NUMBER = "UPDATE users SET phone_number=? WHERE email=?";
    private final static String UPDATE_CLIENT_BALANCE = "UPDATE users SET money_balance=money_balance+? where email = ?";
    private final static String UPDATE_APPOINTMENT_STATUS_PAID = "UPDATE appointments SET is_paid=true where id=?";
    private final static String UPDATE_APPOINTMENT_STATUS_DONE = "UPDATE appointments SET is_done=true where id=?";
    private final static String UPDATE_APPOINTMENT_TIMESLOT = "UPDATE appointments SET timeslot=? where id=?";
    private final static String DELETE_FROM_WORKERS_SERVICES = "DELETE FROM workers_services WHERE worker_email = ?";
    private final static String DELETE_FROM_WORKERS_WORKING_DAYS = "DELETE FROM workers_workingdays WHERE worker_email = ?";
    private final static String DELETE_SERVICE = "DELETE FROM Services WHERE Name= ?";
    private final static String DELETE_USER = "DELETE FROM users WHERE email= ?";
    private final static String DELETE_APPOINTMENT = "DELETE FROM appointments WHERE id= ?";

    private static DBManager instance;
    private final DataSource ds;
    private static final Logger LOG = LogManager.getLogger(DBManager.class);

    private DBManager() throws DBException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            ds = (DataSource) envContext.lookup("jdbc/beautysalondb");
            LOG.info("Data source ==> " + ds);
        } catch (NamingException ex) {
            LOG.error("Cannot obtain the data source", ex);
            throw new DBException("Cannot obtain the data source", ex);
        }
    }

    public static synchronized DBManager getInstance() throws DBException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
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


    public void addService(Service service) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(INSERT_INTO_SERVICES);
            pstm.setString(1, service.getName());
            pstm.setFloat(2, service.getPrice());
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

    public void addWorker(String email, String password, String role, String first_name, String last_name, String phone_number, String[] services, String[] workingDays) throws DBException {
        Connection conn = null;
        try {
            conn = getConnection();
            addUser(conn, email, password, role, first_name, last_name, phone_number);
            addWorkerServices(conn, email, services);
            addWorkerWorkingDays(conn, email, workingDays);
            conn.commit();
        } catch (DBException | SQLException ex) {
            rollback(conn);
            LOG.error("Cannot add worker", ex);
            throw new DBException("Cannot add worker", ex);
        } finally {
            close(conn);
        }

    }

    private void addWorkerWorkingDays(Connection conn, String email, String[] workingDays) throws DBException {
        StringBuilder sql = new StringBuilder(INSERT_INTO_WORKERS_WORKING_DAYS);
        for (int i = 0; i < workingDays.length; ++i) {
            sql.append("(?,?)");
            if (i != workingDays.length - 1) {
                sql.append(",");
            }
        }
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql.toString());
            int i = 1;
            for (String day : workingDays) {
                pstm.setString(i++, email);
                pstm.setString(i++, day);
            }
            if (workingDays.length != 0) {
                pstm.executeUpdate();
            }
        } catch (SQLException ex) {
            LOG.error("Cannot add working days for worker", ex);
            throw new DBException("Cannot add working days for worker", ex);
        } finally {
            close(pstm);
        }

    }

    private void addWorkerServices(Connection conn, String email, String[] services) throws DBException {
        StringBuilder sql = new StringBuilder(INSERT_INTO_WORKERS_SERVICES);
        for (int i = 0; i < services.length; ++i) {
            sql.append("(?,?)");
            if (i != services.length - 1) {
                sql.append(",");
            }
        }
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(sql.toString());
            int i = 1;
            for (String service : services) {
                pstm.setString(i++, email);
                pstm.setString(i++, service);
            }
            if (services.length != 0) {
                pstm.executeUpdate();
            }
        } catch (SQLException ex) {
            LOG.error("Cannot add services for worker", ex);
            throw new DBException("Cannot add services for worker", ex);
        } finally {
            close(pstm);
        }


    }

    private void addUser(Connection conn, String email, String password, String role, String first_name, String last_name, String phone_number) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(INSERT_INTO_USERS);
            pstm.setString(1, email);
            pstm.setString(2, password);
            pstm.setString(3, role);
            pstm.setString(4, first_name);
            pstm.setString(5, last_name);
            pstm.setString(6, phone_number);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot add user", ex);
            throw new DBException("Cannot add user", ex);
        } finally {
            close(pstm);
        }
    }

    public void addAppointment(String workerEmail, String clientEmail, String datetime, String service) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(INSERT_INTO_APPOINTMENTS);

            pstm.setString(1, workerEmail);
            pstm.setString(2, clientEmail);
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
                    appointment.setClient(findUser(rs.getString("client_email")));
                    appointment.setWorker(findUser(rs.getString("worker_email")));
                    appointment.setId(id);
                    appointment.setTimeslot(rs.getString("timeslot"));
                    appointment.setService(findService(rs.getString("service")));
                    appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                    appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
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

    public Service findService(String name) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_SERVICE);

            pstm.setString(1, name);

            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String name1 = rs.getString("Name");
                    int price = rs.getInt("Price");
                    return new Service(name1, price);
                }
                return null;
            }

        } catch (SQLException ex) {
            LOG.error("Cannot find service", ex);
            throw new DBException("Cannot find service", ex);
        } finally {
            close(pstm);
            close(conn);
        }
    }

    private User findAdmin() throws DBException{
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_ADMIN);

            rs = pstm.executeQuery();

            if (rs.next()) {

                return new User(rs.getString("email"), rs.getString("password"), rs.getString("role"),
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone_number"),rs.getInt("money_balance"),
                        rs.getInt("rating"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());
            }
            return null;
        } catch (SQLException ex) {
            LOG.error("Cannot find admin", ex);
            throw new DBException("Cannot find admin", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }
    }

    public User findUser(String email) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_USER);
            pstm.setString(1, email);

            rs = pstm.executeQuery();

            if (rs.next()) {
                User user = new User(email, rs.getString("password"), rs.getString("role"),
                        rs.getString("first_name"), rs.getString("last_name"), rs.getString("phone_number"),rs.getInt("money_balance"),
                        rs.getInt("rating"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new HashMap<>());

                queryWorkerServices(conn, user);
                queryWorkerWorkingDays(conn, user);

                return user;
            }
            return null;
        } catch (SQLException ex) {
            LOG.error("Cannot find user", ex);
            throw new DBException("Cannot find user", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }
    }

    public void updateUserBalance(String userEmail, int amount) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_CLIENT_BALANCE);

            pstm.setInt(1,amount);
            pstm.setString(2,userEmail);
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

    private void updateUserBalance(Connection conn, String userEmail, int amount) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(UPDATE_CLIENT_BALANCE);

            pstm.setInt(1,amount);
            pstm.setString(2,userEmail);
            pstm.executeUpdate();

        } catch (SQLException ex) {
            LOG.error("Cannot update balance", ex);
            throw new DBException("Cannot update balance", ex);
        } finally {
            close(pstm);
        }
    }

    public void updateService(Service service) throws DBException {

        Connection conn = null;
        PreparedStatement pstm = null;

        try {
            conn = getConnection();
            pstm = conn.prepareStatement(UPDATE_SERVICE);

            pstm.setInt(1, service.getPrice());
            pstm.setString(2, service.getName());
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

    private void updateUserPhoneNumber(Connection conn, String phoneNumber, String email) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(UPDATE_USER_PHONE_NUMBER);
            pstm.setString(1, phoneNumber);
            pstm.setString(2, email);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot update user phone number", ex);
            throw new DBException("Cannot update user phone number", ex);
        } finally {
            close(pstm);
        }
    }

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

    public void updateWorker(String email, String phoneNumber, String[] services, String[] workingDays) throws DBException {
        Connection conn = null;
        try {
            conn = getConnection();
            updateUserPhoneNumber(conn, phoneNumber, email);
            deleteFromWorkersServices(conn, email);
            deleteFromWorkersWorkingDays(conn, email);
            addWorkerServices(conn, email, services);
            addWorkerWorkingDays(conn, email, workingDays);
            conn.commit();
        } catch (DBException | SQLException ex) {
            rollback(conn);
            LOG.error("Cannot update worker");
            throw new DBException("Cannot update worker",ex);
        } finally {
            close(conn);
        }

    }

    public void payForAppointment(int id) throws DBException {
        Appointment appointment = findAppointment(id);
        int price = appointment.getService().getPrice();
        int balance = appointment.getClient().getMoneyBalance();
        Connection conn = null;
        if (appointment.getStatus().startsWith("Paid")) {
            return;
        }
        if(price > balance) {
            LOG.error("There is not enough money on balance");
            throw new DBException("There is not enough money on balance");
        } else {
            try {
                conn = getConnection();
                updateUserBalance(conn, appointment.getClient().getEmail(), -price);
                updateUserBalance(conn, findAdmin().getEmail(), price);
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

    private void deleteFromWorkersServices(Connection conn, String email) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(DELETE_FROM_WORKERS_SERVICES);
            pstm.setString(1, email);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot delete worker's service", ex);
            throw new DBException("Cannot delete worker's service", ex);
        } finally {
            close(pstm);
        }
    }

    private void deleteFromWorkersWorkingDays(Connection conn, String email) throws DBException {
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement(DELETE_FROM_WORKERS_WORKING_DAYS);
            pstm.setString(1, email);
            pstm.executeUpdate();
        } catch (SQLException ex) {
            LOG.error("Cannot delete worker's working day", ex);
            throw new DBException("Cannot delete worker's working day", ex);
        } finally {
            close(pstm);
        }
    }

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

    public void deleteWorker(String email) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(DELETE_USER);
            pstm.setString(1, email);
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
     * @return List of all services which are available in beauty salon now
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
                String name = rs.getString("Name");
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
     * @return List of workers who are registered in beauty salon now ordered by second parameter
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
                worker.setFirst_name(rs.getString("first_name"));
                worker.setLast_name(rs.getString("last_name"));
                worker.setEmail(rs.getString("email"));
                worker.setPhone_number(rs.getString("phone_number"));
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
            pstm.setString(1, worker.getEmail());
            rs = pstm.executeQuery();
            while (rs.next()) {
                String name = rs.getString("service");
                worker.getServices().add(new Service(name,findService(name).getPrice()));
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
            pstm.setString(1, worker.getEmail());
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

    public List<String> queryAvailableSlots(String workerEmail, String date) throws DBException {
        List<String> allSlots = new ArrayList<>();
        List<Appointment> workerAppointments = queryWorkerAppointments(workerEmail);
        List<String> occupiedSlots = new ArrayList<>();
        for (Appointment appointment:workerAppointments) {
            if(appointment.getTimeslot().startsWith(date)) {
                occupiedSlots.add(appointment.getTimeslot().split(" ")[1]);
            }
        }
        for (int i = 10; i < 22; ++i) {
            if(!occupiedSlots.contains(i + ":00")) {
                allSlots.add(i + ":00");
            }
            if(!occupiedSlots.contains(i + ":30")) {
                allSlots.add(i + ":30");
            }
        }
        return allSlots;
    }

    public List<Appointment> queryClientAppointments(String clientEmail) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_CLIENT_APPOINTMENTS);
            pstm.setString(1, clientEmail);
            rs = pstm.executeQuery();
            List<Appointment> list = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setWorker(findUser(rs.getString("worker_email")));
                appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
                appointment.setTimeslot(rs.getString("timeslot"));
                appointment.setService(findService(rs.getString("service")));
                list.add(appointment);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get all client's appointments", ex);
            throw new DBException("Cannot get all client's appointments", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }

    }

    public List<Appointment> queryWorkerAppointments(String workerEmail) throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_WORKER_APPOINTMENTS);
            pstm.setString(1, workerEmail);
            rs = pstm.executeQuery();
            List<Appointment> list = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setClient(findUser(rs.getString("client_email")));
                appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
                appointment.setTimeslot(rs.getString("timeslot"));
                appointment.setService(findService(rs.getString("service")));
                list.add(appointment);
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

    public List<Appointment> queryAllAppointments() throws DBException {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstm = conn.prepareStatement(SELECT_ALL_APPOINTMENTS);
            rs = pstm.executeQuery();
            List<Appointment> list = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setWorker(findUser(rs.getString("worker_email")));
                appointment.setClient(findUser(rs.getString("client_email")));
                appointment.setStatus(rs.getBoolean("is_paid") ? "Paid" : "Unpaid");
                appointment.setStatus(appointment.getStatus() + (rs.getBoolean("is_done") ? ",done" : ",not done"));
                appointment.setTimeslot(rs.getString("timeslot"));
                appointment.setService(findService(rs.getString("service")));
                list.add(appointment);
            }
            return list;
        } catch (SQLException ex) {
            LOG.error("Cannot get all appointments", ex);
            throw new DBException("Cannot get all appointments", ex);
        } finally {
            close(rs);
            close(pstm);
            close(conn);
        }

    }

}
