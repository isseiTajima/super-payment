package jp.co.payment.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    @DisplayName("ユーザー作成のテスト")
    fun create_userCreation() {
        // Given
        val companyName = CompanyName("Test Company")
        val username = Username("testuser")
        val email = Email("test@example.com")
        val password = Password("password123")

        // When
        val user = User(
            userId = null,
            companyName = companyName,
            userName = username,
            email = email,
            password = password
        )

        // Then
        assertThat(user.userId).isNull()
        assertThat(user.companyName).isEqualTo(companyName)
        assertThat(user.userName).isEqualTo(username)
        assertThat(user.email).isEqualTo(email)
        assertThat(user.password).isEqualTo(password)
        assertThat(user.companyName.value).isEqualTo("Test Company")
        assertThat(user.userName.value).isEqualTo("testuser")
        assertThat(user.email.value).isEqualTo("test@example.com")
    }

    @Test
    @DisplayName("IDを持つユーザーのテスト")
    fun create_userWithId() {
        // Given
        val userId = 1
        val companyName = CompanyName("Another Company")
        val username = Username("anotheruser")
        val email = Email("another@example.com")
        val password = Password("secure456")

        // When
        val user = User(
            userId = userId,
            companyName = companyName,
            userName = username,
            email = email,
            password = password
        )

        // Then
        assertThat(user.userId).isEqualTo(userId)
        assertThat(user.companyName).isEqualTo(companyName)
        assertThat(user.userName).isEqualTo(username)
        assertThat(user.email).isEqualTo(email)
        assertThat(user.password).isEqualTo(password)
        assertThat(user.companyName.value).isEqualTo("Another Company")
        assertThat(user.userName.value).isEqualTo("anotheruser")
        assertThat(user.email.value).isEqualTo("another@example.com")
    }
}
