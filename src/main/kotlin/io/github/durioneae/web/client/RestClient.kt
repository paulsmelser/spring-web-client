package io.github.durioneae.web.client

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

class RestClient(private val restTemplate: RestOperations, private val resourceBaseUri: URI, private val faultHandler: StatusCodeExceptionMapper = DefaultStatusCodeExceptionMapper()) : RestResource {

    override fun request(): HttpRequestBuilder {
        return HttpRequestBuilder(this, UriComponentsBuilder.fromUri(resourceBaseUri))
    }

    override fun request(mediaType: MediaType): HttpRequestBuilder {
        return HttpRequestBuilder(this, UriComponentsBuilder.fromUri(resourceBaseUri)).header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
    }

    internal fun delete(uri: URI, headers: HttpHeaders): ResponseEntity<*> {
        return tryExecute { restTemplate.exchange(uri, HttpMethod.DELETE, HttpEntity<Any>(headers), String::class.java) }
    }

    internal fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: Class<R>): ResponseEntity<R> {
		return tryExecute { restTemplate.exchange(uri, httpMethod, entity, responseType) }
    }

    internal fun <T, R> execute(uri: URI, httpMethod: HttpMethod, entity: HttpEntity<T>, responseType: ParameterizedTypeReference<R>): ResponseEntity<R> {
        return tryExecute { restTemplate.exchange(uri, httpMethod, entity, responseType) }
    }

    private fun <T> tryExecute(action: () -> T): T {
        return try {
            action()
        } catch (e: HttpStatusCodeException) {
            throw faultHandler.handleFailure(e)
        }
    }
}
