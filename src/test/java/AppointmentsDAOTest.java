import exception.DBException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.core.classloader.annotations.PrepareForTest;
import utils.DBManager;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@PrepareForTest(DBManager.class)
public class AppointmentsDAOTest {

    @Mock
    private DataSource mockDataSource;
    @Mock
    private Connection mockConn;
    @Mock
    private PreparedStatement mockPreparedStmt;
    @Mock
    private ResultSet mockResultSet;
    private DBManager myDao;

    @BeforeEach
    public void setUp() throws Exception {
        myDao = DBManager.getInstance(mockDataSource);
        when(mockDataSource.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenReturn(mockPreparedStmt);
    }

    @AfterEach
    public void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = DBManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testAddAppointmentWithNoExceptions() throws SQLException, DBException {
        myDao.addAppointment(1,2,"","");

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(2)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(2)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testAddServiceWithPreparedStmtException() throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException());
        assertEquals("Cannot add appointment", assertThrows(DBException.class,
                () -> myDao.addAppointment(1,2,"","")).getMessage());
    }

    @Test
    public void testFindAppointment() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.findAppointment(1);
        verify(mockConn, times(5)).prepareStatement(anyString());
        verify(mockConn, times(4)).close();
        verify(mockPreparedStmt, times(5)).executeQuery();
        verify(mockPreparedStmt, times(5)).close();
        verify(mockResultSet, times(5)).next();
        verify(mockResultSet, times(5)).close();
    }

    @Test
    public void testUpdateAppointment() throws SQLException, DBException {
        myDao.updateAppointment("",1);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testUpdateAppointmentStatusPaid() throws SQLException, DBException {
        myDao.updateAppointmentStatusPaid(1);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testUpdateAppointmentStatusDone() throws SQLException, DBException {
        myDao.updateAppointmentStatusDone(1);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testDeleteAppointment() throws DBException, SQLException {
        myDao.deleteAppointment(1);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testQueryAppointment() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.queryAllAppointments();
        verify(mockConn, times(4)).prepareStatement(anyString());
        verify(mockConn, times(4)).close();
        verify(mockPreparedStmt, times(4)).executeQuery();
        verify(mockPreparedStmt, times(4)).close();
        verify(mockResultSet, times(5)).next();
        verify(mockResultSet, times(4)).close();
    }

    @Test
    public void testQueryAvailableSlots() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.queryAvailableSlots(1,"2022-08-01 8:00");
        verify(mockConn, times(4)).prepareStatement(anyString());
        verify(mockConn, times(4)).close();
        verify(mockPreparedStmt, times(4)).executeQuery();
        verify(mockPreparedStmt, times(4)).close();
        verify(mockResultSet, times(5)).next();
        verify(mockResultSet, times(4)).close();
    }

    @Test
    public void testPayForAppointment() throws DBException, SQLException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");
        myDao.payForAppointment(1);

        verify(mockConn, times(8)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(6)).close();
        verify(mockPreparedStmt, times(8)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(2)).executeUpdate();
        verify(mockPreparedStmt, times(8)).close();
    }

}