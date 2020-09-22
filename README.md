# Jackson Datatype Money

[![Stability: Sustained](https://masterminds.github.io/stability/sustained.svg)](https://masterminds.github.io/stability/sustained.html)
![Build Status](https://github.com/zalando/jackson-datatype-money/workflows/build/badge.svg)
[![Coverage Status](https://img.shields.io/coveralls/zalando/jackson-datatype-money/main.svg)](https://coveralls.io/r/zalando/jackson-datatype-money)
[![Code Quality](https://img.shields.io/codacy/grade/7fdac4ae509b403eb837b246e288856f/main.svg)](https://www.codacy.com/app/whiskeysierra/jackson-datatype-money)
[![Javadoc](http://javadoc.io/badge/org.zalando/jackson-datatype-money.svg)](http://www.javadoc.io/doc/org.zalando/jackson-datatype-money)
[![Release](https://img.shields.io/github/release/zalando/jackson-datatype-money.svg)](https://github.com/zalando/jackson-datatype-money/releases)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/jackson-datatype-money.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/jackson-datatype-money)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/zalando/jackson-datatype-money/main/LICENSE)


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
- Java 8 or higher
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

For serialization this module currently supports
[`javax.money.MonetaryAmount`](https://github.com/JavaMoney/jsr354-api/blob/master/src/main/java/javax/money/MonetaryAmount.java)
and will, by default, serialize it as:

```json
{
  "amount": 99.95,
  "currency": "EUR"
}
```

To serialize number as a JSON string, you have to configure the quoted decimal number value serializer:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule().withQuotedDecimalNumbers());
```

```json
{
  "amount": "99.95",
  "currency": "EUR"
}
```

### Formatting

A special feature for serializing monetary amounts is *formatting*, which is **disabled by default**. To enable it, you
have to either enable default formatting:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule().withDefaultFormatting());
```

... or pass in a `MonetaryAmountFormatFactory` implementation to the `MoneyModule`:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withFormatting(new CustomMonetaryAmountFormatFactory()));
```

The default formatting delegates directly to `MonetaryFormats.getAmountFormat(Locale, String...)`.

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
        .withMonetaryAmount(new CustomMonetaryAmountFactory()));
```

You can also pass in a method reference:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withMonetaryAmount(FastMoney::of));
```

*Jackson Datatype Money* comes with support for all `MonetaryAmount` implementations from Moneta, the reference
implementation of JavaMoney:

| `MonetaryAmount` Implementation     | Factory                                                                                                                               |
|-------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| `org.javamoney.moneta.FastMoney`    | [`new MoneyModule().withFastMoney()`](src/main/java/org/zalando/jackson/datatype/money/FastMoneyFactory.java)       |
| `org.javamoney.moneta.Money`        | [`new MoneyModule().withMoney()`](src/main/java/org/zalando/jackson/datatype/money/MoneyFactory.java)               |
| `org.javamoney.moneta.RoundedMoney` | [`new MoneyModule().withRoundedMoney()`](src/main/java/org/zalando/jackson/datatype/money/RoundedMoneyFactory.java) |                                                                                                                             |

Module supports deserialization of amount number from JSON number as well as from JSON string without any special configuration required.

### Custom Field Names

As you have seen in the previous examples the `MoneyModule` uses the field names `amount`, `currency` and `formatted`
 by default. Those names can be overridden if desired:

```java
ObjectMapper mapper = new ObjectMapper()
    .registerModule(new MoneyModule()
        .withAmountFieldName("value")
        .withCurrencyFieldName("unit")
        .withFormattedFieldName("pretty"));
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
[contribution guidelines](.github/CONTRIBUTING.md).
