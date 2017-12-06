$(function() {

    $( "#changeChategoryContent" ).click(function() {
        var data = {"id": 2};
        $.ajax({
            url: "/product",
            type: "POST",
            data: JSON.stringify(data),
            dataType: "json",
            contentType: "application/json",
            success: function(result){
                var category = result.category;
                var productList = result.products;
                populateHtmlWithProducts(productList);

        }});
    });

    function populateHtmlWithProducts (productlist) {
            $("#products").empty();
            for (var i = 0; i < productlist.length; i++) {
                $("#products").append(
                `
                <div id="productsList" class="item col-xs-4 col-lg-4">
                    <div class="thumbnail">
                        <img class="group list-group-image" src="/img/product_${productlist[i].id}.jpg"  alt="" />
                        <div class="caption">
                            <h4 id="productName" class="group inner list-group-item-heading">${productlist[i].name}</h4>
                            <p id="productDescription" class="group inner list-group-item-text">${productlist[i].description}</p>
                            <div class="row">
                                <div class="col-xs-12 col-md-6">
                                    <p id="productPrice" class="lead">${productlist[i].defaultPrice}</p>
                                </div>
                                <div class="col-xs-12 col-md-6">
                                    <a class="btn btn-success" href="#">Add to cart</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                `
                )
            }
        };
});