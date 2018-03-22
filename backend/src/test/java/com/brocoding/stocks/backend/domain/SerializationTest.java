package com.brocoding.stocks.backend.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import static java.util.Currency.getInstance;
import static java.util.regex.Pattern.compile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;
import static org.springframework.util.ResourceUtils.getFile;

/**
 * Created by Dave van Hooijdonk on 13-3-2018.
 */
@RunWith(SpringRunner.class)
@JsonTest
public class SerializationTest {

    private static final String LORD_BANANA = "Lord Banana";
    private final File testJson;

    private final Price price = new Price.Builder().withAmount(BigDecimal.TEN).withCurrency(getInstance("USD")).build();
    private final Stock testStock = new Stock.Builder().setId(1).setCurrentPrice(price).setName(LORD_BANANA).build();

    @Autowired
    protected JacksonTester<Stock> jsonTesterStock;

    @Autowired
    private JacksonTester<Stock.Builder> jsonTesterStockBuilder;

    public SerializationTest() throws FileNotFoundException {
        testJson = getFile(CLASSPATH_URL_PREFIX.concat("teststock.json"));
    }

    @Test
    public void stockSerialize() throws IOException {
        // When
        final JsonContent<Stock> jsonContent = jsonTesterStock.write(testStock);

        // Then
        assertThat(jsonContent).isEqualToJson(testJson);
        assertThat(jsonContent).hasJsonPathStringValue("@.lastUpdate");
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastUpdate")
                .containsPattern(compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{1,3}$"));
    }

    @Test
    public void stockDeserialize() throws IOException {
        // When
        final Stock stock = jsonTesterStock.readObject(testJson);

        // Then
        assertThat(stock.getId()).isEqualTo(1);
        assertThat(stock.getCurrentPrice().getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(stock.getCurrentPrice().getCurrency()).isEqualTo(getInstance("USD"));
        assertThat(stock.getName()).isEqualTo(LORD_BANANA);
    }

    @Test
    public void stockBuilderDeserialize() throws IOException {
        // When
        final Stock stock = jsonTesterStockBuilder.readObject(testJson).build();

        // Then
        assertThat(stock.getId()).isEqualTo(1);
        assertThat(stock.getCurrentPrice().getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(stock.getCurrentPrice().getCurrency()).isEqualTo(getInstance("USD"));
        assertThat(stock.getName()).isEqualTo(LORD_BANANA);
    }
}
