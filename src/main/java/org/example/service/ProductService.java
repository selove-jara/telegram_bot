package org.example.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public void save(Product product) {
        Product existingProduct = productRepository.findById(product.getId()).orElse(null);
        if (existingProduct == null) {
            productRepository.save(product);
            log.info("Товар {} сохранен в БД.", product.getId());
        } else {
            if (!existingProduct.isPosted() && existingProduct.getProduct() > product.getProduct()) {
                productRepository.save(product);
                log.info("Товар {} пересохранен в БД.", product.getId());
            } else {
                log.info("Товар {} уже существует в БД. Пропускаем.", product.getId());
            }
        }
    }
}