//
//  MainViewController.swift
//  DroidKaigi 2019
//
//  Created by koogawa on 2019/01/28.
//

import UIKit
import XLPagerTabStrip

class MainViewController: ButtonBarPagerTabStripViewController, StoryboardInstantiable {

    override func viewDidLoad() {
        setupTabAppearance()
        super.viewDidLoad()
        navigationItem.rightBarButtonItems = [
            UIBarButtonItem(image: #imageLiteral(resourceName: "baseline_location_on_white_18dp"), style: .plain, target: self, action: #selector(locationButtonTapped(_:)))
        ]
    }

    override func viewControllers(for pagerTabStripController: PagerTabStripViewController) -> [UIViewController] {
        let day1VC = SessionsViewController(viewModel: SessionsViewModel(type: .day1))
        let day2VC = SessionsViewController(viewModel: SessionsViewModel(type: .day2))
        let myPlanVC = SessionsViewController(viewModel: SessionsViewModel(type: .myPlan))
        let childViewControllers = [day1VC, day2VC, myPlanVC]
        return childViewControllers
    }

    private func setupTabAppearance() {
        settings.style.buttonBarBackgroundColor = UIColor.DK.primary.color
        settings.style.buttonBarItemBackgroundColor = UIColor.DK.primary.color
        settings.style.selectedBarBackgroundColor = UIColor.DK.primaryDark.color
        settings.style.buttonBarItemFont = UIFont.systemFont(ofSize: 12)
        settings.style.selectedBarHeight = 4
    }
}

private extension MainViewController {
    @objc func locationButtonTapped(_ sender: Any?) {
        let floorMap = FloorMapViewController.instantiateFromStoryboard()
        show(floorMap, sender: nil)
    }
}
