//
//  SessionRepository.swift
//  DroidKaigi 2019
//
//  Created by takahiro menju on 2019/01/13.
//

import Foundation
import ios_combined
import RxSwift
import RxCocoa

final class SessionRepository {
    
    func fetch() -> Single<SessionContents> {
        return Single<SessionContents>.create { observer -> Disposable in
            // TODO: How can we get errors from DroidKaigiApi?
            ApiComponentKt.generateDroidKaigiApi().getSessions() { response in
                observer(.success(ResponseToModelMapperKt.toModel(response)))
                return KotlinUnit()
            }
            return Disposables.create()
        }
    }
}
