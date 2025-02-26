package no.nav.pensjon.opptjening.filadapter.web

import no.nav.pensjon.opptjening.filadapter.Application
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableMockOAuth2Server
class AdminWebApiTest {

    @Autowired
    private lateinit var adminwebApi: AdminWebApi

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var mockOAuth2Server: MockOAuth2Server

    @LocalServerPort
    private var port: Int = 0

    fun token(): String {
        return mockOAuth2Server.issueToken(
            issuerId = "azure",
            subject = "test-user",
            audience = "someaudience",
        ).serialize()
    }

    @Test
    fun `testmiljøet er oppe og fungerer`() {
        assertThat(adminwebApi).isNotNull()
        assertThat(restTemplate).isNotNull()
        println(port)
        println(mockOAuth2Server)
    }

    @Test
    fun `ping svarer`() {
        val response = OkHttpClient().newCall(
            Request.Builder()
                .get()
                .url("http://localhost:$port/ping")
                .addHeader("Authorization", "Bearer ${token()}")
                .build()
        ).execute()
        assertThat(response.body?.string()).isEqualTo("pong")
    }

    @Test
    fun `list endepunkter`() {
        val response = OkHttpClient().newCall(
            Request.Builder()
                .get()
                .url("http://localhost:$port/actuator/mappings")
                .addHeader("Authorization", "Bearer ${token()}")
                .build()
        ).execute()
        println(response.body?.string())
    }
}