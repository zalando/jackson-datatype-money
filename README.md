# Jackson Datatype Money

[![Build Status](https://img.shields.io/travis/zalando/jackson-datatype-money/master.svg)](https://travis-ci.org/zalando/jackson-datatype-money)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-datatype-money/master.svg)](https://coveralls.io/r/zalando/jackson-datatype-money)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.zalando/jackson-datatype-money/badge.svg)](http://www.javadoc.io/doc/org.zalando/jackson-datatype-money)
[![Release](https://img.shields.io/github/release/zalando/jackson-datatype-money.svg)](https://github.com/zalando/jackson-datatype-money/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-datatype-money.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-datatype-money)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/jackson-datatype-money/master/LICENSE)


*Jackson Datatype Money* is a [Jackson](https://github.com/codehaus/jackson) module to support JSON serialization and
deserialization of [JavaMoney](https://github.com/JavaMoney/jsr354-api) data types. It fills a niche, in that it
integrates JavaMoney and Jackson so that they work seamlessly together, without requiring additional
developer effort. In doing so, it aims to perform a small but repetitive task — once and for all.

This library reflects our API preferences for [representing monetary amounts in JSON](MONEY.md):

```json
{
  "amount": 29.95,
  "currency": "EUR"
}
```

## Features
- enables you to express monetary amounts in JSON
- can be used in a REST APIs
- customized field names
- localization of formatted monetary amounts
- allows you to implement RESTful API endpoints that format monetary amounts based on the Accept-Language header
- is unique and flexible

## Dependencies
- Java 7 or higher
- Any build tool using Maven Central, or direct download
- Jackson
- JavaMoney

## Installation

Add the following dependency to your project:

```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>jackson-datatype-money</artifactId>
    <version>${jackson-datatype-money.version}</version>
</dependency>
```
For ultimate flexibility, this module is compatible with the official version as well as the backport of JavaMoney. The actual version will be selected by a profile based on the current JDK version.

## Configuration

Register the module with your `ObjectMapper`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule());
```

Alternatively, you can use the SPI capabilities:

```java
ObjectMapper mapper = new ObjectMapper()
    .findAndRegisterModules();
```

### Serialization

For serialization this module currently supports the following data types:

| Input                                                                                                                             | Standard                                          | Output                                 |
|-----------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|----------------------------------------|
| [`javax.money.CurrencyUnit`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/CurrencyUnit.java)     | [ISO-4217](http://en.wikipedia.org/wiki/ISO_4217) | `EUR`                                  |
| [`javax.money.NumberValue`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/NumberValue.java)       |                                                   | `99.95`                                |
| [`javax.money.MonetaryAmount`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/MonetaryAmount.java) |                                                   | `{"amount": 99.95, "currency": "EUR"}` |


By default amount's number value is serialized as a JSON number.
To serialize number as a JSON string, you have to configure the quoted decimal number value serializer:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withNumberValueSerializer(new QuotedDecimalNumberValueSerializer()));
```

```json
{
  "amount": "99.95",
  "currency": "EUR"
}
```

### Formatting

A special feature for serializing monetary amounts is *formatting*, which is **disabled by default**. To enable it, you
have to pass in a `MonetaryAmountFormatFactory` implementation to the `MoneyModule`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withFormatFactory(new DefaultMonetaryAmountFormatFactory()));
```

The `DefaultMonetaryAmountFormatFactory` delegates directly to `MonetaryFormats.getAmountFormat(Locale, String...)`.

Formatting only affects the serialization and can be customized based on the *current* locale, as defined by the
[`SerializationConfig`](https://fasterxml.github.io/jackson-databind/javadoc/2.0.0/com/fasterxml/jackson/databind/SerializationConfig.html#with\(java.util.Locale\)). This allows to implement RESTful API endpoints
that format monetary amounts based on the `Accept-Language` header.

The first example serializes a monetary amount using the `de_DE` locale:

```java
ObjectWriter writer = mapper.writer().with(Locale.GERMANY);
writer.writeValueAsString(Money.of(29.95, "EUR"));
```

```json
{
  "amount": 29.95,
  "currency": "EUR",
  "formatted": "29,95 EUR"
}
```

The following example uses `en_US`:

```java
ObjectWriter writer = mapper.writer().with(Locale.US);
writer.writeValueAsString(Money.of(29.95, "USD"));
```

```json
{
  "amount": 29.95,
  "currency": "USD",
  "formatted": "USD29.95"
}
```

More sophisticated formatting rules can be supported by implementing `MonetaryAmountFormatFactory` directly.

### Deserialization

This module will use `org.javamoney.moneta.Money` as an implementation for `javax.money.MonetaryAmount` by default when
deserializing money values. If you need a different implementation, you can pass a different `MonetaryAmountFactory`
to the `MoneyModule`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withAmountFactory(new FastMoneyFactory()));
```

If you're using Java 8, you can also pass in a method reference:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule().withAmountFactory(FastMoney::of));
```

*Jackson Datatype Money* comes with support for all `MonetaryAmount` implementations from Moneta, the reference
implementation of JavaMoney:

| `MonetaryAmount` Implementation     | Factory                                                                                                                               |
|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `org.javamoney.moneta.FastMoney`    | [`org.zalando.jackson.datatype.money.FastMoneyFactory`](src/main/java/org/zalando/jackson/datatype/money/FastMoneyFactory.java)       |
| `org.javamoney.moneta.Money`        | [`org.zalando.jackson.datatype.money.MoneyFactory`](src/main/java/org/zalando/jackson/datatype/money/MoneyFactory.java)               |
| `org.javamoney.moneta.RoundedMoney` | [`org.zalando.jackson.datatype.money.RoundedMoneyFactory`](src/main/java/org/zalando/jackson/datatype/money/RoundedMoneyFactory.java) |                                                                                                                             |

Module supports deserialization of amount number from JSON number as well as from JSON string without any special configuration required.

### Custom Field Names

As you have seen in the previous examples the `MoneyModule` uses the field names `amount`, `currency` and `formatted`
 by default. Those names can be overridden if desired:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withFieldNames(FieldNames.valueOf("value", "unit", "pretty")));
```

Overriding only one of them can be achieved by using:

```java
FieldNames.defaults().withCurrency("unit")
```

## Usage

After registering and configuring the module you're now free to directly use `MonetaryAmount` in your data types:

```java
import javax.money.MonetaryAmount;

public class Product {
    private String sku;
    private MonetaryAmount price;
    ...
}
```

## Getting help

If you have questions, concerns, bug reports, etc, please file an issue in this repository's Issue Tracker.

## Getting involved

To contribute, simply make a pull request and add a brief description (1-2 sentences) of your addition or change.
Please note that we aim to keep this project straightforward and focused. We are not looking to add lots of features;
we just want it to keep doing what it does, as well and as powerfully as possible. For more details check the
[contribution guidelines](CONTRIBUTING.md).
