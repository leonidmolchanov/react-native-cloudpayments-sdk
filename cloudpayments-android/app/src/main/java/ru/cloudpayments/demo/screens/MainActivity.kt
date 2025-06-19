package ru.cloudpayments.demo.screens

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import ru.cloudpayments.demo.R
import ru.cloudpayments.demo.base.BaseActivity
import ru.cloudpayments.demo.databinding.ActivityMainBinding
import ru.cloudpayments.demo.support.CardIOScanner
import ru.cloudpayments.sdk.api.models.PaymentDataPayer
import ru.cloudpayments.sdk.api.models.PaymentDataReceipt
import ru.cloudpayments.sdk.api.models.PaymentDataReceiptAmounts
import ru.cloudpayments.sdk.api.models.PaymentDataReceiptItem
import ru.cloudpayments.sdk.api.models.PaymentDataRecurrent
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.configuration.PaymentConfiguration
import ru.cloudpayments.sdk.configuration.PaymentData

class MainActivity : BaseActivity() {

	private val cpSdkLauncher = CloudpaymentsSDK.getInstance().launcher(this, result = {

		if (it.status != null) {

			val builder: AlertDialog.Builder = AlertDialog.Builder(this)
			builder.setPositiveButton("OK") { dialog, which ->

			}

			if (it.status == CloudpaymentsSDK.TransactionStatus.Succeeded) {
				builder
					.setTitle("Success")
					.setMessage("Transaction ID: ${it.transactionId}")
			} else {
				builder.setTitle("Fail")
				if (it.reasonCode != 0) {
					builder.setMessage("Transaction ID: ${it.transactionId}, reason code: ${it.reasonCode}")
				} else {
					builder.setMessage("Transaction ID: ${it.transactionId}")
				}
			}

			val dialog: AlertDialog = builder.create()
			dialog.show()
		}
	})

	override val layoutId = R.layout.activity_main

	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityMainBinding.inflate(layoutInflater)
		val view = binding.root
		setContentView(view)

		binding.buttonRunTop.setOnClickListener {
			runCpSdk()
		}

		binding.buttonRun.setOnClickListener {
			runCpSdk()
		}

		binding.buttonRunTpayTop.setOnClickListener {
			runCpSdkTPayMode()
		}

		binding.buttonRunTpay.setOnClickListener {
			runCpSdkTPayMode()
		}

		binding.buttonRunSbpTop.setOnClickListener {
			runCpSdkSbpMode()
		}

		binding.buttonRunSbp.setOnClickListener {
			runCpSdkSbpMode()
		}

		binding.buttonRunSberPayTop.setOnClickListener {
			runCpSdkSberPayMode()
		}

		binding.buttonRunSberPay.setOnClickListener {
			runCpSdkSberPayMode()
		}
	}

	private fun runCpSdk() {

		val apiUrl = binding.editApiUrl.text.toString()
		val publicId = binding.editPublicId.text.toString()
		val amount = binding.editAmount.text.toString()
		val currency = binding.editCurrency.text.toString()
		val invoiceId = binding.editInvoiceId.text.toString()
		val description = binding.editDescription.text.toString()
		val accountId = binding.editAccountId.text.toString()
		val email = binding.editEmail.text.toString()

		val payerFirstName = binding.editPayerFirstName.text.toString()
		val payerLastName = binding.editPayerLastName.text.toString()
		val payerMiddleName = binding.editPayerMiddleName.text.toString()
		val payerBirthDay = binding.editPayerBirth.text.toString()
		val payerAddress = binding.editPayerAddress.text.toString()
		val payerStreet = binding.editPayerStreet.text.toString()
		val payerCity = binding.editPayerCity.text.toString()
		val payerCountry = binding.editPayerCountry.text.toString()
		val payerPhone = binding.editPayerPhone.text.toString()
		val payerPostcode = binding.editPayerPostcode.text.toString()
		
		val jsonData = binding.editJsonData.text.toString()

		val isDualMessagePayment = binding.checkboxDualMessagePayment.isChecked

		var payer = PaymentDataPayer()
		payer.firstName = payerFirstName
		payer.lastName = payerLastName
		payer.middleName = payerMiddleName
		payer.birthDay = payerBirthDay
		payer.address = payerAddress
		payer.street = payerStreet
		payer.city = payerCity
		payer.country = payerCountry
		payer.phone = payerPhone
		payer.postcode = payerPostcode

		val receiptItem = PaymentDataReceiptItem(
			label = description,
			price = 300.0,
			quantity = 3.0,
			amount = 900.0,
			vat = 20,
			method = 0,
			objectt = 0
		)

		val receiptItems = ArrayList<PaymentDataReceiptItem>()
		receiptItems.add(receiptItem)

		val receiptAmounts = PaymentDataReceiptAmounts(
			electronic = 900.0,
			advancePayment = 0.0,
			credit = 0.0,
			provision = 0.0
		)

		val receipt = PaymentDataReceipt(
			items = receiptItems,
			taxationSystem = 0,
			email = email,
			phone = payerPhone,
			isBso = false,
			amounts = receiptAmounts
		)

		val recurrent = PaymentDataRecurrent(
			interval = "Month",
			period = 1,
			customerReceipt = receipt
		)

		val paymentData = PaymentData(
			amount = amount,
			currency = currency,
			invoiceId = invoiceId,
			description = description,
			accountId = accountId,
			email = email,
			payer = payer,
			receipt = receipt,
			recurrent = recurrent,
			jsonData = jsonData
		)

		val configuration = PaymentConfiguration(
			publicId = publicId,
			paymentData = paymentData,
			scanner = CardIOScanner(),
			requireEmail = true,
			useDualMessagePayment = isDualMessagePayment,
			apiUrl = apiUrl,
			testMode = true
		)
		cpSdkLauncher.launch(configuration)
	}

	private fun runCpSdkTPayMode() {

		val apiUrl = binding.editApiUrl.text.toString()
		val publicId = binding.editPublicId.text.toString()
		val amount = binding.editAmount.text.toString()
		val currency = binding.editCurrency.text.toString()
		val invoiceId = binding.editInvoiceId.text.toString()
		val description = binding.editDescription.text.toString()
		val accountId = binding.editAccountId.text.toString()
		val email = binding.editEmail.text.toString()

		val payerFirstName = binding.editPayerFirstName.text.toString()
		val payerLastName = binding.editPayerLastName.text.toString()
		val payerMiddleName = binding.editPayerMiddleName.text.toString()
		val payerBirthDay = binding.editPayerBirth.text.toString()
		val payerAddress = binding.editPayerAddress.text.toString()
		val payerStreet = binding.editPayerStreet.text.toString()
		val payerCity = binding.editPayerCity.text.toString()
		val payerCountry = binding.editPayerCountry.text.toString()
		val payerPhone = binding.editPayerPhone.text.toString()
		val payerPostcode = binding.editPayerPostcode.text.toString()

		val jsonData = binding.editJsonData.text.toString()

		val isDualMessagePayment = binding.checkboxDualMessagePayment.isChecked

		val isSaveCard = binding.checkboxSaveCard.isChecked

		var payer = PaymentDataPayer()
		payer.firstName = payerFirstName
		payer.lastName = payerLastName
		payer.middleName = payerMiddleName
		payer.birthDay = payerBirthDay
		payer.address = payerAddress
		payer.street = payerStreet
		payer.city = payerCity
		payer.country = payerCountry
		payer.phone = payerPhone
		payer.postcode = payerPostcode

		val paymentData = PaymentData(
			amount = amount,
			currency = currency,
			invoiceId = invoiceId,
			description = description,
			accountId = accountId,
			email = email,
			payer = payer,
			jsonData = jsonData
		)

		val configuration = PaymentConfiguration(
			publicId = publicId,
			paymentData = paymentData,
			useDualMessagePayment = isDualMessagePayment,
			apiUrl = apiUrl,
			mode = CloudpaymentsSDK.SDKRunMode.TPay,
			showResultScreenForSinglePaymentMode = false,
			saveCardForSinglePaymentMode = isSaveCard,
			testMode = true
		)
		cpSdkLauncher.launch(configuration)
	}

	private fun runCpSdkSbpMode() {

		val apiUrl = binding.editApiUrl.text.toString()
		val publicId = binding.editPublicId.text.toString()
		val amount = binding.editAmount.text.toString()
		val currency = binding.editCurrency.text.toString()
		val invoiceId = binding.editInvoiceId.text.toString()
		val description = binding.editDescription.text.toString()
		val accountId = binding.editAccountId.text.toString()
		val email = binding.editEmail.text.toString()

		val payerFirstName = binding.editPayerFirstName.text.toString()
		val payerLastName = binding.editPayerLastName.text.toString()
		val payerMiddleName = binding.editPayerMiddleName.text.toString()
		val payerBirthDay = binding.editPayerBirth.text.toString()
		val payerAddress = binding.editPayerAddress.text.toString()
		val payerStreet = binding.editPayerStreet.text.toString()
		val payerCity = binding.editPayerCity.text.toString()
		val payerCountry = binding.editPayerCountry.text.toString()
		val payerPhone = binding.editPayerPhone.text.toString()
		val payerPostcode = binding.editPayerPostcode.text.toString()

		val jsonData = binding.editJsonData.text.toString()

		val isDualMessagePayment = binding.checkboxDualMessagePayment.isChecked

		val isSaveCard = binding.checkboxSaveCard.isChecked

		var payer = PaymentDataPayer()
		payer.firstName = payerFirstName
		payer.lastName = payerLastName
		payer.middleName = payerMiddleName
		payer.birthDay = payerBirthDay
		payer.address = payerAddress
		payer.street = payerStreet
		payer.city = payerCity
		payer.country = payerCountry
		payer.phone = payerPhone
		payer.postcode = payerPostcode

		val paymentData = PaymentData(
			amount = amount,
			currency = currency,
			invoiceId = invoiceId,
			description = description,
			accountId = accountId,
			email = email,
			payer = payer,
			jsonData = jsonData
		)

		val configuration = PaymentConfiguration(
			publicId = publicId,
			paymentData = paymentData,
			useDualMessagePayment = isDualMessagePayment,
			apiUrl = apiUrl,
			mode = CloudpaymentsSDK.SDKRunMode.SBP,
			saveCardForSinglePaymentMode = isSaveCard,
			testMode = true
		)
		cpSdkLauncher.launch(configuration)
	}

	private fun runCpSdkSberPayMode() {

		val apiUrl = binding.editApiUrl.text.toString()
		val publicId = binding.editPublicId.text.toString()
		val amount = binding.editAmount.text.toString()
		val currency = binding.editCurrency.text.toString()
		val invoiceId = binding.editInvoiceId.text.toString()
		val description = binding.editDescription.text.toString()
		val accountId = binding.editAccountId.text.toString()
		val email = binding.editEmail.text.toString()

		val payerFirstName = binding.editPayerFirstName.text.toString()
		val payerLastName = binding.editPayerLastName.text.toString()
		val payerMiddleName = binding.editPayerMiddleName.text.toString()
		val payerBirthDay = binding.editPayerBirth.text.toString()
		val payerAddress = binding.editPayerAddress.text.toString()
		val payerStreet = binding.editPayerStreet.text.toString()
		val payerCity = binding.editPayerCity.text.toString()
		val payerCountry = binding.editPayerCountry.text.toString()
		val payerPhone = binding.editPayerPhone.text.toString()
		val payerPostcode = binding.editPayerPostcode.text.toString()

		val jsonData = binding.editJsonData.text.toString()

		val isDualMessagePayment = binding.checkboxDualMessagePayment.isChecked

		val isSaveCard = binding.checkboxSaveCard.isChecked

		var payer = PaymentDataPayer()
		payer.firstName = payerFirstName
		payer.lastName = payerLastName
		payer.middleName = payerMiddleName
		payer.birthDay = payerBirthDay
		payer.address = payerAddress
		payer.street = payerStreet
		payer.city = payerCity
		payer.country = payerCountry
		payer.phone = payerPhone
		payer.postcode = payerPostcode

		val paymentData = PaymentData(
			amount = amount,
			currency = currency,
			invoiceId = invoiceId,
			description = description,
			accountId = accountId,
			email = email,
			payer = payer,
			jsonData = jsonData
		)

		val configuration = PaymentConfiguration(
			publicId = publicId,
			paymentData = paymentData,
			useDualMessagePayment = isDualMessagePayment,
			apiUrl = apiUrl,
			mode = CloudpaymentsSDK.SDKRunMode.SberPay,
			saveCardForSinglePaymentMode = isSaveCard,
			testMode = true
		)
		cpSdkLauncher.launch(configuration)
	}
}