package com.codecool.shop.controller;

import com.codecool.shop.Utils;
import com.codecool.shop.Validator;
import com.codecool.shop.dao.implementation.Db.OrderDaoJdbc;
import com.codecool.shop.dao.implementation.Db.ProductCategoryDaoJdbc;
import com.codecool.shop.dao.implementation.Db.ProductDaoJdbc;
import com.codecool.shop.dao.implementation.Db.SupplierDaoJdbc;
import com.codecool.shop.model.LineItem;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrderController {

    private static Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    private static ProductDaoJdbc productDaoJdbc = ProductDaoJdbc.getInstance();
    private static Validator validator = Validator.getInstance();

    public static Order checkAndCreateOrder (Integer userId) {
        Order userOrder = null;
        Integer orderId = OrderDaoJdbc.checkActiveOrder(userId);
        if(orderId != null) {
            userOrder = OrderDaoJdbc.createOrderFromData(orderId, userId);
        } else {
            userOrder = new Order(userId);
        }
        return userOrder;
    }
    /**
     * This method is responsible to create a lineitem object that will represent the actual item object in an
     * orderable format
     * @param req request sent from client side
     * @param res response sent after processing the request
     * @return Returns a map with all the lineitems in the sopping cart completed with the newly item in the cart
     */
    public static String handleOrder(Request req, Response res) {
        int productId = Integer.parseInt(req.params("id"));
        Product product = productDaoJdbc.find(productId);
        LOGGER.info("Line-item selected by id ({}) from order request: {}", productId, product);
        int userId = req.session().attribute("userId");
        int orderId = OrderDaoJdbc.checkActiveOrder(userId);

        if (!isLineItem(product, req)) {
            LineItem newLineItem = new LineItem(product, product.getDefaultPrice(), userId);
            Order.getActiveOrder(userId).add(newLineItem);
            OrderDaoJdbc.addLineItem(newLineItem, orderId,userId);

            LOGGER.info("New lineitem was created: {}", newLineItem);
        }

        Map<String, Object> response = getShoppingCartData(req);

        LOGGER.debug("Shopping card data with the added lineitem to jasonify: {}", response);

        return Utils.toJson(response);
    }

    /**
     * This method is responsible for adding user data to the order.
     * @param request request sent from client side
     * @param response response sent after processing the request
     * @return a confirmation that the update for the order has been processed
     */
    public static String addUserData(Request request, Response response) {

        Map<String, String> userData = Utils.parseJson(request);
        Map<String, String> res = new HashMap<>();

        LOGGER.debug("Userdata to jasonify after reading the request data in: {}", userData);
        LOGGER.debug("order updated with user data");


        int userId = request.session().attribute("userId");

        if (validator.validateUserData(userData, res)) {
            Order.getActiveOrder(userId).setUserData(userData);

            LOGGER.debug("Userdata to JSONify after reading the request data in: {}", userData);
            LOGGER.debug("order updated with user data");

            return Utils.toJson("OK");
        } else {
            response.status(400);
            return Utils.toJson(res);
        }
    }

    /**
     * This method is responsible for adding payment data to the order.
     * @param request request sent from client side
     * @param response response sent after processing the request
     * @return a confirmation that the update for the order has been processed
     */
    public static String addPaymentData(Request request, Response response) {
        System.out.println("im here!");
        Map<String, String> userData = Utils.parseJson(request);
        Integer userId = request.session().attribute("userId");
        int orderId = OrderDaoJdbc.checkActiveOrder(userId);
        Order.getActiveOrder(userId).setPaymentData(userData);
        Map<String, String> res = new HashMap<>();
        System.out.println("im not here");
        if (validator.validatePaymentData(userData, res)) {
            System.out.println("Am i here?");
            LOGGER.debug("Payment data to JSONify after reading the request data in: {}", userData);

            LOGGER.info("Order updated with payment data");
            LOGGER.debug("order updated with payment data");
            OrderDaoJdbc.deactivateLineItem(orderId);
            OrderController.checkAndCreateOrder(userId);
            return Utils.toJson("OK");

        } else {
            System.out.println("im in the false");
            response.status(400);
            return Utils.toJson(res);
        }
    }

    /**
     * This method is responsible to check if the argument item is a lineitem ready to be processed as a
     * shopping cart item.
     * @param targetItem
     * @return gives the state of the argument item
     */
    private static boolean isLineItem(Product targetItem, Request req) {

        LOGGER.debug("Line-item to check if it is lineitem: {}", targetItem);
        int userId = req.session().attribute("userId");
        int orderId = OrderDaoJdbc.checkActiveOrder(userId);
        for (LineItem lineItem : Order.getActiveOrder(userId).getAddedItems()) {
            if (lineItem.getItem().getId() == targetItem.getId()) {
                lineItem.incrementQuantity(userId);
                OrderDaoJdbc.updateLineItem(lineItem, orderId);
                LOGGER.info("Lineitem is checked, and it returned true");
                return true;
            }
        }

        LOGGER.info("Lineitem is checked, and it returned false");

        return false;
    }

    /**
     * This method is responsible to update the desired quantity of a lineitem of interest upon request
     * @param req request sent from client side
     * @param res response sent after processing the request
     * @return updated sopping cart data in a jasonified format
     */
    public static String changeQuantity(Request req, Response res) {
        Map<String, String> data = Utils.parseJson(req);
        int userId = req.session().attribute("userId");
        int orderId = OrderDaoJdbc.checkActiveOrder(userId);
        List<LineItem> lineItems = Order.getActiveOrder(userId).getAddedItems();
        LineItem targetLineItem = null;
        for (LineItem lineItem : lineItems) {
            if (lineItem.getItem().getId() == Integer.parseInt(data.get("Id"))) { /* TODO: check if NaN error, if NaN return with error message */
                targetLineItem = lineItem;
                break;
            }
        }
        if (Objects.equals(data.get("change"), "plus")) {
            if (targetLineItem != null) {

                LOGGER.info("Quantity of targetlineitem ({}) has been increased", targetLineItem);

                targetLineItem.incrementQuantity(userId);
                OrderDaoJdbc.updateLineItem(targetLineItem, orderId);
            }
            Order.getActiveOrder(userId).changeTotalPrice();

            LOGGER.info("Total price changed");
        } else {
            if (targetLineItem != null && targetLineItem.getQuantity() > 0) {

                LOGGER.info("Quantity of targetlineitem ({}) has been decreased", targetLineItem);

                targetLineItem.decrementQuantity(userId);
                OrderDaoJdbc.updateLineItem(targetLineItem, orderId);
                if (targetLineItem.getQuantity() == 0) {
                    Order.getActiveOrder(userId).getAddedItems().remove(targetLineItem);
                    OrderDaoJdbc.removeLineItem(targetLineItem, orderId);

                    LOGGER.info("Targetlineitem ({}) has been removed from order list", targetLineItem);

                }

            }
            Order.getActiveOrder(userId).changeTotalPrice();

            LOGGER.info("Total price changed");

        }
        Map<String, Object> response = getShoppingCartData(req);
        return Utils.toJson(response);
    }

    /**
     * This method gethers all possible data about the shopping cart.
     * @return a map with all the fields of the shopping cart for further processing
     */
    private static Map<String, Object> getShoppingCartData(Request req) {
        int userId = req.session().attribute("userId");
        List<LineItem> orderItems = Order.getActiveOrder(userId).getAddedItems();

        LOGGER.info("Shopping card data successfully gethered: {}", orderItems);

        List<Map> orders = ModelBuilder.lineItemModel(orderItems);
        Map<String, Object> response = new HashMap<>();
        response.put("itemsNumber", Integer.toString(Order.getActiveOrder(userId).getTotalSize()));
        response.put("totalPrice", Float.toString((float) Math.round(Order.getActiveOrder(userId).getTotalPrice() * 100) / 100));
        response.put("shoppingCart", orders);

        LOGGER.debug("Response with shopping cart data: {}", response);

        return response;
    }

    public static String loadShoppingCartForClientSide (Request request, Response response) {
        return Utils.toJson(getShoppingCartData(request));
    }
}
