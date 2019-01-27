//
//  SessionDetailTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/25.
//

import UIKit
import SnapKit

class SessionDetailTableViewCell: UITableViewCell, Reusable {

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(label)
        label.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(16)
            $0.trailing.equalToSuperview().inset(20)
            $0.top.bottom.equalToSuperview().inset(7)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    lazy var label: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = .black
        label.numberOfLines = 0
        return label
    }()
}
