package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.Test;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class CurrencyUnitDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    public void shouldDeserialize() throws IOException {
        final CurrencyUnit actual = mapper.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();
        
        assertThat(actual, is(expected));
    }

    @Test(expected = UnknownCurrencyException.class)
    public void shouldNotDeserializeInvalidCurrency() throws IOException {
        mapper.readValue("\"FOO\"", CurrencyUnit.class);
    }
    
}