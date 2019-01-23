//
//  SessionDataSource.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import Foundation
import UIKit
import RxCocoa
import RxSwift
import ios_combined

final class SessionDataSource: NSObject, UITableViewDataSource {

    typealias Element = [SessionByStartTime]
    var items: Element = []

    func numberOfSections(in tableView: UITableView) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items[section].sessions.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: SessionTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
        cell.session = items[indexPath.section].sessions[indexPath.row]
        return cell
    }
    
    func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return items[section].startTimeText
    }
}

extension SessionDataSource: RxTableViewDataSourceType {

    public func tableView(_ tableView: UITableView, observedEvent: Event<Element>) {
        Binder(self) { dataSource, element in
            dataSource.items = element
            tableView.reloadData()
        }
        .on(observedEvent)
    }
}
