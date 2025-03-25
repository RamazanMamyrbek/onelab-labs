package com.onelab.users_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "exchange_rates")
@Data
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_pair", nullable = false, unique = true)
    private String currencyPair;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "date", nullable = false)
    private LocalDate date = LocalDate.now();

}
