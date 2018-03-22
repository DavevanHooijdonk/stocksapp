package com.brocoding.stocks.backend.service;

import com.brocoding.stocks.backend.data.StocksInitializer;
import com.brocoding.stocks.backend.domain.Price;
import com.brocoding.stocks.backend.domain.Stock;
import com.brocoding.stocks.backend.domain.Stock.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Currency.getInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dave van Hooijdonk on 13-3-2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private StockService stockService = new StockServiceInMemory();

    @Before
    public void init() {
        final StocksInitializer stocksInitializer = new StocksInitializer("mockstocks", stockService, objectMapper);
        stocksInitializer.loadStocksFromJson();
    }

    @Test
    public void fetchStockShouldReturnAStockIfPresent() {
        // Given
        final Integer id = 1;

        // When
        final Optional<Stock> stock = stockService.fetchStock(id);

        // Then
        assertTrue("Stock was expected but is missing", stock.isPresent());
        assertTrue("Correct Id was expected but is incorrect", stock.map(Stock::getId).filter(id::equals).isPresent());
    }

    @Test
    public void fetchStockShouldReturnEmptyOptionalIfAbsent() {
        assertFalse("No Stock was expected but one was founds", stockService.fetchStock(0).isPresent());
    }

    @Test
    public void fetchStocksShouldReturnCorrectNumberOfStocksPerPage() {
        assertEquals(21, stockService.fetchStocks().size());
    }

    @Test
    public void fetchStocksShouldReturnEmptyStockListIfThereAreNoEntries() {
        stockService = new StockServiceInMemory();
        assertTrue("No Stocks were expected but at least one was found", stockService.fetchStocks().isEmpty());
    }

    @Test
    public void updateStockShouldReturnUpdatedStockIfPresent() {
        // Given
        final Integer id = 1;
        final String name = "Uppie Mac Update Corp.";
        final Price price = new Price.Builder().withAmount(new BigDecimal("300.24")).withCurrency(getInstance("EUR")).build();
        final Builder builder = new Builder().setName(name).setCurrentPrice(price).setId(id);

        // When
        final Optional<Stock> stock = stockService.updateStock(id, builder);
        final Optional<Stock> fetchedStock = stockService.fetchStock(id);

        assertTrue("A stock was expected but nothing was founds", stock.isPresent());
        assertEquals(name, stock.map(Stock::getName).orElse(""));
        assertEquals(price, stock.map(Stock::getCurrentPrice).orElse(null));
        assertTrue("The fetched stock was not matching with the returned stock", fetchedStock.filter(fetched -> fetched.equals(stock.get())).isPresent());
    }

    @Test
    public void updateStockShouldReturnEmptyOptionalIfAbsent() {
        assertFalse("No Stock was expected but one was founds", stockService.updateStock(Integer.MAX_VALUE, new Builder()).isPresent());
    }

    @Test
    public void createStockShouldAddANewStockWithTheNextIndex() {
        // Given
        final Price price = new Price.Builder().withAmount(BigDecimal.ZERO).withCurrency(getInstance("EUR")).build();
        final Builder builder = new Builder().setName("Joan of Arc Bv.").setCurrentPrice(price);
        final Integer stocksSize = stockService.fetchStocks().size();
        final Integer lastIndex = stockService.fetchStocks().stream().mapToInt(Stock::getId).max().orElse(0);

        // When
        final Stock stock = stockService.createStock(builder);

        // Then
        assertEquals(stocksSize + 1, stockService.fetchStocks().size());
        assertTrue("The index is not the expected index", (lastIndex + 1 == stock.getId()));
    }
}
