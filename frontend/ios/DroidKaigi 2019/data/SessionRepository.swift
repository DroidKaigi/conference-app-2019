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


// MARK: - Extensions for Kotlin Deferred with RxSwift

extension KotlinThrowable: LocalizedError {
    public var errorDescription: String? {
        return self.message ?? "No message. \(self)"
    }
}


extension Kotlinx_coroutines_core_nativeDeferred {
    
    func asSingle<ElementType>(_ elementType: ElementType.Type) -> Single<ElementType> {
        return Single<ElementType>.create { observer in
            self.invokeOnCompletion { cause in
                if let cause = cause {
                    observer(.error(cause))
                    return KotlinUnit()
                }

                if let result = self.getCompleted() as? ElementType {
                    observer(.success(result))
                    return KotlinUnit()
                }

                fatalError("Illegal state or invalid elementType.")
            }

            return Disposables.create {
                self.cancel()
            }
        }
    }
}
