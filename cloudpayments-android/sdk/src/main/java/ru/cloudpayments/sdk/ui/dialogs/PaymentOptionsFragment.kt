package ru.cloudpayments.sdk.ui.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import com.google.android.material.checkbox.MaterialCheckBox
import ru.cloudpayments.sdk.R
import ru.cloudpayments.sdk.api.models.SBPBanksItem
import ru.cloudpayments.sdk.databinding.DialogCpsdkPaymentOptionsBinding
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.ui.PaymentActivity
import ru.cloudpayments.sdk.ui.dialogs.base.BasePaymentBottomSheetFragment
import ru.cloudpayments.sdk.util.InjectorUtils
import ru.cloudpayments.sdk.util.TextWatcherAdapter
import ru.cloudpayments.sdk.util.emailIsValid
import ru.cloudpayments.sdk.util.hideKeyboard
import ru.cloudpayments.sdk.viewmodel.PaymentOptionsViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentOptionsViewState

internal enum class PaymentOptionsStatus {
	Waiting,
	TPayLoading,
	TPaySuccess,
	SberPayLoading,
	SberPaySuccess,
	SbpLoading,
	SbpSuccess,
	MirPayLoading,
	MirPaySuccess,
	Failed;
}

internal class PaymentOptionsFragment :
	BasePaymentBottomSheetFragment<PaymentOptionsViewState, PaymentOptionsViewModel>() {
	interface IPaymentOptionsFragment {
		fun runCardPayment()
		fun runTPay(qrUrl: String, transactionId: Long)
		fun runSberPay(qrUrl: String, transactionId: Long)
		fun runSbp(qrUrl: String, providerQrId: String, transactionId: Long, listOfBanks: ArrayList<SBPBanksItem>)
		fun runMirPay(deepLink: String, guid: String)
		fun onQrPayError(transactionId: Long?, errorCode: String?)
		fun runGooglePay()
	}

	companion object {
		fun newInstance() = PaymentOptionsFragment().apply {
			arguments = Bundle()
		}
	}

	private var _binding: DialogCpsdkPaymentOptionsBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = DialogCpsdkPaymentOptionsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override val viewModel: PaymentOptionsViewModel by viewModels {
		InjectorUtils.providePaymentOptionsViewModelFactory(paymentConfiguration!!)
	}

	override fun render(state: PaymentOptionsViewState) {

		if (sdkConfig?.availablePaymentMethods?.googlePayAvailable == true && paymentConfiguration?.paymentData?.splits.isNullOrEmpty()) {
			binding.buttonGooglepay.root.visibility = View.VISIBLE
		} else {
			binding.buttonGooglepay.root.visibility = View.GONE
		}

		if (sdkConfig?.availablePaymentMethods?.tPayAvailable == true && paymentConfiguration?.paymentData?.splits.isNullOrEmpty()) {
			binding.buttonTPay.visibility = View.VISIBLE
		} else {
			binding.buttonTPay.visibility = View.GONE
		}

		if (sdkConfig?.availablePaymentMethods?.sberPayAvailable == true && paymentConfiguration?.paymentData?.splits.isNullOrEmpty()) {
			binding.buttonSberPay.visibility = View.VISIBLE
		} else {
			binding.buttonSberPay.visibility = View.GONE
		}

		if (sdkConfig?.availablePaymentMethods?.sbpAvailable == true && paymentConfiguration?.paymentData?.splits.isNullOrEmpty()) {
			binding.buttonSbp.visibility = View.VISIBLE
		} else {
			binding.buttonSbp.visibility = View.GONE
		}

		if (sdkConfig?.availablePaymentMethods?.mirPayAvailable == true && paymentConfiguration?.paymentData?.splits.isNullOrEmpty()) {
			binding.buttonMirPay.visibility = View.VISIBLE
		} else {
			binding.buttonMirPay.visibility = View.GONE
		}

		updateWith(state.status, state.reasonCode)
	}

	private fun updateWith(status: PaymentOptionsStatus, errorCode: String? = null) {

		var status = status

		when (status) {
			PaymentOptionsStatus.Waiting -> {
				setTPayLoading(false)
				setSberPayLoading(false)
				setSbpLoading(false)
				setMirPayLoading(false)
			}
			PaymentOptionsStatus.TPayLoading -> {
				setTPayLoading(true)
				disableAllButtons()
			}
			PaymentOptionsStatus.TPaySuccess -> {
				setTPayLoading(false)
				enableAllButtons()
				val listener = requireActivity() as? IPaymentOptionsFragment
				val qrUrl = viewModel.currentState.qrUrl
				val transactionId = viewModel.currentState.transactionId
				if (qrUrl != null && transactionId != null) {
					listener?.runTPay(qrUrl, transactionId)
					dismiss()
				}
			}
			PaymentOptionsStatus.SberPayLoading -> {
				setSberPayLoading(true)
				disableAllButtons()
			}
			PaymentOptionsStatus.SberPaySuccess -> {
				setSberPayLoading(false)
				enableAllButtons()
				val listener = requireActivity() as? IPaymentOptionsFragment
				val qrUrl = viewModel.currentState.qrUrl
				val transactionId = viewModel.currentState.transactionId
				if (qrUrl != null && transactionId != null) {
					listener?.runSberPay(qrUrl, transactionId)
					dismiss()
				}
			}
			PaymentOptionsStatus.SbpLoading -> {
				setSbpLoading(true)
				disableAllButtons()
			}
			PaymentOptionsStatus.SbpSuccess -> {
				setSbpLoading(false)
				enableAllButtons()
				val listener = requireActivity() as? IPaymentOptionsFragment
				val qrUrl = viewModel.currentState.qrUrl
				val providerQrId = viewModel.currentState.providerQrId
				val transactionId = viewModel.currentState.transactionId
				val listOfBanks = viewModel.currentState.listOfBanks
				if (qrUrl != null && providerQrId != null && transactionId != null && listOfBanks != null) {
					listener?.runSbp(qrUrl, providerQrId, transactionId, listOfBanks)
					dismiss()
				}
			}
			PaymentOptionsStatus.Failed -> {
				setTPayLoading(false)
				setSberPayLoading(false)
				setSbpLoading(false)
				setMirPayLoading(false)
				enableAllButtons()

				if (errorCode == ApiError.CODE_ERROR_CONNECTION) {
					val listener = requireActivity() as? PaymentActivity
					listener?.onInternetConnectionError()
					dismiss()
					return
				} else {
					val listener = requireActivity() as? IPaymentOptionsFragment
					listener?.onQrPayError(viewModel.currentState.transactionId, errorCode)
				}
			}

			PaymentOptionsStatus.MirPayLoading -> {
				setMirPayLoading(true)
				disableAllButtons()
			}
			PaymentOptionsStatus.MirPaySuccess -> {
				setMirPayLoading(false)
				enableAllButtons()

				val listener = requireActivity() as? IPaymentOptionsFragment
				val deepLink = viewModel.currentState.mirPayDeepLink
				val guid = viewModel.currentState.mirPayGuid
				if (deepLink != null && guid != null) {
					listener?.runMirPay(deepLink, guid)
					dismiss()
				}
			}
		}
	}

	private fun checkSaveCardState () {

		paymentConfiguration?.paymentData?.accountId?.let { accountId ->
			if (accountId.isNotEmpty()) {
				if (paymentConfiguration?.paymentData?.jsonDataHasRecurrent() == true && sdkConfig?.terminalConfiguration?.isSaveCard == 1) {
					setSaveCardHintVisible()
				}
				if (paymentConfiguration?.paymentData?.jsonDataHasRecurrent() == true && sdkConfig?.terminalConfiguration?.isSaveCard == 2) {
					setSaveCardHintVisible()
				}
				if (paymentConfiguration?.paymentData?.jsonDataHasRecurrent() == false && sdkConfig?.terminalConfiguration?.isSaveCard == 2) {
					setSaveCardCheckBoxVisible()
				}
				if (sdkConfig?.terminalConfiguration?.isSaveCard == 3) {
					setSaveCardHintVisible()
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		activity().component.inject(viewModel)

		setTPayLoading(false)
		setSberPayLoading(false)
		setSbpLoading(false)
		setMirPayLoading(false)

		checkSaveCardState()

		binding.editEmail.setText(paymentConfiguration!!.paymentData.email)
		errorMode(
			!binding.editEmail.hasFocus() && !emailIsValid(binding.editEmail.text.toString()),
			binding.editEmail, binding.textFieldEmail
		)

		binding.editEmail.setOnFocusChangeListener { _, hasFocus ->
			errorMode(
				!hasFocus && !emailIsValid(binding.editEmail.text.toString()),
				binding.editEmail, binding.textFieldEmail
			)
		}

		binding.editEmail.addTextChangedListener(object : TextWatcherAdapter() {
			override fun afterTextChanged(s: Editable?) {
				super.afterTextChanged(s)
				updateStateButtons()
			}
		})

		if (paymentConfiguration!!.requireEmail) {
			binding.checkboxSendReceipt.visibility = View.GONE
			binding.textFieldEmail.visibility = View.VISIBLE
			binding.textEmailRequire.visibility = View.VISIBLE
		} else {
			binding.checkboxSendReceipt.visibility = View.VISIBLE
			if (paymentConfiguration!!.paymentData.email.isNullOrEmpty()) {
				binding.checkboxSendReceipt.checkedState = MaterialCheckBox.STATE_UNCHECKED
				binding.textFieldEmail.visibility = View.GONE
			} else {
				binding.checkboxSendReceipt.checkedState = MaterialCheckBox.STATE_CHECKED
				binding.textFieldEmail.visibility = View.VISIBLE
			}
			binding.textEmailRequire.visibility = View.GONE
		}

		binding.checkboxSendReceipt.setOnCheckedChangeListener { _, isChecked ->
			binding.textFieldEmail.isGone = !isChecked
			requireActivity().hideKeyboard()
			updateStateButtons()
		}

		updateStateButtons()

		binding.buttonPayCard.setOnClickListener {
			updateEmail()
			updateSaveCard()

			val listener = requireActivity() as? IPaymentOptionsFragment
			listener?.runCardPayment()
			dismiss()
		}

		binding.buttonGooglepay.root.setOnClickListener {
			updateEmail()
			updateSaveCard()
			val listener = requireActivity() as? IPaymentOptionsFragment
			listener?.runGooglePay()
			dismiss()
		}

		binding.buttonTPay.setOnClickListener {
			updateEmail()
			updateSaveCard()

			viewModel.getTQrPayLink(sdkConfig?.saveCard)
		}

		binding.buttonSberPay.setOnClickListener {
			updateEmail()
			updateSaveCard()

			viewModel.getSberQrPayLink(sdkConfig?.saveCard)
		}

		binding.buttonSbp.setOnClickListener {
			updateEmail()
			updateSaveCard()

			viewModel.getSBPQrPayLink(sdkConfig?.saveCard)
		}

		binding.buttonMirPay.setOnClickListener {
			updateEmail()
			updateSaveCard()

			viewModel.getMirPayLink()
		}

		binding.buttonSaveCardPopup.setOnClickListener {
			showPopupSaveCardInfo()
		}

		binding.buttonCardBeSavedPopup.setOnClickListener {
			showPopupSaveCardInfo()
		}
	}

	private fun updateEmail() {
		if (paymentConfiguration!!.requireEmail || binding.checkboxSendReceipt.isChecked) {
			paymentConfiguration?.paymentData?.email = binding.editEmail.text.toString()
		} else {
			paymentConfiguration?.paymentData?.email = ""
		}
	}

	private fun updateSaveCard() {
		if (binding.checkboxSaveCard.visibility == View.VISIBLE) {
			sdkConfig?.saveCard = binding.checkboxSaveCard.isChecked
		}
	}

	private fun setSaveCardCheckBoxVisible() {
		binding.checkboxSaveCard.visibility = View.VISIBLE
		binding.buttonSaveCardPopup.visibility = View.VISIBLE
		binding.checkboxSaveCard.checkedState = MaterialCheckBox.STATE_UNCHECKED
	}

	private fun setSaveCardHintVisible() {
		binding.textCardBeSaved.visibility = View.VISIBLE
		binding.buttonCardBeSavedPopup.visibility = View.VISIBLE
	}

	private fun showPopupSaveCardInfo() {
		val popupView = layoutInflater.inflate(R.layout.popup_cpsdk_save_card_info, null)

		val wid = LinearLayout.LayoutParams.WRAP_CONTENT
		val high = LinearLayout.LayoutParams.WRAP_CONTENT
		val focus= true
		val popupWindow = PopupWindow(popupView, wid, high, focus)

		val background = activity?.let { ContextCompat.getDrawable(it, R.drawable.cpsdk_bg_popup) }
		popupView.background = background

		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
	}

	private fun updateStateButtons() {
		if (paymentConfiguration!!.requireEmail) {
			if (isValid()) {
				enableAllButtons()
			} else {
				disableAllButtons()
			}
		} else {
			if (binding.checkboxSendReceipt.checkedState == MaterialCheckBox.STATE_CHECKED) {
				if (isValid()) {
					enableAllButtons()
				} else {
					disableAllButtons()
				}
			} else {
				enableAllButtons()
			}
		}
	}

	private fun isValid(): Boolean {
		val valid = if (paymentConfiguration!!.requireEmail) {
			emailIsValid(binding.editEmail.text.toString())
		} else {
			!binding.checkboxSendReceipt.isChecked || emailIsValid(binding.editEmail.text.toString())
		}
		return valid
	}

	private fun disableAllButtons() {
		binding.viewBlockButtons.visibility = View.VISIBLE
	}

	private fun enableAllButtons() {
		binding.viewBlockButtons.visibility = View.GONE
	}

	private fun setTPayLoading(isLoading: Boolean) {
		if (isLoading) {
			binding.buttonTPayLogo.visibility = View.GONE
			binding.buttonTPayProgress.visibility = View.VISIBLE
		} else {
			binding.buttonTPayLogo.visibility = View.VISIBLE
			binding.buttonTPayProgress.visibility = View.GONE
		}
	}

	private fun setSberPayLoading(isLoading: Boolean) {
		if (isLoading) {
			binding.buttonSberPayLogo.visibility = View.GONE
			binding.buttonSberPayProgress.visibility = View.VISIBLE
		} else {
			binding.buttonSberPayLogo.visibility = View.VISIBLE
			binding.buttonSberPayProgress.visibility = View.GONE
		}
	}

	private fun setSbpLoading(isLoading: Boolean) {
		if (isLoading) {
			binding.buttonSbpLogo.visibility = View.GONE
			binding.buttonSbpProgress.visibility = View.VISIBLE
		} else {
			binding.buttonSbpLogo.visibility = View.VISIBLE
			binding.buttonSbpProgress.visibility = View.GONE
		}
	}

	private fun setMirPayLoading(isLoading: Boolean) {
		if (isLoading) {
			binding.buttonMirPayLogo.visibility = View.GONE
			binding.buttonMirPayProgress.visibility = View.VISIBLE
		} else {
			binding.buttonMirPayLogo.visibility = View.VISIBLE
			binding.buttonMirPayProgress.visibility = View.GONE
		}
	}

	override fun onCancel(dialog: DialogInterface) {
		super.onCancel(dialog)
		activity?.finish()
	}
}