//
//  StartViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/21.
//

import UIKit

class StartViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        setupRoot()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.view.window?.layer.add(CATransition(), forKey: kCATransition)
    }

    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    private lazy var logoView: UIView = {
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 160, height: 22))
        imageView.image = UIImage(named: "logo")
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFit
        // https://stackoverflow.com/questions/17719947/size-of-image-on-uinavigationbar/17720347#17720347
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 160, height: 22))
        view.addSubview(imageView)
        return view
    }()

    private func setupRoot() {
        let mainViewController = MainViewController.instantiateFromStoryboard()
        let navigationController = UINavigationController(rootViewController: mainViewController)
        mainViewController.navigationItem.titleView = logoView
        addChild(navigationController)
        navigationController.view.frame = view.bounds
        view.addSubview(navigationController.view)
        navigationController.didMove(toParent: self)
    }
}
