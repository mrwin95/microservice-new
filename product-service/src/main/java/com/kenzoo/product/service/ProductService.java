package com.kenzoo.product.service;

import com.kenzoo.product.dto.ProductRequest;
import com.kenzoo.product.dto.ProductResponse;
import com.kenzoo.product.model.Product;
import com.kenzoo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info(format("Product is saved: %s ", product.getId()));
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
//        return products.stream().map(product -> mapToProductResponse(product)).collect(Collectors.toList());
        return products.stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
