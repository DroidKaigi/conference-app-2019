//
//  KotlinxDeferred+Extension.swift
//  DroidKaigi 2019
//

import Foundation
import RxSwift
import ioscombined

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
