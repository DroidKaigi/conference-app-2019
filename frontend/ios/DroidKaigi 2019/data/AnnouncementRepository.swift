//
//  AnnouncementRepository.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/03.
//

import Foundation
import ioscombined
import RxSwift

final class AnnouncementRepository {
    
    func fetch() -> Single<[AnnouncementResponse]> {
        return ApiComponentKt.generateDroidKaigiApi()
        .getAnnouncementsAsync(lang: LangParameter.en)
        .asSingle([AnnouncementResponse].self)
        .catchError { throw handledKotlinException($0)}
    }
}
