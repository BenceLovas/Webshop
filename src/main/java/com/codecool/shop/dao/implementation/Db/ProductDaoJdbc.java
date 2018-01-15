package com.codecool.shop.dao.implementation.Db;


import com.codecool.shop.Db_handler;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.Mem.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductDaoJdbc provides access to product objects through the SQL database
 */

public class ProductDaoJdbc implements ProductDao {

    private static Db_handler db_handler = Db_handler.getInstance();
    private static ProductDaoJdbc instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ProductDaoJdbc.class);


    /* A private Constructor prevents any other class from instantiating.
     */
    private ProductDaoJdbc() {
    }

    /**
     * Returns the data access object for JDBC
     */
    public static ProductDaoJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoJdbc();
        }
        return instance;
    }

    @Override
    public void add(Product product) {
        String query = "INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) " +
                "VALUES (?,?,?,?,?,?,?);";
        logger.debug("Product add query created");
        db_handler.createPreparedStatementForAdd(product, query);
    }


    /**
     * @implNote returns null if no record is found in the database
     */
    @Override
    public Product find(int id) {

        ProductDaoMem productDaoMem = ProductDaoMem.getInstance();
        if (productDaoMem.getAll().contains(productDaoMem.find(id))) {
            logger.debug("Memory contains product id {}", id);
            return productDaoMem.find(id);
        } else {

            String query = "SELECT * FROM product WHERE id = ?;";
            SupplierDaoJdbc supplierDaoJdbc = SupplierDaoJdbc.getInstance();
            ProductCategoryDaoJdbc productCategoryDaoJdbc = ProductCategoryDaoJdbc.getInstance();
            ResultSet foundElement = db_handler.createPreparedStatementForFind(id, query);
            try {
                foundElement.next();
                Product foundProduct = new Product(
                        foundElement.getString("name"),
                        foundElement.getFloat("default_price"),
                        foundElement.getString("currency_string"),
                        foundElement.getString("description"),
                        productCategoryDaoJdbc.find(foundElement.getInt("category_id")),
                        supplierDaoJdbc.find(foundElement.getInt("supplier_id")));

                foundProduct.setId(foundElement.getInt("id"));
                ProductDaoMem.getInstance().add(foundProduct);
                logger.debug("Product {} added to ProductDaoMem", foundProduct.getName());
                return foundProduct;
            } catch (SQLException e) {
                logger.warn("No SQL entry found for product id {}", id);
                return null;
            }
        }
    }

    @Override
    public void remove(int id) {
        ProductDaoMem.getInstance().remove(id);
        logger.debug("Product id {} removed from DaoMem", id);
        String query = "DELETE FROM product WHERE id = ?;";
        db_handler.createPreparedStatementForRemove(id, query);
    }

    /**
     * @throws SQLException when the products table is empty
     */
    @Override
    public List<Product> getAll() {

        ProductDaoMem productDaoMem = ProductDaoMem.getInstance();
        productDaoMem.clear();
        logger.debug("ProductDaoMem cleared");

        ArrayList<Product> products = new ArrayList<>();
        SupplierDaoJdbc supplierDaoJdbc = SupplierDaoJdbc.getInstance();
        ProductCategoryDaoJdbc productCategoryDaoJdbc = ProductCategoryDaoJdbc.getInstance();
        String query = "SELECT * FROM product";
        ResultSet foundElements = db_handler.createPreparedStatementForGetAll(query);
        try {
            while (foundElements.next()){
                Product newProduct = new Product(foundElements.getString("name"),
                        foundElements.getFloat("default_price"),
                        foundElements.getString("currency_string"),
                        foundElements.getString("description"),
                        productCategoryDaoJdbc.find(foundElements.getInt("category_id")),
                        supplierDaoJdbc.find(foundElements.getInt("supplier_id"))
                        );
                newProduct.setId(foundElements.getInt("id"));
                ProductDaoMem.getInstance().add(newProduct);
                products.add(newProduct);
            }
        } catch (SQLException e) {
            logger.warn("Product table empty!");
            e.printStackTrace();
        }

        logger.debug("{} products found", products.size());
        return products;
    }

    @Override
    public List<Product> getBy(Supplier supplier) {
        List<Product> products = getAll();

        List<Product> productsBySupplier = new ArrayList<>();

        for (Product product : products) {
            if (product.getSupplier().getId() == supplier.getId()) {
                productsBySupplier.add(product);
            }
        }
        logger.debug("{} products added to supplier list of {}", productsBySupplier.size(), supplier.getName());

        return productsBySupplier;
    }

    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        List<Product> products = getAll();

        List<Product> productsByCategory = new ArrayList<>();

        for (Product product : products) {
            if (product.getProductCategory().getId() == productCategory.getId()) {
                productsByCategory.add(product);
            }
        }
        logger.debug("{} products added to supplier list of {}", productsByCategory.size(), productCategory.getName());

        return productsByCategory;
    }
}
