package ru.cloudpayments.sdk.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.cloudpayments.sdk.api.models.CloudPaymentsGetMirPayCryptogramResponse
import ru.cloudpayments.sdk.api.models.CloudPaymentsGetMirPayLinkResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsBinInfoResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsGetQrPayLinkResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsGetSBPQrLinkResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsMerchantConfigurationResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsPublicKeyResponse
import ru.cloudpayments.sdk.api.models.CloudpaymentsTransactionResponse
import ru.cloudpayments.sdk.api.models.GetMirPayCryptogramBody
import ru.cloudpayments.sdk.api.models.MirPayLinkBody
import ru.cloudpayments.sdk.api.models.PaymentRequestBody
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitBody
import ru.cloudpayments.sdk.api.models.QrLinkStatusWaitResponse
import ru.cloudpayments.sdk.api.models.QrPayLinkBody
import ru.cloudpayments.sdk.api.models.SBPQrLinkBody
import ru.cloudpayments.sdk.api.models.ThreeDsRequestBody

interface CloudpaymentsApiService {
	@POST("payments/cards/charge")
	fun charge(@Body body: PaymentRequestBody): Single<CloudpaymentsTransactionResponse>

	@POST("payments/cards/auth")
	fun auth(@Body body: PaymentRequestBody): Single<CloudpaymentsTransactionResponse>

	@POST("payments/ThreeDSCallback")
	fun postThreeDs(@Body body: ThreeDsRequestBody): Single<Boolean>

	@GET("bins/info/{firstSixDigits}")
	fun getBinInfo(@Path("firstSixDigits") firstSixDigits: String): Single<CloudpaymentsBinInfoResponse>

	@GET("bins/info")
	fun getBinInfo(@QueryMap queryMap: Map<String, String>): Single<CloudpaymentsBinInfoResponse>

	@GET("payments/publickey")
	fun getPublicKey(): Single<CloudpaymentsPublicKeyResponse>

	@GET("merchant/configuration")
	fun getMerchantConfiguration(@Query("terminalPublicId") publicId: String): Single<CloudpaymentsMerchantConfigurationResponse>

	@POST("payments/qr/sbp/link")
	fun getSbpQrLink(@Body body: SBPQrLinkBody): Single<CloudpaymentsGetSBPQrLinkResponse>

	@POST("payments/qr/tinkoffpay/link")
	fun getTPayQrLink(@Body body: QrPayLinkBody): Single<CloudpaymentsGetQrPayLinkResponse>

	@POST("payments/qr/sberpay/link")
	fun getSberPayQrLink(@Body body: QrPayLinkBody): Single<CloudpaymentsGetQrPayLinkResponse>

	@POST("payments/qr/status/wait")
	fun qrLinkStatusWait(@Body body: QrLinkStatusWaitBody): Single<QrLinkStatusWaitResponse>

	@POST("payments/mirpay/deeplinks")
	fun getMirPayLink(@Body body: MirPayLinkBody): Single<CloudPaymentsGetMirPayLinkResponse>

	@POST("payments/mirpay/cryptograms/get")
	fun getMirPayCryptogram(@Body body: GetMirPayCryptogramBody): Single<CloudPaymentsGetMirPayCryptogramResponse>
}