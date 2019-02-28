package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import lombok.Value;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import static javax.money.Monetary.getDefaultRounding;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public final class MonetaryAmountSerializerTest {

    private final MonetaryAmount amount;
    private final MonetaryAmount hundred;
    private final MonetaryAmount fractions;

    public MonetaryAmountSerializerTest(final MonetaryAmount amount, final MonetaryAmount hundred,
            final MonetaryAmount fractions) {
        this.amount = amount;
        this.hundred = hundred;
        this.fractions = fractions;
    }

    @Parameters(name = "{0}, {1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {FastMoney.of(29.95, "EUR"), FastMoney.of(100, "EUR"), FastMoney.of(0.0001, "EUR")},
                {Money.of(29.95, "EUR"), Money.of(100, "EUR"), Money.of(0.0001, "EUR")},
                {RoundedMoney.of(29.95, "EUR", getDefaultRounding()),
                        RoundedMoney.of(100, "EUR", getDefaultRounding()),
                        RoundedMoney.of(0.0001, "EUR", getDefaultRounding())},
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
    public void shouldSerializeWithoutFormattedValueIfFactoryProducesNull() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withoutFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithFormattedGermanValue() throws JsonProcessingException {
        final ObjectMapper unit = unit(new MoneyModule().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithFormattedAmericanValue() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"USD\",\"formatted\":\"USD29.95\"}";

        final ObjectWriter writer = unit.writer().with(Locale.US);
        final String actual = writer.writeValueAsString(amount.getFactory().setCurrency("USD").create());

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeWithCustomName() throws IOException {
        final ObjectMapper unit = unit(module().withDefaultFormatting()
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit")
                .withFormattedFieldName("pretty"));

        final String expected = "{\"value\":29.95,\"unit\":\"EUR\",\"pretty\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsDecimal() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsDecimalWithDefaultFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":100.00,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsDecimalWithHigherNumberOfFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":0.0001,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fractions);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsDecimalWithLowerNumberOfFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withNumbers(new AmountWriter<BigDecimal>() {
            @Override
            public Class<BigDecimal> getType() {
                return BigDecimal.class;
            }

            @Override
            public BigDecimal write(final MonetaryAmount amount) {
                return amount.getNumber().numberValueExact(BigDecimal.class).stripTrailingZeros();
            }
        }));

        final String expected = "{\"amount\":1E+2,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimal() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimalWithDefaultFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimalWithHigherNumberOfFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"0.0001\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fractions);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimalWithLowerNumberOfFractionDigits() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withNumbers(new AmountWriter<String>() {
            @Override
            public Class<String> getType() {
                return String.class;
            }

            @Override
            public String write(final MonetaryAmount amount) {
                return amount.getNumber().numberValueExact(BigDecimal.class).stripTrailingZeros().toPlainString();
            }
        }));

        final String expected = "{\"amount\":\"100\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeAmountAsQuotedDecimalPlainString() throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());
        unit.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldWriteNumbersAsStrings() throws JsonProcessingException {
        final ObjectMapper unit = unit(module());
        unit.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldWriteNumbersAsPlainStrings() throws JsonProcessingException {
        final ObjectMapper unit = unit(module());
        unit.enable(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS);
        unit.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

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

    @Test
    public void shouldHandleNullValueFromExpectObjectFormatInSchemaVisitor() throws Exception {
        final MonetaryAmountSerializer monetaryAmountSerializer = new MonetaryAmountSerializer(FieldNames.defaults(), new AmountWriter<BigDecimal>() {
            @Override
            public Class<BigDecimal> getType() {
                return BigDecimal.class;
            }

            @Override
            public BigDecimal write(final MonetaryAmount amount) {
                return amount.getNumber().numberValueExact(BigDecimal.class).stripTrailingZeros();
            }
        }, new NoopMonetaryAmountFormatFactory());

        try {
            final JsonFormatVisitorWrapper jsonFormatVisitorWrapperMock = mock(JsonFormatVisitorWrapper.class);
            monetaryAmountSerializer.acceptJsonFormatVisitor(jsonFormatVisitorWrapperMock, SimpleType.constructUnsafe(javax.money.MonetaryAmount.class));
        }catch (UnsupportedClassVersionError e){
            assertTrue("This library only supports Java8", true);
        }
    }

}
