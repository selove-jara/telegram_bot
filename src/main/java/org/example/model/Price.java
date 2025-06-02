package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Модель цены в рублях.
 * Используется в истории цен и других местах, где требуется представление цены.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    @JsonProperty("RUB")
    private int RUB;
}
