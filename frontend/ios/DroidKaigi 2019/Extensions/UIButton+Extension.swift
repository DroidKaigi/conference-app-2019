//
//  UIButton+Extension.swift
//  DroidKaigi 2019
//
//  Created by woxtu on 2019/01/31.
//

import UIKit

extension UIButton {
    func setImageTintColor(color: UIColor, for state: UIControl.State) {
        if let image = image(for: state) {
            setImage(applyTintColor(color: color, to: image), for: state)
        }
    }
    
    private func applyTintColor(color: UIColor, to image: UIImage) -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(image.size, false, UIScreen.main.scale)
        
        guard let context = UIGraphicsGetCurrentContext() else {
            return image
        }

        let rect = CGRect(origin: .zero, size: image.size)

        context.translateBy(x: 0, y: image.size.height)
        context.scaleBy(x: 1, y: -1)

        if let image = image.cgImage {
            context.setBlendMode(.normal)
            context.draw(image, in: rect)
        }
        
        context.setBlendMode(.sourceIn)
        context.setFillColor(color.cgColor)
        context.fill(rect)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
}
