//
//  SessionRepository.swift
//  DroidKaigi 2019
//
//  Created by takahiro menju on 2019/01/13.
//

import Foundation
import main
import RxSwift
import RxCocoa

final class SessionRepository {
    
    func fetch() -> Single<SessionContents> {
        return Single<SessionContents>.create { observer -> Disposable in
            ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { response in
                observer(.success(ResponseToModelMapperKt.toModel(response)))
                return KotlinUnit()
            }, onError: { error in
                observer(.error(NSError(domain: error.message ?? "No message", code: -1, userInfo: nil)))
                return KotlinUnit()
            })
            return Disposables.create()
        }
    }
}
