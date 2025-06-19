package ru.cloudpayments.sdk.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentDataRecurrent(
	@SerializedName("interval") var interval: String,
	@SerializedName("period") var period: Int,
	@SerializedName("maxPeriods")var maxPeriods: Int? = null,
	@SerializedName("startDate")var startDate: String? = null,
	@SerializedName("amount")var amount: Float? = null,
	@SerializedName("customerReceipt") var customerReceipt: PaymentDataReceipt? = null) : Parcelable
