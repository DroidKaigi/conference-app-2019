//
//  NotificationsViewController.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/01.
//

import UIKit
import ioscombined
import RxSwift

class AnnouncementsViewController: UIViewController {

    private let dataSource = AnnouncementDataSource()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        bind()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    @IBOutlet weak var tableView: UITableView! {
        didSet {
            tableView.estimatedRowHeight = 200
            tableView.rowHeight = UITableView.automaticDimension
            tableView.delegate = dataSource
            tableView.dataSource = dataSource
        }
    }
    
    private func bind() {
        //TODO: will replace with real data
        let announcementSampleList = [Announcement.init(title: "お知らせのテスト", content: "これは日本語テストのアナウンスです。改行も行います！改行も行います！改行も行います！", publishedAt: Date(), type: AnnouncementType.notification),
            Announcement.init(title: "アラートのテスト", content: "これはアラートのテストです。改行も行います！改行も行います！改行も行います！", publishedAt: Date(), type: AnnouncementType.alert),
            Announcement.init(title: "フィードバックのテスト", content: "これはフィードバックのテストです。改行も行います！改行も行います！改行も行います！", publishedAt: Date(), type: AnnouncementType.feedback)]
        dataSource.items = announcementSampleList
        tableView.reloadData()
    }
}

extension AnnouncementsViewController: StoryboardInstantiable {
    static let storyboardName = "Announcements"
}
