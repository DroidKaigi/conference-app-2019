//
//  SessionDetailViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/25.
//

import UIKit
import ios_combined
import SnapKit

class SessionDetailViewController: UIViewController {

    init(session: Session) {
        self.session = session
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupSubviews()
        setupSections()
    }

    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.backgroundColor = .white
        tableView.estimatedRowHeight = 200
        tableView.estimatedSectionHeaderHeight = .leastNonzeroMagnitude
        tableView.rowHeight = UITableView.automaticDimension
        tableView.dataSource = self
        tableView.delegate = self
        return tableView
    }()

    private enum Section: Int {
        case header, desc, target, tags, survey, speakers, document
    }
    private var sections: [Section] = []
    private let session: Session

    private func setupSubviews() {
        view.addSubview(tableView)
        tableView.snp.makeConstraints {
            if #available(iOS 11, *) {
                $0.top.equalTo(view.safeAreaLayoutGuide.snp.topMargin)
            } else {
                $0.top.equalToSuperview()
            }
            $0.leading.trailing.bottom.equalToSuperview()
        }
    }

    private func setupSections() {
        var sections: [Section] = [.header, .desc]
        if let speechSession = session as? SpeechSession {
            sections.append(.tags)
            if speechSession.speakers.count > 0 {
                sections.append(.speakers)
            }
            if speechSession.slideUrl != nil || speechSession.videoUrl != nil {
                sections.append(.document)
            }
        }
        self.sections = sections
        tableView.reloadData()
    }
}

extension SessionDetailViewController: UITableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if sections[section] == .speakers {
            return (session as? SpeechSession)?.speakers.count ?? 0
        }
        return 1
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
}

extension SessionDetailViewController: UITableViewDelegate {

    public func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return nil
    }

    public func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return .leastNonzeroMagnitude
    }
}
