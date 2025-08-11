package com.example.demo.service;

import com.example.demo.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceCheckScheduler {

    private final ProductService productService;
    private final PriceParserService priceParserService;
    private final TextSenderService textSenderService;

    @Scheduled(fixedRate = 100000)
    public void checkPrices() {
        List<Product> products = productService.findAllProducts();

        for(Product product: products) {
            PriceParserService.PriceInfo priceInfo = priceParserService.parseprice(product.getProductUrl());

            if(priceInfo.price() != null) {
                BigDecimal currentPrice = priceInfo.price();
                BigDecimal savedPrice = product.getPrice();

                if (currentPrice.compareTo(savedPrice) < 0) {
                    product.setPrice(currentPrice);
                    productService.updateProduct(product);

                    textSenderService.sendText(product.getUser().getUserId(),
                            "Цена на товар \"" + product.getProductName() + "\" снизилась!\n" +
                                    "Было: " + savedPrice + " ₽\n" +
                                    "Стало: " + currentPrice + " ₽\n" +
                                    "Ссылка: " + product.getProductUrl()
                    );
                }
            }
        }
    }
}
