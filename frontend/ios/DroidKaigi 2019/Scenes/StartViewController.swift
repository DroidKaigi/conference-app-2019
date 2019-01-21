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
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)

        let sessionsViewController = SessionsViewController.instantiateFromStoryboard()
        // TODO: Replace rootViewController
        let navigationController = UINavigationController(rootViewController: sessionsViewController)
        self.view.window?.rootViewController = navigationController
        self.view.window?.layer.add(CATransition(), forKey:kCATransition)
    }
}
