package com.brocoding.stocks.backend.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

/**
 * Created by Dave van Hooijdonk on 12-3-2018.
 */
@JsonDeserialize(builder = Stock.Builder.class)
public class Stock {

    private final Integer id;
    private final String name;
    private final Price currentPrice;
    private final LocalDateTime lastUpdate;

    private Stock(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.currentPrice = builder.currentPrice;
        this.lastUpdate = now();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Price getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {

        private Integer id;
        private String name;
        private Price currentPrice;

        public Builder setId(final Integer id) {
            this.id = id;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setCurrentPrice(final Price currentPrice) {
            this.currentPrice = currentPrice;
            return this;
        }

        /**
         * @param stock reference values that will be used if missing
         * @return {@link Builder} for stock updated with values from the reference stock
         */
        public Builder mergeFromReference(final Stock stock) {
            if (id == null) {
                id = stock.id;
            }
            if (name == null) {
                name = stock.name;
            }
            if (currentPrice == null) {
                currentPrice = stock.currentPrice;
            }
            return this;
        }

        public Stock build() {
            return new Stock(this);
        }
    }
}
