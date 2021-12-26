package com.datavirtue.nevitium.services;

import com.datavirtue.nevitium.database.orm.UserDao;
import java.sql.SQLException;
import com.datavirtue.nevitium.models.security.User;
import com.datavirtue.nevitium.services.exceptions.DuplicateUserNameException;
import com.datavirtue.nevitium.services.exceptions.FailedPasswordException;
import com.j256.ormlite.dao.DaoManager;
import datavirtue.PBE;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author SeanAnderson
 */
public class UserService extends BaseService<UserDao, User> {

    private static User currentUser;
    private static String defaultUsername = "admin";
    public static void setCurrentUser(User user){
        currentUser = user;
    }
    public static User getCurrentUser() {
        
        if (currentUser == null) {
            currentUser = new User();
            currentUser.setAdmin(true);
            currentUser.setUserName(defaultUsername);
        }
        return currentUser;
    }
    
    @Override
    public UserDao getDao() throws SQLException {
        return DaoManager.createDao(connection, User.class);
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

    public User authenticateUser(String username, char[] password) throws FailedPasswordException, DuplicateUserNameException, SQLException {
                        
        var users = this.getDao().queryForEq("username", username);
        if (users == null) {
            return null;
        }
        if (users.size() > 1) {
            throw new DuplicateUserNameException();
        }
        var user = users.get(0);

        var cipher = user.getPassword();

        String decrypted = null;
        try {
            decrypted = PBE.decrypt(password, cipher);
        } catch (Exception ex) {            
            throw new FailedPasswordException();
        }
        if (decrypted != null) {
            var pw = new String(password);
            if (decrypted.equals(pw)) {
                return user;
            } else {
                throw new FailedPasswordException();
            }
        }else {
            throw new FailedPasswordException();
        }        
    }

}
