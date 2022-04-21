
package services;

import dataaccess.RoleDB;
import java.sql.SQLException;
import java.util.List;
import models.Role;


public class RoleService {
    
    public List<Role> getAll() throws Exception {
        RoleDB roleDB = new RoleDB();
        List<Role> roles = roleDB.getAll();
        return roles;
    }
    
        public int getRoleID(String role_name) throws SQLException {
        RoleDB roleDB = new RoleDB();
        int role_id = roleDB.getRoleID(role_name);
        return role_id;
    }

    public String getRoleName(int role_id) throws SQLException {
        RoleDB roleDB = new RoleDB();
        String role_name = roleDB.getRoleName(role_id);
        return role_name;
    }
}
