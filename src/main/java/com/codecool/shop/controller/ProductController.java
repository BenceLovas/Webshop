package com.codecool.shop.controller;

import com.codecool.shop.Utils;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.google.gson.JsonObject;
import spark.ModelAndView;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.*;

import spark.Request;
import spark.Response;

import java.util.*;

import static com.codecool.shop.Utils.parseJson;

public class ProductController {

    public static String renderProducts(Request req, Response res) {
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        Utils utils = Utils.getInstance();

        Map params = new HashMap<>();
        params.put("categories", productCategoryDataStore.getAll());
        params.put("order", Order.getCurrentOrder());
        return utils.renderTemplate(params, "product/index");
    }

    public static String handleOrder(Request req, Response res) {
        Map<String,String> request  = Utils.parseJson(req);
        Product targetItem = ProductDaoMem.getInstance().find(Integer.parseInt(request.get("productid")));
        LineItem newLineItem = new LineItem(targetItem, targetItem.getDefaultPrice());
        Order.getCurrentOrder().add(newLineItem);
        Map<String, String> response = new HashMap<>();
        response.put("itemsNumber", Integer.toString(Order.getCurrentOrder().getAddedItems().size()));
        response.put("totalPrice", Float.toString(Order.getCurrentOrder().getTotalPrice()));
        return Utils.toJson(response);
    }

    public static String getProductsByCategory (Request req, Response res) {
        Map<String, String> idMap = parseJson(req);
        int categoryId = Integer.parseInt(idMap.get("id"));

        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        List <Product> currentProducts = productDataStore.getBy(productCategoryDataStore.find(categoryId));

        List<Map> categoryMapList = new ArrayList<>();

        Map<String, String> categoryMap = new HashMap<>();
        categoryMap.put("id", Integer.toString(productCategoryDataStore.find(categoryId).getId()));
        categoryMap.put("name", productCategoryDataStore.find(categoryId).getName());
        categoryMap.put("department", productCategoryDataStore.find(categoryId).getDepartment());
        categoryMap.put("description", productCategoryDataStore.find(categoryId).getDescription());
        categoryMapList.add(categoryMap);

        List<Map> productMapList = new ArrayList<>();

        for (int i = 0; i < currentProducts.size(); i++) {
            Product currentProduct = currentProducts.get(i);
            Map<String, String> currentProductMap = new HashMap<>();
            currentProductMap.put("id", Integer.toString(currentProduct.getId()));
            currentProductMap.put("name", currentProduct.getName());
            currentProductMap.put("description", currentProduct.getDescription());
            currentProductMap.put("defaultPrice", Float.toString(currentProduct.getDefaultPrice()));
            currentProductMap.put("defaultCurrency", currentProduct.getDefaultCurrency().toString());
            currentProductMap.put("supplier", currentProduct.getSupplier().toString());
            productMapList.add(currentProductMap);
        }

        Map <String,List> currentParams = new HashMap<>();
        currentParams.put("category", categoryMapList);
        currentParams.put("products", productMapList);

        return Utils.toJson(currentParams);
    }
}
