//
//  Session+Extension.swift
//  DroidKaigi 2019
//
//  Created by 柵木奨 on 2019/01/27.
//

import Foundation
import ios_combined

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
}
