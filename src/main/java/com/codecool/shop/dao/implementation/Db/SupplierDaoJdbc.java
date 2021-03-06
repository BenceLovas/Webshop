package com.codecool.shop.dao.implementation.Db;

import com.codecool.shop.Db_handler;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.Mem.ProductDaoMem;
import com.codecool.shop.dao.implementation.Mem.SupplierDaoMem;
import com.codecool.shop.model.Supplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDaoJdbc implements SupplierDao {

    private static Db_handler db_handler = Db_handler.getInstance();
    private static SupplierDaoJdbc instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    private SupplierDaoJdbc() {
    }

    public static SupplierDaoJdbc getInstance() {
        if (instance == null) {
            instance = new SupplierDaoJdbc();
        }
        return instance;
    }

    @Override
    public void add(Supplier supplier) {
        String query = "INSERT INTO supplier (id, name, description) " +
                "VALUES (?,?,?);";

        db_handler.createPreparedStatementForAdd(supplier, query);
    }

    @Override
    public Supplier find(int id) {
        SupplierDaoMem supplierDaoMem = SupplierDaoMem.getInstance();

        if (supplierDaoMem.getAll().contains(supplierDaoMem.find(id))) {
            return supplierDaoMem.find(id);
        } else {

            String query = "SELECT * FROM supplier WHERE id = ?;";

            ResultSet foundElement = db_handler.createPreparedStatementForFind(id, query);
            try {
                foundElement.next();
                Supplier foundSupplier = new Supplier(foundElement.getString("name"), foundElement.getString("description"));
                foundSupplier.setId(foundElement.getInt("id"));
                supplierDaoMem.add(foundSupplier);
                return foundSupplier;
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public void remove(int id) {
        SupplierDaoMem.getInstance().remove(id);
        String query = "DELETE FROM supplier WHERE id = ?;";
        db_handler.createPreparedStatementForRemove(id, query);
    }

    @Override
    public List<Supplier> getAll() {
        SupplierDaoMem.getInstance().clear();
        ArrayList<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM supplier";
        ResultSet foundElements = db_handler.createPreparedStatementForGetAll(query);

        try {
            while (foundElements.next()){
                Supplier newSupplier = new Supplier(foundElements.getString("name"),
                        foundElements.getString("description"));
                newSupplier.setId(foundElements.getInt("id"));
                SupplierDaoMem.getInstance().add(newSupplier);
                suppliers.add(newSupplier);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }
}
