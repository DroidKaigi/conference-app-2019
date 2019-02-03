//
//  SlideTransitionable.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/03.
//

import UIKit

protocol SlideTransitionable: class {
    var rootViewController: UIViewController { get }
    var slideViewController: UIViewController? { get }
    var slideContainerView: UIView { get set }
    var opacityView: UIView { get set }
    var statusBarShadowView: UIView { get set }
    var contentViewOpacity: CGFloat { get }
    var slideViewWidth: CGFloat { get }
    var tapGesture: UITapGestureRecognizer { get }
    var panGesture: UIPanGestureRecognizer { get }
}

struct PanState {
    static var frameAtStartOfPan: CGRect = CGRect.zero
    static var startPointOfPan: CGPoint = CGPoint.zero
    static var wasOpenAtStartOfPan: Bool = false
    static var wasHiddenAtStartOfPan: Bool = false
    static var lastState: UIGestureRecognizer.State = .ended
}

enum SlideAction {
    case open
    case close
}

struct PanInfo {
    var action: SlideAction
    var shouldBounce: Bool
    var velocity: CGFloat
}

extension SlideTransitionable where Self: UIViewController {

    func setupSlideTransitionableSubviews() {
        let slideContainerView: UIView = {
            var frame: CGRect = self.view.bounds
            frame.size.width = slideViewWidth
            frame.origin.x = -slideViewWidth
            let offset: CGFloat = 0
            frame.origin.y += offset
            frame.size.height -= offset
            let view = UIView(frame: frame)
            view.backgroundColor = .red
            view.autoresizingMask = .flexibleHeight
            return view
        }()
        let opacityView: UIView = {
            let view = UIView(frame: self.view.bounds)
            view.backgroundColor = .black
            view.autoresizingMask = [UIView.AutoresizingMask.flexibleHeight, UIView.AutoresizingMask.flexibleWidth]
            view.layer.opacity = 0.0
            return view
        }()
        let shadowView: UIView = {
            let view = UIView(frame: UIApplication.shared.statusBarFrame)
            view.backgroundColor = .black
            view.layer.opacity = 0.0
            return view
        }()
        slideContainerView.addGestureRecognizer(panGesture)
        opacityView.addGestureRecognizer(panGesture)
        opacityView.addGestureRecognizer(tapGesture)
        rootViewController.view.addSubview(opacityView)
        rootViewController.view.addSubview(slideContainerView)
        rootViewController.view.addSubview(shadowView)
        self.slideContainerView = slideContainerView
        self.opacityView = opacityView
        self.statusBarShadowView = shadowView
    }

    func setupViewController(_ targetViewController: UIViewController) {
        targetViewController.view.frame = slideContainerView.bounds
        if !rootViewController.children.contains(where: { $0 is DrawerViewController}) {
            rootViewController.addChild(targetViewController)
            slideContainerView.addSubview(targetViewController.view)
            targetViewController.didMove(toParent: self)
        }
    }

    func closeSlide() {
        if slideViewController == nil { return }
        slideViewController?.beginAppearanceTransition(isLeftHidden(), animated: true)
        closeLeftWithVelocity(0.0)
    }

    func openSlide() {
        if slideViewController == nil { return }
        slideViewController?.beginAppearanceTransition(isLeftHidden(), animated: true)
        openLeftWithVelocity(0.0)
    }

    private func openLeftWithVelocity(_ velocity: CGFloat) {
        let xOrigin: CGFloat = slideContainerView.frame.origin.x
        let finalXOrigin: CGFloat = 0
        var frame: CGRect = slideContainerView.frame
        frame.origin.x = finalXOrigin
        var duration: TimeInterval = 0.3
        if velocity != 0.0 {
            duration = Double(abs(xOrigin) / velocity)
            duration = Double(fmax(0.1, fmin(1.0, duration)))
        }
        self.statusBarShadowView.layer.opacity = 0.3
        UIView.animate(
            withDuration: duration,
            delay: 0.0,
            options: .curveEaseOut,
            animations: { [weak self] in
                guard let `self` = self else { return }
                self.slideContainerView.frame = frame
                self.opacityView.layer.opacity = Float(self.contentViewOpacity)
            },
            completion: { [weak self] _ in
                guard let `self` = self else { return }
                self.slideViewController?.endAppearanceTransition()
            }
        )
    }

    private func closeLeftWithVelocity(_ velocity: CGFloat) {
        let xOrigin: CGFloat = slideContainerView.frame.origin.x
        let finalXOrigin: CGFloat = -slideContainerView.frame.size.width
        var frame: CGRect = slideContainerView.frame
        frame.origin.x = finalXOrigin
        var duration: TimeInterval = 0.3
        if velocity != 0.0 {
            duration = Double(abs(xOrigin + slideContainerView.frame.size.width) / velocity)
            duration = Double(fmax(0.1, fmin(1.0, duration)))
        }
        UIView.animate(
            withDuration: duration,
            delay: 0.0,
            options: .curveEaseOut,
            animations: { [weak self] in
                guard let `self` = self else { return }
                self.slideContainerView.frame = frame
                self.opacityView.layer.opacity = 0.0
            },
            completion: { [weak self] _ in
                guard let `self` = self else { return }
                self.slideViewController?.endAppearanceTransition()
                self.statusBarShadowView.layer.opacity = 0.0
            }
        )
    }

    private func isLeftOpen() -> Bool {
        return slideViewController != nil &&
            slideContainerView.frame.origin.x == 0
    }

    private func isLeftHidden() -> Bool {
        return slideContainerView.frame.origin.x <= -slideContainerView.frame.width
    }

    func panAction(_ panGesture: UIPanGestureRecognizer) {
        if slideViewController == nil { return }
        switch panGesture.state {
        case .began:
            if PanState.lastState != .ended && PanState.lastState != .cancelled && PanState.lastState != .failed {
                return
            }
            beginPanAction(panGesture)
        case .changed:
            if PanState.lastState != .began && PanState.lastState != .changed {
                return
            }
            changePanAction(panGesture)
        case .ended, .cancelled:
            if PanState.lastState != .changed {
                return
            }
            endOrCancelPanAction(panGesture)
        case .failed, .possible:
            break
        }
        PanState.lastState = panGesture.state
    }

    private func beginPanAction(_ panGesture: UIPanGestureRecognizer) {
        PanState.frameAtStartOfPan = slideContainerView.frame
        PanState.startPointOfPan = panGesture.location(in: view)
        PanState.wasOpenAtStartOfPan = isLeftOpen()
        PanState.wasHiddenAtStartOfPan = isLeftHidden()
        slideViewController?.beginAppearanceTransition(PanState.wasHiddenAtStartOfPan, animated: true)
    }

    private func changePanAction(_ panGesture: UIPanGestureRecognizer) {
        let translation: CGPoint = panGesture.translation(in: panGesture.view!)
        slideContainerView.frame = applySlideTranslation(translation, toFrame: PanState.frameAtStartOfPan)
        applySlideOpacity()
    }

    private func endOrCancelPanAction(_ panGesture: UIPanGestureRecognizer) {
        let velocity: CGPoint = panGesture.velocity(in: panGesture.view)
        let panInfo: PanInfo = panSlideResultInfoForVelocity(velocity)
        if panInfo.action == .open {
            if !PanState.wasHiddenAtStartOfPan {
                slideViewController?.beginAppearanceTransition(true, animated: true)
            }
            openLeftWithVelocity(panInfo.velocity)
        } else {
            if PanState.wasHiddenAtStartOfPan {
                slideViewController?.beginAppearanceTransition(false, animated: true)
            }
            closeLeftWithVelocity(panInfo.velocity)
        }
    }

    private func minOrigin() -> CGFloat {
        return -slideContainerView.frame.width
    }

    private func panSlideResultInfoForVelocity(_ velocity: CGPoint) -> PanInfo {
        let thresholdVelocity: CGFloat = -1000.0
        let pointOfNoReturnWidth: CGFloat = 130
        let pointOfNoReturn: CGFloat = pointOfNoReturnWidth
        let rightOrigin: CGFloat = slideContainerView.frame.origin.x + slideContainerView.frame.width
        var panInfo: PanInfo = PanInfo(action: .close, shouldBounce: false, velocity: 0.0)
        panInfo.action = rightOrigin >= pointOfNoReturn ? .open : .close
        if velocity.x <= thresholdVelocity {
            panInfo.action = .close
            panInfo.velocity = velocity.x
        } else if velocity.x >= (-1.0 * thresholdVelocity) {
            panInfo.action = .open
            panInfo.velocity = velocity.x
        }
        return panInfo
    }

    private func applySlideTranslation(_ translation: CGPoint, toFrame: CGRect) -> CGRect {
        var newOrigin: CGFloat = toFrame.origin.x
        newOrigin += translation.x
        let minOrigin: CGFloat = self.minOrigin()
        let maxOrigin: CGFloat = 0
        var newFrame: CGRect = toFrame
        if newOrigin < minOrigin {
            newOrigin = minOrigin
        } else if newOrigin > maxOrigin {
            newOrigin = maxOrigin
        }
        newFrame.origin.x = newOrigin
        return newFrame
    }

    private func openedSlideRatio() -> CGFloat {
        let width: CGFloat = slideContainerView.frame.size.width
        let currentPosition: CGFloat = slideContainerView.frame.origin.x + slideContainerView.frame.size.width
        return currentPosition / width
    }

    private func applySlideOpacity() {
        let openedRightRatio: CGFloat = openedSlideRatio()
        let opacity: CGFloat = contentViewOpacity * openedRightRatio
        opacityView.layer.opacity = Float(opacity)
    }
}
