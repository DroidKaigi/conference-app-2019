//
//  TagContent.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/27.
//

import Foundation
import UIKit
import ioscombined

enum TagContent: Hashable {
    case category(category: ioscombined.Category)
    case lang(lang: Lang)
    case beginner
    case interpretation
    case other(text: LocaledString)

    var textColor: UIColor {
        switch self {
        case .category, .other, .interpretation: return UIColor(hex: "353435")
        case .lang(let lang):
            if lang == Lang.ja {
                return UIColor(hex: "F63529")
            } else {
                return UIColor(hex: "190089")
            }
        case .beginner: return UIColor(hex: "00BE80")
        }
    }

    var backgroundColor: UIColor {
        switch self {
        case .category, .interpretation: return UIColor(hex: "E5E8EA")
        case .lang(let lang):
            if lang == Lang.ja {
                return UIColor(hex: "FDD7D4")
            } else {
                return UIColor(hex: "E8E6F3")
            }
        case .beginner: return UIColor(hex: "C1EBDD")
        case .other: return UIColor(hex: "D8D7DC")
        }
    }

    var text: String {
        switch self {
        case .category(let category):
            return category.name.getByLang(lang: LangKt.defaultLang())
        case .lang(let lang):
            return lang.text.getByLang(lang: LangKt.defaultLang())
        case .beginner:
            let text = LocaledString(ja: "初心者歓迎", en: "For Beginner")
            return text.getByLang(lang: LangKt.defaultLang())
        case .interpretation:
            let text = LocaledString(ja: "同時通訳", en: "Simultaneous Interpretation")
            return text.getByLang(lang: LangKt.defaultLang())
        case .other(let text):
            return text.getByLang(lang: LangKt.defaultLang())
        }
    }
}
