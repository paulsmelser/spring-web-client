package io.github.durioneae.web.client

import com.psmelser.jackson.json.Json
import io.github.durioneae.web.client.RestResourceFactory.createTestResource
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.givenThat
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.assertj.core.api.SoftAssertions
import org.junit.Rule
import org.junit.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RestClientTest {
    @Rule
    fun wireMockRule(): WireMockRule = wireMockRule

    private val wireMockRule = WireMockRule(WireMockConfiguration.options().dynamicPort())

    @Test
    fun `When calling get method expected response is received`() {
        val expected = TestClass()
        givenThat(get(urlEqualTo("/test")).willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(Json.toJson(expected))))

        val resource = createTestResource(wireMockRule.port())
        val response = resource.request()
                .path("/test")
                .get(TestClass::class.java)
                .body

        SoftAssertions.assertSoftly {
            it.assertThat(response.name).describedAs("Name Is Equal").isEqualTo(expected.name)
            it.assertThat(response.double).describedAs("double Equal").isEqualByComparingTo(expected.double)
            it.assertThat(response.number).describedAs("Number Is Equal").isEqualTo(expected.number)
            it.assertThat(response.instant).describedAs("Instant Is Equal").isEqualTo(expected.instant)
            it.assertThat(response.zonedDateTime).describedAs("ZonedDateTime Is Equal").isEqualTo(expected.zonedDateTime)
            it.assertThat(response.localDateTime).describedAs("LocalDateTime Is Equal").isEqualTo(expected.localDateTime)
            it.assertThat(response.offsetDateTime.toInstant()).describedAs("OffsetDateTime Is Equal").isEqualTo(expected.offsetDateTime.toInstant())
        }
    }

}