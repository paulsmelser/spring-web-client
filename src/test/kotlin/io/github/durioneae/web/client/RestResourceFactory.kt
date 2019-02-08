package io.github.durioneae.web.client

import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.ResourceHttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Arrays

object RestResourceFactory {
    @JvmStatic fun createTestResource(port: Int) : RestResource {
        val client = RestTemplate()
        client.messageConverters = getMessageConverters()
        return RestClient(client, URI.create("http://localhost:$port"))
    }

    private fun getJsonMessageConverter(): MappingJackson2HttpMessageConverter {
        return MappingJackson2HttpMessageConverter()
    }

    private fun getMessageConverters(): List<HttpMessageConverter<*>> {
        val messageConverters = ArrayList<HttpMessageConverter<*>>()
        messageConverters.add(StringHttpMessageConverter())
        messageConverters.add(getFormMessageConverter())
        messageConverters.add(getJsonMessageConverter())
        messageConverters.add(getByteArrayMessageConverter())
        return messageConverters
    }

    private fun getFormMessageConverter(): FormHttpMessageConverter {
        val converter = FormHttpMessageConverter()
        converter.setCharset(Charset.forName("UTF-8"))
        val partConverters = ArrayList<HttpMessageConverter<*>>()

        partConverters.add(ByteArrayHttpMessageConverter())

        val stringHttpMessageConverter = StringHttpMessageConverter(Charset.forName("UTF-8"))
        stringHttpMessageConverter.setWriteAcceptCharset(false)
        partConverters.add(stringHttpMessageConverter)

        partConverters.add(ResourceHttpMessageConverter())
        converter.setPartConverters(partConverters)

        return converter
    }

    private fun getByteArrayMessageConverter(): ByteArrayHttpMessageConverter {
        val converter = ByteArrayHttpMessageConverter()
        converter.supportedMediaTypes = Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG)
        return converter
    }
}