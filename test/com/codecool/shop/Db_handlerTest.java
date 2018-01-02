package com.codecool.shop;

import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;
import org.junit.jupiter.api.Test;

class Db_handlerTest {

    @Test
    void testCreatePreparedStatementForAddSupplier() {
        Supplier supplier = new Supplier("Test", "Test description");
        Db_handler db_handler = Db_handler.getInstance();
        String query = "INSERT INTO supplier (id, name, description) " +
                "VALUES (?,?,?);";
        db_handler.createPreparedStatementForAdd(supplier, query);
    }

    @Test
    void testCreatePreparedStatementForAddProduct() {
        Supplier supplier = new Supplier("Test", "Test description");
        ProductCategory productCategory = new ProductCategory("TestCategory", "Testing", "Test description for prodCategory");
        Product product = new Product("TestProduct", 14f, "EUR", "TestDescription", productCategory, supplier);

        String query = "INSERT INTO product (id, name, description, currency_string, default_price, category_id, supplier_id) " +
                "VALUES (?,?,?,?,?,?,?);";

        Db_handler db_handler = Db_handler.getInstance();
        db_handler.createPreparedStatementForAdd(product, query);

    }

}