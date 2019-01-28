//
//  SessionDetailTagsTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/25.
//

import UIKit
import SnapKit

class SessionDetailTagsTableViewCell: UITableViewCell, Reusable {

    var tagContents: [TagContent] = [] {
        didSet {
            collectionView.reloadData()
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        contentView.addSubview(collectionView)
        collectionView.snp.makeConstraints {
            $0.leading.trailing.equalToSuperview().inset(16)
            $0.top.bottom.equalToSuperview().inset(14).priority(.low)
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func prepareForReuse() {
        super.prepareForReuse()
        tagContents.removeAll()
        collectionView.reloadData() // write to fix bug
    }

    override func systemLayoutSizeFitting(_ targetSize: CGSize,
                                          withHorizontalFittingPriority horizontalFittingPriority: UILayoutPriority,
                                          verticalFittingPriority: UILayoutPriority) -> CGSize {
        let defaultSize = super.systemLayoutSizeFitting(
                targetSize,
                withHorizontalFittingPriority: horizontalFittingPriority,
                verticalFittingPriority: verticalFittingPriority
        )
        let hash = tagContents.hashValue
        if let cachedHeight = cachedHeights[hash] {
            return CGSize.init(width: targetSize.width, height: cachedHeight)
        }
        struct Static {
            static let cell = SessionDetailTagsTableViewCell(style: .default, reuseIdentifier: nil)
        }
        let cell = Static.cell
        cell.tagContents = tagContents
        cell.bounds.size = CGSize(width: targetSize.width, height: 0)
        cell.setNeedsLayout()
        cell.layoutIfNeeded()
        cell.collectionView.reloadData()
        cell.collectionView.setNeedsLayout()
        cell.collectionView.layoutIfNeeded()
        cell.collectionView.collectionViewLayout.invalidateLayout()
        let collectionViewHeight = cell.collectionView.collectionViewLayout.collectionViewContentSize.height
        cachedHeights[hash] = collectionViewHeight + defaultSize.height
        return CGSize.init(width: targetSize.width, height: collectionViewHeight + defaultSize.height)
    }

    private var cachedHeights: [Int: CGFloat] = [:]

    private lazy var collectionView: UICollectionView = {
        let layout = TagsLeftAlignedCollectionViewFlowLayout()
        layout.minimumInteritemSpacing = 6
        layout.minimumLineSpacing = 9
        layout.estimatedItemSize = .init(width: 100, height: 30)
        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.isScrollEnabled = false
        collectionView.backgroundColor = .clear
        collectionView.register(TagsCollectionViewCell.self)
        return collectionView
    }()
}

extension SessionDetailTagsTableViewCell: UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return tagContents.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell: TagsCollectionViewCell = collectionView.dequeueReusableCell(forIndexPath: indexPath)
        cell.tagContent = tagContents[indexPath.item]
        cell.label.font = .systemFont(ofSize: 14)
        return cell
    }
}

extension SessionDetailTagsTableViewCell: UICollectionViewDelegateFlowLayout {

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        struct Static {
            static let cell = TagsCollectionViewCell()
        }
        let cell = Static.cell
        cell.tagContent = tagContents[indexPath.item]
        cell.label.font = .systemFont(ofSize: 14)
        let cellSize = cell.label.intrinsicContentSize
        let width = cellSize.width > collectionView.bounds.size.width - 20 ? collectionView.bounds.size.width - 20 : cellSize.width
        return CGSize(width: width, height: cellSize.height)
    }
}
