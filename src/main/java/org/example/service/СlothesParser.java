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
public class СlothesParser {

    private final Set<Categories> categories = new HashSet<>(Arrays.asList(
            new Categories(306, "blackhole"), new Categories(9977, "hand_accessories1"), new Categories(9971, "clothes_accessories3"), new Categories(8126, "bl_shirts"), new Categories(8127, "pants"), new Categories(63010, "outwear1"), new Categories(8130, "jumpers_cardigans"), new Categories(8131, "jeans"), new Categories(8133, "overalls"), new Categories(8134, "costumes"), new Categories(9411, "sweatshirts_hoodies"), new Categories(8136, "blazers_wamuses"), new Categories(8137, "dresses"), new Categories(8140, "sweatshirts_hoodies"), new Categories(8141, "sweatshirts_hoodies"), new Categories(8142, "preset_tops_tshirts1"), new Categories(128996, "women_bathrobes"), new Categories(10567, "shorts"), new Categories(8143, "skirts"), new Categories(349, "blackhole"), new Categories(350, "moms1"), new Categories(354, "moms1"), new Categories(355, "moms1"), new Categories(356, "moms1"), new Categories(357, "moms2"), new Categories(358, "moms2"), new Categories(359, "moms3"), new Categories(360, "moms2"), new Categories(361, "moms2"), new Categories(362, "moms2"), new Categories(9214, "moms2"), new Categories(363, "moms1"), new Categories(364, "moms3"), new Categories(365, "moms2"), new Categories(8135, "blackhole"), new Categories(128987, "women_homewear1"), new Categories(128988, "women_homewear1"), new Categories(128989, "women_homewear1"), new Categories(128990, "women_homewear1"), new Categories(128991, "women_homewear1"), new Categories(128992, "women_homewear1"), new Categories(128993, "women_homewear2"), new Categories(128994, "women_homewear2"), new Categories(128995, "women_homewear2"), new Categories(10460, "blackhole"), new Categories(10462, "office_bigroot2"), new Categories(10461, "office_bigroot1"), new Categories(10464, "office_bigroot1"), new Categories(10465, "office_bigroot1"), new Categories(10467, "office_bigroot1"), new Categories(10466, "office_bigroot1"), new Categories(8533, "office_bigroot2"), new Categories(10470, "office_bigroot1"), new Categories(10468, "office_bigroot1"), new Categories(10043, "beach1"), new Categories(10045, "beach1"), new Categories(10044, "beach1"), new Categories(10046, "beach1"), new Categories(629, "blackhole"), new Categories(128330, "children_shoes"), new Categories(631, "children_shoes"), new Categories(645, "children_shoes"), new Categories(8225, "children_shoes"), new Categories(128329, "blackhole"), new Categories(8177, "women_shoes1"), new Categories(8178, "women_shoes1"), new Categories(8176, "women_shoes1"), new Categories(8182, "women_shoes2"), new Categories(62281, "women_shoes2"), new Categories(8206, "women_shoes2"), new Categories(8186, "women_shoes3"), new Categories(8187, "women_shoes3"), new Categories(8188, "women_shoes3"), new Categories(8175, "women_shoes3"), new Categories(751, "men_shoes"), new Categories(8191, "men_shoes"), new Categories(8194, "men_shoes"), new Categories(62282, "men_shoes"), new Categories(8199, "men_shoes"), new Categories(8201, "men_shoes"), new Categories(8202, "men_shoes"), new Categories(8200, "men_shoes"), new Categories(62875, "shoes_accessories1"), new Categories(131715, "shoes_accessories1"), new Categories(131716, "shoes_accessories1"), new Categories(131717, "shoes_accessories1"), new Categories(131718, "shoes_accessories1"), new Categories(131721, "shoes_accessories1"), new Categories(131719, "shoes_accessories1"),new Categories(131720, "shoes_accessories1"), new Categories(131722, "shoes_accessories1"), new Categories(131723, "shoes_accessories1"), new Categories(131724, "shoes_accessories1"), new Categories(131725, "shoes_accessories1"), new Categories(131726, "shoes_accessories1"), new Categories(131727, "shoes_accessories1"), new Categories(131728, "shoes_accessories1"), new Categories(115, "blackhole"), new Categories(146, "blackhole"), new Categories(8310, "children_girls1"), new Categories(8311, "children_girls1"), new Categories(63083, "children_girls1"), new Categories(8314, "children_girls1"), new Categories(8315, "children_girls1"), new Categories(8316, "children_girls1"), new Categories(8313, "children_girls2"), new Categories(8317, "children_girls2"), new Categories(8318, "children_girls2"), new Categories(8320, "children_girls2"), new Categories(10057, "children_girls2"), new Categories(9413, "children_girls2"), new Categories(128535, "children_girls2"), new Categories(8321, "children_girls4"), new Categories(130226, "children_girls4"), new Categories(8322, "children_girls3"), new Categories(8323, "children_girls3"), new Categories(8325, "children_girls3"), new Categories(8326, "children_girls3"), new Categories(8327, "children_girls3"), new Categories(8469, "children_girls3"), new Categories(8328, "children_girls3"), new Categories(130665, "children_girls3"), new Categories(8883, "children_girls3"), new Categories(8329, "children_girls3"), new Categories(182, "blackhole"), new Categories(8334, "children_boys1"), new Categories(63082, "children_boys1"), new Categories(8344, "children_boys1"), new Categories(8337, "children_boys3"), new Categories(8335, "children_boys2"), new Categories(8341, "children_boys1"), new Categories(8347, "children_boys1"), new Categories(8345, "children_boys1"), new Categories(8339, "children_boys3"), new Categories(9414, "children_boys2"), new Categories(128536, "children_boys2"), new Categories(8343, "children_boys4"), new Categories(130266, "children_boys4"), new Categories(130267, "children_boys4"), new Categories(130268, "children_boys4"), new Categories(130269, "children_boys4"), new Categories(130270, "children_boys4"), new Categories(130271, "children_boys4"), new Categories(130272, "children_boys4"), new Categories(130273, "children_boys4"), new Categories(130274, "children_boys4"), new Categories(8340, "children_boys1"), new Categories(10058, "children_boys3"), new Categories(8333, "children_boys2"), new Categories(8342, "children_boys2"), new Categories(8338, "children_boys3"), new Categories(8470, "children_boys1"), new Categories(8332, "children_boys3"), new Categories(8884, "children_boys3"), new Categories(199, "babies1"), new Categories(61310, "babies1"), new Categories(201, "babies1"), new Categories(202, "babies1"), new Categories(63081, "babies1"), new Categories(212, "babies1"), new Categories(204, "babies1"), new Categories(209, "babies1"), new Categories(208, "babies1"), new Categories(211, "babies1"), new Categories(214, "babies1"), new Categories(130690, "children_things2"), new Categories(128533, "babies1"), new Categories(128534, "babies1"), new Categories(216, "babies1"), new Categories(8558, "babies1"), new Categories(218, "babies1"), new Categories(130666, "babies1"), new Categories(58522, "electronic36"), new Categories(60806, "toys1"), new Categories(131395, "blackhole"), new Categories(131396, "blackhole"), new Categories(131397, "babies_room1"), new Categories(131398, "babies_room1"), new Categories(131407, "babies_room1"), new Categories(131408, "babies_room1"), new Categories(131409, "babies_room1"), new Categories(131433, "babies_room1"), new Categories(131399, "babies_room1"), new Categories(131410, "babies_room1"), new Categories(131403, "babies_room1"), new Categories(131404, "blackhole"), new Categories(131434, "babies_room1"), new Categories(131435, "babies_room1"), new Categories(131436, "babies_room1"), new Categories(131437, "babies_room1"), new Categories(131438, "babies_room1"), new Categories(131405, "babies_room1"), new Categories(131411, "babies_room1"), new Categories(131431, "babies_room1"), new Categories(131432, "babies_room1"), new Categories(131412, "babies_room1"), new Categories(131413, "babies_room1"), new Categories(131414, "babies_room1"), new Categories(131415, "babies_room1"), new Categories(131416, "babies_room1"), new Categories(131417, "babies_room1"), new Categories(131439, "babies_room1"), new Categories(131418, "babies_room1"), new Categories(131419, "babies_room1"), new Categories(131420, "babies_room1"), new Categories(131440, "babies_room1"), new Categories(131441, "babies_room1"), new Categories(131442, "babies_room1"), new Categories(131443, "babies_room1"), new Categories(131444, "babies_room1"), new Categories(131445, "babies_room1"), new Categories(131446, "babies_room1"), new Categories(131447, "babies_room1"), new Categories(131448, "babies_room1"), new Categories(131449, "babies_room1"), new Categories(131421, "blackhole"), new Categories(131422, "babies_room2"), new Categories(131423, "babies_room2"), new Categories(131424, "babies_room2"), new Categories(131425, "babies_room2"), new Categories(131426, "babies_room2"), new Categories(131427, "babies_room2"), new Categories(131428, "babies_room2"), new Categories(131429, "babies_room2"), new Categories(131430, "babies_room2"), new Categories(243, "children_things3"), new Categories(8098, "children_things3"), new Categories(244, "children_things3"), new Categories(9961, "children_things3"), new Categories(8100, "children_things3"), new Categories(249, "children_things3"), new Categories(7107, "children_things2"), new Categories(8841, "blackhole"), new Categories(130604, "gift35"), new Categories(130609, "gift36"), new Categories(130603, "gift11"), new Categories(130612, "gift35"), new Categories(130605, "gift12"), new Categories(130613, "gift12"), new Categories(130610, "gift12"), new Categories(130608, "gift12"), new Categories(130606, "gift12"), new Categories(130611, "gift12"), new Categories(566, "blackhole"), new Categories(8144, "men_clothes1"), new Categories(63011, "men_clothes1"), new Categories(8148, "men_clothes2"), new Categories(8149, "men_clothes2"), new Categories(8152, "men_clothes2"), new Categories(8153, "men_clothes2"), new Categories(9412, "men_clothes3"), new Categories(129176, "men_clothes3"), new Categories(8155, "men_clothes3"), new Categories(129258, "men_clothes3"), new Categories(8156, "men_clothes3"), new Categories(8158, "men_clothes4"), new Categories(8159, "preset_men_clothes6"), new Categories(129257, "men_clothes5"), new Categories(129172, "men_clothes5"), new Categories(11428, "men_clothes5"), new Categories(8154, "men_clothes8"), new Categories(10471, "men_clothes5"), new Categories(10039, "men_mixtape4"), new Categories(914, "sport13"), new Categories(60811, "sport13"), new Categories(58639, "sport13"), new Categories(10065, "sport13"), new Categories(9099, "sport14"), new Categories(62165, "sport33"), new Categories(62183, "sport20"), new Categories(62188, "sport20"), new Categories(62228, "sport20"), new Categories(875, "sport29"), new Categories(879, "sport29"), new Categories(60062, "sport24"), new Categories(60068, "sport23"), new Categories(809, "sport23"), new Categories(63055, "sport23")
    ));

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PriceHistoryService priceHistoryService;
    private final ImageDownloader imageDownloader;
    private final ProductService productService;

    @Autowired
    public СlothesParser(PriceHistoryService priceHistoryService, ImageDownloader imageDownloader, ProductService productService) {
        this.priceHistoryService = priceHistoryService;
        this.imageDownloader = imageDownloader;
        this.productService = productService;
    }

    @Async
    public CompletableFuture<Void> parseCategory() {
        while (true) { // Бесконечный цикл
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
                        log.error("Ошибка при обработке категории одежды {}: {}", category.getId(), e.getMessage());
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
                log.warn("История цен недоступна для товара из категории одежды {}. Пропускаем.", product.getId());
                continue;
            }

            int sale = (int) (((double) (product.getOldPrice() - product.getProduct()) / product.getOldPrice()) * 100);

            if (sale > 50 && product.getTotalQuantity() > 3 && product.getProduct() < product.getOldPrice() && product.getOldPrice() != 0) {
                String imagePath = imageDownloader.downloadImage(product.getId());
                if (imagePath != null) {
                    product.setImagePath(imagePath);
                }
                productService.saveNewProduct(product);
            } else {
                log.warn("Условия не подходят для одежды {}. Пропускаем.", product.getId());
            }
        }
    }
}


