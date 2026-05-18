package com.tcg.portal.infrastructure.scryfall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enforces Scryfall's recommended ≤10 req/s rate limit across all threads
 * and retries 429 responses with exponential back-off.
 *
 * Registered on the scryfallRestClient bean so every provider in the
 * chain is covered without any per-provider changes.
 */
@Component
public class ScryfallRateLimitInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ScryfallRateLimitInterceptor.class);

    private static final long MIN_INTERVAL_MS = 110;   // ~9 req/s — safely below 10 req/s
    private static final int  MAX_RETRIES     = 3;

    private final AtomicLong lastRequestMs = new AtomicLong(0);

    @Override
    public ClientHttpResponse intercept(HttpRequest request,
                                        byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        throttle();
        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode().value() != 429) {
            return response;
        }

        // 429 — respect Retry-After if present, otherwise exponential back-off
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            long delay = retryDelayMs(response, attempt);
            log.warn("Scryfall 429 on {} (attempt {}/{}), waiting {}ms",
                    request.getURI().getPath(), attempt, MAX_RETRIES, delay);
            response.close();
            sleep(delay);
            throttle();
            response = execution.execute(request, body);
            if (response.getStatusCode().value() != 429) break;
        }

        return response;
    }

    private synchronized void throttle() {
        long wait = MIN_INTERVAL_MS - (System.currentTimeMillis() - lastRequestMs.get());
        if (wait > 0) sleep(wait);
        lastRequestMs.set(System.currentTimeMillis());
    }

    private long retryDelayMs(ClientHttpResponse response, int attempt) {
        String retryAfter = response.getHeaders().getFirst("Retry-After");
        if (retryAfter != null) {
            try { return Long.parseLong(retryAfter.trim()) * 1000; } catch (NumberFormatException ignored) {}
        }
        return 1000L * attempt;   // 1s, 2s, 3s
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
