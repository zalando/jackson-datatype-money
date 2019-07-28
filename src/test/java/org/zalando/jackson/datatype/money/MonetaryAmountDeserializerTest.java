package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class MonetaryAmountDeserializerTest<M extends MonetaryAmount> {

    static Iterable<Arguments> data() {
        return Arrays.asList(
                of(MonetaryAmount.class, module -> module),
                of(FastMoney.class, MoneyModule::withFastMoney),
                of(Money.class, MoneyModule::withMoney),
                of(RoundedMoney.class, MoneyModule::withRoundedMoney),
                of(RoundedMoney.class, module -> module.withRoundedMoney(Monetary.getDefaultRounding()))
        );
    }

    private static <M extends MonetaryAmount> Arguments of(final Class<M> type, final Configurer configurer) {
        return Arguments.of(type, configurer);
    }

    private interface Configurer {
        MoneyModule configure(MoneyModule module);
    }

    private ObjectMapper unit(final Configurer configurer) {
        return unit(module(configurer));
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private MoneyModule module(final Configurer configurer) {
        return configurer.configure(new MoneyModule());
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeMoneyByDefault() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(instanceOf(Money.class)));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeToCorrectType(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount, is(instanceOf(type)));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserialize(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeWithHighNumberOfFractionDigits(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.9501,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.9501")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeCorrectlyWhenAmountIsAStringValue(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":\"29.95\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeWithCustomNames(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(module(configurer)
                .withAmountFieldName("value")
                .withCurrencyFieldName("unit"));

        final String content = "{\"value\":29.95,\"unit\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldIgnoreFormattedValue(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), is(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldUpdateExistingValueUsingTreeTraversingParser(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount, is(notNullValue()));

        // we need a json node to get a TreeTraversingParser with codec of type ObjectReader
        final JsonNode ownerNode =
                unit.readTree("{\"value\":{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00EUR\"}}");

        final Owner owner = new Owner();
        owner.setValue(amount);

        // try to update
        final Owner result = unit.readerForUpdating(owner).readValue(ownerNode);
        assertThat(result, is(notNullValue()));
        assertThat(result.getValue(), is(amount));
    }

    private static class Owner {

        private MonetaryAmount value;

        MonetaryAmount getValue() {
            return value;
        }

        void setValue(final MonetaryAmount value) {
            this.value = value;
        }

    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldFailToDeserializeWithoutAmount(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"currency\":\"EUR\"}";

        final JsonProcessingException exception = assertThrows(
                JsonProcessingException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage(), containsString("Missing property: 'amount'"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldFailToDeserializeWithoutCurrency(final Class<M> type, final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95}";

        final JsonProcessingException exception = assertThrows(
                JsonProcessingException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage(), containsString("Missing property: 'currency'"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) {
        final ObjectMapper unit = unit(configurer);

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";

        final JsonProcessingException exception = assertThrows(
                UnrecognizedPropertyException.class, () -> unit.readValue(content, type));

        assertThat(exception.getMessage(), startsWith(
                "Unrecognized field \"version\" (class javax.money.MonetaryAmount), " +
                        "not marked as ignorable (3 known properties: \"amount\", \"currency\", \"formatted\"])"));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotFailToDeserializeWithAdditionalProperties(final Class<M> type,
            final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"source\":{\"provider\":\"ECB\",\"date\":\"2016-09-29\"},\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";
        unit.readValue(content, type);
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeWithTypeInformation(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer)
                .enableDefaultTypingAsProperty(BasicPolymorphicTypeValidator.builder().build(), DefaultTyping.OBJECT_AND_NON_CONCRETE, "type")
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"type\":\"org.javamoney.moneta.Money\",\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        // type information is ignored?!
        assertThat(amount, is(instanceOf(type)));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldDeserializeWithoutTypeInformation(final Class<M> type, final Configurer configurer) throws IOException {
        final ObjectMapper unit = unit(configurer).enableDefaultTyping(BasicPolymorphicTypeValidator.builder().build());

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        assertThat(amount, is(instanceOf(type)));
    }

}
