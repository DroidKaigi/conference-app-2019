//
//  Reusable.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/21.
//

import UIKit

protocol Reusable: class {
    static var defaultReuseIdentifier: String { get }
}

extension Reusable where Self: UIView {
    static var defaultReuseIdentifier: String {
        return String(describing: self)
    }
}
