//
//  MainViewController.swift
//  DroidKaigi 2019
//
//  Created by koogawa on 2019/01/28.
//

import RxSwift
import UIKit
import XLPagerTabStrip

class MainViewController: ButtonBarPagerTabStripViewController, StoryboardInstantiable {

    private let bag = DisposeBag()
    
    override func viewDidLoad() {
        settings.style.buttonBarBackgroundColor = UIColor.DK.primary.color
        settings.style.buttonBarItemBackgroundColor = UIColor.DK.primary.color
        settings.style.selectedBarBackgroundColor = UIColor.DK.primaryDark.color
        super.viewDidLoad()
        
        navigationItem.rightBarButtonItems = [
            UIBarButtonItem(image: #imageLiteral(resourceName: "baseline_location_on_white_18dp"), style: .plain, target: self, action: #selector(locationButtonTapped(_:)))
            ]
    }

    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        let day1ViewController = SessionsViewController.instantiateFromStoryboard()
        let day2ViewController = SessionsViewController.instantiateFromStoryboard()
        day1ViewController.day = Day(title: "Day1", day: 1)
        day2ViewController.day = Day(title: "Day2", day: 2)
        let childViewControllers = [day1ViewController, day2ViewController]
        return childViewControllers
    }
}

private extension MainViewController {
    @objc func locationButtonTapped(_ sender: Any?) {
        guard let floorMap = R.storyboard.floorMap.instantiateInitialViewController() else {
            assertionFailure("FloorMap.storyboard instantiate error.")
            return
        }
        show(floorMap, sender: nil)
    }
}
