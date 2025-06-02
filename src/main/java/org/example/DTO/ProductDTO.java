package org.example.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * DTO для представления продукта с маркетплейса.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private long id;

    private String name;

    private int totalQuantity;

    private List<Size> sizes;
}
