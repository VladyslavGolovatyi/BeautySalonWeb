package utils;

import entity.User;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;

public class MyUtils {

    public static User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    public static void storeLoggedInUser(HttpSession session, User loggedInUser) {
        session.setAttribute("loggedInUser", loggedInUser);
    }

    public static void deleteLoggedInUser(HttpSession session) {
        session.setAttribute("loggedInUser", null);
    }

}
