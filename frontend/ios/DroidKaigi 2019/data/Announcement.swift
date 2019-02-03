//
//  Announcement.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/02.
//

import Foundation
import class UIKit.UIImage

struct Announcement {
    let title: String
    let content: String
    let publishedAt: Date
    let type: AnnouncementType
    
    init(title: String, content: String, publishedAt: Date, type: AnnouncementType) {
        self.title = title
        self.content = content
        self.publishedAt = publishedAt
        self.type = type
    }
}

enum AnnouncementType {
    case notification
    case alert
    case feedback
}

extension AnnouncementType {
    var iconImage : UIImage {
        switch self {
        case .notification:
            return #imageLiteral(resourceName: "annnoucement")
        case .alert:
            return #imageLiteral(resourceName: "alert")
        case .feedback:
            return #imageLiteral(resourceName: "feedback")
        }
    }
}
