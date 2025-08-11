package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    @Column(name = "opriginal_price")
    private BigDecimal originalPrice;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_url", unique = true)
    private String productUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
