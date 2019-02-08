package io.github.durioneae.web.client;

import static io.github.durioneae.web.client.RestResourceFactory.createTestResource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import org.assertj.core.api.SoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.psmelser.jackson.json.Json;

public class RestClientJavaTest {
	@Rule
	public final WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort());


	@Test
	public void whenCallingGetMethod_thenExpectedResponseIsReceived() {
		TestClass expected = new TestClass();
		givenThat(get(urlEqualTo("/test")).willReturn(aResponse()
				.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.withBody(Json.toJson(expected))));

		RestResource resource = createTestResource(wireMockRule.port());
		TestClass response = resource.request()
				.path("/test")
				.get(TestClass.class)
                .getBody();

		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getName()).describedAs("Name Is Equal").isEqualTo(expected.getName());
			softly.assertThat(response.getDouble()).describedAs("double Equal").isEqualByComparingTo(expected.getDouble());
			softly.assertThat(response.getNumber()).describedAs("Number Is Equal").isEqualTo(expected.getNumber());
			softly.assertThat(response.getInstant()).describedAs("Instant Is Equal").isEqualTo(expected.getInstant());
			softly.assertThat(response.getZonedDateTime()).describedAs("ZonedDateTime Is Equal").isEqualTo(expected.getZonedDateTime());
			softly.assertThat(response.getLocalDateTime()).describedAs("LocalDateTime Is Equal").isEqualTo(expected.getLocalDateTime());
			softly.assertThat(response.getOffsetDateTime().toInstant()).describedAs("OffsetDateTime Is Equal").isEqualTo(expected.getOffsetDateTime().toInstant());
		});
	}
}
