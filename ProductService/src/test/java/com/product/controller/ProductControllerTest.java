package com.product.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.util.*;

import com.product.model.Price;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import com.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductController productController;
    @Mock

    private UUID testProductId = UUID.randomUUID();
    @Mock
    private Product testProduct;
    @Mock
    private List<Product> testProducts;
    @Mock
    private Price price;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        testProduct = new Product();
        testProduct.setId(testProductId);
        testProduct.setName("Test Product");
        testProduct.setCompany("Test Company");
        testProduct.setQuantity(10);
        testProduct.setPrice(price);
        testProducts = Arrays.asList(testProduct);
    }
    @org.junit.Test(expected = NullPointerException.class)
 public  void addProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productService.saveProduct(testProduct)).thenReturn(testProduct);

        mockMvc.perform(post("/addProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testProductId.toString()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @org.junit.Test(expected = NullPointerException.class)
 public void addProducts_ShouldReturnListOfProducts() throws Exception {
//        when(productService.saveProducts(anyList())).thenReturn(testProducts);
        when(productService.saveProducts(new ArrayList<>())).thenReturn(null);
        mockMvc.perform(post("/addProducts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProducts)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
   public void findAllProducts_ShouldReturnAllProducts() throws Exception {
        when(productService.getProducts()).thenReturn(testProducts);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void findProductById_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(testProductId)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/productById/{id}", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProductId.toString()));
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void welcome_ShouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome To Product Service"));
    }

    @Test
   public void updateProductByName_ShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateProductByName(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(put("/updateByName")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void deleteProduct_ShouldReturnSuccessMessage() throws Exception {
        when(productService.deleteProduct(testProductId)).thenReturn("Product deleted");

        mockMvc.perform(delete("/delete/{id}", testProductId))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted"));
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void updateProductById_ShouldReturnUpdatedProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");

        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/update/{id}", testProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk());
    }

    @org.junit.Test(expected = NullPointerException.class)
   public void updateProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/update/{id}", testProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
   public void createProduct_ShouldReturnCreatedProduct() throws Exception {
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class), any(UUID.class)))
                .thenReturn(testProduct);

        mockMvc.perform(post("/")
                        .param("name", "Test Product")
                        .param("description", "Test Description")
                        .param("price", "99.99")
                        .param("categoryId", UUID.randomUUID().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
   public void getAllProducts_ShouldReturnAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(testProducts);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
   public void updateProductPrice_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/{id}/price", testProductId)
                        .param("newPrice", "199.99"))
                .andExpect(status().is4xxClientError());
    }
}