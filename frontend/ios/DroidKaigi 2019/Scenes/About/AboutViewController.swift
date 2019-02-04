//
//  AboutViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/04.
//

import UIKit
import SnapKit
import MaterialComponents.MaterialSnackbar
import class ioscombined.LocaledString
import class ioscombined.LangKt
import class ioscombined.Lang

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
        tableView.bounces = false
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(AboutCoverTableViewCell.self)
        tableView.register(AboutTableViewCell.self)
        tableView.register(AboutDescriptionTableViewCell.self)
        tableView.registerReusableHeaderFooterView(AboutTableViewFooterView.self)
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
        case .access:
            let cell: AboutTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.titleLabel.text = LocaledString(ja: "会場アクセス", en: "Directions").getByLang(lang: LangKt.defaultLang())
            cell.subLabel.text = LocaledString(ja: "Mapを開く", en: "Open with Maps").getByLang(lang: LangKt.defaultLang())
            return cell
        case .policy:
            let cell: AboutTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.titleLabel.text = LocaledString(ja: "プライバシーポリシー", en: "Privacy Policy").getByLang(lang: LangKt.defaultLang())
            cell.subLabel.text = LocaledString(ja: "見てみる", en: "See Details").getByLang(lang: LangKt.defaultLang())
            return cell
        case .appver:
            let cell: AboutTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
            cell.titleLabel.text = LocaledString(ja: "アプリバージョン", en: "Version").getByLang(lang: LangKt.defaultLang())
            cell.subLabel.text = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
            cell.subLabel.textColor = .gray
            return cell
        }
    }
}

extension AboutViewController: UITableViewDelegate {

    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch sections[indexPath.section] {
        case .access:
            guard let url = URL(string: "comgooglemaps://"),
                  UIApplication.shared.canOpenURL(url) else {

                MDCSnackbarManager.show(MDCSnackbarMessage(text: "Can not open GoogleMap."))
                return
            }
            let place = "ベルサール新宿グランドコンファレンスセンター"
            let latitude = "35.696065"
            let longitude = "139.690426"
            if let mapUrl = URL(string: "comgooglemaps://?q=\(place)&center=\(latitude),\(longitude)") {
                UIApplication.shared.open(mapUrl)
            }
        case .policy:
            let urlString: String
            if LangKt.defaultLang() == Lang.ja {
                urlString = "http://www.association.droidkaigi.jp/privacy"
            } else {
                urlString = "http://www.association.droidkaigi.jp/en/privacy"
            }
            if let url = URL(string: urlString) {
                UIApplication.shared.open(url)
            }
        default:
            return
        }
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return .leastNonzeroMagnitude
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let view: AboutTableViewFooterView = tableView.dequeueReusableHeaderFooterView()
        return view
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        switch sections[section] {
        case .cover, .appver:
            return .leastNonzeroMagnitude
        default:
            return 4
        }
    }
}


class AboutTableViewFooterView: UITableViewHeaderFooterView, Reusable {

    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        borderView.backgroundColor = .white
        contentView.addSubview(borderView)
        borderView.snp.makeConstraints {
            $0.leading.trailing.equalToSuperview().inset(16)
            $0.height.equalTo(1)
            $0.centerY.equalToSuperview()
        }
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var borderView = DashedLineView()
}
