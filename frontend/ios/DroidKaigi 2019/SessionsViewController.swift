//
//  SessionsViewController.swift
//  DroidKaigi 2019
//
//  Created by 菊池 紘 on 2019/01/09.
//

import UIKit
import ios_combined
import RxSwift
import RxCocoa

final class SessionsViewController: UIViewController {
    
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
            .bind {[weak self] (errorMessage) in
                if let errMsg = errorMessage {
                    let alert = UIAlertController(title: "ERROR", message: errMsg, preferredStyle: UIAlertController.Style.alert)
                    let closeButton = UIAlertAction(title: "Close", style: UIAlertAction.Style.cancel, handler: nil)
                    alert.addAction(closeButton)
                    
                    self?.present(alert, animated: true, completion: nil)
                }
            }
            .disposed(by: disposeBag)

        self.viewModel = viewModel
    }
}
