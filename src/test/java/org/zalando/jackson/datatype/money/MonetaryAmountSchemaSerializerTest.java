package org.zalando.jackson.datatype.money;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

final class MonetaryAmountSchemaSerializerTest {

    @Test
    void shouldSerializeJsonSchema() throws Exception {
        final ObjectMapper unit = unit(module());
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"amount\":{\"type\":\"number\",\"required\":true}," +
                "\"currency\":{\"type\":\"string\",\"required\":true}," +
                "\"formatted\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    @Test
    void shouldSerializeJsonSchemaWithCustomFieldNames() throws Exception {
        final ObjectMapper unit = unit(module().withAmountFieldName("value")
                                         .withCurrencyFieldName("unit")
                                         .withFormattedFieldName("pretty"));
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"value\":{\"type\":\"number\",\"required\":true}," +
                "\"unit\":{\"type\":\"string\",\"required\":true}," +
                "\"pretty\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    @Test
    void shouldSerializeJsonSchemaWithQuotedDecimalNumbers() throws Exception {
        final ObjectMapper unit = unit(module().withQuotedDecimalNumbers());
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(unit);
        final JsonSchema jsonSchema = generator.generateSchema(MonetaryAmount.class);
        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"type\":\"object\",\"id\":\"urn:jsonschema:javax:money:MonetaryAmount\",\"properties\":" +
                "{\"amount\":{\"type\":\"string\",\"required\":true}," +
                "\"currency\":{\"type\":\"string\",\"required\":true}," +
                "\"formatted\":{\"type\":\"string\"}}}";

        assertThat(actual, is(expected));
    }

    @Test
    void shouldSerializeJsonSchemaWithMultipleMonetayAmountsAndAlternativeGenerator() throws Exception {
        final ObjectMapper unit = unit(module());
        final com.kjetland.jackson.jsonSchema.JsonSchemaGenerator generator =
                new com.kjetland.jackson.jsonSchema.JsonSchemaGenerator(unit);

        final JsonNode jsonSchema = generator.generateJsonSchema(SchemaTestClass.class);

        final String actual = unit.writeValueAsString(jsonSchema);
        final String expected = "{\"$schema\":\"http://json-schema.org/draft-04/schema#\",\"title\":\"Schema Test Class\"," +
                "\"type\":\"object\",\"additionalProperties\":false,\"properties\":{\"moneyOne\":{\"$ref\":" +
                "\"#/definitions/MonetaryAmount\"},\"moneyTwo\":{\"$ref\":\"#/definitions/MonetaryAmount\"}}," +
                "\"definitions\":{\"MonetaryAmount\":{\"type\":\"object\",\"additionalProperties\":false,\"properties\"" +
                ":{\"amount\":{\"type\":\"number\"},\"currency\":{\"type\":\"string\"},\"formatted\":" +
                "{\"type\":\"string\"}},\"required\":[\"amount\",\"currency\"]}}}";

        assertThat(actual, is(expected));
    }

    private ObjectMapper unit(final Module module) {
        return new ObjectMapper().registerModule(module);
    }

    private MoneyModule module() {
        return new MoneyModule();
    }
}
