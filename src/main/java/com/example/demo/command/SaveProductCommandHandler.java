package com.example.demo.command;

import com.example.demo.entity.UserState;
import com.example.demo.service.TextSenderService;

import java.util.Map;

public class SaveProductCommandHandler implements CommandHandler{

    private final TextSenderService textSenderService;
    private final Map<Long, UserState> userStates;

    public SaveProductCommandHandler(TextSenderService textSenderService, Map<Long, UserState> userStates) {
        this.textSenderService = textSenderService;
        this.userStates = userStates;
    }

    @Override
    public void handle(long userId, String ignore) {
        userStates.put(userId, UserState.WAITING_FOR_PRODUCT_URL);
        textSenderService.sendText(userId, "Отправьте ссылку на товар");
    }
}
