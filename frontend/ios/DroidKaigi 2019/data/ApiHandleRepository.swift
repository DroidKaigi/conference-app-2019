//
//  ApiHandleRepository.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/03.
//

import Foundation
import ioscombined

//FIXME: If you have a more better idea, we can refine this.
func handledKotlinException(_ error: Error) -> Error {
    
    guard let cause = error as? KotlinThrowable else {
        fatalError("Unexpedeted Error: \(error)")
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
