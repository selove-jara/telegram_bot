package org.example.service;

import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ProductPublisherBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7807555157:AAGyXDruNDICaJYp2aG69uUfEfWoYpXrzx8";
    private static final String CHANNEL_ID = "@skidki_Ozon_Wildberries_sale";
    // private static final String CHANNEL_ID = "@public_products";
    private int currentHashtagIndex = 0;
    private final List<String> hashtags = Arrays.asList(
            "#выгодно", "#акция",
            "#промокоды", "#скидки", "#распродажа",
            "#маркетплейсы", "#экономия"
    );

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка входящих сообщений (если нужно)
    }

    @Override
    public String getBotUsername() {
        return "YourBotName";
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    /**
     * Метод для публикации товаров каждые 15 минут.
     */
    @Scheduled(fixedDelay = 900000) // 15 минут = 900 000 миллисекунд
    public void publishProducts() {
        // Найти товары, которые еще не были опубликованы
        List<Product> unpublishedProducts = productRepository.findByPostedFalse();

        if (!unpublishedProducts.isEmpty()) {
            // Берем первый товар из списка
            Collections.shuffle(unpublishedProducts);

            Product product = unpublishedProducts.get(0);

            // Экранируем только те части, которые не являются Markdown-разметкой
            String escapedName = escapeMarkdownV2(product.getName());
            String escapedProductPrice = escapeMarkdownV2(String.valueOf(product.getProduct()));
            String escapedBasicPrice = escapeMarkdownV2(String.valueOf(product.getBasic()));

            // Формируем сообщение для публикации
            String message = String.format(
                    "✨ *%s* ✨\n\n" + // Упрощенная разметка
                            "🤑 Цена: ~%s~ ₽ \\- %s ₽ \n\n" + // Экранируем дефис
                            "👉 [Ссылка на товар](https://www.wildberries.ru/catalog/%d/detail.aspx) \n" +
                            "\n%s",
                    escapedName, // Уже экранировано
                    escapedBasicPrice, // Уже экранировано
                    escapedProductPrice, // Уже экранировано
                    product.getId(),
                    getNextHashtag()
            );


            try {
                if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                    // Если есть изображение, отправляем фото с описанием
                    sendPhotoWithCaption(product, message);
                } else {
                    // Если изображение отсутствует, отправляем только текст
                    sendTextMessage(message);
                }

                // Обновляем флаг posted в базе данных
                product.setPosted(true);
                productRepository.save(product);
                System.out.println("Товар успешно опубликован: " + product.getName());
            } catch (Exception e) {
                System.out.println("Ошибка при публикации товара: " + product.getName());
                e.printStackTrace();
            }
        } else {
            System.out.println("Нет товаров для публикации.");
        }
    }

    private void sendTextMessage(String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(CHANNEL_ID);
        sendMessage.setText(message);
        sendMessage.setParseMode("MarkdownV2"); // Устанавливаем parse_mode перед отправкой
        execute(sendMessage);
    }

    private void sendPhotoWithCaption(Product product, String caption) throws TelegramApiException {
        File imageFile = new File(product.getImagePath());
        if (!imageFile.exists()) {
            throw new RuntimeException("Файл изображения не найден: " + product.getImagePath());
        }

        InputFile photo = new InputFile(imageFile);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(CHANNEL_ID);
        sendPhoto.setPhoto(photo);
        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("MarkdownV2");
        execute(sendPhoto);
    }

    /**
     * Экранирует специальные символы MarkdownV2 в тексте.
     */
    private String escapeMarkdownV2(String text) {
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

    private String getNextHashtag() {
        // Получаем текущий хэштег
        String hashtag = hashtags.get(currentHashtagIndex);

        // Добавляем ссылку перед хэштегом
        String formattedHashtag = String.format(
                "[Мир скидок](https://t.me/+DJHQEb0s6D9kZjYy) %s",
                hashtag.replace("#", "\\#") // Экранируем символ '#'
        );

        // Переходим к следующему хэштегу
        currentHashtagIndex = (currentHashtagIndex + 1) % hashtags.size();

        return formattedHashtag;
    }
}