package io.github.durioneae.web.client.retry

import org.springframework.http.HttpStatus
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

class HttpRetryService(private val retryTemplate: RetryTemplate, private vararg val statusCodeToNotRetry: HttpStatus) {
    fun <R> doWithRetry(function: () -> R): R {
        return retryTemplate.execute<R, Throwable> {
            try {
                function.invoke()
            } catch (e: RuntimeException) {
                if (e.cause is HttpClientErrorException) {
                    val httpStatus = (e.cause as HttpClientErrorException).statusCode
                    if (statusCodeToNotRetry.contains(httpStatus)) {
                        it.setExhaustedOnly()
                    }
                } else if (e is HttpServerErrorException) {
                    val httpStatus = e.statusCode
                    if (statusCodeToNotRetry.contains(httpStatus)) {
                        it.setExhaustedOnly()
                    }
                }
                throw e
            }

        }
    }
}
