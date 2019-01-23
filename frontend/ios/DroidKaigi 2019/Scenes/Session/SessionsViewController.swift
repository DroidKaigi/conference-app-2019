//
//  SessionsViewController.swift
//  DroidKaigi 2019
//
//  Created by 菊池 紘 on 2019/01/09.
//

import UIKit
import ios_combined
import MaterialComponents.MaterialSnackbar
import RxSwift
import RxCocoa

final class SessionsViewController: UIViewController, StoryboardInstantiable {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        bind()
    }

    @IBOutlet private weak var tableView: UITableView! {
        didSet {
            tableView.separatorStyle = .none
            tableView.rowHeight = UITableView.automaticDimension
            tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 10, right: 0)
            tableView.register(SessionTableViewCell.self)
        }
    }

    private var viewModel = SessionsViewModel()
    private let bag = DisposeBag()

    private let dataSource = SessionDataSource()

    private func bind() {
        let input = SessionsViewModel.Input(initTrigger: Observable.just(()))
        let output = viewModel.transform(input: input)
        output.error
              .drive(onNext: { errorMessage in
                  if let errMsg = errorMessage {
                      MDCSnackbarManager.show(MDCSnackbarMessage(text: errMsg))
                  }
              })
              .disposed(by: bag)
        output.sessions
              .drive(tableView.rx.items(dataSource: dataSource))
              .disposed(by: bag)
    }
}
