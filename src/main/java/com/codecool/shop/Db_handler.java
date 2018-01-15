package com.codecool.shop;

import com.codecool.shop.model.BaseModel;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Db_handler is the class providing connection to postgreSQL database.
 */
public class Db_handler {


    /**
     * Db handler instance to be returned every time.
     */
    private static final Logger logger = LoggerFactory.getLogger(Db_handler.class);
    private static Db_handler instance = null;
    private static Properties props = null;

    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private Db_handler() {
    }

    /**
     * Returns the database handler object
     * @return Db_handler object instance.
     */
    public static Db_handler getInstance() {
        if (instance == null) {
            instance = new Db_handler();
        }
        return instance;
    }

    /**
     * Establishes a connection with postgresSQL database.
     * @throws SQLException if connection cannot be established.
     * @return Returns Connection object to the DB.
     */
    public Connection getConnection() throws SQLException {
        if (props == null) {
            props = new Properties();
            FileInputStream in = null;
            try {
                in = new FileInputStream("configuration/DataBase.properties");
                logger.debug("Property file source for DB set to: {}.", in);
                try {
                    props.load(in);
                    in.close();
                    logger.info("Successfully loaded the properties for DB.");
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.warn("Unsuccessful load for properties.");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.error("Error in file source for properties.");
            }
        }
        String url = props.getProperty("jdbc.url");
        logger.debug("Database URL set to: {}", url);
        String username = props.getProperty("jdbc.username");
        logger.debug("Database username set to: {}", username);
        String password = props.getProperty("jdbc.password");
        logger.debug("Database password set to: {}", password); // I don't know if this is a good idea //

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Executes a PreparedStatement when there is a ResultSet object to return.
     * @param preparedStatement PreparedStatement object to be executed.
     * @return ResultSet object as a cursor in DB.
     */
    private ResultSet executePreparedStatementQuery(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Executes a PreparedStatement when there is no ResultSet object to return.
     * @param preparedStatement PreparedStatement object to be executed.
     */
    private void executePreparedStatement(PreparedStatement preparedStatement) {
        try {
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and executes a PreparedStatement for adding BaseModel object to DB.
     * @param object BaseModel object which needs to be added to DB.
     * @param query Query to be rendered as PreparedStatement.
     */
    public void createPreparedStatementForAdd(BaseModel object, String query) {
        try {
            Connection conn = getConnection();
            PreparedStatement prepStatement = conn.prepareStatement(query);
            logger.info("Creating prepared statement for adding data.");
            switch (object.getClass().getSimpleName()) {
                case "Product":
                    logger.info("Filling prepared statement for Product adding.");
                    fillPreparedStatementFields((Product) object, prepStatement);
                    break;
                case "ProductCategory":
                    logger.info("Filling prepared statement for ProductCategory adding.");
                    fillPreparedStatementFields((ProductCategory) object, prepStatement);
                    break;
                case "Supplier":
                    logger.info("Filling prepared statement for Supplier adding.");
                    fillPreparedStatementFields((Supplier) object, prepStatement);
                    break;
            }
            executePreparedStatement(prepStatement);
            logger.info("Executed SQL statement for adding data.");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in execution for adding data to DB.");
        }
    }

    /**
     * Fills the PreparedStatementFields's for adding ProductCategory to DB.
     * @param productCategory ProductCategory object which needs to be added to DB.
     * @param prepStatement PreparedStatement object to be filled with data from productCategory object.
     * @throws SQLException if the type of fields in PreparedStatement doesn't match the type of arguments to be filled.
     */
    private void fillPreparedStatementFields(ProductCategory productCategory, PreparedStatement prepStatement) throws SQLException {
        prepStatement.setInt(1, productCategory.getId());
        prepStatement.setString(2, productCategory.getName());
        prepStatement.setString(3, productCategory.getDescription());
        prepStatement.setString(4, productCategory.getDepartment());
    }

    /**
     * Fills the PreparedStatement's fields for adding Supplier to DB.
     * @param supplier Supplier object which needs to be added to DB.
     * @param prepStatement PreparedStatement object to be filled with data from supplier object.
     @throws SQLException if the type of fields in PreparedStatement doesn't match the type of arguments to be filled.
     */
    private void fillPreparedStatementFields(Supplier supplier, PreparedStatement prepStatement) throws SQLException {
        prepStatement.setInt(1, supplier.getId());
        prepStatement.setString(2, supplier.getName());
        prepStatement.setString(3, supplier.getDescription());
    }

    /**
     * Fills the PreparedStatement's fields for adding Product to DB.
     * @param product Product object which needs to be added to DB.
     * @param prepStatement PreparedStatement object to be filled with data from Product object.
     @throws SQLException if the type of fields in PreparedStatement doesn't match the type of arguments to be filled.
     */
    private void fillPreparedStatementFields(Product product, PreparedStatement prepStatement) throws SQLException {
        prepStatement.setInt(1, product.getId());
        prepStatement.setString(2, product.getName());
        prepStatement.setString(3, product.getDescription());
        prepStatement.setString(4, product.getDefaultCurrency().getCurrencyCode());
        prepStatement.setFloat(5, product.getDefaultPrice());
        prepStatement.setInt(6, product.getProductCategory().getId());
        prepStatement.setInt(7, product.getSupplier().getId());
    }

    /**
     * Creates and executes a PreparedStatement for finding record in DB.
     * @param id ID of the wanted element.
     * @param query Query to be rendered as PreparedStatement.
     * @return ResultSet object with pointer to found element.
     */
    public ResultSet createPreparedStatementForFind(int id, String query) {
        try {
            Connection conn = getConnection();
            logger.info("Creating prepared statement for finding values in DB.");
            PreparedStatement prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, id);
            logger.info("Executing SQL statement for finding values in DB.");
            return executePreparedStatementQuery(prepStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in execution for finding statement in DB.");
        }
        return null;
    }

    /**
     * Creates and executes a PreparedStatement for removing record from DB.
     * @param id ID of the wanted element.
     * @param query Query to be rendered as PreparedStatement.
     */
    public void createPreparedStatementForRemove(int id, String query) {
        try {
            Connection conn = getConnection();
            logger.info("Creating prepared statement for removing values in DB.");
            PreparedStatement prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, id);
            executePreparedStatement(prepStatement);
            logger.info("Executed SQL statement for removing data from DB.");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in execution for removing data from DB.");
        }
    }

    /**
     * Creates and executes a PreparedStatement for getting all records in from selected DB table.
     * @param query Query to be rendered as PreparedStatement.
     * @return ResultSet object with pointer to found elements.
     */
    public ResultSet createPreparedStatementForGetAll(String query) {
        try {
            Connection conn = getConnection();
            logger.info("Creating prepared statement for getting all fields of selected table.");
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            logger.info("Executing SQL statement for getting all fields of selected table.");
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Error in execution for getting all fields of selected table.");
        }
        return null;
    }

}
