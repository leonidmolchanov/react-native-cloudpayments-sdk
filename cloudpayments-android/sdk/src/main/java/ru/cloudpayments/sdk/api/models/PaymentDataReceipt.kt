package ru.cloudpayments.sdk.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentDataReceipt(
	@SerializedName("taxationSystem") var taxationSystem: Int,
	@SerializedName("email") var email: String = "",
	@SerializedName("phone") var phone: String = "",
	@SerializedName("isBso") var isBso: Boolean = false,
	@SerializedName("Items") var items: ArrayList<PaymentDataReceiptItem> = ArrayList(),
	@SerializedName("amounts") var amounts: PaymentDataReceiptAmounts? = null) : Parcelable

@Parcelize
data class PaymentDataReceiptItem(
	@SerializedName("label") var label: String,
	@SerializedName("price") var price: Double,
	@SerializedName("quantity") var quantity: Double,
	@SerializedName("amount") var amount: Double,
	@SerializedName("vat") var vat: Int,
	@SerializedName("method") var method: Int,
	@SerializedName("object") var objectt: Int) : Parcelable

@Parcelize
data class PaymentDataReceiptAmounts(
	@SerializedName("electronic") var electronic: Double,
	@SerializedName("advancePayment") var advancePayment: Double,
	@SerializedName("credit") var credit: Double,
	@SerializedName("provision") var provision: Double) : Parcelable
