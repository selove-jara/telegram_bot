package org.example.service;

import jakarta.annotation.PostConstruct;
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

@Component
public class ProductPublisherBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "7807555157:AAGyXDruNDICaJYp2aG69uUfEfWoYpXrzx8";
    private static final String CHANNEL_ID = "@skidki_Ozon_Wildberries_sale";
    private int currentHashtagIndex = 0;
    private final List<String> hashtags = Arrays.asList(
            "#выгодно", "#акция",
            "#промокоды", "#скидки", "#распродажа",
            "#маркетплейсы", "#экономия", "#wb", "#мода",
            "#wilbberies", "#ozon", "#покупки", "#шопинг", "#новинка", "#sale",
            "#покупкионлайн", "#промо", "#покупкионлайн", "#горячиескидки", "#спецпредложения", "#шопингонлайн",
            "#дешевленекуда", "#лучшиецены", "#wildberriesскидки", "#ozonвыгода", "#советыпокупки", "#каксэкономить",
            "#полезныесоветы", "#модныепокупки", "#рекомендации", "#горячиескидки", "#спецпредложения", "#шопингонлайн"
    );

    private ProductRepository productRepository;

    private ImageDownloader imageDownloader;

    private ProductService productService;

    @Autowired
    public ProductPublisherBot(ImageDownloader imageDownloader, ProductService productService, ProductRepository productRepository) {
        this.imageDownloader = imageDownloader;
        this.productService = productService;
        this.productRepository = productRepository;
    }



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
     * Метод для публикации товаров каждые 10 минут с 6:00 по 23:59.
     */
    @Scheduled(cron = "0 0/10 6-23 * * *", zone = "Europe/Moscow")
    public void publishProducts() {
        // Найти товары, которые еще не были опубликованы
        List<Product> unpublishedProducts = productRepository.findByPostedFalse();

        if (!unpublishedProducts.isEmpty()) {
            // Перемешиваем список товаров
            Collections.shuffle(unpublishedProducts);

            // Берем первый товар из списка
            Product product = unpublishedProducts.get(0);

            // Проверяем, что количество товара не меньше 3 и цена не равна 0
            if (product.getTotalQuantity() >= 3 && product.getProduct() > 0) {
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
                        getNextHashtags()
                );

                try {
                    // Скачиваем изображение перед публикацией
                    String imagePath = imageDownloader.downloadImage(product.getId());

                    if (imagePath != null) {
                        // Отправляем фото с описанием
                        sendPhotoWithCaption(imagePath, message);
                        new File(imagePath).delete();
                    } else {
                        // Если изображение не удалось скачать, отправляем только текст
                        sendTextMessage(message);
                    }

                    // Обновляем флаг posted в базе данных
                    product.setPosted(true);
                    productService.save(product);
                    System.out.println("Товар успешно опубликован: " + product.getName());
                } catch (Exception e) {
                    System.out.println("Ошибка при публикации товара: " + product.getName());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Товар не опубликован, так как его количество меньше 3 или цена равна 0: " + product.getName());
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

    private void sendPhotoWithCaption(String imagePath, String caption) throws TelegramApiException {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            throw new RuntimeException("Файл изображения не найден: " + imagePath);
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

    private String getNextHashtags() {
        StringBuilder hashtagsBuilder = new StringBuilder();
        hashtagsBuilder.append("🔥 [Мир скидок](https://t.me/+DJHQEb0s6D9kZjYy) "); // Добавляем ссылку один раз
        Collections.shuffle(hashtags);
        for (int i = 0; i < 3; i++) {
            String hashtag = hashtags.get(currentHashtagIndex);
            hashtagsBuilder.append(hashtag.replace("#", "\\#")).append(" "); // Добавляем хэштег

            // Переходим к следующему хэштегу
            currentHashtagIndex = (currentHashtagIndex + 1) % hashtags.size();
        }

        return hashtagsBuilder.toString().trim(); // Убираем лишний пробел в конце
    }

    @Scheduled(fixedDelay = 86400000) // Очистка раз в сутки
    public void cleanTempImages() {
        File imagesDir = new File("/tmp/images");
        if (imagesDir.exists()) {
            for (File file : imagesDir.listFiles()) {
                if (file.isFile() && System.currentTimeMillis() - file.lastModified() > 86400000) {
                    file.delete(); // Удаляем файлы старше 24 часов
                }
            }
        }
    }

    @PostConstruct
    private void initializeImagesDirectory() {
        File imagesDir = new File("/tmp/images");
        if (!imagesDir.exists()) {
            imagesDir.mkdirs(); // Создаем папку, если она не существует
        }
    }
}