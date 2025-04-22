package jp.co.payment.infrastructure

import jp.co.payment.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

/**
 * ExposedUserRepositoryのテストクラス
 * H2インメモリデータベースを使用してリポジトリの実装をテストします
 */
class ExposedUserRepositoryTest {

    private lateinit var userRepository: UserRepository

    /**
     * 各テスト実行前にH2データベースを初期化し、リポジトリのインスタンスを作成します
     */
    @BeforeEach
    fun setUp() {
        TestDatabaseFactory.init()
        userRepository = TestDatabaseFactory.createUserRepository()
    }

    /**
     * 各テスト実行後にデータベースをクリーンアップします
     */
    @AfterEach
    fun tearDown() {
        TestDatabaseFactory.cleanup()
    }

    @Test
    @DisplayName("ユーザー保存が成功すること")
    fun save_userSave() {
        // Given: テスト用のユーザーデータを作成
        val user = createTestUser()

        // When: リポジトリを使用してユーザーを保存
        val savedUser = userRepository.save(user)

        // Then: 保存されたユーザーのIDがnullでないことを確認
        assertThat(savedUser.userId).isNotNull
        // Then: 保存されたユーザーの各フィールドが元のユーザーと一致することを確認
        assertThat(savedUser.companyName.value).isEqualTo(user.companyName.value)
        assertThat(savedUser.userName.value).isEqualTo(user.userName.value)
        assertThat(savedUser.email.value).isEqualTo(user.email.value)
        assertThat(savedUser.password.value).isEqualTo(user.password.value)
    }

    @Test
    @DisplayName("ユーザー名による検索が成功すること")
    fun findByUsername_searchByUsername() {
        // Given: テスト用のユーザーを作成して保存
        val user = createTestUser(username = "testuser")
        userRepository.save(user)

        // When: ユーザー名でユーザーを検索
        val foundUser = userRepository.findByUsername("testuser")

        // Then: 検索結果がnullでないことを確認
        assertThat(foundUser).isNotNull
        // Then: 検索されたユーザーの各フィールドが元のユーザーと一致することを確認
        assertThat(foundUser?.userName?.value).isEqualTo("testuser")
        assertThat(foundUser?.companyName?.value).isEqualTo(user.companyName.value)
        assertThat(foundUser?.email?.value).isEqualTo(user.email.value)
        assertThat(foundUser?.password?.value).isEqualTo(user.password.value)
    }

    @Test
    @DisplayName("存在しないユーザー名による検索がnullを返すこと")
    fun findByUsername_nonexistentUsername() {
        // Given: テスト用のユーザーを作成して保存
        val user = createTestUser(username = "existinguser")
        userRepository.save(user)

        // When: 存在しないユーザー名でユーザーを検索
        val foundUser = userRepository.findByUsername("nonexistentuser")

        // Then: 検索結果がnullであることを確認
        assertThat(foundUser).isNull()
    }

    /**
     * テスト用のユーザーオブジェクトを作成するヘルパーメソッド
     */
    private fun createTestUser(
        companyName: String = "Test Company",
        username: String = UUID.randomUUID().toString(),
        email: String = "test@example.com",
        password: String = "password123"
    ): User {
        return User(
            userId = null,
            companyName = CompanyName(companyName),
            userName = Username(username),
            email = Email(email),
            password = Password(password)
        )
    }
}
