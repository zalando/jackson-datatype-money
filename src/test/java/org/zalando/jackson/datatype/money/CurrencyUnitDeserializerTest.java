package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.junit.jupiter.api.Test;

import javax.money.CurrencyUnit;
import javax.money.UnknownCurrencyException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class CurrencyUnitDeserializerTest {

    private final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldDeserialize() throws IOException {
        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldNotDeserializeInvalidCurrency() {
        assertThrows(UnknownCurrencyException.class, () ->
                unit.readValue("\"FOO\"", CurrencyUnit.class));
    }

    @Test
    void shouldDeserializeWithTyping() throws IOException {
        unit.enableDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final CurrencyUnit actual = unit.readValue("\"EUR\"", CurrencyUnit.class);
        final CurrencyUnit expected = CurrencyUnitBuilder.of("EUR", "default").build();

        assertThat(actual, is(expected));
    }

}
