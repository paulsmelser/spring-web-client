package io.github.durioneae.web.client

import io.github.durioneae.web.client.retry.HttpRetryService
import io.github.durioneae.web.client.retry.RetryBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.retry.support.RetryTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.util.Arrays.asList

class HttpRequestBuilder internal constructor(private val restResource: RestClient, val uriBuilder: UriComponentsBuilder) {
    val headers: HttpHeaders = HttpHeaders()

	private var httpRetryService: HttpRetryService? = HttpRetryService(RetryBuilder.defaultRetry(), FORBIDDEN, UNAUTHORIZED)

	fun header(key: String, vararg value: String): HttpRequestBuilder {
        headers[key] = asList(*value)
        return this
    }

    fun pathSegment(vararg pathSegments: String): HttpRequestBuilder {
        uriBuilder.pathSegment(*pathSegments)
        return this
    }

    fun queryParam(name: String, vararg values: Any): HttpRequestBuilder {
        uriBuilder.queryParam(name, *values)
        return this
    }

    fun noRetry(): HttpRequestBuilder {
        httpRetryService = null
        return this
    }

    fun withRetry(retryTemplate: RetryTemplate, vararg statusCodeToNotRetry: HttpStatus): HttpRequestBuilder {
        httpRetryService = HttpRetryService(retryTemplate, *statusCodeToNotRetry)
        return this
    }

    fun path(path: String): HttpRequestBuilder {
        uriBuilder.path(path)
        return this
    }

    inline fun <T, reified R> put(payload: T): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.PUT, HttpEntity(payload, headers), R::class.java)
    }

    inline fun <T, reified R> post(payload: T): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.POST, HttpEntity(payload, headers), R::class.java)
    }

    inline fun <T, reified R> patch(payload: T): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.PATCH, HttpEntity(payload, headers), R::class.java)
    }

    fun <T> get(aClass: Class<T>): ResponseEntity<T> {
        return execute(uriBuilder.build().toUri(), HttpMethod.GET, HttpEntity<Any>(headers), aClass)
    }

    inline fun <reified T> get(): ResponseEntity<T> {
        return execute(uriBuilder.build().toUri(), HttpMethod.GET, HttpEntity<Any>(headers), typeRef())
    }

    fun head(): ResponseEntity<Void> {
        return execute(uriBuilder.build().toUri(), HttpMethod.HEAD, HttpEntity<Any>(headers), Void::class.java)
    }

    fun <T> get(aClass: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return execute(uriBuilder.build().toUri(), HttpMethod.GET, HttpEntity<Any>(headers), aClass)
    }

    fun delete(): ResponseEntity<*> {
        return httpRetryService?.doWithRetry { restResource.delete(uriBuilder.build().toUri(), headers) }
                ?: restResource.delete(uriBuilder.build().toUri(), headers)
    }

    fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: Class<R>): ResponseEntity<R> {
        return httpRetryService?.doWithRetry { restResource.execute(uri, httpMethod, entity, responseType) }
        ?: restResource.execute(uri, httpMethod, entity, responseType)
    }

    fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: ParameterizedTypeReference<R>): ResponseEntity<R> {
        return httpRetryService?.doWithRetry { restResource.execute(uri, httpMethod, entity, responseType) }
        ?: restResource.execute(uri, httpMethod, entity, responseType)
    }

    inline fun <reified T> typeRef(): ParameterizedTypeReference<T> = object: ParameterizedTypeReference<T>(){}
}
