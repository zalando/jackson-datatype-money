package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.Money;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MoneyDeserializerTest {

    @Test
    public void shouldDeserializeMoneyByDefault() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final Money amount = unit.readValue(content, Money.class);

        assertThat(amount, is(instanceOf(Money.class)));
    }

    @Test
    public void shouldDeserializeMoneyCorrectly() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final Money amount = unit.readValue(content, Money.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        final BigDecimal expected = new BigDecimal("29.95");

        assertThat(actual, comparesEqualTo(expected));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final Money amount = unit.readValue(content, Money.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        assertThat(actual, comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

}
