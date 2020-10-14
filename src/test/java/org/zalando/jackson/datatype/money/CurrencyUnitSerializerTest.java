package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;

import static org.assertj.core.api.Assertions.assertThat;


final class CurrencyUnitSerializerTest {

    private final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldSerialize() throws JsonProcessingException {
        final String expected = "EUR";
        final CurrencyUnit currency = CurrencyUnitBuilder.of(expected, "default").build();

        final String actual = unit.writeValueAsString(currency);

        assertThat(actual).isEqualTo('"' + expected + '"');
    }

}
