package org.example.repository;

import org.example.model.Categories;
import org.example.model.Product;

import java.util.List;

public interface CategoryParser {
    List<Product> parseCategory();
}