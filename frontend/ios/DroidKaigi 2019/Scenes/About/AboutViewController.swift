//
//  AboutViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/04.
//

import UIKit
import SnapKit

class AboutViewController: UIViewController {

    init() {
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "About"
        view.addSubview(tableView)
        tableView.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
    }

    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.backgroundColor = .white
        tableView.estimatedRowHeight = 200
        tableView.rowHeight = UITableView.automaticDimension
        tableView.separatorStyle = .none
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(AboutCoverTableViewCell.self)
        tableView.register(AboutDescriptionTableViewCell.self)
        return tableView
    }()

    private enum Section: Int {
        case cover, desc, access, policy, appver
    }
    private var sections: [Section] = [.cover, .desc, .access, .policy, .appver]
}

extension AboutViewController: UITableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch sections[indexPath.section] {
        case .cover:
            let cell: AboutCoverTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            return cell
        case .desc:
            let cell: AboutDescriptionTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            return cell
        default:
            return UITableViewCell()
        }
    }
}

extension AboutViewController: UITableViewDelegate {

    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return .leastNonzeroMagnitude
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return nil
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return .leastNonzeroMagnitude
    }
}
