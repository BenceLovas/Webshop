package com.codecool.shop.controller;

import com.codecool.shop.Utils;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.*;

import com.sun.org.apache.xpath.internal.operations.Or;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

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
        if (!isLineItem(targetItem)) {
            LineItem newLineItem = new LineItem(targetItem, targetItem.getDefaultPrice());
            Order.getCurrentOrder().add(newLineItem);
        }
        Map<String, String> response = new HashMap<>();
        response.put("itemsNumber", Integer.toString(Order.getCurrentOrder().getTotalSize()));
        response.put("totalPrice", Float.toString(Order.getCurrentOrder().getTotalPrice()));
        System.out.println(response);
        return Utils.toJson(response);
    }

    private static boolean isLineItem(Product targetItem) {
        for (LineItem lineItem: Order.getCurrentOrder().getAddedItems()) {
            if (lineItem.getItem().equals(targetItem)){
                lineItem.incrementQuantity();
                Order.getCurrentOrder().incrementTotalSize();
                return true;
            }
        }
        return false;
    }

}
