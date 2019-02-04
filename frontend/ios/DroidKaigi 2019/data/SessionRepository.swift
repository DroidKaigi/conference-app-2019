//
//  SessionRepository.swift
//  DroidKaigi 2019
//
//  Created by takahiro menju on 2019/01/13.
//

import Foundation
import ioscombined
import RxSwift

final class SessionRepository {

    func fetch() -> Single<SessionContents> {
        return ApiComponentKt.generateDroidKaigiApi()
            .getSessionsAsync()
            .asSingle(Response.self)
            .map { ResponseToModelMapperKt.toModel($0) }
            .catchError { throw handledKotlinException($0) }
            // .do(onError: { NSLog("fetch error: \($0)") })
    }

}
