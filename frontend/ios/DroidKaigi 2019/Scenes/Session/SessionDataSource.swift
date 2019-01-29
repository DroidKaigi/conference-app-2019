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
import ioscombined

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
}

extension SessionDataSource: UITableViewDelegate {
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let view = SessionHeaderView(frame: CGRect(x: 0,
                                                   y: 0,
                                                   width: tableView.frame.width,
                                                   height: 30))
        view.set(startTimeText: items[section].startTimeText)
        return view
    }

    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
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

extension SessionDataSource: SectionedViewDataSourceType {
    func model(at indexPath: IndexPath) throws -> Any {
        return items[indexPath.section].sessions[indexPath.row]
    }
}
