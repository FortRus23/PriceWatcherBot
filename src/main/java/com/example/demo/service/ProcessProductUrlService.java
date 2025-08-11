package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.entity.UserState;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class ProcessProductUrlService {

    private final ProductService productService;
    private final UserRepository userRepository;
    private final Map<Long, UserState> userStates;
    private final TextSenderService senderService;

    public void handle(long userId, String input) {
        userStates.put(userId, UserState.PROCESSING_PRODUCT_URL);

        boolean isMatch = Pattern.compile("^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*$")
                .matcher(input)
                .find();

        if (!isMatch) {
            senderService.sendText(userId, "Прислан не верный URL");
            return;
        }

        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Product product = productService.saveProduct(input, user);
            senderService.sendText(userId, "Товар сохранен: " + product.getProductName() + "\nОригинальная цена: " + " ₽" + product.getOriginalPrice() + "\nТекущая цена: " + " ₽"+ product.getPrice());
            userStates.put(userId, UserState.IDLE);
        } catch (Exception e) {
            senderService.sendText(userId, "Ошибка при сохранении товара. Попробуйте ещё раз.");
            userStates.put(userId, UserState.WAITING_FOR_PRODUCT_URL);
        }
    }
}
