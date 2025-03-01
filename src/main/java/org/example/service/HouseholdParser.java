package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.ProductDTO;
import org.example.DTO.Root;
import org.example.DTO.Size;
import org.example.model.Categories;
import org.example.model.Product;
import org.example.repository.CategoryParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class HouseholdParser {

    private final Set<Categories> categories = new HashSet<>(Arrays.asList(
            new Categories(258, "blackhole"), new Categories(259, "blackhole"), new Categories(260, "bathroom3"), new Categories(60456, "bathroom3"), new Categories(261, "bathroom3"), new Categories(60454, "bathroom3"), new Categories(128426, "bathroom3"), new Categories(262, "bathroom2"), new Categories(269, "blackhole"), new Categories(8708, "kitchen22"), new Categories(8709, "kitchen22"), new Categories(8710, "kitchen23"), new Categories(8701, "kitchen22"), new Categories(8711, "kitchen16"), new Categories(61239, "kitchen5"), new Categories(8702, "kitchen16"), new Categories(8703, "kitchen16"), new Categories(8704, "kitchen16"), new Categories(8712, "blackhole"), new Categories(130076, "kitchen9"), new Categories(130077, "kitchen9"), new Categories(130078, "kitchen9"), new Categories(130079, "kitchen10"), new Categories(130080, "kitchen9"), new Categories(130081, "kitchen11"), new Categories(130508, "kitchen12"), new Categories(130084, "kitchen9"), new Categories(130082, "kitchen9"), new Categories(130507, "kitchen9"), new Categories(130083, "kitchen9"), new Categories(130505, "kitchen9"), new Categories(130506, "kitchen13"), new Categories(130510, "kitchen9"), new Categories(130481, "kitchen9"), new Categories(8705, "kitchen24"), new Categories(8720, "kitchen22"), new Categories(8721, "blackhole"), new Categories(130073, "kitchen7"), new Categories(130074, "kitchen7"), new Categories(130072, "kitchen7"), new Categories(130066, "kitchen7"), new Categories(130070, "kitchen7"), new Categories(130071, "kitchen7"), new Categories(130069, "kitchen7"), new Categories(130065, "kitchen7"), new Categories(130068, "kitchen8"), new Categories(130067, "kitchen8"), new Categories(130075, "kitchen8"), new Categories(8706, "kitchen18"), new Categories(12861, "kitchen18"), new Categories(8722, "kitchen18"), new Categories(8707, "kitchen18"), new Categories(8713, "kitchen18"), new Categories(8714, "kitchen18"), new Categories(9566, "kitchen18"), new Categories(8716, "kitchen18"), new Categories(8717, "kitchen19"), new Categories(8724, "kitchen19"), new Categories(8049, "kitchen19"), new Categories(8718, "kitchen19"), new Categories(8728, "kitchen19"), new Categories(60598, "blackhole"), new Categories(62855, "bedroom2"), new Categories(8466, "bedroom3"), new Categories(60599, "interior3"), new Categories(60600, "bedroom3"), new Categories(60608, "bedroom2"), new Categories(12811, "bedroom3"), new Categories(60611, "bedroom3"), new Categories(287, "blackhole"), new Categories(130088, "bedding2"), new Categories(130509, "bedding2"), new Categories(130091, "bedding2"), new Categories(130092, "bedding2"), new Categories(130094, "bedding2"), new Categories(130096, "bedding2"), new Categories(130098, "bedding2"), new Categories(130100, "bedding2"), new Categories(130102, "bedding3"), new Categories(130104, "bedding3"), new Categories(130105, "bedding3"), new Categories(130108, "bedding2"), new Categories(60619, "bedroom5"), new Categories(60624, "blackhole"), new Categories(8421, "livingroom5"), new Categories(299, "livingroom5"), new Categories(60625, "livingroom6"), new Categories(60626, "livingroom6"), new Categories(60627, "livingroom6"), new Categories(60638, "livingroom6"), new Categories(305, "livingroom7"), new Categories(60545, "blackhole"), new Categories(60578, "children_room8"), new Categories(60562, "children_room8"), new Categories(60558, "children_room8"), new Categories(62079, "children_room6"), new Categories(62159, "children_room6"), new Categories(60546, "babies_room1"), new Categories(128546, "children_room3"), new Categories(60583, "children_room7"), new Categories(60589, "children_room7"), new Categories(62826, "interior3"), new Categories(8459, "interior3"), new Categories(10027, "housecraft6"), new Categories(9203, "housecraft_bigroot1"), new Categories(9204, "housecraft_bigroot1"), new Categories(9206, "housecraft_bigroot1"), new Categories(9205, "housecraft_bigroot1"), new Categories(9207, "housecraft_bigroot1"), new Categories(10395, "housecraft_bigroot1"), new Categories(9183, "interior3"), new Categories(7643, "blackhole"), new Categories(9726, "housecraft8"), new Categories(9683, "housecraft8"), new Categories(9695, "housecraft8"), new Categories(9734, "housecraft8"), new Categories(9735, "housecraft8"), new Categories(9725, "housecraft8"), new Categories(9694, "housecraft8"), new Categories(9733, "housecraft8"), new Categories(9730, "housecraft8"), new Categories(9731, "housecraft8"), new Categories(9727, "housecraft8"), new Categories(128301, "housecraft8"), new Categories(9696, "housecraft9"), new Categories(9693, "housecraft9"), new Categories(9692, "housecraft8"), new Categories(9715, "housecraft8"), new Categories(9713, "housecraft8"), new Categories(13149, "housecraft8"), new Categories(128302, "housecraft8"), new Categories(63064, "housecraft8"), new Categories(13152, "housecraft8"), new Categories(9716, "housecraft8"), new Categories(13154, "housecraft8"), new Categories(13156, "housecraft8"), new Categories(13157, "housecraft8"), new Categories(9704, "housecraft8"), new Categories(13158, "housecraft8"), new Categories(13161, "housecraft8"), new Categories(9722, "housecraft9"), new Categories(9697, "housecraft9"), new Categories(9723, "housecraft9"), new Categories(62506, "housecraft12"), new Categories(128304, "housecraft12"), new Categories(62508, "housecraft12"), new Categories(131385, "housecraft12"), new Categories(23896, "housecraft12"), new Categories(23897, "housecraft12"), new Categories(23900, "housecraft12"), new Categories(131386, "housecraft12"), new Categories(62516, "housecraft12"), new Categories(62523, "housecraft12"), new Categories(23906, "housecraft12"), new Categories(128303, "housecraft12"), new Categories(62514, "housecraft12"), new Categories(63075, "housecraft12"), new Categories(23915, "housecraft12"), new Categories(23917, "housecraft12"), new Categories(130672, "hallway1"), new Categories(304, "housecraft12"), new Categories(295, "interior4"), new Categories(12838, "interior4"), new Categories(130656, "interior4"), new Categories(12841, "interior4"), new Categories(12842, "interior4"), new Categories(12844, "interior4"), new Categories(62836, "blackhole"), new Categories(130831, "housecraft13"), new Categories(130836, "housecraft11"), new Categories(130835, "housecraft11"), new Categories(130837, "housecraft11"), new Categories(130832, "blackhole"), new Categories(131034, "housecraft14"), new Categories(131035, "housecraft14"), new Categories(131036, "housecraft15"), new Categories(131084, "housecraft15"), new Categories(130833, "housecraft13"), new Categories(130834, "housecraft11"), new Categories(543, "blackhole"), new Categories(4872, "beauty7"), new Categories(63002, "beauty7"), new Categories(63003, "beauty7"), new Categories(62998, "beauty7"), new Categories(62999, "beauty7"), new Categories(63005, "beauty7"), new Categories(8987, "beauty7"), new Categories(63001, "beauty7"), new Categories(63004, "beauty7"), new Categories(8961, "blackhole"), new Categories(8989, "beauty26"), new Categories(59872, "beauty26"), new Categories(8965, "beauty26"), new Categories(8963, "beauty26"), new Categories(8962, "beauty26"), new Categories(8964, "beauty26"), new Categories(9454, "beauty1"), new Categories(8988, "beauty9"), new Categories(6837, "beauty9"), new Categories(60751, "beauty1"), new Categories(58217, "koreancosmetics"), new Categories(59744, "appliances1"), new Categories(8924, "beauty13"), new Categories(8938, "beauty25"), new Categories(8935, "beauty25"), new Categories(8944, "beauty12"), new Categories(8925, "beauty12"), new Categories(8999, "beauty14"), new Categories(23968, "beauty12"), new Categories(9645, "beauty15"), new Categories(8998, "beauty15"), new Categories(23969, "beauty15"), new Categories(130482, "beauty46"), new Categories(10012, "beauty45"), new Categories(563, "blackhole"), new Categories(9232, "beauty36"), new Categories(9000, "blackhole"), new Categories(130965, "beauty29"), new Categories(130966, "beauty29"), new Categories(130967, "beauty30"), new Categories(130968, "beauty30"), new Categories(130969, "beauty31"), new Categories(130970, "beauty31"), new Categories(130971, "beauty31"), new Categories(130974, "beauty32"), new Categories(130975, "beauty33"), new Categories(130976, "beauty34"), new Categories(130977, "beauty34"), new Categories(130978, "beauty34"), new Categories(9001, "blackhole"), new Categories(130979, "beauty38"), new Categories(130980, "beauty38"), new Categories(130981, "beauty37"), new Categories(130982, "beauty38"), new Categories(130983, "beauty38"), new Categories(130984, "beauty38"), new Categories(130985, "beauty39"), new Categories(130986, "beauty39"), new Categories(130987, "beauty40"), new Categories(130988, "beauty38"), new Categories(130989, "beauty37"), new Categories(130990, "beauty41"), new Categories(130972, "beauty35"), new Categories(130973, "beauty35"), new Categories(59860, "beauty46"), new Categories(7036, "beauty47"), new Categories(8997, "beauty46"), new Categories(8996, "beauty46"), new Categories(8967, "blackhole"), new Categories(8990, "beauty19"), new Categories(8971, "beauty20"), new Categories(8968, "beauty20"), new Categories(62355, "beauty20"), new Categories(8973, "beauty21"), new Categories(8985, "beauty21"), new Categories(8986, "beauty23"), new Categories(8976, "beauty22"), new Categories(8975, "beauty23"), new Categories(8974, "beauty24"), new Categories(8972, "beauty24"), new Categories(1, "blackhole"), new Categories(9965, "head_accessories1"), new Categories(9966, "blackhole"), new Categories(130484, "bijouterie1"), new Categories(130485, "bijouterie1"), new Categories(130486, "bijouterie2"), new Categories(130487, "bijouterie1"), new Categories(130488, "bijouterie1"), new Categories(130489, "bijouterie1"), new Categories(130490, "bijouterie1"), new Categories(130491, "bijouterie1"), new Categories(130492, "bijouterie1"), new Categories(130493, "bijouterie3"), new Categories(130494, "bijouterie1"), new Categories(130495, "bijouterie3"), new Categories(130964, "bijouterie3"), new Categories(130496, "bijouterie3"), new Categories(130497, "bijouterie4"), new Categories(130498, "bijouterie4"), new Categories(130499, "bijouterie4"), new Categories(130500, "bijouterie4"), new Categories(130501, "bijouterie4"), new Categories(130502, "bijouterie4"), new Categories(10023, "hand_accessories2"), new Categories(9980, "clothes_accessories2"), new Categories(9967, "head_accessories3"), new Categories(9979, "hand_accessories2"), new Categories(9974, "hand_accessories2"), new Categories(9973, "hand_accessories1"), new Categories(9982, "head_accessories1"), new Categories(9978, "hand_accessories1"), new Categories(9972, "clothes_accessories1"), new Categories(9988, "clothes_accessories1"), new Categories(9987, "clothes_accessories1"), new Categories(58328, "clothes_accessories1"), new Categories(58327, "clothes_accessories1"), new Categories(9986, "clothes_accessories1"), new Categories(131364, "clothes_accessories1"), new Categories(9970, "clothes_accessories3"), new Categories(9969, "blackhole"), new Categories(16146, "bags1"), new Categories(8820, "bags1"), new Categories(9998, "bags3"), new Categories(10000, "bags1"), new Categories(10001, "bags3"), new Categories(62908, "bags1"), new Categories(10006, "bags2"), new Categories(8828, "bags3"), new Categories(10294, "bags3"), new Categories(10003, "bags3"), new Categories(9976, "hand_accessories2"), new Categories(62903, "bags1"), new Categories(4830, "blackhole"), new Categories(9835, "electronic37"), new Categories(9468, "electronic38"), new Categories(58513, "electronic36"), new Categories(15693, "electronic36"), new Categories(130772, "electronic36"), new Categories(61808, "electronic36"), new Categories(9509, "electronic36"), new Categories(15692, "electronic36"), new Categories(10491, "electronic36"), new Categories(59132, "electronic39"), new Categories(128516, "books3"), new Categories(9455, "blackhole"), new Categories(481, "blackhole"), new Categories(482, "toys1"), new Categories(15302, "toys1"), new Categories(15308, "toys1"), new Categories(16149, "toys1"), new Categories(9174, "toys1"), new Categories(15310, "toys1")
    ));

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PriceHistoryService priceHistoryService;
    private final ProductService productService;

    @Autowired
    public HouseholdParser(PriceHistoryService priceHistoryService, ProductService productService) {
        this.priceHistoryService = priceHistoryService;
        this.productService = productService;
    }

    @Async
    public CompletableFuture<Void> parseCategory() {
        while (true) {
            List<Categories> categoryList = new ArrayList<>(categories);
            Collections.shuffle(categoryList);

            for (Categories category : categoryList) {
                int page = 1;
                boolean hasMorePages = true;

                while (hasMorePages) {
                    String urlString = String.format(
                            "https://catalog.wb.ru/catalog/%s/v2/catalog?ab_testing=false&appType=1&cat=%d&curr=rub&dest=-1257786&hide_dtype=10&lang=ru&page=%d&sort=benefit&spp=30",
                            category.getShard(), category.getId(), page
                    );

                    try {
                        String jsonResponse = fetchJsonFromUrl(urlString);
                        Root root = objectMapper.readValue(jsonResponse, Root.class);

                        if (root.getData().getProducts().isEmpty()) {
                            hasMorePages = false;
                        } else {
                            processProducts(root.getData().getProducts());
                            page++;
                        }
                    } catch (IOException e) {
                        log.error("Ошибка при обработке категории предметов {}: {}", category.getId(), e.getMessage());
                        hasMorePages = false;
                    }
                }
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

    private List<Product> processProducts(List<ProductDTO> products) {
        List<Product> result = new ArrayList<>();

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
                log.warn("История цен недоступна для товара из категории предметов {}. Пропускаем.", product.getId());
                continue;
            }

            int sale = (int) (((double) (product.getOldPrice() - product.getProduct()) / product.getOldPrice()) * 100);

            if (sale > 40 && product.getTotalQuantity() > 10 && product.getProduct() < product.getOldPrice() && product.getOldPrice() != 0) {
                productService.saveNewProduct(product);
            } else {
                log.warn("Условия не подходят для предметов {}. Пропускаем.", product.getId());
            }
        }

        return result;
    }
}
