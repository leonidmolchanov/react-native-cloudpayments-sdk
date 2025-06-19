package ru.cloudpayments.sdk.ui.dialogs

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import ru.cloudpayments.sdk.R
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.databinding.DialogCpsdkPaymentSberpayBinding
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.ui.dialogs.base.BasePaymentDialogFragment
import ru.cloudpayments.sdk.util.InjectorUtils
import ru.cloudpayments.sdk.viewmodel.PaymentSberPayViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentSberPayViewState

internal enum class PaymentSberPayStatus {
	InProcess,
	Succeeded,
	Failed;
}

internal class PaymentSberPayFragment: BasePaymentDialogFragment<PaymentSberPayViewState, PaymentSberPayViewModel>() {
	interface IPaymentSberPayFragment {
		fun onPaymentQrPayFinished(transactionId: Long)
		fun onPaymentQrPayFailed(transactionId: Long, reasonCode: Int?)
		fun finishPayment()
		fun retryPayment()
	}

	companion object {
		private const val ARG_QR_URL = "ARG_QR_URL"
		private const val ARG_TRANSACTION_ID = "ARG_TRANSACTION_ID"


		fun newInstance(qrUrl: String, transactionId: Long) = PaymentSberPayFragment().apply {
			arguments = Bundle()
			arguments?.putString(ARG_QR_URL, qrUrl)
			arguments?.putLong(ARG_TRANSACTION_ID, transactionId)
		}
	}

	private var _binding: DialogCpsdkPaymentSberpayBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = DialogCpsdkPaymentSberpayBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onResume() {
		super.onResume()
		if (paymentConfiguration?.mode == CloudpaymentsSDK.SDKRunMode.SberPay) {
			currentState?.transactionId.let {
				viewModel.qrLinkStatusWait(it)
			}
		} else {
			transactionId.let {
				viewModel.qrLinkStatusWait(it)
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private var currentState: PaymentSberPayViewState? = null

	override val viewModel: PaymentSberPayViewModel by viewModels {
		InjectorUtils.providePaymentSberPayViewModelFactory(
			qrUrl,
			transactionId,
			paymentConfiguration!!,
			sdkConfig?.saveCard)
	}

	override fun render(state: PaymentSberPayViewState) {
		currentState = state
		updateWith(state.status, state.errorMessage)
	}

	private val qrUrl by lazy {
		arguments?.getString(ARG_QR_URL) ?: ""
	}

	private val transactionId by lazy {
		arguments?.getLong(ARG_TRANSACTION_ID) ?: 0
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (savedInstanceState == null) {
			activity().component.inject(viewModel)
		}
	}

	private fun updateWith(status: PaymentSberPayStatus, error: String? = null) {

		when (status) {
			PaymentSberPayStatus.InProcess -> {
				binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_progress)
				binding.textStatus.setText(R.string.cpsdk_text_sber_pay_title)
				binding.textDescription.visibility = View.VISIBLE
				binding.textDescription.setText(R.string.cpsdk_text_alt_pays_description)
				binding.buttonFinish.visibility = View.VISIBLE
				binding.buttonFinish.setBackgroundResource(R.drawable.cpsdk_bg_rounded_white_button_with_border)
				binding.buttonFinish.setTextColor(context?.let { ContextCompat.getColor(it, R.color.cpsdk_blue) } ?: 0xFFFFFF)

				if (paymentConfiguration?.mode == CloudpaymentsSDK.SDKRunMode.SberPay) {
					binding.buttonFinish.setText(R.string.cpsdk_text_process_button_close)
					binding.buttonFinish.setOnClickListener {
						val listener = requireActivity() as? IPaymentSberPayFragment
						listener?.finishPayment()
						dismiss()
					}

					if (currentState?.qrUrl.isNullOrBlank()) {
						viewModel.getSberQrPayLink()
					} else {
						runBankApp(currentState?.qrUrl)
						viewModel.qrLinkStatusWait(currentState?.transactionId)
					}

				} else {

						binding.buttonFinish.setText(R.string.cpsdk_text_process_button_tpay_sberpay_sbp)
						binding.buttonFinish.setOnClickListener {
							val listener = requireActivity() as? IPaymentSberPayFragment
							listener?.retryPayment()
							dismiss()
						}

						runBankApp(qrUrl)
						viewModel.qrLinkStatusWait(transactionId)
				}
			}

			PaymentSberPayStatus.Succeeded, PaymentSberPayStatus.Failed -> {

				if (!isActive) {
					return
				}

				val listener = requireActivity() as? IPaymentSberPayFragment

				if (paymentConfiguration?.mode != CloudpaymentsSDK.SDKRunMode.SelectPaymentMethod) {
					if(paymentConfiguration?.showResultScreenForSinglePaymentMode == false) {
						if (status == PaymentSberPayStatus.Succeeded) {
							listener?.onPaymentQrPayFinished(currentState?.transactionId ?: 0)
						} else {
							listener?.onPaymentQrPayFailed(currentState?.transactionId ?: 0, currentState?.reasonCode)
						}
						listener?.finishPayment()
						dismiss()
						activity?.finish()
					}
				}

				binding.buttonFinish.visibility = View.VISIBLE
				binding.buttonFinish.setBackgroundResource(R.drawable.cpsdk_bg_rounded_blue_button)
				binding.buttonFinish.setTextColor(context?.let { ContextCompat.getColor(it, R.color.cpsdk_white) } ?: 0xFFFFFF)

				if (status == PaymentSberPayStatus.Succeeded) {
					binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_success)
					binding.textStatus.setText(R.string.cpsdk_text_process_title_success)
					binding.textDescription.text = ""
					binding.textDescription.visibility = View.GONE
					binding.buttonFinish.setText(R.string.cpsdk_text_process_button_success)

					listener?.onPaymentQrPayFinished(currentState?.transactionId ?: 0)

					binding.buttonFinish.setOnClickListener {

						listener?.finishPayment()
						dismiss()
					}
				} else {

					binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_failure)
					binding.textStatus.text =
						context?.let { ApiError.getErrorDescription(it, currentState?.reasonCode.toString()) }
					binding.textDescription.text =
						context?.let {
							val desc = ApiError.getErrorDescriptionExtra(it, currentState?.reasonCode.toString())
							if (desc.isEmpty()) {
								binding.textDescription.visibility = View.GONE
								""
							} else {
								binding.textDescription.visibility = View.VISIBLE
								desc
							}
						}

					if (paymentConfiguration?.mode == CloudpaymentsSDK.SDKRunMode.SberPay) {
						binding.buttonFinish.setText(R.string.cpsdk_text_process_button_close)
					} else {
						binding.buttonFinish.setText(R.string.cpsdk_text_process_button_error)
					}

					listener?.onPaymentQrPayFailed(currentState?.transactionId ?: 0, currentState?.reasonCode)

					binding.buttonFinish.setOnClickListener {
						if (paymentConfiguration?.mode == CloudpaymentsSDK.SDKRunMode.SberPay) {
							listener?.finishPayment()
						} else {
							listener?.retryPayment()
						}
						dismiss()
					}
				}
			}
		}
	}

	private fun runBankApp(qrUrl: String?) {
		try {
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(qrUrl))
			context?.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			Toast.makeText(requireContext(), getString(R.string.cpsdk_text_alt_pays_no_bank_app), Toast.LENGTH_LONG).show()
		}
	}
}