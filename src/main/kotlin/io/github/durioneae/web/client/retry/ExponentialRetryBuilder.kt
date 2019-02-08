package io.github.durioneae.web.client.retry

import java.util.Objects.nonNull

import org.springframework.retry.backoff.BackOffPolicy
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

class ExponentialRetryBuilder : RetryBuilder {
    private var maxAttempts: Int = 0
    private var initialInterval: Int = 0
    private var multiplier: Double = 0.0
    private var maxInterval: Int = 0

    fun maxAttempts(maxAttempts: Int): ExponentialRetryBuilder {
        this.maxAttempts = maxAttempts
        return this
    }

    fun initialInterval(initialInterval: Int): ExponentialRetryBuilder {
        this.initialInterval = initialInterval
        return this
    }

    fun multiplier(multiplier: Double): ExponentialRetryBuilder {
        this.multiplier = multiplier
        return this
    }

    fun maxInterval(maxInterval: Int): ExponentialRetryBuilder {
        this.maxInterval = maxInterval
        return this
    }

    override fun build(): RetryTemplate {
        val template = RetryTemplate()
        template.setBackOffPolicy(buildBackoffPolicy(
                if (nonNull(initialInterval)) initialInterval else DEFAULT_INITIAL_INTERVAL,
                if (nonNull(multiplier)) multiplier else DEFAULT_MULTIPLIER,
                if (nonNull(maxInterval)) maxInterval else DEFAULT_MAX_INTERVAL))
        template.setRetryPolicy(buildRetryPolicy(if (nonNull(maxAttempts)) maxAttempts else DEFAULT_MAX_ATTEMPTS))
        return template
    }

    companion object {
        private const val DEFAULT_MAX_ATTEMPTS = 3
        private const val DEFAULT_INITIAL_INTERVAL = 50
        private const val DEFAULT_MULTIPLIER = 1.5
        private const val DEFAULT_MAX_INTERVAL = 175

        fun builder(): ExponentialRetryBuilder {
            return ExponentialRetryBuilder()
        }

        private fun buildBackoffPolicy(initialInterval: Int, multiplier: Double, maxInterval: Int): BackOffPolicy {
            val backOffPolicy = ExponentialBackOffPolicy()
            backOffPolicy.initialInterval = initialInterval.toLong()
            backOffPolicy.multiplier = multiplier
            backOffPolicy.maxInterval = maxInterval.toLong()
            return backOffPolicy
        }

        private fun buildRetryPolicy(maxAttemtps: Int): SimpleRetryPolicy {
            val policy = SimpleRetryPolicy()
            policy.maxAttempts = maxAttemtps
            return policy
        }
    }
}
