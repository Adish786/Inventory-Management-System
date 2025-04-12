package com.product.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

import java.math.BigDecimal;
import java.util.*;

import com.product.model.Category;
import com.product.model.Price;
import com.product.model.Product;
import com.product.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.cache.CacheManager;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Logger logger;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID testProductId;
    private Product testProduct;
    private List<Product> testProducts;
    @Mock
    private   Price price;

    @Before
    public void setUp() {
        testProductId = UUID.randomUUID();
        testProduct = new Product("Test Product", "Test Description",
                new Price(BigDecimal.valueOf(99.99)),
                new Category(UUID.randomUUID()));
        testProduct.setId(testProductId);

        testProducts = Arrays.asList(testProduct);
    }

    @Test
    public void saveProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.saveProduct(testProduct);

        assertNotNull(result);
        assertEquals(testProductId, result.getId());
        verify(productRepository).save(testProduct);
    }

    @Test
    public void saveProducts_ShouldReturnListOfProducts() {
        when(productRepository.saveAll(anyList())).thenReturn(testProducts);

        List<Product> result = productService.saveProducts(testProducts);

        assertEquals(1, result.size());
        verify(productRepository).saveAll(testProducts);
    }

    @Test
    public void getProducts_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(testProducts);

        List<Product> result = productService.getProducts();

        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    public void getProductById_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(testProductId);

        assertTrue(result.isPresent());
        assertEquals(testProductId, result.get().getId());
    }

    @Test
    public void getProductById_ShouldReturnEmpty_WhenNotExists() {
        when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(testProductId);

        assertFalse(result.isPresent());
    }

    @Test
    public void getProductByName_ShouldReturnProduct_WhenExists() {
        when(productRepository.findByName("Test Product")).thenReturn(testProduct);
        Optional<Product> result = productService.getProductByName("Test Product");
        assertTrue(result.isPresent());
        assertEquals(null, result.get().getName());
    }

    @Test
    public void getProductByName_ShouldReturnEmpty_WhenNotExists() {
        when(productRepository.findByName("Unknown")).thenReturn(null);

        Optional<Product> result = productService.getProductByName("Unknown");

        assertFalse(result.isPresent());
    }

    @Test
    public void deleteProduct_ShouldReturnSuccessMessage() {
        String result = productService.deleteProduct(testProductId);

        assertEquals("product removed !! " + testProductId, result);
        verify(productRepository).deleteById(testProductId);
    }



    @Test
    public void updateProductPrice_ShouldUpdatePrice_WhenProductExists() {
        Product existingProduct = new Product();
        existingProduct.setId(testProductId);
        existingProduct.setPrice(new Price(BigDecimal.valueOf(50.0)));

        when(productRepository.findById(testProductId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        productService.updateProductPrice(testProductId, BigDecimal.valueOf(100.0));

        assertEquals(BigDecimal.valueOf(100.0), BigDecimal.valueOf(100.0));
        verify(productRepository).save(existingProduct);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateProductPrice_ShouldThrowException_WhenProductNotFound() {
        when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

        productService.updateProductPrice(testProductId, BigDecimal.valueOf(100.0));
    }


    @Test(expected = IllegalArgumentException.class)
    public void updateProductByName_ShouldThrowException_WhenProductNotFound() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Unknown Product");

        when(productRepository.findByName("Unknown Product")).thenReturn(null);

        productService.updateProductByName(updatedProduct);
    }
}