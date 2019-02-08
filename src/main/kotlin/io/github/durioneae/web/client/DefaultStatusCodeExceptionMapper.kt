package io.github.durioneae.web.client

import org.springframework.web.client.HttpStatusCodeException

class DefaultStatusCodeExceptionMapper : StatusCodeExceptionMapper {
    override fun handleFailure(exception: HttpStatusCodeException): RuntimeException {
        throw exception
    }
}