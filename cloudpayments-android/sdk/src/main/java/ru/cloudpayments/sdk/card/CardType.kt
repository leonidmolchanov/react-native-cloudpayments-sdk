package ru.cloudpayments.sdk.card

import ru.cloudpayments.sdk.R
import java.util.*

enum class CardType {
	AMERICANEXPRESS,
	MASTERCARD,
	MIR,
	MAESTRO,
	VISA,
	UATP,
	DISCOVER,
	DINERSCLUB,
	DANKORT,
	INSTAPAYMENT,
	JCB,
	UNIONPAY,
	HUMO,
	UZCARD,
	UNKNOWN;

	companion object {

		const val KEY_AMERICANEXPRESS = "american-express"
		const val KEY_MASTERCARD = "mastercard"
		const val KEY_MIR = "mir"
		const val KEY_MAESTRO = "maestro"
		const val KEY_VISA = "visa"
		const val KEY_UATP = "uatp"
		const val KEY_DISCOVER = "discover"
		const val KEY_DINERSCLUB = "diners"
		const val KEY_DANKORT = "dankort"
		const val KEY_INSTAPAYMENT = "instapayment"
		const val KEY_JCB15 = "jcb15"
		const val KEY_JCB = "jcb"
		const val KEY_UNIONPAY = "unionPay"
		const val KEY_HUMO = "humo"
		const val KEY_UZCARD = "uzcard"
		const val KEY_UNKNOWN = "unknown"

		fun fromString(value: String): CardType = when(value.lowercase(Locale.getDefault())){
			KEY_AMERICANEXPRESS -> AMERICANEXPRESS
			KEY_MASTERCARD -> MASTERCARD
			KEY_MIR -> MIR
			KEY_MAESTRO -> MAESTRO
			KEY_VISA -> VISA
			KEY_UATP -> UATP
			KEY_DISCOVER -> DISCOVER
			KEY_DINERSCLUB -> DINERSCLUB
			KEY_DANKORT -> DANKORT
			KEY_INSTAPAYMENT -> INSTAPAYMENT
			KEY_JCB15 -> JCB
			KEY_JCB -> JCB
			KEY_UNIONPAY -> UNIONPAY
			KEY_HUMO -> HUMO
			KEY_UZCARD -> UZCARD
			else -> UNKNOWN
		}

		fun getType(cardNumber: String): CardType {

			// начинается с 34/37; 15 символов
			val amExRegex = "^3[47]\\d{0,13}$".toRegex()

			// начинается с 8600; 16 символов
			//val uzCardRegex = "^(8600)\\d{0,12}$".toRegex() // Добавить 5614!!!
			val uzCardRegex = "(^(8600)\\d{0,12})|(^(5614)\\d{0,12})$".toRegex()

			// начинается с 9860/55553660; 16 символов
			val humoRegex = "(^(9860)\\d{0,12})|(^(55553660)\\d{0,8})$".toRegex()

			// начинается с 51-55/2221–2720; 16 символов
			val mastercardRegex = "^(5[1-5]\\d{0,2}|22[2-9]\\d{0,1}|2[3-7]\\d{0,2})\\d{0,12}$".toRegex()

			// начинается с 5019/4175/4571; 16 символов
			val dankortRegex = "^(5019|4175|4571)\\d{0,12}$".toRegex()

			// начинается с 50/56-58/6304/67; 16 символов
			val maestroRegex = "^(?:5[0678]\\d{0,2}|6304|67\\d{0,2})\\d{0,12}$".toRegex()

			// начинается с 22; 16 символов
			val mirRegex = "^220[0-4]\\d{0,12}$".toRegex()

			// начинается с 4; 16 символов
			val visaRegex = "^4\\d{0,15}$".toRegex()

			// начинается с 1; 15 символов, не начинается с 1800 (jcb card)
			val uatpRegex = "^(?!1800)1\\d{0,14}\$".toRegex()

			// начинается с 6011/65/644-649; 16 символов
			val discoverRegex = "^(?:6011|65\\d{0,2}|64[4-9]\\d?)\\d{0,12}$".toRegex()

			// начинается с 300-305/309 or 36/38/39; 14 символов
			val dinersClubRegex = "^3(?:0([0-5]|9)|[689]\\d?)\\d{0,11}$".toRegex()

			// начинается с 637-639; 16 символов
			val instapaymentRegex = "^63[7-9]\\d{0,13}$".toRegex()

			// начинается с 2131/1800; 15 символов
			val jcb15Regex = "^(?:2131|1800)\\d{0,11}$".toRegex()

			// начинается с 2131/1800/35; 16 символов
			val jcbRegex = "^(?:35\\d{0,2})\\d{0,12}$".toRegex()

			// начинается с 62/81; 16 символов
			val unionPayRegex = "^(62|81)\\d{0,14}$".toRegex()

			return when {
				cardNumber.matches(amExRegex) -> AMERICANEXPRESS
				cardNumber.matches(uzCardRegex) -> UZCARD
				cardNumber.matches(humoRegex) -> HUMO
				cardNumber.matches(mastercardRegex) -> MASTERCARD
				cardNumber.matches(dankortRegex) -> DANKORT
				cardNumber.matches(maestroRegex) -> MAESTRO
				cardNumber.matches(mirRegex) -> MIR
				cardNumber.matches(visaRegex) -> VISA
				cardNumber.matches(uatpRegex) -> UATP
				cardNumber.matches(discoverRegex) -> DISCOVER
				cardNumber.matches(dinersClubRegex) -> DINERSCLUB
				cardNumber.matches(instapaymentRegex) -> INSTAPAYMENT
				cardNumber.matches(jcb15Regex) -> JCB
				cardNumber.matches(jcbRegex) -> JCB
				cardNumber.matches(unionPayRegex) -> UNIONPAY
				else -> UNKNOWN
			}
		}
	}

	override fun toString(): String {
		return when (this) {
			AMERICANEXPRESS -> KEY_AMERICANEXPRESS
			MASTERCARD -> KEY_MASTERCARD
			MIR -> KEY_MIR
			MAESTRO -> KEY_MAESTRO
			VISA -> KEY_VISA
			UATP -> KEY_UATP
			DISCOVER -> KEY_DISCOVER
			DINERSCLUB -> KEY_DINERSCLUB
			DANKORT -> KEY_DANKORT
			INSTAPAYMENT -> KEY_INSTAPAYMENT
			JCB -> KEY_JCB
			UNIONPAY -> KEY_UNIONPAY
			HUMO -> KEY_HUMO
			UZCARD -> KEY_UZCARD
			else -> KEY_UNKNOWN
		}
	}

	fun getIconRes(): Int? = when (this) {
		AMERICANEXPRESS -> R.drawable.cpsdk_ic_ps_amex
		MASTERCARD -> R.drawable.cpsdk_ic_ps_mastercard
		MIR -> R.drawable.cpsdk_ic_ps_mir
		MAESTRO -> R.drawable.cpsdk_ic_ps_maestro
		VISA -> R.drawable.cpsdk_ic_ps_visa
		UATP -> R.drawable.cpsdk_ic_ps_uatp
		DISCOVER -> R.drawable.cpsdk_ic_ps_discover
		DINERSCLUB -> R.drawable.cpsdk_ic_ps_dinersclub
		DANKORT -> R.drawable.cpsdk_ic_ps_dankort
		INSTAPAYMENT -> R.drawable.cpsdk_ic_ps_instapayment
		JCB -> R.drawable.cpsdk_ic_ps_jcb
		UNIONPAY -> R.drawable.cpsdk_ic_ps_unionpay
		HUMO -> R.drawable.cpsdk_ic_ps_humo
		UZCARD -> R.drawable.cpsdk_ic_ps_uzcard
		else -> null
	}
}