//
//  AboutDescriptionTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/04.
//

import UIKit
import class ioscombined.LocaledString
import class ioscombined.LangKt
import SnapKit

enum LinkItem: CaseIterable {
    case twitter, github, youtube, medium

    var icon: UIImage? {
        switch self {
        case .twitter: return UIImage(named: "twitter")
        case .github: return UIImage(named: "github")
        case .youtube: return UIImage(named: "youtube")
        case .medium: return UIImage(named: "medium")
        }
    }

    var link: URL? {
        switch self {
        case .twitter: return URL(string: "https://twitter.com/droidkaigi")
        case .github: return URL(string: "https://github.com/DroidKaigi/conference-app-2019")
        case .youtube: return URL(string: "https://www.youtube.com/channel/UCgK6L-PKx2OZBuhrQ6mmQZw")
        case .medium: return URL(string: "https://medium.com/droidkaigi")
        }
    }
}

class AboutDescriptionTableViewCell: UITableViewCell, Reusable {

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private let desc = LocaledString(
            ja: """
                DroidKaigiはエンジニアが主役のカンファレンスです。
                技術情報の共有とコミュニケーションを目的に、2019年2月7日(木)、8日(金)の2日間開催します。 
                """,
            en: """
                DroidKaigi is a conference tailored for developers.
                It's scheduled to take place on the 7th and 8th of February 2019. 
                """
    )


    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.boldSystemFont(ofSize: 16)
        label.numberOfLines = 1
        label.text = "What is DroidKaigi?"
        return label
    }()
    private lazy var descriptionLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 14)
        label.numberOfLines = 0
        label.textColor = UIColor.black.withAlphaComponent(0.8)
        label.text = desc.getByLang(lang: LangKt.defaultLang())
        return label
    }()
    private lazy var linkStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .leading
        stackView.distribution = .equalSpacing
        stackView.spacing = 12
        return stackView
    }()

    private func setupSubviews() {
        LinkItem.allCases.forEach { link in
            let view = LinkItemView(link: link)
            view.delegate = self
            linkStackView.addArrangedSubview(view)
        }

        [titleLabel, descriptionLabel, linkStackView].forEach(contentView.addSubview)
        titleLabel.snp.makeConstraints {
            $0.top.equalToSuperview().offset(24)
            $0.leading.trailing.equalToSuperview().inset(16)
        }
        descriptionLabel.snp.makeConstraints {
            $0.top.equalTo(titleLabel.snp.bottom).offset(16)
            $0.leading.trailing.equalTo(titleLabel)
        }
        linkStackView.snp.makeConstraints {
            $0.top.equalTo(descriptionLabel.snp.bottom).offset(16)
            $0.leading.equalTo(titleLabel)
            $0.height.equalTo(44)
            $0.bottom.equalToSuperview().inset(20)
        }
    }
}

extension AboutDescriptionTableViewCell: LinkItemViewDelegate {

    func linkItemView(_ linkItemView: LinkItemView, didSelectItem linkItem: LinkItem) {
        guard let url = linkItem.link else { return }
        UIApplication.shared.open(url)
    }
}

protocol LinkItemViewDelegate: class {
    func linkItemView(_ linkItemView: LinkItemView, didSelectItem linkItem: LinkItem)
}

final class LinkItemView: UIView {

    weak var delegate: LinkItemViewDelegate?

    init(link: LinkItem) {
        self.link = link
        super.init(frame: .zero)
        iconImageView.image = link.icon
        addSubview(iconImageView)
        iconImageView.snp.makeConstraints {
            $0.edges.equalToSuperview().inset(8)
            $0.height.width.equalTo(28)
        }
        addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(touchUpInside)))
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private let link: LinkItem

    private lazy var iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()

    @objc private func touchUpInside() {
        delegate?.linkItemView(self, didSelectItem: link)
    }
}
