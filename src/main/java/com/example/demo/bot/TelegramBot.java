package com.example.demo.bot;

import com.example.demo.command.CommandHandler;
import com.example.demo.command.SaveProductCommandHandler;
import com.example.demo.command.StartCommandHandler;
import com.example.demo.entity.UserState;
import com.example.demo.service.ProcessProductUrlService;
import com.example.demo.service.TextSenderService;
import com.example.demo.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final TextSenderService senderService;
    private final UserService userService;
    private final ProcessProductUrlService productUrlService;

    private final Map<Long, UserState> userStates = new ConcurrentHashMap<>();
    private final Map<String, CommandHandler> commandHandler = new HashMap<>();

    public TelegramBot(TextSenderService senderService, UserService userService, ProcessProductUrlService productUrlService) {
        this.senderService = senderService;
        this.userService = userService;
        this.productUrlService = productUrlService;
        registerCommand();
    }

    @PostConstruct
    public void init() {
        senderService.setBot(this);
    }

    @Value("${telegram.bot.api}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return "FortAkumaBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Message msg = update.getMessage();
        String text = msg.getText();
        long userId = msg.getFrom().getId();
        long chatId = msg.getChatId();
        String username = msg.getFrom().getUserName();

        userService.findOrCreateUser(userId, username);

        UserState currentState = userStates.getOrDefault(userId, UserState.IDLE);

        switch (currentState) {
            case WAITING_FOR_PRODUCT_URL -> {

                if (text.isEmpty()) {
                    senderService.sendText(chatId, "Отправь ссылку на товар:");
                    return;
                }

                productUrlService.handle(userId, text);
                userStates.put(userId, UserState.IDLE);
            }

            case IDLE -> {
                if (isCommand(text)) {
                    handleCommand(userId, text);
                } else {
                    senderService.sendText(chatId, "Доступные команды /start");
                }
            }
        }
    }

    private void registerCommand() {
        commandHandler.put("/start", new StartCommandHandler(senderService));
        commandHandler.put("/save_product", new SaveProductCommandHandler(senderService, userStates));
    }

    private void handleCommand(Long userId, String command) {
        CommandHandler handler = commandHandler.get(command);
        if (handler != null) {
            handler.handle(userId, null);
        } else {
            senderService.sendText(userId, "Неизвестная команда");
        }
    }

    private boolean isCommand(String text) {
        return text.startsWith("/");
    }

}
