package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Модель для записи истории изменения цены.
 * Используется для вычисления средней цены.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceHistoryEntry {
    private long dt;
    private Price price;
}

