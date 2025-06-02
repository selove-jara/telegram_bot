package org.example.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Сущность продукта для хранения в базе данных.
 * Соответствует таблице "products" в БД.
 */

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    private long id;

    private String name;

    private int totalQuantity; // Остаток товара

    private int basic; //Цена базовая

    private int product; //Цена покупателя

    @Column(name = "average_price")
    private int averagePrice; // Средняя цена

    @Column(name = "posted", nullable = false)
    private boolean posted = false; //Опубликован

    @Column(name = "image_path")
    private String imagePath; //Путь до фото
}
