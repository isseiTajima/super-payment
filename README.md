# スーパーペイメント

## 技術スタック

- **バックエンド**: Kotlin（Ktorフレームワーク）/ JDK 21
- **データベース**: PostgreSQL（Exposed ORM）
- **認証**: JWTベースの認証
- **依存性注入**: Koin
- **テスト**: JUnit5（AssertJ）
- **パスワードセキュリティ**: BCryptによるパスワードハッシュ化

## プロジェクト構造

このプロジェクトはクリーンアーキテクチャの原則とドメイン駆動設計に従っています：

```
src/
├── main/
│   ├── kotlin/
│   │   ├── application/    # アプリケーションサービスとユースケース
│   │   ├── domain/         # ドメインモデルとビジネスロジック
│   │   ├── infrastructure/ # データベース等の実装
│   │   └── presentation/   # コントローラーとAPIエンドポイント
│   └── resources/          # 設定ファイル
└── test/                   # テストクラス
```

## セットアップと実行方法

1. JDK 21とDockerがインストールされていることを確認
2. 以下のコマンドでdockerを起動し、dbを立ち上げる
   ```
   cd env
   docker-compose up -d
   ```
3. src/main/resources/application.conf`でデータベース接続を設定
4. Gradleを使用してアプリケーションを実行：
   ```
   ./gradlew run
   ```
4. サーバーはポート8080で起動します

## 主なAPIエンドポイント

- **POST /users**: 新規ユーザーの作成
- **POST /users/auth**: 認証とJWTトークンの取得
- **POST /invoices**: 新規請求書の作成
- **GET /invoices**: 期間内の請求書一覧の取得

## データベース

なるべくベースのddlを活かしつつ、今回のapiに関係のある外部キー制約とindexだけは追加しています。

```sql
-- ユーザーテーブル
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       company_name VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_name ON users(name);

-- 請求書テーブル
CREATE TABLE invoices (
                          id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL,
                          issue_date DATE NOT NULL,
                          payment_amount DECIMAL(15, 2) NOT NULL,
                          fee DECIMAL(15, 2) NOT NULL,
                          fee_rate DECIMAL(5, 2) NOT NULL,
                          tax_amount DECIMAL(15, 2) NOT NULL,
                          tax_rate DECIMAL(5, 2) NOT NULL,
                          total_amount DECIMAL(15, 2) NOT NULL,
                          payment_due_date DATE NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          CONSTRAINT fk_invoices_users
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
);
CREATE INDEX idx_invoices_user_id ON invoices(user_id);
CREATE INDEX idx_invoices_payment_due_date ON invoices(payment_due_date);

```

## 実装における考慮点

### アーキテクチャ設計

- **クリーンアーキテクチャの採用**: ドメイン、アプリケーション、インフラストラクチャ、プレゼンテーションの各層に分離し、依存関係が内側に向かうように設計
- **ドメイン駆動設計(DDD)の原則**: ドメインモデルを中心に据え、ビジネスロジックをドメイン層に集約

### セキュリティ対策

- **パスワードハッシュ化**: BCryptを使用した安全なパスワード保存（Password.kt）
- **JWT認証**: トークンベースの認証システムによるセキュアなAPI保護（JwtService.kt）
- **環境変数による秘匿情報管理**: JWTシークレットなどの機密情報を環境変数で管理
- **入力値バリデーション**: ドメインオブジェクトでの入力検証によるセキュリティリスク軽減

### パフォーマンスと拡張性

- **依存性注入(DI)**: Koinを使用したDIによる疎結合なコンポーネント設計（KoinModules.kt）
- **トランザクション管理**: Exposedフレームワークを使用したデータベーストランザクションの適切な管理

### テスト

- **ユニットテスト**: ドメインモデルとビジネスロジックの単体テスト（UserTest.kt, InvoiceTest.kt など）
- **統合テスト**: コントローラーとエンドポイントの統合テスト（UserControllerTest.kt, InvoiceControllerTest.kt）
- **テスト容易性**: インターフェースと依存性注入を活用したテスト容易性

### エラーハンドリング

- **例外処理**: 適切な例外処理と意味のあるエラーメッセージ
- **バリデーション**: ドメインオブジェクトでの入力検証（Email.kt, Password.kt など）

## 実施できなかったこと

時間の兼ね合いもあり、以下の項目は実施できませんでした。

- グローバル(インターセプターとか)にエラーハンドリングを実装し、共通的なレスポンスで返すことができませんでした
- 各層で適切な例外を作成できませんでした。一律validationエラーとかはIllegalArgumentExceptionを投げています
- 全て実装してコミットしてしまったため、コミットを分けることができませんでした。実際の開発だとレビュワーの負担を減らすためにコミットの粒度を細かくすべきだとは思います。
- 値オブジェクトのテストを実装できませんでした。重要なドメインやhttpリクエストでの結合テストの正常系は実施しました。
- ktorやexposedは初めて使うので、冗長だったり、非効率な実装になっている部分があるかもしれません。
- 