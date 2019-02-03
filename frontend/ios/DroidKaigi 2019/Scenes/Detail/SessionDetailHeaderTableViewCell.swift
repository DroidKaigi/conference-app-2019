//
//  SessionDetialHeaderTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/25.
//

import UIKit
import SnapKit
import ioscombined

class SessionDetailHeaderTableViewCell: UITableViewCell, Reusable {

    var session: Session? {
        didSet {
            guard let session = session else { return }
            let lang = LangKt.defaultLang()
            switch session {
            case let speechSession as SpeechSession:
                titleLabel.text = speechSession.title.getByLang(lang: lang)
            case let serviceSession as ServiceSession:
                titleLabel.text = serviceSession.title.getByLang(lang: lang)
            default:
                break
            }
            timeAndRoomLabel.text = "\(session.timeInMinutes)min / \(session.room.name)"
            // TODO: Get TimezoneOffset From Device Setting
            let timezoneOffset = KlockDateTimeSpan(years: 0, months: 0, weeks: 0, days: 0, hours: 9, minutes: 0, seconds: 0, milliseconds: 0)
            dateLabel.text = session.timeSummary(lang: lang, timezoneOffset: timezoneOffset)
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupSubviews()
        selectionStyle = .none
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.boldSystemFont(ofSize: 24)
        label.textColor = .black
        label.numberOfLines = 0
        return label
    }()
    private lazy var timeAndRoomLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.boldSystemFont(ofSize: 16)
        label.textColor = .black
        label.numberOfLines = 1
        return label
    }()
    private lazy var iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "watch")?.withRenderingMode(.alwaysTemplate)
        imageView.tintColor = .gray
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()
    private lazy var dateLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = .black
        label.numberOfLines = 1
        return label
    }()

    private func setupSubviews() {
        [titleLabel, timeAndRoomLabel, iconImageView, dateLabel].forEach(contentView.addSubview)
        titleLabel.snp.makeConstraints {
            $0.top.equalToSuperview().inset(30)
            $0.leading.trailing.equalToSuperview().inset(16)
        }
        timeAndRoomLabel.snp.makeConstraints {
            $0.top.equalTo(titleLabel.snp.bottom).offset(5)
            $0.leading.trailing.equalTo(titleLabel)
        }
        iconImageView.snp.makeConstraints {
            $0.top.equalTo(timeAndRoomLabel.snp.bottom).offset(14)
            $0.leading.equalTo(titleLabel)
            $0.width.height.equalTo(20)
            $0.bottom.equalToSuperview().inset(18)
        }
        dateLabel.snp.makeConstraints {
            $0.centerY.equalTo(iconImageView)
            $0.leading.equalTo(iconImageView.snp.trailing).offset(9)
            $0.trailing.equalToSuperview().inset(16)
        }
    }
}
