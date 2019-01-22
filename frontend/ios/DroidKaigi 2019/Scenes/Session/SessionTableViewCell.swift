//
//  SessionTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit
import ios_combined
import SnapKit

class SessionTableViewCell: UITableViewCell, Reusable {

    var session: Session? {
        didSet {
            guard let session = session else { return }
            timeAndRoomLabel.text = "\(session.timeInMinutes)min / \(session.room.name)"
            liveMark.isHidden = !session.isOnGoing
            speakersStackView.isHidden = session is ServiceSession
            remakeTimeAndRoomLabelConstraints()
            switch session {
            case let serviceSession as ServiceSession:
                titleLabel.text = serviceSession.title.getByLang(lang: LangKt.defaultLang())
            case let speechSession as SpeechSession:
                titleLabel.text = speechSession.title.getByLang(lang: LangKt.defaultLang())
            default:
                return
            }
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16, weight: .bold)
        label.textColor = .black
        label.numberOfLines = 0
        return label
    }()
    private lazy var liveMark: UILabel = {
        let label = UILabel()
        label.text = "LIVE"
        label.font = UIFont.systemFont(ofSize: 10, weight: .medium)
        label.textColor = .white
        label.backgroundColor = UIColor.DK.primary.color
        label.layer.cornerRadius = 3
        label.clipsToBounds = true
        label.textAlignment = .center
        return label
    }()
    private lazy var speakersStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        return stackView
    }()
    private lazy var timeAndRoomLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 12)
        label.textColor = .darkGray
        label.numberOfLines = 1
        return label
    }()

    private func setupSubviews() {
        [titleLabel, liveMark, speakersStackView, timeAndRoomLabel].forEach(contentView.addSubview)
        titleLabel.snp.makeConstraints {
            $0.top.equalToSuperview().inset(5)
            $0.leading.equalToSuperview().inset(90)
            $0.trailing.equalTo(liveMark.snp.leading).offset(-4)
        }
        liveMark.snp.makeConstraints {
            $0.trailing.equalToSuperview().inset(16)
            $0.top.equalTo(titleLabel)
            $0.width.equalTo(32)
            $0.height.equalTo(16)
        }
        speakersStackView.snp.makeConstraints {
            $0.top.equalTo(titleLabel.snp.bottom).offset(8)
            $0.leading.equalTo(titleLabel)
        }
        remakeTimeAndRoomLabelConstraints()
    }

    private func remakeTimeAndRoomLabelConstraints() {
        timeAndRoomLabel.snp.remakeConstraints {
            if session is ServiceSession {
                $0.top.equalTo(titleLabel.snp.bottom).offset(5)
            } else {
                $0.top.equalTo(speakersStackView.snp.bottom).offset(8)
            }
            $0.leading.equalTo(titleLabel)
            $0.bottom.equalToSuperview().inset(26)
        }
    }
}
