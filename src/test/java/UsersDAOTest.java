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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@PrepareForTest(DBManager.class)
public class UsersDAOTest {

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
    public void testAddClientWithNoExceptions() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt("last_insert_id()")).thenReturn(1);
        myDao.addClient("", "", "", "", "", "");

        verify(mockConn, times(2)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(6)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).executeQuery();
        verify(mockPreparedStmt, times(2)).close();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt("last_insert_id()");
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void testAddWorkerWithNoExceptions() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt("last_insert_id()")).thenReturn(1);
        myDao.addWorker("", "", "", "", "", "", Collections.singletonList(""), Collections.singletonList(""));

        verify(mockConn, times(4)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(8)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(2)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(3)).executeUpdate();
        verify(mockPreparedStmt, times(1)).executeQuery();
        verify(mockPreparedStmt, times(4)).close();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt("last_insert_id()");
        verify(mockResultSet, times(1)).close();
    }

    @Test
    public void testAddClientWithPreparedStmtException() throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException());
        assertEquals("Cannot add client", assertThrows(DBException.class,
                () -> myDao.addClient("", "", "", "", "", "")).getMessage());
    }

    @Test
    public void testAddWorkerWithPreparedStmtException() throws SQLException {
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException());
        assertEquals("Cannot add worker", assertThrows(DBException.class,
                () -> myDao.addWorker("", "", "", "", "", "",new ArrayList<>(),new ArrayList<>())).getMessage());
    }

    @Test
    public void testFindUser() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.findUser(1);
        verify(mockConn, times(3)).prepareStatement(anyString());
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(3)).executeQuery();
        verify(mockPreparedStmt, times(3)).close();
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(3)).close();
    }

    @Test
    public void testUpdateUserBalance() throws SQLException, DBException {
        myDao.updateUserBalance(1,100);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testUpdateWorker() throws SQLException, DBException {
        myDao.updateWorker(1,"",  "", Collections.singletonList(""), Collections.singletonList(""));

        verify(mockConn, times(5)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(4)).setString(anyInt(), anyString());
        verify(mockPreparedStmt, times(5)).executeUpdate();
        verify(mockPreparedStmt, times(5)).close();
    }

    @Test
    public void testDeleteWorker() throws DBException, SQLException {
        myDao.deleteWorker(1);

        verify(mockConn, times(1)).prepareStatement(anyString());
        verify(mockConn, times(1)).commit();
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(1)).setInt(anyInt(), anyInt());
        verify(mockPreparedStmt, times(1)).executeUpdate();
        verify(mockPreparedStmt, times(1)).close();
    }

    @Test
    public void testQueryWorker() throws SQLException, DBException {
        when(mockPreparedStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(mockResultSet.getInt(anyString())).thenReturn(1);
        when(mockResultSet.getString(anyString())).thenReturn("");

        myDao.queryWorker("rating",new ArrayList<>(),null);
        verify(mockConn, times(3)).prepareStatement(anyString());
        verify(mockConn, times(1)).close();
        verify(mockPreparedStmt, times(3)).executeQuery();
        verify(mockPreparedStmt, times(3)).close();
        verify(mockResultSet, times(4)).next();
        verify(mockResultSet, times(3)).close();
    }

}