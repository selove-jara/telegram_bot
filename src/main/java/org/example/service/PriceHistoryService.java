package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.PriceHistoryEntry;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceHistoryService {

    private final ObjectMapper objectMapper;
    private final UrlGenerator urlGenerator;

    /**
     * Получает историю цен для продукта по его ID.
     *
     * @param productId ID продукта
     * @return Последняя стоимость товара или 0, если история цен недоступна
     */
    public int getPriceHistory(long productId) {
        var averagePrice = 0;

        try {
            String url = urlGenerator.generatePriceUrl(productId);
            String jsonResponse = fetchJsonFromUrl(url);

            if (jsonResponse == null) {
                log.warn("История цен для продукта {} недоступна (404)", productId);
                return averagePrice; // Возвращаем 0, если история цен недоступна
            }

            List<PriceHistoryEntry> entries = objectMapper.readValue(jsonResponse, new TypeReference<List<PriceHistoryEntry>>() {});
            if (entries != null && !entries.isEmpty() && entries.size() > 3) {
                int totalPrices = 0;
                int count = 0;

                // Суммируем все цены
                for (PriceHistoryEntry entry : entries) {
                    totalPrices += entry.getPrice().getRUB();
                    count++;
                }

                // Вычисляем среднюю цену
                if (count > 0) {
                    averagePrice = totalPrices / count;
                    log.debug("Средняя цена: {}", averagePrice);
                }
            }
        } catch (IOException e) {
            log.error("Ошибка при получении истории цен для продукта {}: {}", productId, e.getMessage());
        }

        return averagePrice;
    }

    private String fetchJsonFromUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return null; // Возвращаем null, если товар не найден
        }

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
}