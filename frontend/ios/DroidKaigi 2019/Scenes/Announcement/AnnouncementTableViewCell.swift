//
//  AnnouncementTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/02.
//

import UIKit
import ioscombined

class AnnouncementTableViewCell: UITableViewCell {
    @IBOutlet weak var announcementIcon: UIImageView!
    @IBOutlet weak var publishedAt: UILabel!
    @IBOutlet weak var title: UILabel!
    @IBOutlet weak var content: UILabel!
    
    var announcement: AnnouncementResponse? {
        didSet {
            guard let announcement = announcement else {
                return }
            publishedAt.text = announcement.publishedAt
            announcementIcon.image = getIconImage(announcementType: announcement.type)
            title.text = announcement.title
            content.text = announcement.content
        }
    }
    
    func getIconImage(announcementType: String) -> UIImage{
        switch announcementType {
        case "notification":
            return #imageLiteral(resourceName: "annnoucement")
        case "alert":
            return #imageLiteral(resourceName: "alert")
        case "feedback":
            return #imageLiteral(resourceName: "feedback")
        default:
            return #imageLiteral(resourceName: "annnoucement")
        }
    }
}
