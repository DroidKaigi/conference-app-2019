//
//  Session+Extension.swift
//  DroidKaigi 2019
//
//  Created by 柵木奨 on 2019/01/27.
//

import Foundation
import ioscombined

extension Session {
    
    var startTimeText: String {
        struct Static {
            static let dateFormatter: DateFormatter = {
                let dateFormatter = DateFormatter()
                dateFormatter.timeZone = TimeZone(identifier: "Asia/Tokyo")
                dateFormatter.locale = NSLocale.current
                dateFormatter.dateFormat = "HH:mm"
                return dateFormatter
            }()
        }
        return Static.dateFormatter.string(from: Date(timeIntervalSince1970: startTime / 1000))
    }
}

extension SpeechSession {
    
    var tagContents: [TagContent] {
        var tags: [TagContent] = []
        tags = [.lang(lang: lang)]
        if forBeginners {
            tags.append(.beginner)
        }
        if isInterpretationTarget {
            tags.append(.interpretation)
        }
        tags.append(.category(category: category))
        return tags
    }
    
    // redefine for now
    func doCopy(id: String? = nil,
                 dayNumber: Int32? = nil,
                 startTime: Double? = nil,
                 endTime: Double? = nil,
                 title: LocaledString? = nil,
                 desc: String? = nil,
                 room: Room? = nil,
                 format: String? = nil,
                 lang: Lang? = nil,
                 category: ioscombined.Category? = nil,
                 intendedAudience: String? = nil,
                 videoUrl: String? = nil,
                 slideUrl: String? = nil,
                 isInterpretationTarget: Bool? = nil,
                 isFavorited: Bool? = nil,
                 speakers: [Speaker]? = nil,
                 forBeginners: Bool? = nil,
                 message: LocaledString? = nil
    ) -> SpeechSession {
        return doCopy(id: id ?? self.id_,
                      dayNumber: dayNumber ?? self.dayNumber,
                      startTime: startTime ?? self.startTime,
                      endTime: endTime ?? self.endTime,
                      title: title ?? self.title,
                      desc: desc ?? self.desc,
                      room: room ?? self.room,
                      format: format ?? self.format,
                      lang: lang ?? self.lang,
                      category: category ?? self.category,
                      intendedAudience: intendedAudience ?? self.intendedAudience,
                      videoUrl: videoUrl ?? self.videoUrl,
                      slideUrl: slideUrl ?? self.slideUrl,
                      isInterpretationTarget: isInterpretationTarget ?? self.isInterpretationTarget,
                      isFavorited: isFavorited ?? self.isFavorited,
                      speakers: speakers ?? self.speakers,
                      forBeginners: forBeginners ?? self.forBeginners,
                      message: message ?? self.message)
    }
}

extension ServiceSession {
    
    // redefine for now
    func doCopy(id: String? = nil,
                 dayNumber: Int32? = nil,
                 startTime: Double? = nil,
                 endTime: Double? = nil,
                 title: LocaledString? = nil,
                 desc: String? = nil,
                 room: Room? = nil,
                 sessionType: SessionType? = nil,
                 isFavorited: Bool? = nil
    ) -> ServiceSession {
        return doCopy(id: id ?? self.id_,
                      dayNumber: dayNumber ?? self.dayNumber,
                      startTime: startTime ?? self.startTime,
                      endTime: endTime ?? self.endTime,
                      title: title ?? self.title,
                      desc: desc ?? self.desc,
                      room: room ?? self.room,
                      sessionType: sessionType ?? self.sessionType,
                      isFavorited: isFavorited ?? self.isFavorited)
    }
}
