package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class ImageDownloader {

    private final UrlGenerator urlGenerator;

    public ImageDownloader(UrlGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }

    /**
     * Скачивает изображение по ID товара и сохраняет его в папку /images.
     *
     * @param productId ID товара
     * @return Путь к сохраненному изображению
     */
    public String downloadImage(long productId) {
        String imageUrl = urlGenerator.generatePhotoUrl(productId);
        String imageName = "image_" + productId + ".jpg";
        String imagePath = "images/" + imageName;

        try {
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                fileOutputStream.close();
                log.info("Image downloaded successfully: {}", imagePath);
            } else {
                log.warn("Failed to download image: {}", imageUrl);
                return null;
            }

            connection.disconnect();
        } catch (IOException e) {
            log.error("Error downloading image: {}", e.getMessage());
            return null;
        }

        return imagePath;
    }
}