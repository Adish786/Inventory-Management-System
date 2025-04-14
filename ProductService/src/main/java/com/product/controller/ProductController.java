package com.product.controller;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.product.model.Product;
import com.product.repository.ProductRepository;

@RestController
public class ProductController {
    private Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService service;


    private final ProductRepository productRepository;

    public ProductController(ProductService service, ProductRepository productRepository) {
        this.service = service;
        this.productRepository = productRepository;
    }

    @RequestMapping(value = "/addProduct", method = RequestMethod.POST)
    @ApiOperation(value = "add product details", notes = "add product details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Product addProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @RequestMapping(value = "/addProducts", method = RequestMethod.POST)
    @ApiOperation(value = "add products details", notes = "add products details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public List<Product> addProducts(@RequestBody List<Product> products) {
       log.info("product service data are save");
        return service.saveProducts(products);
    }

    @RequestMapping(value = "/products", method = RequestMethod.GET)
    @ApiOperation(value = "get product details", notes = "find product details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public List<Product> findAllProducts() {
        log.info("find product service data");
        return service.getProducts();
    }

    @RequestMapping(value = "/productById/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "get products details", notes = "find products details by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Optional<Product> findProductById(@PathVariable UUID id) {
        log.info("find product service data based on UUID" +id);
        return service.getProductById(id);
    }

    //When @GetMapping("/welcome") Method are down then Falback Method are call by Shopping Service

    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String Welcome() {
        return "Welcome To Product Service";
    }

    @PutMapping("/updateByName")
    @ApiOperation(value = "update product details", notes = "update product details by name")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Product> updateProductByName(@RequestBody Product product) {
        log.info("product service update by name");
        Product updatedProduct = service.updateProductByName(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "delete product details", notes = "delete product details by id ")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public String deleteProduct(@PathVariable UUID id) {
        log.info("product service deleted by UUID" +id);
        return service.deleteProduct(id);
    }


    @PutMapping("/update/{name}")
    @ApiOperation(value = "update product details", notes = "update product details by name")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Product updateProductByName(@PathVariable(name = "name") String name, Product product) {
        log.info("product service find by the name");
        return service.updateProductByName(product);
    }

 /*   
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Product updateProduct(@RequestBody Product product) {
        return service.updateProduct(product);
    }
   */

    @PutMapping("update/{id}")
    @ApiOperation(value = "update product details", notes = "update product details by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Product> update(@PathVariable("id") UUID id, @RequestBody Product product) {
        Optional<Product> optionalProject = productRepository.findById(id);
        if (optionalProject.isPresent()) {
            Product p = optionalProject.get();
            if (product.getCompany() != null)
                p.setCompany(product.getCompany());
            if (product.getName() != null)
                p.setName(product.getName());
            if (product.getQuantity() != 0)
                p.setQuantity(product.getQuantity());
            if (product.getPrice() != null)
                p.setPrice(product.getPrice());
            return new ResponseEntity<>(productRepository.save(p), HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    @ApiOperation(value = "add product details", notes = "add product details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Product> createProduct(@RequestParam String name,
                                                 @RequestParam String description,
                                                 @RequestParam BigDecimal price,
                                                 @RequestParam UUID categoryId) {
        return ResponseEntity.ok(service.createProduct(name, description, price, categoryId));
    }

    @GetMapping
    @ApiOperation(value = "get product details", notes = "get product details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @PatchMapping("/{id}/price")
    @ApiOperation(value = "add product details", notes = "add product details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Void> updateProductPrice(@PathVariable UUID id, @RequestParam BigDecimal newPrice) {
        log.info("update the product price");
        service.updateProductPrice(id, newPrice);
        return ResponseEntity.noContent().build();
    }

}
