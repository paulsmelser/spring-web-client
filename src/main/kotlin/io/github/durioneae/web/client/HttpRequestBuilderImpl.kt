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

class HttpRequestBuilderImpl internal constructor(private val restResource: RestClient, private val uriBuilder: UriComponentsBuilder) : HttpRequestBuilder {
    private val headers: HttpHeaders = HttpHeaders()
	private var httpRetryService: HttpRetryService? = HttpRetryService(RetryBuilder.defaultRetry(), FORBIDDEN, UNAUTHORIZED)

	override fun header(key: String, vararg value: String): HttpRequestBuilder {
        headers[key] = asList(*value)
        return this
    }

    override fun pathSegment(vararg pathSegments: String): HttpRequestBuilder {
        uriBuilder.pathSegment(*pathSegments)
        return this
    }

    override fun queryParam(name: String, vararg values: Any): HttpRequestBuilder {
        uriBuilder.queryParam(name, *values)
        return this
    }

    override fun noRetry(): HttpRequestBuilder {
        httpRetryService = null
        return this
    }

    override fun withRetry(retryTemplate: RetryTemplate, vararg statusCodeToNotRetry: HttpStatus): HttpRequestBuilder {
        httpRetryService = HttpRetryService(retryTemplate, *statusCodeToNotRetry)
        return this
    }

    override fun path(path: String): HttpRequestBuilder {
        uriBuilder.path(path)
        return this
    }

    override fun <T, R> put(payload: T, aClass: Class<R>): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.PUT, HttpEntity(payload, headers), aClass)
    }

    override fun <T, R> post(payload: T, aClass: Class<R>): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.POST, HttpEntity(payload, headers), aClass)
    }

    override fun <T, R> patch(payload: T, aClass: Class<R>): ResponseEntity<R> {
        return execute(uriBuilder.build().toUri(), HttpMethod.PATCH, HttpEntity(payload, headers), aClass)
    }

    override fun <T> get(aClass: Class<T>): ResponseEntity<T> {
        return execute(uriBuilder.build().toUri(), HttpMethod.GET, HttpEntity<Any>(headers), aClass)
    }

    override fun head(): ResponseEntity<Void> {
        return execute(uriBuilder.build().toUri(), HttpMethod.HEAD, HttpEntity<Any>(headers), Void::class.java)
    }

    override fun <T> get(aClass: ParameterizedTypeReference<T>): ResponseEntity<T> {
        return execute(uriBuilder.build().toUri(), HttpMethod.GET, HttpEntity<Any>(headers), aClass)
    }

    override fun delete(): ResponseEntity<*> {
        return httpRetryService?.doWithRetry { restResource.delete(uriBuilder.build().toUri(), headers) }
                ?: restResource.delete(uriBuilder.build().toUri(), headers)
    }

    private fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: Class<R>): ResponseEntity<R> {
        return httpRetryService?.doWithRetry { restResource.execute(uri, httpMethod, entity, responseType) }
        ?: restResource.execute(uri, httpMethod, entity, responseType)
    }

    private fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: ParameterizedTypeReference<R>): ResponseEntity<R> {
        return httpRetryService?.doWithRetry { restResource.execute(uri, httpMethod, entity, responseType) }
        ?: restResource.execute(uri, httpMethod, entity, responseType)
    }
}
