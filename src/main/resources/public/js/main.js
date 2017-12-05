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
                console.log(result);
        }});
    });

});