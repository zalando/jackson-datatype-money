package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Currency;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class CurrencySerializerTest {
    
    private final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        final String expected = "EUR";
        final Currency currency = Currency.getInstance(expected);

        final String actual = unit.writeValueAsString(currency);
        
        assertThat(actual, is('"' + expected + '"'));
    }

}