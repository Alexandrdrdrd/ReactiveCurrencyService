# Currency Exchange API

This project provides a Currency Exchange API built with Spring WebFlux and R2DBC. It offers endpoints to retrieve exchange rates and perform currency conversions based on the latest data, which is updated daily.

## Endpoints

### 1. Get All Exchange Rates

#### Endpoint
GET /api/exchange-rates

#### Description
Returns all available exchange rates.

#### Response
- HTTP Status: 200 OK
- Content-Type: application/json
- Body: An array of ExchangeRate objects.

### 2. Get Exchange Rates for a Specific Currency

#### Endpoint
GET /api/get-exchange-rates-for/{currency}

#### Description
Returns exchange rates for a specific currency. If the currency is "UAH" (Ukrainian Hryvnia), it returns all exchange rates. Otherwise, it calculates the exchange rates relative to the specified currency.

#### Parameters
- `currency` (Path Variable) - The currency code (e.g., USD, EUR, GBP).

#### Response
- HTTP Status: 200 OK
- Content-Type: application/json
- Body: An array of ExchangeRate objects.

### 3. Get Exchange Rates for a Currency Pair

#### Endpoint
GET /api/currency-exchange/from/{from}/to/{to}

#### Description
Returns the exchange rate between two currencies.

#### Parameters
- `from` (Path Variable) - The currency code of the base currency (e.g., USD, EUR, GBP).
- `to` (Path Variable) - The currency code of the target currency (e.g., USD, EUR, GBP).

#### Response
- HTTP Status: 200 OK
- Content-Type: application/json
- Body: A FromToRate object containing the exchange rate between the specified currencies.

### Error Handling

If any request does not find matching exchange rates, the API will return a 404 Not Found status with an error message.

## Dependencies

The project uses the following dependencies:

- `spring-boot-starter-data-r2dbc`: For Reactive SQL support with R2DBC.
- `spring-boot-starter-webflux`: For building reactive web applications.
- `postgresql` and `r2dbc-postgresql`: For the PostgreSQL database and R2DBC driver.
- `spring-boot-starter-test`: For testing support in Spring Boot applications.
- `io.projectreactor:reactor-test`: For testing Reactor components.
- `junit:junit`: For JUnit testing.

## Data Update

The project automatically updates the exchange rates data daily to ensure accurate and up-to-date information.

Feel free to explore and use the API endpoints for currency exchange and rate retrieval!



