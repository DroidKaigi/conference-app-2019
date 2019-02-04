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
        .getAnnouncementsAsync(lang: getDeviceLangParameter())
        .asSingle([AnnouncementResponse].self)
        .catchError { throw handledKotlinException($0)}
    }

    func getDeviceLangParameter() -> LangParameter {
        let preferredDeviceLang = NSLocale.preferredLanguages
        if preferredDeviceLang.count == 0 {
            return LangParameter.en
        } else {
            let deviceLang = preferredDeviceLang[0]
            switch deviceLang {
            case "en":
                return LangParameter.en
            case "ja":
                return LangParameter.jp
            default:
                return LangParameter.en
            }
        }
    }
}
