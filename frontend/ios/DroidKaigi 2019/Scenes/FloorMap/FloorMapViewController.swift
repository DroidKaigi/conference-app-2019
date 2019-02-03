//
//  FloorMapViewController.swift
//  DroidKaigi 2019
//
//  Created by 417.72KI on 2019/01/31.
//

import UIKit
import XLPagerTabStrip

class FloorMapViewController: ButtonBarPagerTabStripViewController {

    override func viewDidLoad() {
        settings.style.buttonBarBackgroundColor = UIColor.DK.primary.color
        settings.style.buttonBarItemBackgroundColor = UIColor.DK.primary.color
        settings.style.selectedBarBackgroundColor = UIColor.DK.primaryDark.color
        settings.style.buttonBarItemFont = UIFont.systemFont(ofSize: 16)
        settings.style.selectedBarHeight = 4
        settings.style.buttonBarItemsShouldFillAvailableWidth = true
        super.viewDidLoad()
        view.setNeedsLayout()
        view.layoutIfNeeded()
//        https://github.com/xmartlabs/XLPagerTabStrip/issues/256
    }

    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        return Floor.allCases.compactMap {
            let content = storyboard?.instantiateViewController(withIdentifier: "content") as? FloorMapContentViewController
            content?.floor = $0
            return content
        }
    }
}

extension FloorMapViewController: StoryboardInstantiable {
    static let storyboardName = "FloorMap"
}
