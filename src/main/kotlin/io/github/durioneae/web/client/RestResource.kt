package io.github.durioneae.web.client

import org.springframework.http.MediaType

interface RestResource {
    fun request(): HttpRequestBuilder

    fun request(mediaType: MediaType): HttpRequestBuilder
}
