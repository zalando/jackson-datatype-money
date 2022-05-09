package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.SimpleType;
import lombok.Value;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import static javax.money.Monetary.getDefaultRounding;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

final class MonetaryAmountSerializerTest {

    static Iterable<MonetaryAmount> amounts() {
        return Arrays.asList(
                FastMoney.of(29.95, "EUR"),
                Money.of(29.95, "EUR"),
                RoundedMoney.of(29.95, "EUR", getDefaultRounding()));
    }

    static Iterable<MonetaryAmount> hundreds() {
        return Arrays.asList(
                FastMoney.of(100, "EUR"),
                Money.of(100, "EUR"),
                RoundedMoney.of(100, "EUR", getDefaultRounding()));
    }

    static Iterable<MonetaryAmount> fractions() {
        return Arrays.asList(
                FastMoney.of(0.0001, "EUR"),
                Money.of(0.0001, "EUR"),
                RoundedMoney.of(0.0001, "EUR", getDefaultRounding()));
    }

    private ObjectMapper unit() {
        return unit(module());
    }

    private ObjectMapper unit(final Module module) {
        return build(module).build();
    }

    private JsonMapper.Builder build() {
        return build(module());
    }

    private JsonMapper.Builder build(final Module module) {
        return JsonMapper.builder()
                .addModule(module);
    }

    private MoneyModule module() {
        return new MoneyModule();
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerialize(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit();

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithoutFormattedValueIfFactoryProducesNull(
            final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withoutFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithFormattedGermanValue(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(new MoneyModule().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithFormattedAmericanValue(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDefaultFormatting());

        final String expected = "{\"amount\":29.95,\"currency\":\"USD\",\"formatted\":\"USD29.95\"}";

        final ObjectWriter writer = unit.writer().with(Locale.US);
        final String actual = writer.writeValueAsString(amount.getFactory().setCurrency("USD").create());

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithCustomName(final MonetaryAmount amount) throws IOException {
        final ObjectMapper unit = unit(module().withDefaultFormatting()
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit")
                .withFormattedFieldName("pretty"));

        final String expected = "{\"value\":29.95,\"unit\":\"EUR\",\"pretty\":\"29,95 EUR\"}";

        final ObjectWriter writer = unit.writer().with(Locale.GERMANY);
        final String actual = writer.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeAmountAsDecimal(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldSerializeAmountAsDecimalWithDefaultFractionDigits(
            final MonetaryAmount hundred) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":100.00,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("fractions")
    void shouldSerializeAmountAsDecimalWithHigherNumberOfFractionDigits(
            final MonetaryAmount fraction) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withDecimalNumbers());

        final String expected = "{\"amount\":0.0001,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fraction);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldSerializeAmountAsDecimalWithLowerNumberOfFractionDigits(
            final MonetaryAmount hundred) throws JsonProcessingException {
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

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeAmountAsQuotedDecimal(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldSerializeAmountAsQuotedDecimalWithDefaultFractionDigits(
            final MonetaryAmount hundred) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("fractions")
    void shouldSerializeAmountAsQuotedDecimalWithHigherNumberOfFractionDigits(
            final MonetaryAmount fraction) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());

        final String expected = "{\"amount\":\"0.0001\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(fraction);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldSerializeAmountAsQuotedDecimalWithLowerNumberOfFractionDigits(
            final MonetaryAmount hundred) throws JsonProcessingException {
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

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldSerializeAmountAsQuotedDecimalPlainString(final MonetaryAmount hundred) throws JsonProcessingException {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());
        unit.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldWriteNumbersAsStrings(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = build()
                .enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
                .build();

        final String expected = "{\"amount\":\"29.95\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(amount);

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("hundreds")
    void shouldWriteNumbersAsPlainStrings(final MonetaryAmount hundred) throws JsonProcessingException {
        final ObjectMapper unit = build()
                .enable(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS)
                .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
                .build();

        final String expected = "{\"amount\":\"100.00\",\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(hundred);

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    private static class Price {
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithType(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module()).activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final String expected = "{\"amount\":{\"amount\":29.95,\"currency\":\"EUR\"}}";
        final String actual = unit.writeValueAsString(new Price(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    private static class PriceUnwrapped {
        @JsonUnwrapped
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithTypeUnwrapped(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module()).activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final String expected = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final String actual = unit.writeValueAsString(new PriceUnwrapped(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Value
    private static class PriceUnwrappedTransformedNames {
        @JsonUnwrapped(prefix = "Price-", suffix = "-Field")
        MonetaryAmount amount;
    }

    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithTypeUnwrappedAndNamesTransformed(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module()).activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final String expected = "{\"Price-amount-Field\":29.95,\"Price-currency-Field\":\"EUR\"}";
        final String actual = unit.writeValueAsString(new PriceUnwrappedTransformedNames(amount));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldHandleNullValueFromExpectObjectFormatInSchemaVisitor() throws Exception {
        final MonetaryAmountSerializer unit = new MonetaryAmountSerializer(FieldNames.defaults(),
                new DecimalAmountWriter(), MonetaryAmountFormatFactory.NONE);

        final JsonFormatVisitorWrapper wrapper = mock(JsonFormatVisitorWrapper.class);
        unit.acceptJsonFormatVisitor(wrapper, SimpleType.constructUnsafe(MonetaryAmount.class));
    }

    /**
     * Fixes a bug that caused the amount field to be written as
     * <code>
     *  "amount": {"BigDecimal":12.34}
     * </code>
     * @param amount
     * @throws JsonProcessingException
     */
    @ParameterizedTest
    @MethodSource("amounts")
    void shouldSerializeWithWrapRootValue(final MonetaryAmount amount) throws JsonProcessingException {
        final ObjectMapper unit = unit(module())
                .configure(SerializationFeature.WRAP_ROOT_VALUE, true);

        final String expected = "{\"Price\":{\"amount\":{\"amount\":29.95,\"currency\":\"EUR\"}}}";
        final String actual = unit.writeValueAsString(new Price(amount));

        assertThat(actual).isEqualTo(expected);
    }

}
