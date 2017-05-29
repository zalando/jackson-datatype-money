package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.annotation.Nullable;
import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.zalando.jackson.datatype.money.FieldNames.defaults;

@RunWith(Parameterized.class)
public final class MonetaryAmountDeserializerTest<M extends MonetaryAmount> {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Class<M> type;
    private final MonetaryAmountFactory<M> factory;

    public MonetaryAmountDeserializerTest(final Class<M> type, @Nullable final MonetaryAmountFactory<M> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Parameters(name = "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {MonetaryAmount.class, null},
                {FastMoney.class, new FastMoneyFactory()},
                {Money.class, new MoneyFactory()},
                {RoundedMoney.class, new RoundedMoneyFactory()},
        });
    }

    private ObjectMapper unit() {
        return unit(module());
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private MoneyModule module() {
        return factory == null ? new MoneyModule() : new MoneyModule().withAmountFactory(factory);
    }

    @Test
    public void shouldDeserializeMoneyByDefault() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(instanceOf(Money.class)));
    }

    @Test
    public void shouldDeserializeToCorrectType() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount, is(instanceOf(type)));
    }

    @Test
    public void shouldDeserializeCorrectly() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldDeserializeCorrectlyWhenAmountIsAStringValue() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"currency\":\"EUR\",\"amount\":\"29.95\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldDeserializeWithCustomNames() throws IOException {
        final ObjectMapper unit = unit(module()
                .withFieldNames(defaults()
                        .withAmount("value")
                        .withCurrency("unit")));

        final String content = "{\"value\":29.95,\"unit\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldIgnoreFormattedValue() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, type);

        assertThat(amount.getNumber().numberValueExact(BigDecimal.class), comparesEqualTo(new BigDecimal("29.95")));
        assertThat(amount.getCurrency().getCurrencyCode(), is("EUR"));
    }

    @Test
    public void shouldUpdateExistingValueUsingTreeTraversingParser() throws IOException {
        final ObjectMapper unit = unit();

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

        public MonetaryAmount getValue() {
            return value;
        }

        public void setValue(final MonetaryAmount value) {
            this.value = value;
        }

    }

    @Test
    public void shouldFailToDeserializeWithoutAmount() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"currency\":\"EUR\"}";

        exception.expect(JsonProcessingException.class);
        exception.expectMessage("Missing property: 'amount'");

        unit.readValue(content, type);
    }

    @Test
    public void shouldFailToDeserializeWithoutCurrency() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"amount\":29.95}";

        exception.expect(JsonProcessingException.class);
        exception.expectMessage("Missing property: 'currency'");

        unit.readValue(content, type);
    }

    @Test
    public void shouldFailToDeserializeWithAdditionalProperties() throws IOException {
        final ObjectMapper unit = unit();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";

        exception.expect(UnrecognizedPropertyException.class);
        exception.expectMessage(startsWith(
                "Unrecognized field \"version\" (class javax.money.MonetaryAmount), " +
                        "not marked as ignorable (3 known properties: \"amount\", \"currency\", \"formatted\"])"));

        unit.readValue(content, type);
    }

    @Test
    public void shouldNotFailToDeserializeWithAdditionalProperties() throws IOException {
        final ObjectMapper unit = unit().disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"source\":{\"provider\":\"ECB\",\"date\":\"2016-09-29\"},\"amount\":29.95,\"currency\":\"EUR\",\"version\":\"1\"}";
        unit.readValue(content, type);
    }

    @Test
    public void shouldDeserializeWithTypeInformation() throws IOException {
        final ObjectMapper unit = unit(module())
                .enableDefaultTypingAsProperty(DefaultTyping.OBJECT_AND_NON_CONCRETE, "type")
                .disable(FAIL_ON_UNKNOWN_PROPERTIES);

        final String content = "{\"type\":\"org.javamoney.moneta.Money\",\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        // type information is ignored?!
        assertThat(amount, is(instanceOf(type)));
    }

    @Test
    public void shouldDeserializeWithoutTypeInformation() throws IOException {
        final ObjectMapper unit = unit(module()).enableDefaultTyping();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final M amount = unit.readValue(content, type);

        assertThat(amount, is(instanceOf(type)));
    }

}
