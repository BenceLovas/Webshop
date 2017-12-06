package com.codecool.shop.controller;

import com.codecool.shop.Utils;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.model.Product;
import com.google.gson.JsonObject;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.*;

import static com.codecool.shop.Utils.parseJson;

public class ProductController {

    public static String renderProducts(Request req, Response res) {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();

        Map params = new HashMap<>();
        params.put("category", productCategoryDataStore.find(1));
        params.put("products", productDataStore.getBy(productCategoryDataStore.find(1)));

        Utils utils = Utils.getInstance();
        return utils.renderTemplate(params, "product/index");
    }

    private static String renderTemplate(Map model, String template) {
        return new ThymeleafTemplateEngine().render(new ModelAndView(model, template));
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
