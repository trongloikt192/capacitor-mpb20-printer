import Foundation
import Capacitor

@objc(MkPrinterPlugin)
public class MkPrinterPlugin: CAPPlugin {
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": value
        ])
    }
}
