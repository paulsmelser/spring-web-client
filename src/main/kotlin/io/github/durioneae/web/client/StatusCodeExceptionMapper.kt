package io.github.durioneae.web.client

import org.springframework.web.client.HttpStatusCodeException

interface StatusCodeExceptionMapper {
    fun handleFailure(exception: HttpStatusCodeException): RuntimeException
}