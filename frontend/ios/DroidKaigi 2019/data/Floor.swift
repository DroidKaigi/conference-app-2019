//
//  Floor.swift
//  DroidKaigi 2019
//
//  Created by 417.72KI on 2019/01/31.
//

import Foundation
import class UIKit.UIImage

enum Floor: Int, CaseIterable {
    case floor1 = 1
    case floor5 = 5
}

extension Floor {
    var title: String {
        switch self {
        case .floor1:
            return "1F"
        case .floor5:
            return "5F"
        }
    }
    
    var map: UIImage {
        switch self {
        case .floor1:
            return #imageLiteral(resourceName: "ic_floor1")
        case .floor5:
            return #imageLiteral(resourceName: "ic_floor2")
        }
    }
}
