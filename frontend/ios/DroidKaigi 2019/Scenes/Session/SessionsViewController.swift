//
//  SessionsViewController.swift
//  DroidKaigi 2019
//
//  Created by 菊池 紘 on 2019/01/09.
//

import UIKit
import ioscombined
import MaterialComponents.MaterialSnackbar
import RxSwift
import RxCocoa
import XLPagerTabStrip
import SnapKit

final class SessionsViewController: UIViewController {

    init(viewModel: SessionsViewModel) {
        self.viewModel = viewModel
        super.init(nibName: nil, bundle: nil)
        [tableHeaderView, tableView].forEach(view.addSubview)
        tableHeaderView.snp.makeConstraints {
            $0.top.leading.trailing.equalToSuperview()
            $0.height.equalTo(48)
        }
        tableView.snp.makeConstraints {
            $0.top.equalTo(tableHeaderView.snp.bottom)
            $0.leading.trailing.bottom.equalToSuperview()
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        bind()
        tableView.rx.modelSelected(Session.self)
                .asDriver()
                .drive(onNext: { session in
                    let viewController = SessionDetailViewController(session: session)
                    self.navigationController?.pushViewController(viewController, animated: true)
                }).disposed(by: bag)
    }

    private var viewModel: SessionsViewModel
    private let bag = DisposeBag()

    private let dataSource = SessionDataSource()

    private lazy var tableHeaderView = SessionTableHeaderView()
    
    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .plain)
        tableView.backgroundColor = .white
        tableView.separatorStyle = .none
        tableView.rowHeight = UITableView.automaticDimension
        tableView.contentInset = UIEdgeInsets(top: 10, left: 0, bottom: 10, right: 0)
        tableView.delegate = dataSource
        tableView.register(SessionTableViewCell.self)
        return tableView
    }()

    private func bind() {
        let viewWillAppear = rx.methodInvoked(#selector(self.viewWillAppear)).map { _ in }
        let toggleFavorite = dataSource.toggleFavorite
        let input = SessionsViewModel.Input(viewWillAppear: viewWillAppear, toggleFavorite: toggleFavorite)
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
        
        dataSource.topVisibleSession
            .subscribe(onNext: { print($0.id_) })
            .disposed(by: bag)
    }
}

extension SessionsViewController: IndicatorInfoProvider {
    func indicatorInfo(for pagerTabStripController: PagerTabStripViewController) -> IndicatorInfo {
        return IndicatorInfo(title: viewModel.type.text)
    }
}
