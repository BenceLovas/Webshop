package com.codecool.shop.controller;

import com.codecool.shop.Utils;
import com.codecool.shop.dao.implementation.Db.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.Db.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.Db.SupplierDaoJdbc;
import com.codecool.shop.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.*;

/**
 * Product controller class is responsible to handle server client communication releted to sessions. Also responsible for
 * populating, updating and query for the different stages of orders.
 * @author Team Lava
 */
public class ProductController {

    private static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private static SupplierDaoJdbc supplierDaoJdbc = SupplierDaoJdbc.getInstance();
    private static ProductCategoryDaoJdbc productCategoryDaoJdbc = ProductCategoryDaoJdbc.getInstance();
    private static ProductDaoJdbc productDaoJdbc = ProductDaoJdbc.getInstance();
    /**
     * Render products method is responsible for populating the template generated by thymeleaf.
     * @param req request sent from client side
     * @param res response sent after processing the request
     * @return Renders the page, with the populated data
     */
    public static String renderProducts(Request req, Response res) {
        UserController.ensureUserIsLoggedIn(req, res);
        int userId = req.session().attribute("userId");
        int supplierId = 1;
        Supplier supplier = supplierDaoJdbc.find(supplierId);
        List<Product> products = productDaoJdbc.getBySupplier(supplierId);
        List<Map<String, String>> productsResponse = ModelBuilder.productModel(products);
        Map<String, Object> data = new HashMap<>();
        data.put("username", req.session().attribute("username"));
        data.put("categories", productCategoryDaoJdbc.getAll());
        data.put("suppliers", supplierDaoJdbc.getAll());
        data.put("collectionName", supplier.getName());
        data.put("collection", productsResponse);

        LOGGER.debug("Program finished processing request on loading target supplier data and ready to send back to client" +
                    "with the following data: {}", data);

        return Utils.renderTemplate(data, "product/index");
    }


    /**
     * This method is responsible for gethering all products filtered by supplier and returns as it is
     * convertible to JSON format.
     * @param request request sent from client side
     * @param response response sent after processing the request
     * @return the jasonified list of products filtered by supplier
     */
    public static String getProductsBySupplier(Request request, Response response) {
        int supplierId = Integer.parseInt(request.params("id"));

        LOGGER.info("Supplier id received from request: {}", supplierId);

        Supplier supplier = supplierDaoJdbc.find(supplierId);
        List<Product> products = productDaoJdbc.getBySupplier(supplierId);
        List<Map<String, String>> collection = ModelBuilder.productModel(products);

        Map<String, Object> data = new HashMap<>();
        data.put("collection", collection);
        data.put("collectionName", supplier.getName());

        LOGGER.debug("Response with supplier data to jasonify: {}", data);

        return Utils.toJson(data);
    }

    /**
     * This method is responsible for gethering all products filtered by category and returns as it is
     * convertible to JSON format.
     * @param request request sent from client side
     * @param response response sent after processing the request
     * @return the jasonified list of products filtered by category
     */
    public static String getProductsByCategory(Request request, Response response) {
        int categoryId = Integer.parseInt(request.params("id"));

        LOGGER.info("Category id received from request: {}", categoryId);
        ProductCategory category = productCategoryDaoJdbc.find(categoryId);
        List<Product> products = productDaoJdbc.getByProductCategory(categoryId);
        List<Map<String, String>> collection = ModelBuilder.productModel(products);

        Map<String, Object> data = new HashMap<>();
        data.put("collection", collection);
        data.put("collectionName", category.getName());

        LOGGER.debug("Response with category data to jasonify: {}", data);

        return Utils.toJson(data);
    }

}
