package com.codecool.shop.dao.implementation.Db;

import com.codecool.shop.ConnectionDetails;
import com.codecool.shop.Db_handler;
import com.codecool.shop.dao.UserDao;
import com.codecool.shop.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoJdbc implements UserDao {

    private static Db_handler db_handler = Db_handler.getInstance();
    private static UserDaoJdbc instance = null;
    private static final Logger logger = LoggerFactory.getLogger(UserDaoJdbc.class);

    private UserDaoJdbc() {}

    public static UserDaoJdbc getInstance() {
        if (instance == null) {
            instance = new UserDaoJdbc();
        }
        return instance;
    }

    @Override
    public Integer add(User user) {
        String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?) RETURNING id";

        try {
            Connection connection = db_handler.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            ConnectionDetails connectionDetails = new ConnectionDetails(connection, statement);
            CachedRowSet result = db_handler.fetchQuery(connectionDetails);
            if (result != null) {
                result.next();
                Integer id = result.getInt("id");
                return id;
            }
            logger.debug("User added to database");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();

            return null;
        }

    }

    @Override
    public User find(String username) {
        String query = "SELECT id, name, password FROM users WHERE name = ?";

        try {
            Connection connection = db_handler.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                User user = new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("password"));
                logger.debug("User found in database");
                return user;
            }

        } catch (SQLException e) {
            logger.warn("User not found in database!");
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
