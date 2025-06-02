package org.example.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 * Сущность категории товаров.
 * Соответствует таблице "categories" в БД.
 */

@Entity
@Table(name = "categories")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Categories {
    @Id
    private long id;

    private String shard; // Ссылка на категорию

    @JsonProperty("childs")
    @Transient
    private List<Categories> childs; // дочернии категории

    public Categories(long id, String shard) {
        this.id = id;
        this.shard = shard;
    }

    public Categories() {
    }
}