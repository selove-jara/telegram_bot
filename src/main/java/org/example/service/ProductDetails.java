package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.ProductDTO;
import org.example.DTO.Root;
import org.example.DTO.Size;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Сервис для получения и сохранения подробной информации о продукте по ID.
 * Выполняет запросы к внешнему API Wildberries, сохраняет или обновляет данные о продуктах,
 * а также периодически проверяет изменения цен.
 */
@Slf4j
@Service
public class ProductDetails {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ImageDownloader imageDownloader;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Autowired
    public ProductDetails(ImageDownloader imageDownloader, ProductRepository productRepository, ProductService productService) {
        this.imageDownloader = imageDownloader;
        this.productRepository = productRepository;
        this.productService = productService;
    }

    public void parseProduct(Long idProduct) {
        String urlString = String.format(
                "https://card.wb.ru/cards/v2/detail?dest=-1257786&nm=%s",
                idProduct
        );
        try {
            String jsonResponse = fetchJsonFromUrl(urlString);
            Root root = objectMapper.readValue(jsonResponse, Root.class);

            if (!root.getData().getProducts().isEmpty()) {
                productSave(root.getData().getProducts());
            }
        } catch (IOException e) {
            log.error("Ошибка при обработке продукта {}: {}", idProduct, e.getMessage(), e);
        }
    }

    private String fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }

    public void productSave(List<ProductDTO> productDTOs) {
        for (ProductDTO productDTO : productDTOs) {
            try {
                Product product = convertToProduct(productDTO);

                Product existingProduct = productRepository.findById(product.getId()).orElse(null);

                if (existingProduct != null) {
                    // Условие: цена снизилась на 5% или больше, и товара в наличии >= 3, и цена не нулевая
                    if (product.getProduct() <= existingProduct.getProduct() * 0.95 && product.getTotalQuantity() >= 3 && product.getProduct() != 0) {
                     log.info("Цена продукта {} обновлена. Старая цена: {}, Новая цена: {}. Флаг posted снят.",
                                                      existingProduct.getId(), existingProduct.getProduct(), product.getProduct());
                        existingProduct.setProduct(product.getProduct());
                        existingProduct.setTotalQuantity(product.getTotalQuantity());
                        existingProduct.setPosted(false);
                        productService.save(existingProduct);
                        // Условие: цена выросла
                    } else if (existingProduct.getProduct() < product.getProduct()) {
                        log.info("Цена продукта {} увеличилась. Старая цена: {}, Новая цена: {}. Устанавливаем флаг posted.",
                                existingProduct.getId(), existingProduct.getProduct(), product.getProduct());
                        existingProduct.setTotalQuantity(product.getTotalQuantity());
                        existingProduct.setPosted(true);
                        productService.save(existingProduct);
                        // Условие: товар закончился или цена нулевая
                    } else if (product.getTotalQuantity() == 0 || product.getProduct() == 0) {
                        existingProduct.setTotalQuantity(product.getTotalQuantity());
                        existingProduct.setPosted(true);
                        productService.save(existingProduct);
                    } else {
                        // Цена и количество не изменились
                        log.info("Цена продукта {} осталась неизменной: {}. Проверка других параметров...",
                                existingProduct.getId(), existingProduct.getProduct());
                    }
                } else {
                    // Новый товар: загружаем изображение и сохраняем
                    product.setImagePath(imageDownloader.downloadImage(product.getId()));
                    productService.save(product);
                    log.info("Продукт {} сохранен в БДД.", product.getId());
                }
            } catch (Exception e) {
                log.error("Ошибка при сохранении продукта {}: {}", productDTO.getId(), e.getMessage(), e);
            }
        }
    }

    private Product convertToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setTotalQuantity(productDTO.getTotalQuantity());

        if (productDTO.getSizes() != null && !productDTO.getSizes().isEmpty()) {
            Size size = productDTO.getSizes().get(0);
            if (size.getPrice() != null) {
                product.setBasic(size.getPrice().getBasic() / 100);
                product.setProduct(size.getPrice().getProduct() / 100);
            }
        }


        product.setPosted(false);

        return product;
    }

    /**
     * Периодическая проверка всех товаров из БД.
     * Запускается каждые 2 часа.
     */
   @Scheduled(fixedRate = 2 * 60 * 60 * 1000)
    public void checkPricesPeriodically() {
        log.info("Запуск периодической проверки цен товаров...");

        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            parseProduct(product.getId());
        }

        log.info("Периодическая проверка цен товаров завершена.");
    }
}