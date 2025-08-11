package com.example.demo.command;

import com.example.demo.service.TextSenderService;

public class StartCommandHandler implements CommandHandler{

    private final TextSenderService senderService;

    public StartCommandHandler(TextSenderService senderService) {
        this.senderService = senderService;
    }

    @Override
    public void handle(long userId, String ignore) {
        String text = "commands:";
        senderService.sendText(userId, text);
    }
}