package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Currency;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class CurrencyDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    public void shouldDeserialize() throws IOException {
        final Currency actual = mapper.readValue("\"EUR\"", Currency.class);
        final Currency expected = Currency.getInstance("EUR");
        
        assertThat(actual, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotDeserializeInvalidCurrency() throws IOException {
        mapper.readValue("\"FOO\"", Currency.class);
    }

}