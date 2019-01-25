//
//  SpeakerCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit
import main
import SnapKit
import Kingfisher

class SpeakerCell: UIView {

    init(speaker: Speaker) {
        self.speaker = speaker
        super.init(frame: .zero)
        setupSubviews()
        nameLabel.text = speaker.name
        if let url = URL(string: speaker.imageUrl ?? "") {
            imageView.kf.setImage(with: url)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func layoutSubviews() {
        super.layoutSubviews()
        layoutIfNeeded()
        imageView.layer.cornerRadius = imageView.frame.height / 2
    }

    private let speaker: Speaker

    private lazy var imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        imageView.backgroundColor = .lightGray
        return imageView
    }()
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.font = UIFont.systemFont(ofSize: 12)
        label.textColor = .gray
        return label
    }()

    private func setupSubviews() {
        [imageView, nameLabel].forEach(addSubview)
        imageView.snp.makeConstraints {
            $0.top.bottom.leading.equalToSuperview()
            $0.height.width.equalTo(16)
        }
        nameLabel.snp.makeConstraints {
            $0.centerY.equalTo(imageView)
            $0.leading.equalTo(imageView.snp.trailing).offset(4)
            $0.trailing.equalToSuperview().inset(4)
        }
    }
}
