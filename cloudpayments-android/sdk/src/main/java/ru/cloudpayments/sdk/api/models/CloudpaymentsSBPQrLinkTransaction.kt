package ru.cloudpayments.sdk.api.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class CloudpaymentsSBPQrLinkTransaction(
	@SerializedName("QrUrl") val qrUrl: String?,
	@SerializedName("TransactionId") val transactionId: Long?,
	@SerializedName("MerchantOrderId") val merchantOrderId: String?,
	@SerializedName("ProviderQrId") val providerQrId: String?,
	@SerializedName("Banks") val banks: SBPBanks?,
	@SerializedName("Message") val message: String?
	)

data class SBPBanks(
	@SerializedName("version") val version: String?,
	@SerializedName("dictionary") val dictionary: ArrayList<SBPBanksItem>?
	)

@Parcelize
data class SBPBanksItem(
	@SerializedName("bankName") val bankName: String?,
	@SerializedName("logoURL") val logoURL: String?,
	@SerializedName("schema") val schema: String?,
	@SerializedName("package_name") val packageName: String?,
	@SerializedName("webClientUrl") val webClientUrl: String?,
	@SerializedName("isWebClientActive")  val isWebClientActive: Boolean?
) : Parcelable

