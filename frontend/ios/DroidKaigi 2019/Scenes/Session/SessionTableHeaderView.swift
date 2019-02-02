//
//  SessionTableHeaderView.swift
//  DroidKaigi 2019
//
//  Created by woxtu on 2019/02/03.
//

import UIKit
import RxCocoa
import RxSwift
import SnapKit

class SessionTableHeaderView: UIView {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        commonInit()
    }
    
    private func commonInit() {
        setupSubviews()
    }
    
    private lazy var startDayLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.font = UIFont.systemFont(ofSize: 20)
        label.textColor = UIColor(hex: "95959c")
        label.text = "2019.2.7"
        return label
    }()
    
    private func setupSubviews() {
        addSubview(startDayLabel)
        startDayLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            $0.leading.equalToSuperview().inset(20)
        }
    }
}
