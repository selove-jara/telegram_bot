package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    private long id;

    private String name;

    private int totalQuantity;

    private int basic;

    private int product;

    @Column(name = "old_price")
    private int oldPrice;

    @Column(name = "posted", nullable = false)
    private boolean posted = false;

    @Column(name = "image_path")
    private String imagePath;
}
