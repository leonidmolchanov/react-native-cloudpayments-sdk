package ru.cloudpayments.sdk.dagger2

import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.cloudpayments.sdk.Constants
import ru.cloudpayments.sdk.api.AuthenticationInterceptor
import ru.cloudpayments.sdk.api.CloudpaymentsApiService
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.api.UserAgentInterceptor
import ru.cloudpayments.sdk.log.CloudPaymentsLoggingInterceptor
import ru.cloudpayments.sdk.ui.PaymentActivity
import ru.cloudpayments.sdk.viewmodel.PaymentCardViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentFinishViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentMirPayViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentOptionsViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentProcessViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentSBPViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentSberPayViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentTPayViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class CloudpaymentsModule {
	@Provides
	@Singleton
	fun provideRepository(apiService: CloudpaymentsApiService)
			= CloudpaymentsApi(apiService)
}

@Module
class CloudpaymentsNetModule(private val publicId: String, private var apiUrl: String = Constants.baseApiUrl) {
	@Provides
	@Singleton
	fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
		.setLevel(HttpLoggingInterceptor.Level.BODY)

	@Provides
	@Singleton
	fun providesAuthenticationInterceptor(): AuthenticationInterceptor
			= AuthenticationInterceptor(publicId)

	@Provides
	@Singleton
	fun providesUserAgentInterceptor(): UserAgentInterceptor
			= UserAgentInterceptor(Constants.userAgent)

	@Provides
	@Singleton
	fun provideOkHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder
			= OkHttpClient.Builder()
		.addInterceptor(loggingInterceptor)

	@Provides
	@Singleton
	fun provideApiService(okHttpClientBuilder: OkHttpClient.Builder, userAgentInterceptor: UserAgentInterceptor,
						  authenticationInterceptor: AuthenticationInterceptor): CloudpaymentsApiService {
		val client = okHttpClientBuilder
			.addInterceptor(userAgentInterceptor)
			.addInterceptor(authenticationInterceptor)
			.addNetworkInterceptor(CloudPaymentsLoggingInterceptor())
			.connectTimeout(64, TimeUnit.SECONDS)
			.readTimeout(64, TimeUnit.SECONDS)
			.writeTimeout(64, TimeUnit.SECONDS)
			.followRedirects(false)
			.build()

		if (apiUrl.isEmpty())
			apiUrl = Constants.baseApiUrl

		val retrofit = Retrofit.Builder()
			.baseUrl(apiUrl)
			.addConverterFactory(GsonConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.client(client)
			.build()

		return retrofit.create(CloudpaymentsApiService::class.java)
	}
}

@Singleton
@Component(modules = [CloudpaymentsModule::class, CloudpaymentsNetModule::class])
internal interface CloudpaymentsComponent {
	fun inject(paymentActivity: PaymentActivity)
	fun inject(optionsViewModel: PaymentOptionsViewModel)
	fun inject(cardViewModel: PaymentCardViewModel)
	fun inject(processViewModel: PaymentProcessViewModel)
	fun inject(tPayViewModel: PaymentTPayViewModel)
	fun inject(sberPayViewModel: PaymentSberPayViewModel)
	fun inject(sbpViewModel: PaymentSBPViewModel)
	fun inject(mirPayViewModel: PaymentMirPayViewModel)
	fun inject(finishViewModel: PaymentFinishViewModel)
}
