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
    
    @IBOutlet private weak var tableView: UITableView!
    
    private var viewModel: SessionsViewModel?
    private let disposeBag = DisposeBag()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let viewModel = SessionsViewModel(input: .init(viewDidLoad: Driver.just(())))
        
        viewModel.titles
            .bind(to: tableView.rx.items(cellIdentifier: "Cell")) { index, model, cell in
                cell.textLabel?.text = model
            }
            .disposed(by: disposeBag)
        
        viewModel.error
            .bind { (errorMessage) in
                if let errMsg = errorMessage {
                    MDCSnackbarManager.show(MDCSnackbarMessage(text: errMsg))
                }
            }
            .disposed(by: disposeBag)

        self.viewModel = viewModel
    }
}
