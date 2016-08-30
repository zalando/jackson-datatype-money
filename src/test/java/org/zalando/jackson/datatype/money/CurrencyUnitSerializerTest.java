package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.Test;

import javax.money.CurrencyUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class CurrencyUnitSerializerTest {
    
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        final String expected = "EUR";
        final CurrencyUnit currency = CurrencyUnitBuilder.of(expected, "default").build();

        final String actual = mapper.writeValueAsString(currency);
        
        assertThat(actual, is('"' + expected + '"'));
    }

}