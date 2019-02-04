//
//  AboutCoverTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/04.
//

import UIKit
import SnapKit

class AboutCoverTableViewCell: UITableViewCell, Reusable {

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var coverImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "cover")
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()
    private lazy var logoImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "about_logo")
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()

    private func setupSubviews() {
        [coverImageView, logoImageView].forEach(contentView.addSubview)
        coverImageView.snp.makeConstraints {
            $0.edges.equalToSuperview()
            $0.height.equalTo(coverImageView.snp.width).multipliedBy(5.0/9.0)
        }
        logoImageView.snp.makeConstraints {
            $0.center.equalToSuperview()
            $0.leading.trailing.equalToSuperview().inset(36)
        }
    }
}
