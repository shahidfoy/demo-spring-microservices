package com.shahidfoy.microservices.currencyexchangeservice;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/sample-api")
    // @Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
    @CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    public String sampleApi() {
        logger.info("sample api call received");
        ResponseEntity<String> responseEntity = new RestTemplate().getForEntity("http://localhost:8080/some/url", String.class);
        return responseEntity.getBody();
    }

    @GetMapping("/sample-api-rate-limit")
    // rate limiter every 10s limit 2 calls to service
    @RateLimiter(name="default")
    public String sampleApiRateLimit() {
        logger.info("sample api rate limit call received");
        return "sample api rate limit";
    }

    @GetMapping("/sample-api-bulkhead")
    @Bulkhead(name="sample-api")
    public String sampleApiBulkhead() {
        logger.info("sample api bulkhead call received");
        return "sample api bulkhead";
    }

    public String hardcodedResponse(Exception ex) {
        return "fallback-response";
    }
}
