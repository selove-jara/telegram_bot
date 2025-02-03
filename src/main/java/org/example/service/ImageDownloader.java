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
        String imageName = "image_" + productId + ".jpg"; // Уникальное имя файла
        String imagePath = "images/" + imageName; // Путь к изображению

        try {
            // Создаем папку /images, если она не существует
            File imagesDir = new File("images");
            if (!imagesDir.exists()) {
                imagesDir.mkdirs();
            }

            // Создаем объект URL
            URL url = new URL(imageUrl);
            // Открываем соединение
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Получаем код ответа
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Читаем данные из потока
                InputStream inputStream = connection.getInputStream();
                // Создаем файл для сохранения изображения
                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);

                // Буфер для записи данных
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                // Закрываем потоки
                inputStream.close();
                fileOutputStream.close();
                log.info("Image downloaded successfully: {}", imagePath);
            } else {
                log.warn("Failed to download image: {}", imageUrl);
                return null;
            }

            // Закрываем соединение
            connection.disconnect();
        } catch (IOException e) {
            log.error("Error downloading image: {}", e.getMessage());
            return null;
        }

        return imagePath;
    }
}