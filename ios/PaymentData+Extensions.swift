import Foundation
import Cloudpayments

extension PaymentData {

    private func asDecimal(_ v: Any?) -> Decimal? {
        switch v {
        case let d as Decimal:
            return d
        case let n as NSDecimalNumber:
            // уже Decimal-представление — самый надёжный путь
            return n.decimalValue
        case let n as NSNumber:
            // избегаем Double → String → Decimal; берём через NSDecimalNumber
            return NSDecimalNumber(decimal: n.decimalValue).decimalValue
        case let s as String:
            // поддержим обе разделительные: "," и "."
            let normalized = s.replacingOccurrences(of: ",", with: ".")
            return Decimal(string: normalized, locale: Locale(identifier: "en_US_POSIX"))
        case let d as Double:
            // если всё же пришёл Double — аккуратно завернём
            return Decimal(string: String(d), locale: Locale(identifier: "en_US_POSIX"))
        case let f as Float:
            return Decimal(string: String(f), locale: Locale(identifier: "en_US_POSIX"))
        default:
            return nil
        }
    }

    private func asDouble(_ v: Any?) -> Double? {
        if let d = v as? Double { return d }
        if let n = v as? NSNumber { return n.doubleValue }
        if let s = v as? String { return Double(s.replacingOccurrences(of: ",", with: ".")) }
        return nil
    }

    private func asInt(_ v: Any?) -> Int? {
        if let i = v as? Int { return i }
        if let n = v as? NSNumber { return n.intValue }
        if let s = v as? String { return Int(s) }
        return nil
    }

    private func asBool(_ v: Any?) -> Bool {
        if let b = v as? Bool { return b }
        if let n = v as? NSNumber { return n.boolValue }
        if let s = v as? String { return ["1","true","yes"].contains(s.lowercased()) }
        return false
    }
    private func decToDouble(_ d: Decimal?) -> Double? {
        guard let d else { return nil }
        return NSDecimalNumber(decimal: d).doubleValue
    }

    private func asString(_ v: Any?) -> String { (v as? String) ?? "" }

    convenience init?(from input: [String: Any]) {
        guard
            let amount = input["amount"] as? String,
            let currency = input["currency"] as? String
        else {

            return nil
        }




        self.init()

        _ = setAmount(amount)
        _ = setCurrency(currency)

        if let payerDict = input["payer"] as? [String: Any] {
            let payer = PaymentDataPayer(
                firstName: payerDict["firstName"] as? String ?? "",
                lastName: payerDict["lastName"] as? String ?? "",
                middleName: payerDict["middleName"] as? String ?? "",
                birth: payerDict["birth"] as? String ?? "",
                address: payerDict["address"] as? String ?? "",
                street: payerDict["street"] as? String ?? "",
                city: payerDict["city"] as? String ?? "",
                country: payerDict["country"] as? String ?? "",
                phone: payerDict["phone"] as? String ?? "",
                postcode: payerDict["postcode"] as? String ?? ""
            )
            _ = setPayer(payer)
        }


        if let receiptDict = input["receipt"] as? [String: Any] {

            // items
            let itemsArray = (receiptDict["items"] as? [[String: Any]]) ?? []
            let items: [Receipt.Item] = itemsArray.compactMap { itemDict -> Receipt.Item? in
                guard
                    let label    = itemDict["label"] as? String,
                    let priceDecimal = asDecimal(itemDict["price"]),
                    let quantity = asDouble(itemDict["quantity"]),
                    let amountDecimal = asDecimal(itemDict["amount"])
                else {
                    // print("Skip item: \(itemDict)")
                    return nil
                }

                // Конвертируем Decimal в Double для SDK 2.1.0
                let price = NSDecimalNumber(decimal: priceDecimal).doubleValue
                let amount = NSDecimalNumber(decimal: amountDecimal).doubleValue

                // vat может быть null/отсутствовать → ставим 0 (или сделай поле опциональным в модели)
                let vat    = asInt(itemDict["vat"]) ?? 0
                let method = asInt(itemDict["method"]) ?? 0
                let object = asInt(itemDict["object"]) ?? 0

                return Receipt.Item(
                    label: label,
                    price: price,
                    quantity: quantity,
                    amount: amount,
                    vat: vat,
                    method: method,
                    object: object
                )
            }

            // amounts
            let amountsAny = receiptDict["amounts"] as? [String: Any]
            let amounts = Receipt.Amounts(
                electronic:     asDouble(amountsAny?["electronic"])     ?? 0,
                advancePayment: asDouble(amountsAny?["advancePayment"]) ?? 0,
                credit:         asDouble(amountsAny?["credit"])         ?? 0,
                provision:      asDouble(amountsAny?["provision"])      ?? 0
            )

            print("receiptDict items \(items.count)")

            // итоговый receipt
            let receipt = Receipt(
                items: items,
                taxationSystem: asInt(receiptDict["taxationSystem"]) ?? 0,
                email: asString(receiptDict["email"]),
                phone: asString(receiptDict["phone"]),
                isBso: asBool(receiptDict["isBso"]),
                amounts: amounts
            )

            _ = setReceipt(receipt)
        }

        if let recurrentDict = input["recurrent"] as? [String: Any] {
            let interval = recurrentDict["interval"] as? String ?? "Month"
            let period   = recurrentDict["period"] as? Int ?? 1

            func parseAmount(_ any: Any?) -> Decimal? {
                switch any {
                case let n as NSNumber:
                    return Decimal(string: n.stringValue)
                case let d as Double:
                    return Decimal(d)
                case let s as String:
                    // заменим запятую на точку на всякий случай
                    return Decimal(string: s.replacingOccurrences(of: ",", with: "."))
                default:
                    return nil
                }
            }

            var amount: Decimal?

            // 1) recurrent.amount (если вдруг есть)
            amount = parseAmount(recurrentDict["amount"])

            // 2) customerReceipt.amounts.electronic
            if amount == nil,
               let receipt = recurrentDict["customerReceipt"] as? [String: Any],
               let amounts = receipt["amounts"] as? [String: Any] {
                amount = parseAmount(amounts["electronic"])
            }

            // 3) сумма по items (amount или price)
            if amount == nil,
               let receipt = recurrentDict["customerReceipt"] as? [String: Any],
               let items = receipt["items"] as? [[String: Any]] {
                amount = items.reduce(Decimal(0)) { acc, it in
                    let a = parseAmount(it["amount"]) ?? parseAmount(it["price"]) ?? 0
                    return acc + a
                }
            }

            guard let decimalAmount = amount else {
                print("❌ Не удалось распарсить сумму из recurrent")
                return
            }

            // В SDK 2.1.0 Recurrent.amount имеет тип Double?, а не Decimal
            let amountDouble = NSDecimalNumber(decimal: decimalAmount).doubleValue

            let recurrent = Recurrent(
                interval: interval,
                period: period,
                customerReceipt: nil,
                amount: amountDouble
            )

            _ = setRecurrent(recurrent)
        }


        if let jsonData = input["jsonData"] {
           do {
               let data = try JSONSerialization.data(withJSONObject: jsonData, options: [])
                let jsonString = String(data: data, encoding: .utf8)
                _ = setJsonData(jsonString ?? "")
               } catch {
                        print("❌ Ошибка сериализации jsonData: \(error.localizedDescription)")
                        }
               }
        // setCultureName не существует в SDK 2.1.0 - удалено
        _ = setDescription(input["description"] as? String)
        _ = setEmail(input["email"] as? String)
        _ = setAccountId(input["accountId"] as? String)
    }
}
