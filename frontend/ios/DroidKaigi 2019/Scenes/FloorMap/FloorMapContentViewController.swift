//
//  FloorMapContentViewController.swift
//  DroidKaigi 2019
//
//  Created by 417.72KI on 2019/01/31.
//

import UIKit
import XLPagerTabStrip

class FloorMapContentViewController: UIViewController {

    var floor: Floor!

    // MARK: Outlets
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var imageView: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        imageView.image = floor.map
    }
}

// MARK: - UIScrollViewDelegate
extension FloorMapContentViewController: UIScrollViewDelegate {
    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return imageView
    }
}

// MARK: - IndicatorInfoProvider
extension FloorMapContentViewController: IndicatorInfoProvider {
    func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: floor?.title)
    }
}
