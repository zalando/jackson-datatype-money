package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.junit.Test;

import javax.money.MonetaryAmount;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class MonetaryAmountSchemaSerializerTest {

    @Test
    public void shouldSerializeJsonSchema() throws Exception {
        ObjectMapper unit = unit(module());
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"amount\":{\"type\":\"number\",\"required\":true}," +
                "\"currency\":{\"type\":\"string\",\"required\":true}," +
                "\"formatted\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeJsonSchemaWithCustomFieldNames() throws Exception {
        ObjectMapper unit = unit(module().withAmountFieldName("value")
                                         .withCurrencyFieldName("unit")
                                         .withFormattedFieldName("pretty"));
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"value\":{\"type\":\"number\",\"required\":true}," +
                "\"unit\":{\"type\":\"string\",\"required\":true}," +
                "\"pretty\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    @Test
    public void shouldSerializeJsonSchemaWithQuotedDecimalNumbers() throws Exception {
        ObjectMapper unit = unit(module().withQuotedDecimalNumbers());
        JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"amount\":{\"type\":\"string\",\"required\":true}," +
                "\"currency\":{\"type\":\"string\",\"required\":true}," +
                "\"formatted\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private MoneyModule module() {
        return new MoneyModule();
    }
}
