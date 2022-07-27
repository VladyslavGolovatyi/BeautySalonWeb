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
public class ServicesDAOTest {

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
    public void testAddServiceWithNoExceptions() throws SQLException, DBException {
        myDao.addService("",100);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testAddServiceWithPreparedStmtException() throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException());
        assertEquals("Cannot add service", assertThrows(DBException.class,
                () -> myDao.addService("", 100)).getMessage());
    }

    @Test
    public void testFindService() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.findService("");
        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).executeQuery();
        verify(mockPreparedStmt, times(1)).close();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void testUpdateService() throws SQLException, DBException {
        myDao.updateService("",100);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testDeleteService() throws DBException, SQLException {
        myDao.deleteService("");

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testQueryService() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.queryService();
        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).executeQuery();
        verify(mockPreparedStmt, times(1)).close();
        verify(mockResultSet, times(2)).next();
        verify(mockResultSet, times(1)).close();
    }

}