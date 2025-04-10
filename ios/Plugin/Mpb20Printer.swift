import Foundation
import Capacitor

@objc(Mpb20PrinterPlugin)
public class Mpb20PrinterPlugin: CAPPlugin {
    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": value
        ])
    }
}
