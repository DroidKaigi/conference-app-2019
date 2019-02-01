//
//  ChipsLeftAlignedCollectionViewFlowLayout.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit

final class TagsLeftAlignedCollectionViewFlowLayout: UICollectionViewFlowLayout {

    override func layoutAttributesForElements(in rect: CGRect) -> [UICollectionViewLayoutAttributes]? {
        let attributes = super.layoutAttributesForElements(in: rect)
        var leftMargin = sectionInset.left
        var maxY: CGFloat = -1.0
        attributes?.forEach { layoutAttribute in
            if layoutAttribute.frame.origin.y >= maxY {
                leftMargin = sectionInset.left
            }
            layoutAttribute.frame.origin.x = leftMargin
            leftMargin += layoutAttribute.frame.width + minimumInteritemSpacing
            maxY = max(layoutAttribute.frame.maxY, maxY)
    
            if let collectionView = collectionView {
                let bounds = collectionView.bounds
                let contentInset = collectionView.contentInset
                let maxWidth = bounds.width - contentInset.left - contentInset.right
                layoutAttribute.frame.size.width = min(maxWidth, layoutAttribute.bounds.width)
            }
        }
        return attributes
    }
}
