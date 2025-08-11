package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceParserService parserService;

    public Product saveProduct(String url, User user) {
        PriceParserService.PriceInfo priceInfo = parserService.parseprice(url);

        Product product = new Product();
        product.setProductUrl(url);
        product.setPrice(priceInfo.price());
        product.setOriginalPrice(priceInfo.originalPrice());
        product.setProductName(priceInfo.title());
        product.setUser(user);

        return productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public void updateProduct(Product product) {
        productRepository.save(product);
    }
}
