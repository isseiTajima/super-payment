package jp.co.payment

import org.koin.core.context.stopKoin

/**
 * Koinに関するテストユーティリティ
 */
object KoinTestUtils {
    /**
     * Koinを停止する
     * テスト間でKoinの状態をリセットし、各テストが独立した環境で実行されるようにするために必要
     */
    fun stopKoinAfterTest() {
        try {
            stopKoin()
        } catch (e: IllegalStateException) {
            // Koinが既に停止している場合は無視する
        }
    }
}