package jp.co.payment.infrastructure

import jp.co.payment.domain.Invoice
import jp.co.payment.domain.InvoiceRepository
import jp.co.payment.domain.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

/**
 * H2データベースを使用したテスト用のデータベース設定を提供するクラス
 */
object TestDatabaseFactory {
    /**
     * H2データベースへの接続を初期化し、必要なテーブルを作成します
     */
    fun init() {
        // H2インメモリデータベースへの接続
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )

        // テーブルの作成
        transaction {
            SchemaUtils.create(Users, Invoices)
        }
    }

    /**
     * テスト用のInvoiceRepositoryの実装を返します
     */
    fun createInvoiceRepository(): InvoiceRepository {
        return ExposedInvoiceRepository()
    }

    /**
     * テスト用のUserRepositoryの実装を返します
     */
    fun createUserRepository(): UserRepository {
        return ExposedUserRepository()
    }

    /**
     * テストデータベースをクリーンアップします
     */
    fun cleanup() {
        transaction {
            SchemaUtils.drop(Invoices, Users)
        }
    }
}
