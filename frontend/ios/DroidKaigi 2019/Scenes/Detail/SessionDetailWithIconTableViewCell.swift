//
//  SessionDetailWithIconTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/26.
//

import UIKit
import SnapKit
import Kingfisher
import ios_combined

enum Document {

    case video(url: String)
    case slide(url: String)

    var icon: UIImage? {
        switch self {
        case .video: return UIImage(named: "video")?.withRenderingMode(.alwaysTemplate)
        case .slide: return UIImage(named: "slide")?.withRenderingMode(.alwaysTemplate)
        }
    }

    var text: LocaledString {
        switch self {
        case .video: return LocaledString(ja: "動画", en: "video")
        case .slide: return LocaledString(ja: "スライド", en: "Slide")
        }
    }
}

class SessionDetailWithIconTableViewCell: UITableViewCell, Reusable {

    var cellType: CellType? {
        didSet {
            guard let cellType = cellType else { return }
            iconImageView.snp.remakeConstraints {
                $0.leading.equalToSuperview().inset(16)
                $0.top.bottom.equalToSuperview().inset(7)
                $0.width.height.equalTo(cellType.iconSize)
            }
            switch cellType {
            case .document(let document):
                iconImageView.image = document.icon
                iconImageView.backgroundColor = .clear
                label.text = document.text.getByLang(lang: LangKt.defaultLang())
            case .speaker(let speaker):
                iconImageView.kf.setImage(with: URL(string: speaker.imageUrl ?? ""))
                iconImageView.backgroundColor = .lightGray
                label.text = speaker.name
            }
        }
    }

    enum CellType {

        case document(_ document: Document)
        case speaker(_ speaker: Speaker)

        var iconSize: CGFloat {
            switch self {
            case .document: return 24
            case .speaker: return 64
            }
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        [iconImageView, label].forEach(contentView.addSubview)
        iconImageView.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(16)
            $0.top.bottom.equalToSuperview().inset(7)
            $0.height.width.equalTo(16)
        }
        label.snp.makeConstraints {
            $0.centerY.equalTo(iconImageView)
            $0.leading.equalTo(iconImageView.snp.trailing).offset(10)
            $0.trailing.equalToSuperview().inset(16)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func layoutIfNeeded() {
        super.layoutIfNeeded()
        guard let cellType = cellType else { return }
        switch cellType {
        case .speaker:
            iconImageView.layer.cornerRadius = iconImageView.frame.height / 2
        default:
            break
        }
    }

    private lazy var label: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = UIColor(hex: "e6650f")
        label.numberOfLines = 0
        return label
    }()
    private lazy var iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.tintColor = .gray
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()
}
