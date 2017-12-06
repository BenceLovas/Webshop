const dom = {

    addEventListeners: function () {
        $('.btn-success').click(function(event) {
            var clickedButton = event.target;
            var productId = {"productid" : clickedButton.dataset.prod_id};
            $.ajax({
                type: "POST",
                url: "/api/additem",  // Send the login info to this page
                data: JSON.stringify(productId),
                dataType: "json",
                contentType: "application/json",
                success: dom.refreshHeader,

            });
        })
    },

    refreshHeader: function (response) {
        var itemsNumber = response.itemsNumber;
        var totalPrice = response.totalPrice;
        $('#total-items').text(itemsNumber);
        $('#total-price').text(totalPrice);

    }

}

dom.addEventListeners();