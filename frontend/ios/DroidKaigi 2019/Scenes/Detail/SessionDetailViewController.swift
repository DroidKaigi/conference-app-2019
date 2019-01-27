//
//  SessionDetailViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/25.
//

import UIKit
import ios_combined
import SnapKit
import Kingfisher

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
        tableView.contentInset = UIEdgeInsets(top: 10, left: 0, bottom: 50, right: 0)
        tableView.separatorStyle = .none
        tableView.allowsSelection = false
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(SessionDetailTagsTableViewCell.self)
        tableView.register(SessionDetailHeaderTableViewCell.self)
        tableView.register(SessionDetailTableViewCell.self)
        tableView.register(SessionDetailWithIconTableViewCell.self)
        tableView.register(SessionDetailSectionHeaderView.self)
        tableView.register(SessionDetailSectionFooterView.self)
        return tableView
    }()

    private enum Section: Int {
        case header, desc, target, tags, survey, speakers, document
    }
    private var sections: [Section] = []
    private var documents: [Document] = []
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
            if speechSession.intendedAudience != nil {
                sections.append(.target)
            }
            sections.append(.tags)
            if speechSession.speakers.count > 0 {
                sections.append(.speakers)
            }
            if speechSession.slideUrl != nil || speechSession.videoUrl != nil {
                sections.append(.document)
                if let videoUrl = speechSession.videoUrl {
                    documents.append(.video(url: videoUrl))
                }
                if let slideUrl = speechSession.slideUrl {
                    documents.append(.slide(url: slideUrl))
                }
            }
        }
        self.sections = sections
        tableView.reloadData()
    }
}

extension SessionDetailViewController: UITableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return sections.count
    }

    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch sections[section] {
        case .speakers:
            return (session as? SpeechSession)?.speakers.count ?? 0
        case .target:
            return (session as? SpeechSession)?.intendedAudience == nil ? 0 : 1
        case .document:
            return documents.count
        default: return 1
        }
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch sections[indexPath.section] {
        case .header:
            let cell: SessionDetailHeaderTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.session = session
            return cell
        case .desc:
            let cell: SessionDetailTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            switch session {
            case let speechSession as SpeechSession:
                cell.label.text = speechSession.desc
            case let serviceSession as ServiceSession:
                cell.label.text = serviceSession.desc
            default:
                break
            }
            return cell
        case .target:
            guard let speechSession = session as? SpeechSession,
                  let text = speechSession.intendedAudience else { return UITableViewCell() }
            let cell: SessionDetailTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.label.text = text
            return cell
        case .tags:
            guard let speechSession = session as? SpeechSession else { return UITableViewCell() }
            let cell: SessionDetailTagsTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.tagContents = speechSession.tagContents
            return cell
        case .speakers:
            let cell: SessionDetailWithIconTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            guard let speechSession = session as? SpeechSession else { return UITableViewCell() }
            let speaker = speechSession.speakers[indexPath.row]
            cell.cellType = .speaker(speaker)
            return cell
        case .document:
            let cell: SessionDetailWithIconTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            let document = documents[indexPath.row]
            cell.cellType = .document(document)
            return cell
        default:
            return UITableViewCell()
        }
    }
}

extension SessionDetailViewController: UITableViewDelegate {

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView: SessionDetailSectionHeaderView = tableView.dequeueReusableHeaderFooterView()
        let title: LocaledString?
        switch sections[section] {
        case .target:
            title = LocaledString(ja: "対象者", en: "Target")
        case .tags:
            title = LocaledString(ja: "Tags", en: "Tags")
        case .speakers:
            title = LocaledString(ja: "Speaker", en: "Speaker")
        case .document:
            title = LocaledString(ja: "資料", en: "Document")
        default:
            title = LocaledString(ja: "", en: "")
        }
        headerView.titleLabel.text = title?.getByLang(lang: LangKt.defaultLang())
        return headerView
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        switch sections[section] {
        case .target, .tags, .speakers, .document:
            return UITableView.automaticDimension
        default:
            return .leastNonzeroMagnitude
        }
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        if sections.indices.last == section { return UIView() }
        let footerView: SessionDetailSectionFooterView = tableView.dequeueReusableHeaderFooterView()
        return footerView
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        if sections.indices.last == section { return .leastNonzeroMagnitude }
        return UITableView.automaticDimension
    }
}
