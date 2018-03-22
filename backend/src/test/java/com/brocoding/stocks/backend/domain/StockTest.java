package com.brocoding.stocks.backend.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Currency.getInstance;
import static java.util.function.UnaryOperator.identity;
import static org.junit.Assert.assertEquals;

/**
 * Created by Dave van Hooijdonk on 13-3-2018.
 */
@RunWith(value = Parameterized.class)
public class StockTest {

    private static String referenceName = "Lord Banana";
    private static Integer referenceId = 1;
    private static Price referencePrice = new Price.Builder().withAmount(BigDecimal.TEN).withCurrency(getInstance("USD")).build();
    private static Stock referenceStock = new Stock.Builder().setId(referenceId).setCurrentPrice(referencePrice).setName(referenceName).build();

    private static Price price = new Price.Builder().withAmount(BigDecimal.ONE).withCurrency(getInstance("EUR")).build();
    private static Integer id = 2;
    private static String name = "Emperor Strawberry";

    private final Function<Stock.Builder, Stock.Builder> buildChain;
    private final String assertName;
    private final Integer assertId;
    private final Price assertPrice;

    public StockTest(final Function<Stock.Builder, Stock.Builder> buildChain, final String assertName, final Integer assertId, final Price assertPrice) {
        this.buildChain = buildChain;
        this.assertName = assertName;
        this.assertId = assertId;
        this.assertPrice = assertPrice;
    }

    @Parameterized.Parameters
    public static Collection data() {
        final Function<Stock.Builder, Stock.Builder> fnName = builder -> builder.setName(name);
        final Function<Stock.Builder, Stock.Builder> fnId = builder -> builder.setId(id);
        final Function<Stock.Builder, Stock.Builder> fnPrice = builder -> builder.setCurrentPrice(price);
        Object[][] data = new Object[][]{
                {identity(), referenceName, referenceId, referencePrice},
                {fnName, name, referenceId, referencePrice},
                {fnId, referenceName, id, referencePrice},
                {fnPrice, referenceName, referenceId, price},
                {fnName.andThen(fnId), name, id, referencePrice},
                {fnName.andThen(fnPrice), name, referenceId, price},
                {fnId.andThen(fnPrice), referenceName, id, price},
                {fnName.andThen(fnId).andThen(fnPrice), name, id, price}};
        return asList(data);
    }

    @Test
    public void mergeFromReferenceShouldAddMissingFieldsCorrectlyFromStockParameter() {
        // Given
        final Stock.Builder builder = buildChain.apply(new Stock.Builder());

        // When
        final Stock stock = builder.mergeFromReference(referenceStock).build();

        // Then
        assertEquals(stock.getName(), assertName);
        assertEquals(stock.getId(), assertId);
        assertEquals(stock.getCurrentPrice(), assertPrice);
    }
}
