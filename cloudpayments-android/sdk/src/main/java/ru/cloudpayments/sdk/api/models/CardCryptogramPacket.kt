package ru.cloudpayments.sdk.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardCryptogramPacket(
	@SerializedName("Type") var type: String? = "CloudCard",
	@SerializedName("BrowserInfoBase64") var browserInfoBase64: String? = "",
	@SerializedName("CardInfo") var cardInfo: CardInfo?,
	@SerializedName("KeyVersion") var keyVersion: String?,
	@SerializedName("Format") var format: Int? = 1,
	@SerializedName("Value") var value: String?) : Parcelable

@Parcelize
data class CardInfo(
	@SerializedName("FirstSixDigits") var firstSixDigits: String?,
	@SerializedName("LastFourDigits") var lastFourDigits: String?,
	@SerializedName("ExpDateYear") var expDateYear: String?,
	@SerializedName("ExpDateMonth") var expDateMonth: String?) :Parcelable

@Parcelize
data class MirPayCardCryptogramPacket(
	@SerializedName("Type") var type: String? = "CloudMirPay",
	@SerializedName("BrowserInfoBase64") var browserInfoBase64: String? = "",
	@SerializedName("CardInfo") var cardInfo: CardInfo? = null,
	@SerializedName("KeyVersion") var keyVersion: String? = null,
	@SerializedName("Format") var format: Int? = null,
	@SerializedName("Value") var value: String?) : Parcelable

