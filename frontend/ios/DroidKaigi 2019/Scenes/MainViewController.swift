//
//  MainViewController.swift
//  DroidKaigi 2019
//
//  Created by koogawa on 2019/01/28.
//

import UIKit
import XLPagerTabStrip

class MainViewController: ButtonBarPagerTabStripViewController, StoryboardInstantiable, SlideTransitionable {

    override func viewDidLoad() {
        setupTabAppearance()
        super.viewDidLoad()
        setupSlideTransitionableSubviews()
        setupViewController(drawerViewController)
        drawerViewController.delegate = self
        containerView.bounces = false
        navigationItem.leftBarButtonItems = [
            UIBarButtonItem(image: #imageLiteral(resourceName: "menu"), style: .plain, target: self, action: #selector(open))
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

    private var drawerViewController = DrawerViewController()

    var rootViewController: UIViewController {
        return AppDelegate.shared?.rootViewController ?? UIViewController()
    }
    var slideViewController: UIViewController? {
        return drawerViewController
    }
    var tapGesture: UITapGestureRecognizer {
        return UITapGestureRecognizer(target: self, action: #selector(close))
    }
    var panGesture: UIPanGestureRecognizer {
        return UIPanGestureRecognizer(target: self, action: #selector(handlePanGesture(_:)))
    }
    var slideViewWidth: CGFloat { return 260 }
    var opacityView: UIView = UIView()
    var slideContainerView: UIView = UIView()
    var statusBarShadowView: UIView = UIView()
    var contentViewOpacity: CGFloat { return 0.5 }

    @objc private  func close() {
        closeSlide()
    }

    @objc private  func open() {
        openSlide()
    }

    @objc private func handlePanGesture(_ panGesture: UIPanGestureRecognizer) {
        panAction(panGesture)
    }
}

extension MainViewController: DrawerViewControllerDelegate {

    func drawerViewController(_ drawer: DrawerViewController, didSelectMenuItem: MenuItem) {
        switch didSelectMenuItem {
        case .floorMap:
            let floorMap = FloorMapViewController.instantiateFromStoryboard()
            navigationController?.pushViewController(floorMap, animated: false)
        default:
            break
        }
        closeSlide()
    }
}
