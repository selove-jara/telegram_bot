package org.example.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.ProductDTO;
import org.example.DTO.Root;
import org.example.DTO.Size;
import org.example.model.Categories;
import org.example.model.Product;
import org.example.repository.CategoriesRepository;
import org.example.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService implements CommandLineRunner {

    private final CategoriesService categoriesService; // Зависимость от CategoriesService
    private final CategoriesRepository categoriesRepository;
    private final ProductRepository productRepository; // Репозиторий для сохранения товаров
    private final PriceHistoryService priceHistoryService; // Сервис для работы с историей цен
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ImageDownloader imageDownloader;

    @Override
    public void run(String... args) throws Exception {
        parseAllCategories();
    }

    public void parseAllCategories() {
        List<Categories> categories = categoriesRepository.findAll();

        // Перемешиваем список категорий
        Collections.shuffle(categories);

        for (Categories category : categories) {
            parseCategory(category);
        }
    }

    private void parseCategory(Categories category) {
        int page = 1;
        boolean hasMorePages = true;

        // Проверяем, что shard не равен null
        if (category.getShard() == null) {
            log.warn("Категория {} имеет shard=null. Пропускаем.", category.getId());
            return; // Пропускаем категорию, если shard равен null
        }

        while (hasMorePages) {
            String urlString = String.format(
                    "https://catalog.wb.ru/catalog/%s/v2/catalog?ab_testing=false&appType=1&cat=%d&curr=rub&dest=-1257786&hide_dtype=10&lang=ru&page=%d&sort=benefit&spp=30",
                    category.getShard(), category.getId(), page
            );

            try {
                String jsonResponse = fetchJsonFromUrl(urlString);
                Root root = objectMapper.readValue(jsonResponse, Root.class);

                if (root.getData().getProducts().isEmpty()) {
                    hasMorePages = false; // Нет товаров на странице, завершаем пагинацию
                } else {
                    processProducts(root.getData().getProducts());
                    page++; // Переход к следующей странице
                }
            } catch (IOException e) {
                log.error("Ошибка при обработке категории {}: {}", category.getId(), e.getMessage());
                hasMorePages = false; // Прерываем пагинацию в случае ошибки
            }
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

    private void processProducts(List<ProductDTO> products) {
        for (ProductDTO productDTO : products) {

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

            int oldPric = priceHistoryService.getPriceHistory(product.getId());
            product.setOldPrice(oldPric / 100);
            if (oldPric == 0) {
                log.warn("История цен недоступна для товара {}. Пропускаем.", product.getId());
                continue; // Пропускаем товар, если история цен недоступна
            }

            int sale = (int) (((double) (product.getOldPrice() - product.getProduct()) / product.getOldPrice()) * 100);

            if (sale > 40 && product.getTotalQuantity() > 5 && product.getProduct() < product.getOldPrice() && product.getOldPrice() != 0) {
                String imagePath = imageDownloader.downloadImage(product.getId());
                if (imagePath != null) {
                    product.setImagePath(imagePath); // Сохраняем путь к изображению
                }
                if (productRepository.existsById(productDTO.getId())) {
                    log.info("Товар с ID {} уже существует в базе данных. Пропускаем.", productDTO.getId());
                    continue; // Пропускаем сохранение, если товар уже существует
                }
                productRepository.save(product);
            } else {
                log.warn("Условия не подходят {}. Пропускаем.", product.getId());
            }
        }
    }
}
