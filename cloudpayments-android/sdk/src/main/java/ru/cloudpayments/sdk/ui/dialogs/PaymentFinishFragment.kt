package ru.cloudpayments.sdk.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import ru.cloudpayments.sdk.R
import ru.cloudpayments.sdk.configuration.CloudpaymentsSDK
import ru.cloudpayments.sdk.databinding.DialogCpsdkPaymentFinishBinding
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.ui.dialogs.base.BasePaymentDialogFragment
import ru.cloudpayments.sdk.util.InjectorUtils
import ru.cloudpayments.sdk.viewmodel.PaymentFinishViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentFinishViewState

internal enum class PaymentFinishStatus {
	Succeeded,
	Failed;
}

internal class PaymentFinishFragmentFragment: BasePaymentDialogFragment<PaymentFinishViewState, PaymentFinishViewModel>() {
	interface IPaymentFinishFragment {
		fun finishPayment()
		fun retryPayment()
	}

	companion object {

		private const val ARG_STATUS = "arg_status"
		private const val ARG_TRANSACTION_ID = "arg_transaction_id"
		private const val ARG_REASON_CODE = "arg_reason_code"
		private const val ARG_RETRY_PAYMENT = "arg_retry_payment"

		fun newInstance(status: PaymentFinishStatus) = PaymentFinishFragmentFragment().apply {
			arguments = Bundle()
			arguments?.putString(ARG_STATUS, status.toString())
		}

		fun newInstance(status: PaymentFinishStatus, reasonCode: String, retryPayment: Boolean) = PaymentFinishFragmentFragment().apply {
			arguments = Bundle()
			arguments?.putString(ARG_STATUS, status.toString())
			arguments?.putString(ARG_REASON_CODE, reasonCode)
			arguments?.putBoolean(ARG_RETRY_PAYMENT, retryPayment)
		}

		fun newInstance(status: PaymentFinishStatus, transactionId: Long, reasonCode: String, retryPayment: Boolean) = PaymentFinishFragmentFragment().apply {
			arguments = Bundle()
			arguments?.putString(ARG_STATUS, status.toString())
			arguments?.putLong(ARG_TRANSACTION_ID, transactionId)
			arguments?.putString(ARG_REASON_CODE, reasonCode)
			arguments?.putBoolean(ARG_RETRY_PAYMENT, retryPayment)
		}
	}

	private var _binding: DialogCpsdkPaymentFinishBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = DialogCpsdkPaymentFinishBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override val viewModel: PaymentFinishViewModel by viewModels {
		InjectorUtils.providePaymentFinishViewModelFactory(
			status,
			transactionId,
			reasonCode)
	}

	override fun render(state: PaymentFinishViewState) {
		updateWith(status)
	}

	private val status by lazy {
		val stringStatus = arguments?.getString(ARG_STATUS) ?: ""
		try {
			PaymentFinishStatus.valueOf(stringStatus)
		} catch(e: IllegalArgumentException) {
			PaymentFinishStatus.Failed
		}
	}

	private val transactionId by lazy {
		arguments?.getLong(ARG_TRANSACTION_ID) ?: 0
	}

	private val reasonCode by lazy {
		arguments?.getString(ARG_REASON_CODE) ?: ""
	}

	private val retryPayment by lazy {
		arguments?.getBoolean(ARG_RETRY_PAYMENT) ?: false
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (savedInstanceState == null) {
			activity().component.inject(viewModel)
			updateWith(status)
		}
	}

	private fun updateWith(status: PaymentFinishStatus) {

		val listener = requireActivity() as? IPaymentFinishFragment

		if (paymentConfiguration?.mode != CloudpaymentsSDK.SDKRunMode.SelectPaymentMethod) {
			if(paymentConfiguration?.showResultScreenForSinglePaymentMode == false) {
				listener?.finishPayment()
				dismiss()
				activity?.finish()
			}
		}

		var status = status

		binding.buttonFinish.isInvisible = false
		binding.buttonFinish.setBackgroundResource(R.drawable.cpsdk_bg_rounded_blue_button)
		binding.buttonFinish.setTextColor(context?.let { ContextCompat.getColor(it, R.color.cpsdk_white) } ?: 0xFFFFFF)

		when (status) {
			PaymentFinishStatus.Succeeded -> {
				binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_success)
				binding.textStatus.setText(R.string.cpsdk_text_process_title_success)
				binding.textDescription.text = ""
				binding.buttonFinish.setText(R.string.cpsdk_text_process_button_success)
				binding.buttonFinish.setOnClickListener {
					listener?.finishPayment()
					dismiss()
					activity?.finish()
				}
			}

			PaymentFinishStatus.Failed -> {
					binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_failure)
					binding.textStatus.text =
						context?.let { ApiError.getErrorDescription(it, reasonCode) }
					binding.textDescription.text =
						context?.let { ApiError.getErrorDescriptionExtra(it, reasonCode) }

				if (retryPayment) {
					binding.buttonFinish.setText(R.string.cpsdk_text_process_button_error)
					binding.buttonFinish.setOnClickListener {
						listener?.retryPayment()
						dismiss()
					}
				} else {
					binding.buttonFinish.setText(R.string.cpsdk_text_process_button_ok)
					binding.buttonFinish.setOnClickListener {
						listener?.finishPayment()
						dismiss()
						activity?.finish()
					}
				}
			}
		}
	}
}