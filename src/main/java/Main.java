import com.codecool.shop.controller.ProductController;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.SupplierDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.dao.implementation.SupplierDaoMem;
import com.codecool.shop.model.Order;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.util.Arrays;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {

    public static void main(String[] args) {

        exception(Exception.class, (e, req, res) -> e.printStackTrace());
        staticFiles.location("/public");
        port(5000);

        before((request, response) -> {
            if (Arrays.asList("GET", "DELETE").contains(request.requestMethod())) {
                System.out.println(request.requestMethod() + " @ " + request.url() + " ° " + request.params());
            } else {
                System.out.println(request.requestMethod() + " @ " + request.url() + " ° " + request.body());
            }
            // check if user logged in
            // put data to localStorage
        });

        // populate some data for the memory storage
        populateData();

        // Always start with more specific routes
        get("/hello", (req, res) -> "Hello World");
        post("/product", ProductController::getProductsByCategory);

        // Always add generic routes to the end
        get("/", ProductController::renderProducts);

        post("/api/additem", ProductController::handleOrder);

        // Add this line to your project to enable the debug screen
        enableDebugScreen();
    }

    private static void populateData() {

        Order shoppingCart = new Order();

        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        SupplierDao supplierDataStore = SupplierDaoMem.getInstance();

        //setting up a new supplier
        Supplier amazon = new Supplier("Amazon", "Digital content and services");
        Supplier lenovo = new Supplier("Lenovo", "Computers");
        Supplier nvidia = new Supplier("Nvidia", "GPUs");

        //setting up a new product category
        ProductCategory tablet = new ProductCategory("Tablet", "Hardware", "A tablet computer, commonly shortened to tablet, is a thin, flat mobile computer with a touchscreen display.");
        ProductCategory computer = new ProductCategory("Laptop", "Hardware", "Portable computers for universal porpoises");
        ProductCategory gpu = new ProductCategory("GPU", "Hardware", "A hardware to help you render graphical programs.");
        ProductCategory pc = new ProductCategory("PC", "Hardware", "Personal computer.");

        //setting up products and printing it
        new Product("Amazon Fire", 49.9f, "USD", "Fantastic price. Large content ecosystem. Good parental controls. Helpful technical support.", tablet, amazon);
        new Product("Lenovo IdeaPad Miix 700", 479, "USD", "Keyboard cover is included. Fanless Core m5 processor. Full-size USB ports. Adjustable kickstand.", tablet, lenovo);
        new Product("Amazon Fire HD 8", 89, "USD", "Amazon's latest Fire HD 8 tablet is a great value for media consumption.", tablet, amazon);

        new Product("Laptop1", 8000, "USD", "Op", computer, lenovo);
        new Product("Laptop2", 6000, "USD", "E.Z.", computer, lenovo);
        new Product("Laptop3", 7000, "USD", "Lofasz", computer, lenovo);

        new Product("Nvidia GeForce 1080Ti", 420, "USD", "The greatest GPU on the earth!", gpu, nvidia);
    }

}
