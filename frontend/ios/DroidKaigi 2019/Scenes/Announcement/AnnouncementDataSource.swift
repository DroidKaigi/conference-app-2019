//
//  AnnouncementDataSource.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/02.
//

import Foundation
import UIKit
import RxCocoa
import RxSwift
import ioscombined

final class AnnouncementDataSource: NSObject, UITableViewDataSource {
    typealias Element = [AnnouncementResponse]
    var items: Element = []
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: AnnouncementTableViewCell = (tableView.dequeueReusableCell(withIdentifier: "AnnouncementCell", for: indexPath) as? AnnouncementTableViewCell)!
        let announcement = items[indexPath.row]
        cell.announcement = announcement
        return cell
    }
}

extension AnnouncementDataSource: RxTableViewDataSourceType {
    public func tableView(_ tableView: UITableView, observedEvent: Event<[AnnouncementResponse]>) {
        Binder(self) { dataSource, element in
            dataSource.items = element
            tableView.reloadData()
            }
            .on(observedEvent)
    }
}

extension AnnouncementDataSource: UITableViewDelegate {

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
    }

    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }

    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return UITableView.automaticDimension
    }
}
