//
//  DrawerViewController.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/03.
//

import UIKit
import SnapKit
import ioscombined

enum MenuItem {
    case about
    case floorMap
    case announce

    var title: LocaledString {
        switch self {
        case .about:
            return LocaledString(ja: "DroidKaigiとは", en: "About DroidKaigi")
        case .floorMap:
            return LocaledString(ja: "フロアMap", en: "Floormap")
        case .announce:
            return LocaledString(ja: "お知らせ", en: "Announce")
        }
    }

    var icon: UIImage? {
        switch self {
        case .about: return UIImage(named: "bug_report")
        case .floorMap: return UIImage(named: "room")
        case .announce: return UIImage(named: "info")
        }
    }
}

protocol DrawerViewControllerDelegate: class {
    func drawerViewController(_ drawer: DrawerViewController, didSelectMenuItem: MenuItem)
}

class DrawerViewController: UIViewController {

    weak var delegate: DrawerViewControllerDelegate?

    init() {
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupSubviews()
        view.backgroundColor = UIColor.DK.primary.color
    }

    private let menuItems: [MenuItem] = [.about, .announce, .floorMap]

    private lazy var headerView = DrawerTableHeaderView()
    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: .zero, style: .grouped)
        tableView.backgroundColor = .white
        tableView.contentInset = UIEdgeInsets(top: 12, left: 0, bottom: 0, right: 0)
        tableView.separatorStyle = .none
        tableView.isScrollEnabled = false
        tableView.rowHeight = UITableView.automaticDimension
        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(DrawerMenuTableViewCell.self)
        return tableView
    }()

    private func setupSubviews() {
        [headerView, tableView].forEach(view.addSubview)
        headerView.snp.makeConstraints {
            $0.top.equalToSuperview().offset(UIApplication.shared.statusBarFrame.height)
            $0.leading.trailing.equalToSuperview()
            $0.height.equalTo(headerView.snp.width).multipliedBy(5.0/9.0)
        }
        tableView.snp.makeConstraints {
            $0.top.equalTo(headerView.snp.bottom)
            $0.leading.trailing.bottom.equalToSuperview()
        }
    }
}

extension DrawerViewController: UITableViewDataSource {

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return menuItems.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: DrawerMenuTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
        cell.menuItem = menuItems[indexPath.row]
        return cell
    }
}

extension DrawerViewController: UITableViewDelegate {

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        delegate?.drawerViewController(self, didSelectMenuItem: menuItems[indexPath.row])
    }

    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return nil
    }

    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return .leastNonzeroMagnitude
    }

    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return DashedLineView()
    }

    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 25
    }
}

final class DrawerTableHeaderView: UIView {

    init() {
        super.init(frame: .zero)
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var coverImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "cover")
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()
    private lazy var logoImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "logo")
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()

    private func setupSubviews() {
        [coverImageView, logoImageView].forEach(addSubview)
        coverImageView.snp.makeConstraints {
            $0.edges.equalToSuperview()
        }
        logoImageView.snp.makeConstraints {
            $0.center.equalToSuperview()
            $0.leading.trailing.equalToSuperview().inset(30)
        }
    }
}
