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
            assert(false, "Can not find Storyboard with the same name as the ViewController to generate," +
                    " or the ViewController is not \"Initial ViewController\".")
        }
        return controller!
    }
}
