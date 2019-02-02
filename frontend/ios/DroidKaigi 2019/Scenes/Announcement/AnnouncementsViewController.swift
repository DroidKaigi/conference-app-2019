//
//  NotificationsViewController.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/01.
//

import UIKit

class AnnouncementsViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.separatorStyle = .none
            tableView.rowHeight = UITableView.automaticDimension
            tableView.contentInset = UIEdgeInsets(top: 10, left: 0, bottom: 10, right: 0)
//            tableView.delegate = dataSource
            tableView.register(SessionTableViewCell.self)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
}

extension AnnouncementsViewController: StoryboardInstantiable {
    static let storyboardName = "Announcements"
}
