package org.apiimplementation.apiimplementations.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiimplementation.apiimplementations.config.Message;
import org.apiimplementation.apiimplementations.error.customError.InvalidFormatException;
import org.apiimplementation.apiimplementations.error.customError.NotFoundException;
import org.apiimplementation.apiimplementations.model.Order;
import org.apiimplementation.apiimplementations.security.pattern.IdPattern;
import org.apiimplementation.apiimplementations.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*") // Allow all origins
@RestController
@RequestMapping("/order")
@Tag(name = "open api order management ", description = "manage orders ")
public class OrderController{

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }





    @Operation(method = "GET", summary = "get a specific order using id", responses = {
            @ApiResponse(
                    responseCode = "200", description = "order retrieved successfully", content =
            @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "407", description = "bad request", content = @Content(schema =
            @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "order not found", content = @Content(schema =
            @Schema(implementation = Message.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(
            @Parameter(
                    description = "Order ID 24 digit alphanumeric", name = "id", schema = @Schema(implementation = IdPattern.class),
                    in = ParameterIn.PATH, example = "\"67dc0534b9f7b223114b53dd\"", required = true
            )
            @PathVariable(name = "id") IdPattern orderId
    ) {
        Order order = orderService.getOrder(orderId.getValue());
        if(order == null){
            throw new NotFoundException("Product with ID: " + orderId + "  not found");
        }
        return ResponseEntity.ok(order);
    }





    @Operation(method = "GET",description = "get the list of all orders",responses = {
            @ApiResponse(
                    responseCode = "200",description = "list retrieve successfully",
                    content = @Content(mediaType = "Application/JSON", array = @ArraySchema(schema =
                    @Schema(implementation = Order.class)))
            )
    })
    @GetMapping
    public ResponseEntity<?> getOrderList(){
        return ResponseEntity.ok(orderService.getOrderList());
    }




    @Operation(method = "POST", summary ="Create a new order",responses = {
            @ApiResponse(
                    responseCode = "201", description = "order created successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PostMapping()
    public ResponseEntity<?> createNewOrder(
            @Parameter(
                    description = "order",name="order",content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Order.class)),required = true
            )
            @RequestBody Order order
    ) {

        if(order == null){
            throw new InvalidFormatException("Request body is required and cannot be empty");
        }
        orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Order created"));
    }








    @Operation(method = "POST", summary ="add a new product to existing order",responses = {
            @ApiResponse(
                    responseCode = "201", description = "Product to order  successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PostMapping("/addProductOrder")
    public ResponseEntity<?> addNewProductToOrder(
            @Parameter(description = "ID of the product to add 24 digit alphanumeric", required = true, example = "67dc0534b9f7b223114b53dd")
            @RequestParam("productId") IdPattern productId,

            @Parameter(description = "Quantity of the product", required = true, example = "10")
            @RequestParam("quantity") int quantity,

            @Parameter(description = "ID of the order 24 digit alphanumeric", required = true, example = "67dc0534b9f7b223114b53dd")
            @RequestParam("orderId") IdPattern orderId

    ) {

        orderService.addProductToOrder(productId.getValue(),quantity,orderId.getValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Product created"));
    }







    @Operation(method = "POST", summary ="add a new product to new order",responses = {
            @ApiResponse(
                    responseCode = "201", description = "Product to order  successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PostMapping("/addProductToNewOrder")
    public ResponseEntity<?> addNewProductNewToOrder(
            @Parameter(description = "ID of the product to add 24 digit alphanumeric", required = true, example =
                    "67dc0534b9f7b223114b53dd")
            @RequestParam("productId") IdPattern productId,

            @Parameter(description = "Quantity of the product", required = true, example = "10")
            @RequestParam("quantity") int quantity,

            @Parameter(description = "ID of the User 24 digit alphanumeric", required = true, example = "67dc0534b9f7b223114b53dd")
            @RequestParam("userId") IdPattern userId

    )  {

        System.out.println("Phase 1");
        System.out.println(productId.getValue());
        System.out.println(userId.getValue());
        orderService.addProductToNewOrder(productId.getValue(),quantity,userId.getValue());

        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Product created"));
    }




    @Operation(method = "PUT", summary = "Update an existing order", responses = {
            @ApiResponse(
                    responseCode = "200", description = "Order updated successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @Parameter(
                    description = "Order ID 24 digit alphanumeric", name = "id", schema = @Schema(implementation =
                    IdPattern.class),
                    in = ParameterIn.PATH, example = "\"67dc0534b9f7b223114b53dd\"", required = true
            )
            @PathVariable(name = "id") IdPattern orderId,
            @RequestBody Order order
    ) {
        Order existingOrder = orderService.getOrder(orderId.getValue());

        if (existingOrder == null) {
            throw new NotFoundException("Order with ID: " + orderId + " not found");
        }

        // Ensure the order ID in the request matches the path variable ID
        order.setId(orderId.getValue());

        // Update the order in the database
        orderService.updateOrder(order);

        return ResponseEntity.ok(new Message("Order updated successfully"));
    }





    @Operation(method = "DELETE", summary = "Delete an existing order", responses = {
            @ApiResponse(
                    responseCode = "204", description = "Order deleted successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(
            @Parameter(
                    description = "Order ID 24 digit alphanumeric", name = "id", schema = @Schema(implementation =
                    IdPattern.class),
                    in = ParameterIn.PATH, example = "\"67dc0534b9f7b223114b53dd\"", required = true
            )
            @PathVariable(name = "id") IdPattern orderId
    ) {
        if (orderService.getOrder(orderId.getValue()) != null) {
            orderService.deleteOrder(orderId.getValue());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Message("Order deleted successfully"));
        } else {
            throw new NotFoundException("Order with ID: " + orderId + " not found");
        }
    }



}
