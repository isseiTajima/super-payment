package jp.co.payment.infrastructure

import jp.co.payment.JwtConfig
import jp.co.payment.application.AuthUseCase
import jp.co.payment.application.CreateInvoiceUseCase
import jp.co.payment.application.CreateUserUseCase
import jp.co.payment.application.GetInvoicesUseCase
import jp.co.payment.domain.InvoiceRepository
import jp.co.payment.domain.UserRepository
import jp.co.payment.presentation.InvoiceController
import jp.co.payment.presentation.UserController
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<UserRepository> { ExposedUserRepository() }
    single<InvoiceRepository> { ExposedInvoiceRepository() }
    single { JwtService(get<JwtConfig>()) }

    // Use Cases
    single { CreateUserUseCase(get()) }// get()でUserRepositoryを自動注入
    single { AuthUseCase(get(), get()) }// get()でUserRepositoryとJwtServiceを自動注入
    single { CreateInvoiceUseCase(get()) }// get()でInvoiceRepositoryを自動注入
    single { GetInvoicesUseCase(get()) }// get()でInvoiceRepositoryを自動注入

    // Controllers
    single { UserController(get(), get()) }// get()でCreateUserUseCaseとAuthUseCaseを自動注入
    single { InvoiceController(get(), get()) }// get()でCreateInvoiceUseCaseとGetInvoicesUseCaseを自動注入
}
