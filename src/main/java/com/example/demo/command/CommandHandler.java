package com.example.demo.command;

public interface CommandHandler {
    void handle(long userId, String input);
}
