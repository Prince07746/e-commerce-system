package org.apiimplementation.apiimplementations.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apiimplementation.apiimplementations.config.Message;
import org.apiimplementation.apiimplementations.error.customError.InvalidFormatException;
import org.apiimplementation.apiimplementations.error.customError.NotFoundException;
import org.apiimplementation.apiimplementations.service.ProductService;
import org.apiimplementation.apiimplementations.model.Product;
import org.apiimplementation.apiimplementations.security.pattern.IdPattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@CrossOrigin(origins = "*") // Allow all origins
@RestController
@RequestMapping("/product")
@Tag(name = "open api product management ", description = "manage products ")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @Operation(method = "GET", summary = "get a specific product using id", responses = {
            @ApiResponse(
                    responseCode = "200", description = "product retrieved successfully", content =
            @Content(schema = @Schema(implementation = Product.class))
            ),
            @ApiResponse(
                    responseCode = "407", description = "bad request", content = @Content(schema =
            @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "product not found", content = @Content(schema =
            @Schema(implementation = Message.class)
            ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(
            @Parameter(
                    description = "Product ID", name = "id", schema = @Schema(implementation = IdPattern.class),
                    in = ParameterIn.PATH, example = "\"1aa\"", required = true
            )
            @PathVariable(name = "id") IdPattern productId
    ) {
        Product product = productService.getProduct(productId.getValue());
        if (product == null) {
            throw new NotFoundException("Product with ID: " + productId + "  not found");
        }
        return ResponseEntity.ok(product);
    }


    @Operation(method = "POST", summary = "Create a new product", responses = {
            @ApiResponse(
                    responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PostMapping()
    public ResponseEntity<?> createNewProduct(
            @Parameter(
                    description = "Product", name = "product", content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Product.class)), required = true
            )
            @RequestBody Product product
    ) {

        if (product == null || product.getId() == null || product.getName() == null || product.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(new Message("Invalid product data"));
        }

        String idCheck = new IdPattern(product.getId()).getValue();
        productService.addProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Product created"));

    }


    @Operation(method = "PUT", summary = "Update an existing product", responses = {
            @ApiResponse(
                    responseCode = "200", description = "Product update successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> upDateProduct(
            @Parameter(
                    description = "Product ID", name = "id", schema = @Schema(implementation = IdPattern.class),
                    in = ParameterIn.PATH, example = "\"1aa\"", required = true
            )
            @PathVariable(name = "id") IdPattern productId,
            @RequestBody Product product
    ) {

        if (productService.getProduct(productId.getValue()) != null) {

            productService.updateProduct(product);

            return ResponseEntity.ok(new Message("Product updated successfully"));
        } else {
            throw new NotFoundException("Product with ID: " + productId + "  not found");
        }

    }


    // DELETE A PRODUCT BY ID
    @Operation(method = "DELETE", summary = "delete an existing product", responses = {
            @ApiResponse(
                    responseCode = "204", description = "Product deleted successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @Parameter(
                    description = "Product ID", name = "id", schema = @Schema(implementation = IdPattern.class),
                    in = ParameterIn.PATH, example = "\"1aa\"", required = true
            )
            @PathVariable(name = "id") IdPattern productId
    ) {
        if (productService.getProduct(productId.getValue()) != null) {
            productService.deleteProduct(productId.toString());
            return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(new Message("Product deleted successfully"));
        } else {
            throw new NotFoundException("Product with ID: " + productId + "  not found");
        }
    }


    @Operation(method = "GET", summary = "get product list", responses = {
            @ApiResponse(
                    responseCode = "204", description = "Product empty list",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "200", description = "Product list retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Message.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = Message.class))
            )
    })
    @GetMapping()
    public ResponseEntity<?> getAllProduct() {
        List<Product> products = productService.getProductList();

        if (products.isEmpty()) {
            return ResponseEntity.ok(new Message("Product list empty"));
        }
        return ResponseEntity.ok(products);
    }


}


