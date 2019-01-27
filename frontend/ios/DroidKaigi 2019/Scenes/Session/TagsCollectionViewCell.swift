//
//  TagsCollectionViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit
import SnapKit
import ios_combined

class TagsCollectionViewCell: UICollectionViewCell, Reusable {
    
    var tagContent: TagContent? {
        didSet {
            guard let tagContent = tagContent else { return }
            label.textColor = tagContent.textColor
            label.backgroundColor = tagContent.backgroundColor
            label.text = tagContent.text
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func layoutSubviews() {
        super.layoutSubviews()
        label.layer.cornerRadius = label.frame.height / 2
    }

    lazy var label: PaddingLabel = {
        let label = PaddingLabel()
        label.padding = UIEdgeInsets(top: 6, left: 10, bottom: 6, right: 10)
        label.font = UIFont.systemFont(ofSize: 12)
        label.numberOfLines = 1
        label.textAlignment = .left
        label.lineBreakMode = .byTruncatingTail
        label.clipsToBounds = true
        return label
    }()

    private func setupSubviews() {
        addSubview(label)
        label.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
    }
}
