package com.brocoding.stocks.backend.data;

import com.brocoding.stocks.backend.domain.Price;
import com.brocoding.stocks.backend.domain.Stock;
import com.brocoding.stocks.backend.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

import static java.util.Currency.getInstance;
import static java.util.stream.IntStream.range;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.util.ReflectionUtils.findField;

/**
 * Created by Dave van Hooijdonk on 14-3-2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class StocksInitializerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectReader objectReader;

    @Mock
    private StockService stockService;

    @Mock
    private Logger logger;

    @Test
    public void loadStocksFromJsonShouldCreateCorrectAmountOfStocks() throws IOException {
        // Given
        final Integer numberOfStocks = 60;
        given(objectMapper.readerFor(any(Class.class))).willReturn(objectReader);
        given(objectReader.readValue(any(File.class))).willReturn(createMockStocks(numberOfStocks));
        final StocksInitializer stocksInitializer = new StocksInitializer("mockstocks", stockService, objectMapper);


        // When
        stocksInitializer.loadStocksFromJson();

        // Then
        verify(objectMapper).readerFor(any(Class.class));
        verify(objectReader).readValue(any(File.class));
        verify(stockService, times(numberOfStocks)).createStock(any(Stock.Builder.class));
    }

    @Test
    public void loggerShouldCorrectlyCatchExceptionIfInitFails() throws NoSuchFieldException, IllegalAccessException {
        // Given
        final StocksInitializer stocksInitializer = new StocksInitializer("this does totally not exist and should throw exception", stockService, objectMapper);
        setStaticFinalField(StocksInitializer.class, "logger", logger);

        // When
        stocksInitializer.loadStocksFromJson();

        // Then
        verify(logger).error(any(String.class), any(Object.class));
    }

    private Stock.Builder[] createMockStocks(Integer numberOfStocks) {
        final Price price = new Price.Builder().withAmount(BigDecimal.TEN).withCurrency(getInstance("EUR")).build();
        final Stock stock = new Stock.Builder().setCurrentPrice(price).setName("Fortune Coockie Inc.").build();
        return range(0, numberOfStocks).mapToObj(id -> new Stock.Builder().setId(id + 1)).map(builder -> builder.mergeFromReference(stock)).toArray(Stock.Builder[]::new);
    }

    /**
     * Dirty hack starts here pls avert your eyes
     */
    private void setStaticFinalField(final Class clazz, final String fieldName, final Object targetObject) throws NoSuchFieldException, IllegalAccessException {
        final Field loggerField = findField(clazz, fieldName);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(loggerField);
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(loggerField, modifiers);
        setField(clazz, fieldName, targetObject);
    }
}
