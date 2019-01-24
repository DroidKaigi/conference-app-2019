//
//  SessionRepository.swift
//  DroidKaigi 2019
//
//  Created by takahiro menju on 2019/01/13.
//

import Foundation
import ios_combined
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


//FIXME: If you have a more better idea, we can refine this.
private func handledKotlinException(_ error: Error) -> Error {

    guard let cause = error as? KotlinThrowable else {
        fatalError("Unexpedeted Eroor: \(error)")
    }

    // Obtain `NSError` from Ktor `IosHttpRequestException`.
    // See also: data/api/ThrowableExt.kt
    if let origin = cause.originNSError {
        return origin
    }

    // Return handled kotlin exception.
    if let handledException = cause as? KotlinException {
        return handledException
    }

    fatalError("Unexpedeted KotlinThrowable: \(cause)")
}
