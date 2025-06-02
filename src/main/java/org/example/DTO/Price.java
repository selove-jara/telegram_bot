package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для представления цены продукта с маркетплейса.
 * Содержит базовую цену и цену продукта.
 */

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    private int basic;
    private int product;

    public Price() {

    }
}
