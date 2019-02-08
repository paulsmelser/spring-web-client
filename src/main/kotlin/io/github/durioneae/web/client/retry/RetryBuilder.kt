package io.github.durioneae.web.client.retry

import org.springframework.retry.support.RetryTemplate

interface RetryBuilder {

    fun build(): RetryTemplate

    companion object {
        fun defaultRetry(): RetryTemplate {
            return exponential().maxAttempts(3).initialInterval(100).multiplier(1.5).maxInterval(225).build()
        }

        fun exponential(): ExponentialRetryBuilder {
            return ExponentialRetryBuilder.builder()
        }
    }
}
