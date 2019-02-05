//
//  NotificationsViewController.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/01.
//

import UIKit
import ioscombined
import RxSwift
import MaterialComponents.MaterialSnackbar

class AnnouncementsViewController: UIViewController {

    private let dataSource = AnnouncementDataSource()
    private let viewModel = AnnouncementsViewModel()
    private let bag = DisposeBag()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        bind()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = 200
            tableView.rowHeight = UITableView.automaticDimension
            tableView.delegate = dataSource
            tableView.dataSource = dataSource
        }
    }
    
    private func bind() {
        let viewWillAppear = rx.methodInvoked(#selector(self.viewWillAppear))
            .map { _ in }
        let input = AnnouncementsViewModel.Input(viewWillAppear: viewWillAppear)
        let output = viewModel.transform(input: input)
        output.error
            .drive(onNext: {errorMessage in
                if let errMsg = errorMessage {
                    MDCSnackbarManager.show(MDCSnackbarMessage(text: errMsg))
                }
            })
        .disposed(by: bag)

        output.announcements
        .drive(tableView.rx.items(dataSource: dataSource))
            .disposed(by: bag)
    }
}

extension AnnouncementsViewController: StoryboardInstantiable {
    static let storyboardName = "Announcements"
}
