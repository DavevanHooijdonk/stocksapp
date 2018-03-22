package com.brocoding.stocks.backend.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by Dave van Hooijdonk on 12-3-2018.
 */
@JsonDeserialize(builder = Price.Builder.class)
public class Price {

    private final BigDecimal amount;
    private final Currency currency;

    private Price(final Builder builder) {
        this.amount = builder.amount;
        this.currency = builder.currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static class Builder {
        private BigDecimal amount;
        private Currency currency;

        public Builder withAmount(final BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrency(final Currency currency) {
            this.currency = currency;
            return this;
        }

        public Price build() {
            return new Price(this);
        }
    }
}
