package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.UserDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.security.User;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author SeanAnderson
 */
public class UserService extends BaseService<UserDao, User>{

    @Override
    public UserDao getDao() throws SQLException {
        return dao == null ? new UserDao(connection) : dao;
    }

    public User getRootAdminUser() throws SQLException {
        var results = this.getDao().queryForEq("username", "admin");
        if (results == null || results.size() == 0) {
            return null;
        }
        return results.get(0);
    } 
    
    public boolean isSecurityEnabled() throws SQLException {
        var results = this.getDao().queryForEq("username", "admin");
        if (results == null || results.size() == 0) {
            return false;
        }
        return !StringUtils.isEmpty(results.get(0).getPassword());
    }
    
    
}
