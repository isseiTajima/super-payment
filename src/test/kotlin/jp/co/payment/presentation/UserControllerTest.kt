package jp.co.payment.presentation

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import jp.co.payment.*
import jp.co.payment.infrastructure.TestDatabaseFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserControllerTest {

    @BeforeEach
    fun setUp() {
        // H2データベースの初期化
        TestDatabaseFactory.init()
    }

    @AfterEach
    fun tearDown() {
        // テスト後のクリーンアップ
        TestDatabaseFactory.cleanup()
        // Koinを停止
        KoinTestUtils.stopKoinAfterTest()
    }

    @Test
    @DisplayName("ユーザー作成のテスト")
    fun post_createUser() = testApplication {
        // Given
        val request = CreateUserRequest(
            username = "testuser",
            password = "password123",
            companyName = "テスト会社",
            email = "test@example.com"
        )

        // When - HTTPリクエストを介してユーザーを作成
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateUserRequest.serializer(), request))
        }

        // Then
        assertThat(response.status).isEqualTo(HttpStatusCode.Created)

        // レスポンスボディを解析
        val responseBody = response.toJsonObject()

        // レスポンスフィールドを検証
        assertThat(responseBody.getInt("id")).isNotNull()
        assertThat(responseBody.getString("username")).isEqualTo("testuser")
        assertThat(responseBody.getString("companyName")).isEqualTo("テスト会社")
        assertThat(responseBody.getString("email")).isEqualTo("test@example.com")
    }

    @Test
    @DisplayName("認証のテスト")
    fun auth_user() = testApplication {
        // Given - まず、ユーザーを作成する
        val createRequest = CreateUserRequest(
            username = "testuser",
            password = "password123",
            companyName = "テスト会社",
            email = "test@example.com"
        )

        // HTTPリクエストを介してユーザーを作成
        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(CreateUserRequest.serializer(), createRequest))
        }

        // When - HTTPリクエストを介して認証
        val authRequest = AuthRequest(
            username = "testuser",
            password = "password123"
        )
        val authResponse = client.post("/users/auth") {
            contentType(ContentType.Application.Json)
            setBody(testJson.encodeToString(AuthRequest.serializer(), authRequest))
        }

        // Then
        assertThat(authResponse.status).isEqualTo(HttpStatusCode.OK)

        // レスポンスボディを解析
        val responseBody = authResponse.toJsonObject()

        // トークンが存在することを確認
        assertThat(responseBody.getString("token")).isNotEmpty()
    }
}
