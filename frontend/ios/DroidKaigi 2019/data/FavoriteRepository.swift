//
//  FavoriteRepository.swift
//  DroidKaigi 2019
//
//  Created by woxtu on 2019/01/29.
//

import Foundation
import RxSwift

final class FavoriteRepository {
    private let key = "favoriteSessionIds"
    
    var sessionIdsDidChanged: Observable<[String]> {
        return UserDefaults.standard.rx
            .observe([String].self, key, options: [.initial, .new])
            .map { $0 ?? [] }
    }
    
    func toggle(sessionId: String) {
        var favorites = UserDefaults.standard.object(forKey: key) as? [String] ?? []
        if favorites.contains(sessionId) {
            favorites.removeAll { $0 == sessionId }
        } else {
            favorites.append(sessionId)
        }
        UserDefaults.standard.setValue(favorites, forKey: key)
        UserDefaults.standard.synchronize()
    }
}
