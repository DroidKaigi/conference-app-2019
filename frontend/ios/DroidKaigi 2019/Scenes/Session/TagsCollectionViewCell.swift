//
//  TagsCollectionViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit
import SnapKit
import main

enum TagContent {
    case category(category: main.Category)
    case lang(lang: Lang)
    case beginner
    case other(string: String)

    var textColor: UIColor {
        switch self {
        case .category, .other: return UIColor(hex: "353435")
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
        case .category: return UIColor(hex: "E5E8EA")
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
            return "初心者歓迎"
        case .other(let string):
            return string
        }
    }
}

class TagsCollectionViewCell: UICollectionViewCell, Reusable {

    var tagContent: TagContent? {
        didSet {
            guard let tagContent = tagContent else { return }
            label.textColor = tagContent.textColor
            label.backgroundColor = tagContent.backgroundColor
            label.text = tagContent.text
        }
    }

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func layoutSubviews() {
        super.layoutSubviews()
        label.layer.cornerRadius = label.frame.height / 2
    }

    lazy var label: PaddingLabel = {
        let label = PaddingLabel()
        label.padding = UIEdgeInsets(top: 6, left: 10, bottom: 6, right: 10)
        label.font = UIFont.systemFont(ofSize: 12)
        label.numberOfLines = 1
        label.textAlignment = .center
        label.lineBreakMode = .byTruncatingTail
        label.clipsToBounds = true
        return label
    }()

    private func setupSubviews() {
        addSubview(label)
        label.snp.makeConstraints {
            $0.edges.equalToSuperview()
            $0.width.lessThanOrEqualTo(230)
        }
    }
}
