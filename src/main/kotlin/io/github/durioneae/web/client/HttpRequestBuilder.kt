package io.github.durioneae.web.client

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.retry.support.RetryTemplate

interface HttpRequestBuilder {
    fun header(key: String, vararg value: String): HttpRequestBuilder

    fun pathSegment(vararg pathSegments: String): HttpRequestBuilder

    fun queryParam(name: String, vararg values: Any): HttpRequestBuilder

    fun noRetry(): HttpRequestBuilder

    fun withRetry(retryTemplate: RetryTemplate, vararg statusCodeToNotRetry: HttpStatus): HttpRequestBuilder

    fun path(path: String): HttpRequestBuilder

    fun <T, R> put(payload: T, aClass: Class<R>): ResponseEntity<R>

    fun <T, R> post(payload: T, aClass: Class<R>): ResponseEntity<R>

    fun <T, R> patch(payload: T, aClass: Class<R>): ResponseEntity<R>

    fun <T> get(aClass: Class<T>): ResponseEntity<T>

    fun head(): ResponseEntity<Void>

    fun <T> get(aClass: ParameterizedTypeReference<T>): ResponseEntity<T>

    fun delete(): ResponseEntity<*>
}
