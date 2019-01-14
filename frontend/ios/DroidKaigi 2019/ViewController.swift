//
//  ViewController.swift
//  DroidKaigi 2019
//
//  Created by 菊池 紘 on 2019/01/09.
//

import UIKit
import ios_combined

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        SessionRepository().fetchSessions { (sessionContents) in
            print((sessionContents.sessions[0] as! Session.ServiceSession).title.ja)
        }
    }
}

