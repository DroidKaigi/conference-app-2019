//
//  SessionRepository.swift
//  DroidKaigi 2019
//
//  Created by takahiro menju on 2019/01/13.
//

import Foundation
import ios_combined

class SessionRepository {
    func fetchSessions(callback:@escaping (SessionContents) -> Void){
        ApiComponentKt.generateDroidKaigiApi().getSessions(callback: { (response) -> KotlinUnit in
            callback(ResponseToModelMapperKt.toModel(response))
            return KotlinUnit.init()
            }
        )
    }
}
