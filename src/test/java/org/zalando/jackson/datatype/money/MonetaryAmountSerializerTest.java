package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Value;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.jackson.datatype.money.FieldNames.defaults;

@RunWith(Parameterized.class)
public final class MonetaryAmountSerializerTest {

    private final MonetaryAmount amount;

    public MonetaryAmountSerializerTest(final MonetaryAmount amount) {
        this.amount = amount;
    }

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {FastMoney.of(29.95, "EUR")},
                {Money.of(29.95, "EUR")},
                {RoundedMoney.of(29.95, "EUR", Monetary.getDefaultRounding())},
        });
    }

    private ObjectMapper unit() {
        return unit(module());
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private MoneyModule module() {
        return new MoneyModule();
    }

    @Test
    public void shouldSerialize() throws JsonProcessingException {
        final ObjectMapper unit = unit();

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void defaultConstructorShouldFallbackToNoFormatting() throws IOException {
        final ObjectMapper unit = unit(new SimpleModule()
                .addSerializer(CurrencyUnit.class, new CurrencyUnitSerializer())
                .addSerializer(NumberValue.class, new DecimalNumberValueSerializer())
                .addSerializer(MonetaryAmount.class, new MonetaryAmountSerializer()));

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithoutFormattedValueIfFactoryProducesNull() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withFormatFactory(new NoopMonetaryAmountFormatFactory()));

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithFormattedGermanValue() throws JsonProcessingException {
        final ObjectMapper unit = unit(new MoneyModule().withFormatFactory(new DefaultMonetaryAmountFormatFactory()));

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithFormattedAmericanValue() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withFormatFactory(new DefaultMonetaryAmountFormatFactory()));

        final String expected = "{\"amount\":29.95,\"currency\":\"USD\",\"formatted\":\"USD29.95\"}";

        final ObjectWriter writer = unit.writer().with(Locale.US);
        final String actual = writer.writeValueAsString(amount.getFactory().setCurrency("USD").create());

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithCustomName() throws IOException {
        final ObjectMapper unit = unit(module().withFormatFactory(new DefaultMonetaryAmountFormatFactory())
                        .withFieldNames(defaults()
                                .withAmount("value")
                                .withCurrency("unit")
                                .withFormatted("pretty")));

        final String expected = "{\"value\":29.95,\"unit\":\"EUR\",\"pretty\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimal() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withNumberValueSerializer(new QuotedDecimalNumberValueSerializer()));

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldRespectJacksonFeatureByDefault() throws JsonProcessingException {
        final ObjectMapper unit = unit(module());
        unit.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Value
    private static final class Price {
        MonetaryAmount amount;
    }

    @Test
    public void shouldSerializeWithType() throws JsonProcessingException {
        final ObjectMapper unit = unit(module()).enableDefaultTyping();

        final String expected = "{\"amount\":{\"amount\":29.95,\"currency\":\"EUR\"}}";
        final String actual = unit.writeValueAsString(new Price(amount));

        assertThat(actual, is(expected));
    }

}
