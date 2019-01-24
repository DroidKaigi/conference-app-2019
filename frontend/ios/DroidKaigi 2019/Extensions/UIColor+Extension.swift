//
//  UIColor+Extension.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/21.
//

import UIKit

// swiftlint:disable identifier_name

extension UIColor {

    enum DK: String {
        case primary = "Primary"
        case primaryDark = "PrimaryDark"
        var color: UIColor {
            if #available(iOS 11.0, *) {
                guard let color = UIColor(named: self.rawValue) else {
                    fatalError("Not found color")
                }
                return color
            } else {
                switch self {
                case .primary: return #colorLiteral(red: 0.964705882, green: 0.207843137, blue: 0.160784314, alpha: 1)
                case .primaryDark: return #colorLiteral(red: 0.658823529, green: 0.121568627, blue: 0.125490196, alpha: 1)
                }
            }
        }
    }

    convenience init(hex: String, alpha: CGFloat) {
        let v = hex.map { String($0) } + Array(repeating: "0", count: max(6 - hex.count, 0))
        let r = CGFloat(Int(v[0] + v[1], radix: 16) ?? 0) / 255.0
        let g = CGFloat(Int(v[2] + v[3], radix: 16) ?? 0) / 255.0
        let b = CGFloat(Int(v[4] + v[5], radix: 16) ?? 0) / 255.0
        self.init(red: r, green: g, blue: b, alpha: alpha)
    }

    convenience init(hex: String) {
        self.init(hex: hex, alpha: 1.0)
    }
}

// swiftlint:enable identifier_name
