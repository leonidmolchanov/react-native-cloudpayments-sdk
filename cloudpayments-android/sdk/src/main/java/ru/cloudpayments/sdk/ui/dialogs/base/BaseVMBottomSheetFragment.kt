package ru.cloudpayments.sdk.ui.dialogs.base

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.cloudpayments.sdk.viewmodel.BaseViewModel
import ru.cloudpayments.sdk.viewmodel.BaseViewState

internal abstract class BaseVMBottomSheetFragment<VS: BaseViewState, VM: BaseViewModel<VS>>: BottomSheetDialogFragment() {

	abstract val viewModel: VM
	abstract fun render(state: VS)

	var isActive = false

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.viewState.observe(viewLifecycleOwner, Observer {
			render(it)
		})
	}

	override fun onStart() {
		super.onStart()
		dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
	}

	override fun onResume() {
		super.onResume()

		isActive = true
	}

	override fun onPause() {
		super.onPause()

		isActive = false
	}
}