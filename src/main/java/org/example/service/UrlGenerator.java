package org.example.service;

import org.example.model.Categories;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class UrlGenerator {

    /**
     * Определяет сервер на основе ID товара.
     */
    private String getServer(long vol) {
        long t = vol;
        if (t >= 0 && t <= 143) {
            return "01";
        } else if (t <= 287) {
            return "02";
        } else if (t <= 431) {
            return "03";
        } else if (t <= 719) {
            return "04";
        } else if (t <= 1007) {
            return "05";
        } else if (t <= 1061) {
            return "06";
        } else if (t <= 1115) {
            return "07";
        } else if (t <= 1169) {
            return "08";
        } else if (t <= 1313) {
            return "09";
        } else if (t <= 1601) {
            return "10";
        } else if (t <= 1655) {
            return "11";
        } else if (t <= 1919) {
            return "12";
        } else if (t <= 2045) {
            return "13";
        } else if (t <= 2189) {
            return "14";
        } else if (t <= 2405) {
            return "15";
        } else if (t <= 2621) {
            return "16";
        } else if (t <= 2837) {
            return "17";
        } else if (t <= 3053) {
            return "18";
        } else if (t <= 3269) {
            return "19";
        } else if (t <= 3485) {
            return "20";
        } else {
            return "21";
        }
    }

    public String generatePriceUrl(long productId) {
        long vol = productId / 100000;
        long part = productId / 1000;
        String server = getServer(vol);

        return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/info/price-history.json",
                server, vol, part, productId);
    }

    public String generatePhotoUrl(long productId) {
        long vol = productId / 100000;
        long part = productId / 1000;
        String server = getServer(vol);

        return String.format("https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp",
                server, vol, part, productId);
    }
}