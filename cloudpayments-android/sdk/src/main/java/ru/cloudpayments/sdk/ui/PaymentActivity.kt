package ru.cloudpayments.sdk.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.cloudpayments.sdk.R
import ru.cloudpayments.sdk.api.CloudpaymentsApi
import ru.cloudpayments.sdk.api.models.ExternalPaymentMethods
import ru.cloudpayments.sdk.api.models.SBPBanksItem
import ru.cloudpayments.sdk.api.models.SBPQrLinkBody
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.dagger2.CloudpaymentsComponent
import ru.cloudpayments.sdk.dagger2.CloudpaymentsModule
import ru.cloudpayments.sdk.dagger2.CloudpaymentsNetModule
import ru.cloudpayments.sdk.dagger2.DaggerCloudpaymentsComponent
import ru.cloudpayments.sdk.databinding.ActivityCpsdkPaymentBinding
import ru.cloudpayments.sdk.log.CloudPaymentsSendLogHttpClient
import ru.cloudpayments.sdk.log.CloudPaymentsUncaughtExceptionHandler
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.models.SDKConfiguration
import ru.cloudpayments.sdk.ui.dialogs.PaymentCardFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentFinishFragmentFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentFinishStatus
import ru.cloudpayments.sdk.ui.dialogs.PaymentMirPayFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentOptionsFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentProcessFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentSBPFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentSberPayFragment
import ru.cloudpayments.sdk.ui.dialogs.PaymentTPayFragment
import ru.cloudpayments.sdk.ui.dialogs.base.BasePaymentBottomSheetFragment
import ru.cloudpayments.sdk.util.GooglePayHandler
import ru.cloudpayments.sdk.util.checkAndGetCorrectJsonDataString
import ru.cloudpayments.sdk.util.nextFragment
import javax.inject.Inject

internal class PaymentActivity : FragmentActivity(),
								 BasePaymentBottomSheetFragment.IPaymentFragment,
								 PaymentOptionsFragment.IPaymentOptionsFragment,
								 PaymentCardFragment.IPaymentCardFragment,
								 PaymentProcessFragment.IPaymentProcessFragment,
								 PaymentSBPFragment.IPaymentSBPFragment,
								 PaymentTPayFragment.IPaymentTPayFragment,
								 PaymentSberPayFragment.IPaymentSberPayFragment,
								 PaymentMirPayFragment.IPaymentMirPayFragment {

	val sdkConfiguration: SDKConfiguration = SDKConfiguration()

	private var disposable: Disposable? = null

	@Inject
	lateinit var api: CloudpaymentsApi

	companion object {
		private const val REQUEST_CODE_GOOGLE_PAY = 1
		private const val EXTRA_CONFIGURATION = "EXTRA_CONFIGURATION"

		fun getStartIntent(context: Context, configuration: PaymentConfiguration): Intent {
			val intent = Intent(context, PaymentActivity::class.java)
			intent.putExtra(EXTRA_CONFIGURATION, configuration)
			return intent
		}
	}

	override fun finish() {
		super.finish()
		overridePendingTransition(R.anim.cpsdk_fade_in, R.anim.cpsdk_fade_out)
	}

	internal val component: CloudpaymentsComponent by lazy {
		DaggerCloudpaymentsComponent
			.builder()
			.cloudpaymentsModule(CloudpaymentsModule())
			.cloudpaymentsNetModule(
				CloudpaymentsNetModule(
					paymentConfiguration!!.publicId,
					paymentConfiguration!!.apiUrl
				)
			)
			.build()
	}

	val paymentConfiguration by lazy {
		intent.getParcelableExtra<PaymentConfiguration>(EXTRA_CONFIGURATION)
	}

	private lateinit var binding: ActivityCpsdkPaymentBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		Thread.setDefaultUncaughtExceptionHandler(CloudPaymentsUncaughtExceptionHandler.getInstance(this))
		super.onCreate(savedInstanceState)

		CloudPaymentsSendLogHttpClient.setPublicId(paymentConfiguration?.publicId.toString())

		binding = ActivityCpsdkPaymentBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)

		component.inject(this)

		checkCurrency()

		getPublicKey()
	}

	override fun onDestroy() {
		super.onDestroy()

		disposable?.dispose()
		disposable = null
	}

	private fun getPublicKey() {
		disposable = api.getPublicKey()
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				sdkConfiguration.publicKey.pem = response.pem
				sdkConfiguration.publicKey.version = response.version

				paymentConfiguration?.let { getMerchantConfiguration(it.publicId) }
			}
			.onErrorReturn {
				onInternetConnectionError()
			}
			.subscribe()
	}

	private fun getMerchantConfiguration(publicId: String) {
		disposable = api.getMerchantConfiguration(publicId)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->

				if (response.success == true) {

					var isGooglePayAvailable = false
					var isSbpAvailable = false
					var isTPayAvailable = false
					var isSberPayAvailable = false
					var isMirPayAvailable = false

					for (paymentMethod in response.model?.externalPaymentMethods!!) {
						if (paymentMethod.type == ExternalPaymentMethods.GOOGLE_PAY) {
							isGooglePayAvailable = paymentMethod.enabled!!
							if (!paymentMethod.gPayGatewayName.isNullOrBlank()) {
								sdkConfiguration.terminalConfiguration.gPayGatewayName = paymentMethod.gPayGatewayName
							}
						}
						if (paymentMethod.type == ExternalPaymentMethods.SBP) {
							isSbpAvailable = paymentMethod.enabled!!
						}
						if (paymentMethod.type == ExternalPaymentMethods.T_PAY) {
							isTPayAvailable = paymentMethod.enabled!!
						}
						if (paymentMethod.type == ExternalPaymentMethods.SBER_PAY) {
							isSberPayAvailable = paymentMethod.enabled!!
						}
						if (paymentMethod.type == ExternalPaymentMethods.MIR_PAY) {
							isMirPayAvailable = paymentMethod.enabled!!
						}
					}

					sdkConfiguration.availablePaymentMethods.googlePayAvailable = isGooglePayAvailable
					sdkConfiguration.availablePaymentMethods.sbpAvailable = isSbpAvailable
					sdkConfiguration.availablePaymentMethods.tPayAvailable =
						isTPayAvailable
					sdkConfiguration.availablePaymentMethods.sberPayAvailable =
						isSberPayAvailable
					sdkConfiguration.availablePaymentMethods.mirPayAvailable = isMirPayAvailable

					sdkConfiguration.terminalConfiguration.isSaveCard =
						response.model?.features?.isSaveCard
					sdkConfiguration.terminalConfiguration.isCvvRequired =
						response.model?.isCvvRequired
					sdkConfiguration.terminalConfiguration.isAllowedNotSanctionedCards =
						response.model?.features?.isAllowedNotSanctionedCards
					sdkConfiguration.terminalConfiguration.isQiwi =
						response.model?.features?.isQiwi
					sdkConfiguration.terminalConfiguration.skipExpiryValidation =
						response.model?.skipExpiryValidation

					when (paymentConfiguration?.mode) {
						CloudpaymentsSDK.SDKRunMode.TPay -> {
							if (isTPayAvailable) {
								runTPay("", 0)
								binding.layoutProgress.isVisible = false
							} else {
								Toast.makeText(this, "TPay not available", Toast.LENGTH_SHORT).show()
								finish()
							}
						}

						CloudpaymentsSDK.SDKRunMode.SberPay -> {
							if (isSberPayAvailable) {
								runSberPay("", 0)
								binding.layoutProgress.isVisible = false
							} else {
								Toast.makeText(this, "SberPay not available", Toast.LENGTH_SHORT).show()
								finish()
							}
						}

						CloudpaymentsSDK.SDKRunMode.SBP -> {
							if (isSbpAvailable) {
								getSBPQrPayLink()
							} else {
								Toast.makeText(this, "SBP not available", Toast.LENGTH_SHORT).show()
								finish()
							}
						}

						else -> {
							prepareGooglePay()
						}
					}
				} else {
					Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
					finish()
				}
			}
			.onErrorReturn {
				onInternetConnectionError()
			}
			.subscribe()
	}

	fun getSBPQrPayLink() {

		val jsonDataString: String? = checkAndGetCorrectJsonDataString(paymentConfiguration?.paymentData?.getJsonData())

		val body = SBPQrLinkBody(amount = paymentConfiguration?.paymentData?.amount!!,
								 currency = paymentConfiguration?.paymentData?.currency!!,
								 description = paymentConfiguration?.paymentData?.description ?: "",
								 accountId = paymentConfiguration?.paymentData?.accountId ?: "",
								 email = paymentConfiguration?.paymentData?.email ?: "",
								 jsonData = jsonDataString,
								 invoiceId = paymentConfiguration?.paymentData?.invoiceId ?: "",
								 scheme = if (paymentConfiguration?.useDualMessagePayment!!) "auth" else "charge")

		if (paymentConfiguration?.saveCardForSinglePaymentMode != null) {
			body.saveCard = paymentConfiguration?.saveCardForSinglePaymentMode
		}

		disposable = api.getSBPQrLink(body)
			.toObservable()
			.observeOn(AndroidSchedulers.mainThread())
			.map { response ->
				if (response.success == true) {

					val qrUrl = response.transaction?.qrUrl
					val providerQrId = response.transaction?.providerQrId
					val transactionId = response.transaction?.transactionId
					val listOfBanks = response.transaction?.banks?.dictionary

					if (qrUrl != null && providerQrId != null && transactionId != null && listOfBanks != null) {
						runSbp(qrUrl, providerQrId, transactionId, listOfBanks)
						binding.layoutProgress.isVisible = false
					} else {
						Toast.makeText(this, "SBP not available", Toast.LENGTH_SHORT).show()
						finish()
					}
				} else {
					Toast.makeText(this, "SBP not available", Toast.LENGTH_SHORT).show()
					finish()
				}
			}
			.onErrorReturn {
				Toast.makeText(this, "SBP not available", Toast.LENGTH_SHORT).show()
				binding.layoutProgress.isVisible = false
			}
			.subscribe()
	}

	private fun prepareGooglePay() {

		if (!sdkConfiguration.availablePaymentMethods.googlePayAvailable) {
			showUi()
			return
		}

		if (supportFragmentManager.backStackEntryCount == 0) {
			GooglePayHandler.isReadyToMakeGooglePay(this)
				.toObservable()
				.observeOn(AndroidSchedulers.mainThread())
				.map {
					sdkConfiguration.availablePaymentMethods.googlePayAvailable = it
					showUi()
				}
				.onErrorReturn {
					sdkConfiguration.availablePaymentMethods.googlePayAvailable = false
					showUi()
				}
				.subscribe()
		}
	}

	private fun showUi() {
		binding.layoutProgress.isVisible = false
		val fragment = PaymentOptionsFragment.newInstance()
		fragment.show(supportFragmentManager, "")
	}

	@Deprecated("Deprecated in Java")
	override fun onBackPressed() {
		val fragment = supportFragmentManager.findFragmentById(R.id.frame_content)
		if (fragment is BasePaymentBottomSheetFragment<*, *>) {
			fragment.handleBackButton()
		} else {
			super.onBackPressed()
		}
	}

	override fun runCardPayment() {
		val fragment = PaymentCardFragment.newInstance()
		fragment.show(supportFragmentManager, "")
	}

	override fun runTPay(qrUrl: String, transactionId: Long) {
		val fragment = PaymentTPayFragment.newInstance(qrUrl, transactionId)
		fragment.show(supportFragmentManager, "")
	}

	override fun runSberPay(qrUrl: String, transactionId: Long) {
		val fragment = PaymentSberPayFragment.newInstance(qrUrl, transactionId)
		fragment.show(supportFragmentManager, "")
	}

	override fun runSbp(qrUrl: String, provierQrId: String, transactionId: Long, listOfBanks: ArrayList<SBPBanksItem>) {
		val fragment = PaymentSBPFragment.newInstance(qrUrl, provierQrId, transactionId, listOfBanks)
		fragment.show(supportFragmentManager, "")
	}

	override fun runMirPay(deepLink: String, guid: String) {
		val fragment = PaymentMirPayFragment.newInstance(deepLink, guid)
		fragment.show(supportFragmentManager, "")
	}

	override fun onQrPayError(transactionId: Long?, errorCode: String?) {
		onPaymentQrPayFailed(transactionId ?: 0, 0)
		val fragment = PaymentFinishFragmentFragment.newInstance(
			PaymentFinishStatus.Failed,
			errorCode ?: ApiError.CODE_ERROR,
			false
		)
		fragment.show(supportFragmentManager, "")
	}

	override fun runGooglePay() {
		sdkConfiguration.terminalConfiguration.gPayGatewayName
		GooglePayHandler.present(paymentConfiguration!!, sdkConfiguration.terminalConfiguration.gPayGatewayName, this, REQUEST_CODE_GOOGLE_PAY)
	}

	override fun onSBPFinish(success: Boolean) {
		if (success) {
			val fragment = PaymentFinishFragmentFragment.newInstance(PaymentFinishStatus.Succeeded)
			fragment.show(supportFragmentManager, "")
		} else {
			val fragment = PaymentFinishFragmentFragment.newInstance(PaymentFinishStatus.Failed)
			fragment.show(supportFragmentManager, "")
		}
	}

	override fun onPayClicked(cryptogram: String) {
		val fragment = PaymentProcessFragment.newInstance(cryptogram)
		fragment.show(supportFragmentManager, "")
	}

	override fun onPaymentFinished(transactionId: Long) {
		setResult(Activity.RESULT_OK, Intent().apply {
			putExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, transactionId)
			putExtra(
				CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
				CloudpaymentsSDK.TransactionStatus.Succeeded
			)
		})
	}

	override fun onPaymentFailed(transactionId: Long, reasonCode: Int?) {
		setResult(Activity.RESULT_OK, Intent().apply {
			putExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, transactionId)
			putExtra(
				CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
				CloudpaymentsSDK.TransactionStatus.Failed
			)
			reasonCode?.let { putExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, it) }
		})
	}

	override fun onPaymentQrPayFinished(transactionId: Long) {
		setResult(Activity.RESULT_OK, Intent().apply {
			putExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, transactionId)
			putExtra(
				CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
				CloudpaymentsSDK.TransactionStatus.Succeeded
			)
		})
	}

	override fun onPaymentQrPayFailed(transactionId: Long, reasonCode: Int?) {
		setResult(Activity.RESULT_OK, Intent().apply {
			putExtra(CloudpaymentsSDK.IntentKeys.TransactionId.name, transactionId)
			putExtra(
				CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
				CloudpaymentsSDK.TransactionStatus.Failed
			)
			reasonCode?.let { putExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, it) }
		})
	}

	fun onInternetConnectionError() {
		val fragment = PaymentFinishFragmentFragment.newInstance(
			PaymentFinishStatus.Failed,
			ApiError.CODE_ERROR_CONNECTION,
			false
		)
		fragment.show(supportFragmentManager, "")
	}

	override fun finishPayment() {
		finish()
	}

	override fun onMirPayGetCryptogramFinished(cryptogram: String) {
		val fragment = PaymentProcessFragment.newInstance(cryptogram)
		fragment.show(supportFragmentManager, "")
	}

	override fun onMirPayGetCryptogramFailed(reasonCode: Int?) {
		setResult(Activity.RESULT_OK, Intent().apply {
			putExtra(
				CloudpaymentsSDK.IntentKeys.TransactionStatus.name,
				CloudpaymentsSDK.TransactionStatus.Failed
			)
			reasonCode?.let { putExtra(CloudpaymentsSDK.IntentKeys.TransactionReasonCode.name, it) }
		})
	}

	override fun retryPayment() {
		setResult(Activity.RESULT_CANCELED, Intent())
		showPaymentOptions()
	}

	fun showPaymentOptions() {
		showUi()
	}

	override fun paymentWillFinish() {
		finish()
	}

	@Deprecated("Deprecated in Java")
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

		when (requestCode) {

			REQUEST_CODE_GOOGLE_PAY -> {
				when (resultCode) {
					Activity.RESULT_OK -> {
						handleGooglePaySuccess(data)
					}

					Activity.RESULT_CANCELED, AutoResolveHelper.RESULT_ERROR -> {
						handleGooglePayFailure(data)
					}

					else -> super.onActivityResult(requestCode, resultCode, data)
				}
			}

			else -> super.onActivityResult(requestCode, resultCode, data)
		}
	}

	private fun handleGooglePaySuccess(intent: Intent?) {
		if (intent != null) {
			val paymentData = PaymentData.getFromIntent(intent)
			val token = paymentData?.paymentMethodToken?.token

			if (token != null) {
				val fragment = PaymentProcessFragment.newInstance(token)
				nextFragment(fragment, true, R.id.frame_content)
			}
		}
	}

	private fun handleGooglePayFailure(intent: Intent?) {
		finish()
	}

	private fun checkCurrency() {
		if (paymentConfiguration!!.paymentData.currency.isEmpty()) {
			paymentConfiguration!!.paymentData.currency = "RUB"
		}
	}
}