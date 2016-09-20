package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.RoundedMoney;
import org.junit.Test;

import javax.money.MonetaryAmount;
import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public final class MonetaryAmountDeserializerTest {

    @Test
    public void shouldDeserializeMoneyByDefault() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(instanceOf(Money.class)));
    }

    @Test
    public void shouldDeserializeMoneyCorrectly() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        final BigDecimal expected = new BigDecimal("29.95");

        assertThat(actual, comparesEqualTo(expected));
    }

    @Test
    public void shouldDeserializeFastMoney() throws IOException {
        final ObjectMapper unit = new ObjectMapper().registerModule(new MoneyModule(new FastMoneyFactory()));

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(instanceOf(FastMoney.class)));
    }

    @Test
    public void shouldDeserializeFastMoneyCorrectly() throws IOException {
        final ObjectMapper unit = new ObjectMapper().registerModule(new MoneyModule(new FastMoneyFactory()));

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        final BigDecimal expected = new BigDecimal("29.95");

        assertThat(actual, comparesEqualTo(expected));
    }

    @Test
    public void shouldDeserializeRoundedMoney() throws IOException {
        final ObjectMapper unit = new ObjectMapper().registerModule(new MoneyModule(new RoundedMoneyFactory()));

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(instanceOf(RoundedMoney.class)));
    }

    @Test
    public void shouldDeserializeRoundedMoneyCorrectly() throws IOException {
        final ObjectMapper unit = new ObjectMapper().registerModule(new MoneyModule(new RoundedMoneyFactory()));

        final String content = "{\"amount\":29.95,\"currency\":\"EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        final BigDecimal expected = new BigDecimal("29.95");

        assertThat(actual, comparesEqualTo(expected));
    }
    
    @Test
    public void shouldDeserializeCorrectlyWhenPropertiesAreInDifferentOrder() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();

        final String content = "{\"currency\":\"EUR\",\"amount\":29.95}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        final BigDecimal actual = amount.getNumber().numberValueExact(BigDecimal.class);
        assertThat(actual, comparesEqualTo(new BigDecimal("29.95")));
    }

    @Test
    public void shouldIgnoreFormattedValue() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();
        
        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(notNullValue()));
    }
    
    @Test
    public void shouldUpdateExistingValueUsingTreeTraversingParser() throws IOException {
        final ObjectMapper unit = new ObjectMapper().findAndRegisterModules();
        
        final String content = "{\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"}";
        final MonetaryAmount amount = unit.readValue(content, MonetaryAmount.class);

        assertThat(amount, is(notNullValue()));
        
        // we need a json node to get a TreeTraversingParser with codec of type ObjectReader
        JsonNode ownerNode = unit.readTree("{ \"value\" : {\"amount\":29.95,\"currency\":\"EUR\",\"formatted\":\"30.00 EUR\"} }");
        
        
        final Owner owner = new Owner();
        owner.setValue(amount);
        
        // try to update
        Owner result = unit.readerForUpdating(owner).readValue(ownerNode);
        assertThat(result, is(notNullValue()));
        assertThat(result.getValue(), is(amount));
    }
    
    private static class Owner {
        
        private MonetaryAmount value;

        public MonetaryAmount getValue() {
            return value;
        }

        public void setValue(MonetaryAmount value) {
            this.value = value;
        }
    }

}
