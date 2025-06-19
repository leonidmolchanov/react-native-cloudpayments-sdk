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
import ru.cloudpayments.sdk.databinding.DialogCpsdkPaymentMirpayBinding
import ru.cloudpayments.sdk.models.ApiError
import ru.cloudpayments.sdk.ui.dialogs.base.BasePaymentDialogFragment
import ru.cloudpayments.sdk.util.InjectorUtils
import ru.cloudpayments.sdk.viewmodel.PaymentMirPayViewModel
import ru.cloudpayments.sdk.viewmodel.PaymentMirPayViewState

internal enum class PaymentMirPayStatus {
	InProcess,
	Succeeded,
	Failed;
}

internal class PaymentMirPayFragment :
	BasePaymentDialogFragment<PaymentMirPayViewState, PaymentMirPayViewModel>() {
	interface IPaymentMirPayFragment {
		fun onMirPayGetCryptogramFinished(cryptogram: String)
		fun onMirPayGetCryptogramFailed(reasonCode: Int?)
		fun retryPayment()
	}

	companion object {
		private const val ARG_DEEP_LINK = "ARG_DEEP_LINK"
		private const val ARG_GUID = "ARG_GUID"

		fun newInstance(deepLink: String, guid: String) = PaymentMirPayFragment().apply {
			arguments = Bundle()
			arguments?.putString(ARG_DEEP_LINK, deepLink)
			arguments?.putString(ARG_GUID, guid)
		}
	}

	private var _binding: DialogCpsdkPaymentMirpayBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = DialogCpsdkPaymentMirpayBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	private var currentState: PaymentMirPayViewState? = null

	override val viewModel: PaymentMirPayViewModel by viewModels {
		InjectorUtils.providePaymentMirPayViewModelFactory(
			deepLink,
			guid,
			paymentConfiguration!!
		)
	}

	override fun render(state: PaymentMirPayViewState) {
		currentState = state
		updateWith(state.status, state.errorMessage)
	}

	private val deepLink by lazy {
		arguments?.getString(ARG_DEEP_LINK) ?: ""
	}

	private val guid by lazy {
		arguments?.getString(ARG_GUID) ?: ""
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (savedInstanceState == null) {
			activity().component.inject(viewModel)
		}
	}

	private fun updateWith(status: PaymentMirPayStatus, error: String? = null) {

		when (status) {
			PaymentMirPayStatus.InProcess -> {
				binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_progress)
				binding.textStatus.setText(R.string.cpsdk_text_mir_pay_title)
				binding.textDescription.visibility = View.VISIBLE
				binding.textDescription.setText(R.string.cpsdk_text_alt_pays_description)
				binding.buttonFinish.visibility = View.VISIBLE
				binding.buttonFinish.setBackgroundResource(R.drawable.cpsdk_bg_rounded_white_button_with_border)
				binding.buttonFinish.setTextColor(context?.let {
					ContextCompat.getColor(
						it,
						R.color.cpsdk_blue
					)
				} ?: 0xFFFFFF)

				binding.buttonFinish.setText(R.string.cpsdk_text_process_button_tpay_sberpay_sbp)
				binding.buttonFinish.setOnClickListener {
					val listener = requireActivity() as? IPaymentMirPayFragment
					listener?.retryPayment()
					dismiss()
				}

				runMirPayApp(deepLink)
				viewModel.getCryptogram(guid)
			}

			PaymentMirPayStatus.Succeeded -> {
				dismiss()
				val listener = requireActivity() as? IPaymentMirPayFragment
				listener?.onMirPayGetCryptogramFinished(currentState?.cryptogram!!)
			}

			PaymentMirPayStatus.Failed -> {

				val listener = requireActivity() as? IPaymentMirPayFragment
				listener?.onMirPayGetCryptogramFailed(currentState?.reasonCode)


				binding.iconStatus.setImageResource(R.drawable.cpsdk_ic_failure)
				binding.textStatus.text =
					context?.let {
						ApiError.getErrorDescription(
							it,
							currentState?.reasonCode.toString()
						)
					}
				binding.textDescription.text =
					context?.let {
						val desc = ApiError.getErrorDescriptionExtra(
							it,
							currentState?.reasonCode.toString()
						)
						if (desc.isEmpty()) {
							binding.textDescription.visibility = View.GONE
							""
						} else {
							binding.textDescription.visibility = View.VISIBLE
							desc
						}
					}

				binding.buttonFinish.visibility = View.VISIBLE
				binding.buttonFinish.setBackgroundResource(R.drawable.cpsdk_bg_rounded_blue_button)
				binding.buttonFinish.setTextColor(context?.let {
					ContextCompat.getColor(
						it,
						R.color.cpsdk_white
					)
				} ?: 0xFFFFFF)
				binding.buttonFinish.setText(R.string.cpsdk_text_process_button_error)
				binding.buttonFinish.setOnClickListener {
					listener?.retryPayment()
					dismiss()
				}
			}
		}
	}

	private fun runMirPayApp(deepLink: String?) {
		try {
			val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
			context?.startActivity(intent)
		} catch (e: ActivityNotFoundException) {
			Toast.makeText(
				requireContext(),
				getString(R.string.cpsdk_text_mir_pay_error_app),
				Toast.LENGTH_LONG
			).show()
		}
	}
}