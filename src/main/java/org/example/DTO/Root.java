package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Корневой DTO для ответа API маркетплейса.
 * Содержит основную data-часть ответа.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Root {
    private Data data;
}

