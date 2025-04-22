package jp.co.payment.presentation

import jp.co.payment.application.AuthUseCase
import jp.co.payment.application.CreateUserUseCase
import jp.co.payment.domain.*
import kotlinx.serialization.Serializable

/**
 * ユーザー関連のHTTPリクエストを処理するコントローラー。
 * ユーザー作成と認証のためのエンドポイントを提供します。
 */
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val authUseCase: AuthUseCase
) {
    /**
     * 新しいユーザーを作成します。
     *
     * @param request ユーザー詳細を含むユーザー作成リクエスト
     * @return 作成されたユーザー情報を含むレスポンス
     */
    fun createUser(request: CreateUserRequest): CreateUserResponse {
        return createUserUseCase.execute(request.toDomain()).let {
            CreateUserResponse.fromDomain(it)
        }
    }

    /**
     * ユーザーを認証し、JWTトークンを生成します。
     *
     * @param request 認証情報を含む認証リクエスト
     * @return 認証トークンを含むレスポンス
     * @throws IllegalArgumentException 認証が失敗した場合
     */
    fun auth(request: AuthRequest): AuthResponse {
        return try {
            AuthResponse(authUseCase.execute(request.username, request.password))
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            throw IllegalArgumentException("認証中にエラーが発生しました", e)
        }
    }
}

/**
 * ユーザー作成リクエスト用オブジェクト。
 */
@Serializable
data class CreateUserRequest(
    val username: String,
    val password: String,
    val companyName: String,
    val email: String
) {
    /**
     * リクエストをドメインのUserオブジェクトに変換します。
     * パスワードのハッシュ化と値オブジェクトの作成を処理します。
     *
     */
    fun toDomain(): User {
        return User(
            userId = null,
            companyName = CompanyName(companyName),
            userName = Username(username),
            email = Email(email),
            password = Password.hash(password)
        )
    }
}

/**
 * ユーザー作成レスポンス用オブジェクト。
 */
@Serializable
data class CreateUserResponse(
    val id: Int?,
    val username: String,
    val companyName: String,
    val email: String,
) {
    companion object {
        /**
         * ドメインのUserオブジェクトからレスポンスを作成します。
         */
        fun fromDomain(user: User): CreateUserResponse =
            CreateUserResponse(
                id = user.userId,
                username = user.userName.value,
                companyName = user.companyName.value,
                email = user.email.value,
            )
    }
}

/**
 * 認証リクエスト用オブジェクト。
 */
@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)

/**
 * 認証レスポンス用オブジェクト。
 */
@Serializable
data class AuthResponse(
    val token: String,
)
