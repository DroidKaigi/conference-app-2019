//
//  StoryboardInstantiable.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/21.
//

import Foundation
import UIKit


protocol StoryboardInstantiable {
    static var storyboardName: String { get }
}

extension StoryboardInstantiable where Self: UIViewController {

    static var storyboardName: String {
        return String(describing: self)
    }

    static func instantiateFromStoryboard() -> Self {
        let storyboard = UIStoryboard(name: storyboardName, bundle: nil)
        let controller = storyboard.instantiateInitialViewController() as? Self
        if controller == nil {
            assert(false, "生成したいViewControllerと同じ名前のStorybaordが見つからないか、Initial ViewControllerに設定されていない可能性があります。")
        }
        return controller!
    }
}