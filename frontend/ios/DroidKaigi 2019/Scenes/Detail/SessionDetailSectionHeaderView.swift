//
//  SessionDetailSectionHeaderView.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/27.
//

import UIKit
import SnapKit

class SessionDetailSectionHeaderView: UITableViewHeaderFooterView, Reusable {

    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        contentView.addSubview(titleLabel)
        titleLabel.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(16)
            $0.top.bottom.equalToSuperview().inset(10).priority(.low)
            $0.trailing.equalToSuperview().inset(20)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = .black
        label.font = .boldSystemFont(ofSize: 16)
        label.numberOfLines = 1
        return label
    }()
}
