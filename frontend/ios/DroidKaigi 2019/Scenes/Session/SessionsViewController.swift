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
import XLPagerTabStrip

final class SessionsViewController: UIViewController, StoryboardInstantiable {

    var day: Day!

    override func viewDidLoad() {
        super.viewDidLoad()
        viewModel = SessionsViewModel(day: day)
        bind()
        tableView.rx.modelSelected(Session.self)
                .asDriver()
                .drive(onNext: { session in
                    let viewController = SessionDetailViewController(session: session)
                    self.navigationController?.pushViewController(viewController, animated: true)
                }).disposed(by: bag)
    }

    @IBOutlet private weak var tableView: UITableView! {
        didSet {
            tableView.separatorStyle = .none
            tableView.rowHeight = UITableView.automaticDimension
            tableView.contentInset = UIEdgeInsets(top: 10, left: 0, bottom: 10, right: 0)
            tableView.delegate = dataSource
            tableView.register(SessionTableViewCell.self)
        }
    }

    private var viewModel: SessionsViewModel!
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

extension SessionsViewController: IndicatorInfoProvider {
    func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: day.title)
    }
}
