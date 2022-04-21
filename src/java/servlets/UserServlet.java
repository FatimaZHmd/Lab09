/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import dataaccess.RoleDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.*;
import services.*;

/**
 *
 * @author Sean
 */
@WebServlet(name = "UserServlet", urlPatterns = {"/UserServlet"})
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserService us = new UserService();
        String action = request.getParameter("action");

        if (action == null) {//show all users
            session.setAttribute("editMode", false);
            try {
                List<User> userList = us.getAll();
                session.setAttribute("Role", new Role());
                session.setAttribute("userList", userList);
            } catch (Exception ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (action.equals("edit")) {
            session.setAttribute("editMode", true);
            String userPrimaryKey = request.getParameter("userPrimaryKey");
            User selectedUser = null;

            try {
                selectedUser = us.get(userPrimaryKey);
            } catch (Exception ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            request.setAttribute("selectedUser", selectedUser);
        }

        getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        return;

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserService us = new UserService();
        RoleService rs = new RoleService();

        String action = request.getParameter("action");

        if (action.equals("add")) {
            String email = request.getParameter("email");
            String active_string = request.getParameter("active");
            boolean active = true;
            if (active_string == null) {
                active = false;
            }
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String password = request.getParameter("password");
            int role = Integer.parseInt(request.getParameter("role"));
            
            User user = new User(email, active, firstName, lastName, password);
            user.setRole(new Role(role));

            if (AllFieldsFilled(email, active, firstName, lastName, password, role)) {
                try {
                    us.insert(email, active, firstName, lastName, password, role);
                } catch (Exception ex) {
                    Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                request.setAttribute("user", user);
                request.setAttribute("errorMessage_EddUser", true);
                getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
                return;
            }
        } else if (action.equals("edit")) {
            session.setAttribute("editMode", false);
            String update_email = request.getParameter("update_email");
            String update_firstName = request.getParameter("update_firstName");
            String update_lastName = request.getParameter("update_lastName");
            String update_password = request.getParameter("update_password");
            int update_role = Integer.parseInt(request.getParameter("update_role"));
            String update_active_string = request.getParameter("update_active");
            boolean update_active = true;
            if (update_active_string == null) {
                update_active = false;
            }
            User update_user = new User(update_email, update_active, update_firstName, update_lastName, update_password);
            update_user.setRole(new Role(update_role));
            
            if (AllFieldsFilled(update_email, update_active, update_firstName, update_lastName, update_password, update_role)) {
                try {
                    us.update(update_email, update_active, update_firstName, update_lastName, update_password, update_role);
                } catch (Exception ex) {
                    Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
                response.sendRedirect("users");
                return;
            } else {
                request.setAttribute("selectedUser", update_user);
                request.setAttribute("errorMessage_EditUser", true);
                getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
                return;
            }

        } else if (action.equals("delete")) {
            String userPrimaryKey = request.getParameter("userPrimaryKey");
            try {
                us.delete(userPrimaryKey);

                List<User> userList = us.getAll();
                RoleDB roleDB = new RoleDB();
                session.setAttribute("roles", roleDB);
                session.setAttribute("userList", userList);

            } catch (Exception ex) {
                Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
            return;
        } else if (action.equals("cancel")) {
            session.setAttribute("editMode", false);
            response.sendRedirect("users");
            return;
        }

        try {
            List<User> userList = us.getAll();
            session.setAttribute("userList", userList);
        } catch (Exception ex) {
            Logger.getLogger(UserServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        getServletContext().getRequestDispatcher("/WEB-INF/users.jsp").forward(request, response);
        return;

    }

    private boolean AllFieldsFilled(String email, boolean active, String firstName, String lastName, String password, int role) {
        if (email == null || email.equals("") || firstName == null || firstName.equals("")
                || lastName == null || lastName.equals("")
                || password == null || password.equals("")) {
            return false;
        }
        return true;
    }
}
