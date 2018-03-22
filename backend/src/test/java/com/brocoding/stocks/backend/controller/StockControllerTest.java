package com.brocoding.stocks.backend.controller;

import com.brocoding.stocks.backend.domain.Price;
import com.brocoding.stocks.backend.domain.Stock;
import com.brocoding.stocks.backend.domain.Stock.Builder;
import com.brocoding.stocks.backend.service.StockService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Currency.getInstance;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Dave van Hooijdonk on 14-3-2018.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(StockController.class)
@WithMockUser()
public class StockControllerTest {

    private final Price price = new Price.Builder().withAmount(BigDecimal.TEN).withCurrency(getInstance("AUD")).build();
    private final Builder builder = new Builder().setId(1).setCurrentPrice(price).setName("Kangaroo Karoo");
    private final Stock stock = builder.build();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StockService stockService;

    @Test
    public void getStockShouldReturnNotFoundIfNotPresent() throws Exception {
        // Given
        given(stockService.fetchStock(anyInt())).willReturn(empty());

        // Then
        mvc.perform(get("/stocks/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStockShouldReturnStockAndLinksIfPresent() throws Exception {
        // Given
        given(stockService.fetchStock(anyInt())).willReturn(Optional.of(stock));

        // Then
        mvc.perform(get("/stocks/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.currentPrice").exists())
                .andExpect(jsonPath("$.currentPrice.amount").exists())
                .andExpect(jsonPath("$.currentPrice.currency").exists())
                .andExpect(jsonPath("$.lastUpdate").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void getStocksShouldReturnNotFoundIfListIsEmpty() throws Exception {
        // Given
        given(stockService.fetchStocks()).willReturn(emptyList());

        // Then
        mvc.perform(get("/stocks?page=1&pageSize=1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getStocksShouldReturnStockAndLinksIfPresent() throws Exception {
        // Given
        given(stockService.fetchStocks()).willReturn(of(stock, stock, stock).collect(toList()));

        // Then
        mvc.perform(get("/stocks?page=1&pageSize=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"_embedded\":{\"stockList\":[{\"name\":\"Kangaroo Karoo\"}, {\"name\":\"Kangaroo Karoo\"}]}}"))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.first").exists())
                .andExpect(jsonPath("$._links.last").exists())
                .andExpect(jsonPath("$._links.next").exists())
                .andExpect(jsonPath("$._links.prev").doesNotExist())
                .andExpect(content().json("{\"totalPages\":2,\"numberOfElements\":2,\"totalElements\":3}"));
    }

    @Test
    public void getStocksShouldNotReturnNextLinksIfNoNextPage() throws Exception {
        // Given
        given(stockService.fetchStocks()).willReturn(of(stock).collect(toList()));

        // Then
        mvc.perform(get("/stocks?page=1&pageSize=3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.next").doesNotExist());
    }

    @Test
    public void getStocksShouldReturnPrevLinksIfThereIsAPrevPage() throws Exception {
        // Given
        given(stockService.fetchStocks()).willReturn(of(stock, stock, stock, stock, stock, stock).collect(toList()));

        // Then
        mvc.perform(get("/stocks?page=2&pageSize=3")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.prev").exists());
    }

    @Test
    public void updateStocksShouldReturnNotFoundIfListIsEmpty() throws Exception {
        // Given
        given(stockService.fetchStocks()).willReturn(emptyList());

        // Then
        mvc.perform(put("/stocks/1")
                .with(csrf())
                .content("{\"name\": \"Glorious Inc\", \"currentPrice\": {\"amount\": 34.02,\"currency\": \"USD\"}}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateStockShouldReturnUpdateTheStockAndReturnIt() throws Exception {
        // Given
        given(stockService.updateStock(anyInt(), any(Builder.class))).willReturn(Optional.of(stock));

        // Then
        mvc.perform(put("/stocks/1")
                .with(csrf())
                .content("{\"name\": \"Glorious Inc\", \"currentPrice\": {\"amount\": 34.02,\"currency\": \"USD\"}}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.currentPrice").exists())
                .andExpect(jsonPath("$.currentPrice.amount").exists())
                .andExpect(jsonPath("$.currentPrice.currency").exists())
                .andExpect(jsonPath("$.lastUpdate").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    public void postStockShouldReturnUpdateTheStockAndReturnIt() throws Exception {
        // Given
        given(stockService.createStock(any(Builder.class))).willReturn(stock);

        // Then
        mvc.perform(post("/stocks")
                .with(csrf())
                .content("{\"name\": \"Glorious Inc\", \"currentPrice\": {\"amount\": 34.02,\"currency\": \"USD\"}}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.currentPrice").exists())
                .andExpect(jsonPath("$.currentPrice.amount").exists())
                .andExpect(jsonPath("$.currentPrice.currency").exists())
                .andExpect(jsonPath("$.lastUpdate").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }


}
