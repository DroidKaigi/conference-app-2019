//
//  AboutTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/04.
//

import UIKit
import SnapKit
import MaterialComponents.MDCInkTouchController

class AboutTableViewCell: UITableViewCell, Reusable {

    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.font = UIFont.boldSystemFont(ofSize: 16)
        label.textColor = .black
        return label
    }()
    lazy var subLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = .orange
        return label
    }()

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        setupSubviews()
        inkTouchController.addInkView()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var inkTouchController: MDCInkTouchController = {
        return MDCInkTouchController(view: self)
    }()

    private func setupSubviews() {
        [titleLabel, subLabel].forEach(contentView.addSubview)
        titleLabel.snp.makeConstraints {
            $0.top.bottom.equalToSuperview().inset(18)
            $0.leading.equalToSuperview().inset(16)
            $0.trailing.equalTo(subLabel.snp.leading).inset(16)
        }
        subLabel.snp.makeConstraints {
            $0.centerY.equalTo(titleLabel)
            $0.trailing.equalToSuperview().inset(16)
        }

    }
}
