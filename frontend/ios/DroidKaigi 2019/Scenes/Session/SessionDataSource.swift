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

    let topVisibleSession = PublishSubject<Session>()
    let toggleFavorite = PublishSubject<Session>()
    
    private var cellHeightsCache = [IndexPath: CGFloat]()

    func numberOfSections(in tableView: UITableView) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items[section].sessions.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: SessionTableViewCell = tableView.dequeueReusableCell(forIndexPath: indexPath)
        let session = items[indexPath.section].sessions[indexPath.row]
        cell.session = session
        cell.favoriteButtonDidTap
            .map { _ in session }
            .bind(to: toggleFavorite)
            .disposed(by: cell.bag)
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

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: false)
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        cellHeightsCache[indexPath] = cell.frame.height
    }
    
    func tableView(_ tableView: UITableView, estimatedHeightForRowAt indexPath: IndexPath) -> CGFloat {
        return cellHeightsCache[indexPath] ?? UITableView.automaticDimension
    }
}

extension SessionDataSource: UIScrollViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        if let tableView = scrollView as? UITableView,
            let indexPath = tableView.indexPathsForVisibleRows?.first {
            let session = items[indexPath.section].sessions[indexPath.row]
            topVisibleSession.onNext(session)
        }
    }
}

extension SessionDataSource: RxTableViewDataSourceType {

    func tableView(_ tableView: UITableView, observedEvent: Event<Element>) {
        Binder(self) { dataSource, element in
            dataSource.items = element
            tableView.reloadData()
            
            // get the top visible session before scrolling table view
            if let indexPath = tableView.indexPathsForVisibleRows?.first {
                let session = dataSource.items[indexPath.section].sessions[indexPath.row]
                dataSource.topVisibleSession.onNext(session)
            }
        }
        .on(observedEvent)
    }
}

extension SessionDataSource: SectionedViewDataSourceType {
    
    func model(at indexPath: IndexPath) throws -> Any {
        return items[indexPath.section].sessions[indexPath.row]
    }
}
