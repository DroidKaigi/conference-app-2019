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

    typealias Element = [Session]
    var items: Element = []

    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        let element = items[indexPath.row]
        switch element {
        case let serviceSession as ServiceSession:
            cell.textLabel?.text = serviceSession.title.getByLang(lang: LangKt.defaultLang())
        case let speechSession as SpeechSession:
            cell.textLabel?.text = speechSession.title.getByLang(lang: LangKt.defaultLang())
        default:
            break
        }
        return cell
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
