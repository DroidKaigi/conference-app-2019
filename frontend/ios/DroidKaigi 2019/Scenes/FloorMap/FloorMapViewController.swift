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
        super.viewDidLoad()
    }
    
    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        let floor1 = storyboard?.instantiateViewController(withIdentifier: "content") as? FloorMapContentViewController
        let floor5 = storyboard?.instantiateViewController(withIdentifier: "content") as? FloorMapContentViewController
        floor1?.floor = Floor(title: "1st floor", floor: 1, map: #imageLiteral(resourceName: "ic_floor1"))
        floor5?.floor = Floor(title: "5th floor", floor: 5, map: #imageLiteral(resourceName: "ic_floor2"))
        return [floor1, floor5].compactMap { $0 }
    }
}

extension FloorMapViewController: StoryboardInstantiable {
    static let storyboardName = "FloorMap"
}
