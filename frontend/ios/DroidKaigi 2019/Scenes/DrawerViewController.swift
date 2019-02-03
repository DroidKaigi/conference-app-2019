//
//  DrawerViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/03.
//

import UIKit
import SnapKit

class DrawerViewController: UIViewController {

    init() {
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
//        setupSubviews()
        view.backgroundColor = .white
    }

    private lazy var statusBarShadow: UIView = {
        let view = UIView()
        view.backgroundColor = .black
        view.layer.opacity = 0.3
        return view
    }()

    private func setupSubviews() {
        [statusBarShadow].forEach(view.addSubview)
        statusBarShadow.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.height.equalTo(UIApplication.shared.statusBarFrame.height)
        }
    }
}
