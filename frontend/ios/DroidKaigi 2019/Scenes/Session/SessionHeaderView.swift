//
//  SessionHeaderView.swift
//  DroidKaigi 2019
//
//  Created by Takumi KASHIMA on 2019/01/25.
//

import UIKit
import ioscombined
import SnapKit

class SessionHeaderView: UIView {
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
    
    func set(startTimeText: String) {
        startTimeLabel.text = startTimeText
    }
    
    private lazy var startTimeLabel: UILabel = {
        let label = UILabel()
        label.numberOfLines = 1
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = .black
        return label
    }()
    
    private func setupSubviews() {
        addSubview(startTimeLabel)
        startTimeLabel.snp.makeConstraints {
            $0.centerY.equalToSuperview()
            if #available(iOS 11.0, *) {
                $0.leading.equalTo(safeAreaLayoutGuide.snp.leading).inset(20)
            } else {
                $0.leading.equalToSuperview().inset(20)
            }
        }
    }
}
