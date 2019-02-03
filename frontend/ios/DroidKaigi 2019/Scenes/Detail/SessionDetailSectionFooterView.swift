//
//  SessionDetailSectionFooterView.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/27.
//

import UIKit
import SnapKit

class SessionDetailSectionFooterView: UITableViewHeaderFooterView, Reusable {

    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        borderView.backgroundColor = .white
        contentView.addSubview(borderView)
        borderView.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(16)
            $0.trailing.equalToSuperview().inset(20)
            $0.top.equalToSuperview().inset(18).priority(.low)
            $0.bottom.equalToSuperview().inset(10).priority(.low)
            $0.height.equalTo(1)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var borderView = DashedLineView()
}

final class DashedLineView: UIView {

    init() {
        super.init(frame: .zero)
        backgroundColor = .clear
    }
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func draw(_ rect: CGRect) {
        let  path = UIBezierPath()
        let  start = CGPoint(x: rect.minX, y: rect.midY)
        let  end = CGPoint(x: rect.maxX, y: rect.midY)
        path.move(to: start)
        path.addLine(to: end)
        let  dashes: [CGFloat] = [3.0, 3.0]
        path.setLineDash(dashes, count: dashes.count, phase: 0.0)
        path.lineWidth = 1
        UIColor.lightGray.setStroke()
        path.stroke()
        super.draw(rect)
    }
}
