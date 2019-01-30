//
//  SessionTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/01/22.
//

import UIKit
import ioscombined
import SnapKit
import MaterialComponents.MDCInkTouchController
import RxCocoa
import RxSwift

class SessionTableViewCell: UITableViewCell, Reusable {

    var session: Session? {
        didSet {
            guard let session = session else { return }
            timeAndRoomLabel.text = "\(session.timeInMinutes)min / \(session.room.name)"
            liveMark.isHidden = !session.isOnGoing
            favoriteButton.isSelected = session.isFavorited
            speakersStackView.isHidden = session is ServiceSession
            collectionView.isHidden = session is ServiceSession
            remakeTimeAndRoomLabelConstraints()
            switch session {
            case let serviceSession as ServiceSession:
                titleLabel.text = serviceSession.title.getByLang(lang: LangKt.defaultLang())
            case let speechSession as SpeechSession:
                titleLabel.text = speechSession.title.getByLang(lang: LangKt.defaultLang())
                speechSession.speakers.forEach { speaker in
                    let cell = SpeakerCell(speaker: speaker)
                    speakersStackView.addArrangedSubview(cell)
                }
                tagContents = speechSession.tagContents
            default:
                return
            }
            collectionView.reloadData()
        }
    }
    
    var favoriteButtonDidTapped: ControlEvent<Void> {
        return favoriteButton.rx.tap
    }

    var bag = DisposeBag()

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func prepareForReuse() {
        super.prepareForReuse()
        speakersStackView.subviews.forEach {
            speakersStackView.removeArrangedSubview($0)
            $0.removeFromSuperview()
        }
        tagContents.removeAll()
        collectionView.reloadData() // write to fix bug
        bag = DisposeBag()
    }

    override func systemLayoutSizeFitting(_ targetSize: CGSize,
                                          withHorizontalFittingPriority horizontalFittingPriority: UILayoutPriority,
                                          verticalFittingPriority: UILayoutPriority) -> CGSize {
        let defaultSize = super.systemLayoutSizeFitting(
                targetSize,
                withHorizontalFittingPriority: horizontalFittingPriority,
                verticalFittingPriority: verticalFittingPriority
        )
        let hash = tagContents.hashValue ^ Int(targetSize.width).hashValue
        if let cachedHeight = SessionCalculateHeightTableViewCell.cachedHeights[hash] {
            return CGSize.init(width: targetSize.width, height: cachedHeight + defaultSize.height)
        }
        struct Static {
            static let cell = SessionCalculateHeightTableViewCell(style: .default, reuseIdentifier: nil)
        }
        let cell = Static.cell
        cell.session = session
        cell.bounds.size = CGSize(width: targetSize.width, height: 0)
        cell.setNeedsLayout()
        cell.layoutIfNeeded()
        cell.collectionView.reloadData()
        cell.collectionView.setNeedsLayout()
        cell.collectionView.layoutIfNeeded()
        cell.collectionView.collectionViewLayout.invalidateLayout()
        let collectionViewHeight = cell.collectionView.collectionViewLayout.collectionViewContentSize.height
        SessionCalculateHeightTableViewCell.cachedHeights[hash] = collectionViewHeight
        return CGSize.init(width: targetSize.width, height: collectionViewHeight + defaultSize.height)
    }

    private var tagContents: [TagContent] = []

    private lazy var inkTouchController: MDCInkTouchController = {
        return MDCInkTouchController(view: self)
    }()

    private lazy var titleStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .horizontal
        stackView.alignment = .center
        stackView.distribution = .fill
        stackView.spacing = 8
        stackView.backgroundColor = .blue
        return stackView
    }()
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
    private lazy var favoriteButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "bookmark_border"), for: .normal)
        button.setImage(UIImage(named: "bookmark"), for: .selected)
        button.setImageTintColor(color: .black, for: .normal)
        button.setImageTintColor(color: UIColor.DK.primary.color, for: .selected)
        return button
    }()
    private lazy var speakersStackView: UIStackView = {
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 4
        stackView.distribution = .equalSpacing
        stackView.alignment = .leading
        return stackView
    }()
    private lazy var timeAndRoomLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 12)
        label.textColor = .darkGray
        label.numberOfLines = 1
        return label
    }()
    private lazy var collectionView: UICollectionView = {
        let layout = TagsLeftAlignedCollectionViewFlowLayout()
        layout.minimumInteritemSpacing = 4
        layout.minimumLineSpacing = 4
        layout.estimatedItemSize = .init(width: 100, height: 30)
        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.dataSource = self
        collectionView.isScrollEnabled = false
        collectionView.backgroundColor = .clear
        collectionView.register(TagsCollectionViewCell.self)
        return collectionView
    }()

    private func setupSubviews() {
        [titleLabel, liveMark, favoriteButton].forEach(titleStackView.addArrangedSubview)
        
        [titleStackView, speakersStackView, timeAndRoomLabel, collectionView].forEach(contentView.addSubview)
        titleStackView.snp.makeConstraints {
            $0.top.equalToSuperview().inset(5)
            $0.leading.equalToSuperview().inset(90)
            $0.trailing.equalToSuperview().inset(16)
        }
        liveMark.snp.makeConstraints {
            $0.width.equalTo(32)
            $0.height.equalTo(16)
        }
        favoriteButton.snp.makeConstraints {
            $0.width.height.equalTo(24)
        }
        speakersStackView.snp.makeConstraints {
            $0.top.equalTo(titleStackView.snp.bottom).offset(8)
            $0.leading.equalTo(titleStackView)
            $0.trailing.equalToSuperview().inset(16)
        }
        remakeTimeAndRoomLabelConstraints()
        collectionView.snp.makeConstraints {
            $0.leading.equalTo(titleStackView)
            $0.trailing.equalToSuperview().inset(16)
            $0.top.equalTo(timeAndRoomLabel.snp.bottom).offset(7)
            $0.bottom.equalToSuperview().inset(26)
        }
        
        inkTouchController.addInkView()
    }

    private func remakeTimeAndRoomLabelConstraints() {
        timeAndRoomLabel.snp.remakeConstraints {
            if session is ServiceSession {
                $0.top.equalTo(titleLabel.snp.bottom).offset(5)
            } else {
                $0.top.equalTo(speakersStackView.snp.bottom).offset(8)
            }
            $0.leading.equalTo(titleLabel)
        }
    }
}

extension SessionTableViewCell: UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if session is SpeechSession {
            return tagContents.count
        }
        return 0
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell: TagsCollectionViewCell = collectionView.dequeueReusableCell(forIndexPath: indexPath)
        cell.tagContent = tagContents[indexPath.item]
        return cell
    }
}

final class SessionCalculateHeightTableViewCell: UITableViewCell {

    static var cachedHeights = [Int: CGFloat]()

    var session: Session? {
        didSet {
            guard let speechSession = session as? SpeechSession else { return }
            tagContents = speechSession.tagContents
            collectionView.reloadData()
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupSubviews()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    private var tagContents: [TagContent] = []

    lazy var collectionView: UICollectionView = {
        let layout = TagsLeftAlignedCollectionViewFlowLayout()
        layout.minimumInteritemSpacing = 4
        layout.minimumLineSpacing = 4
        layout.estimatedItemSize = .init(width: 100, height: 30)
        let collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        collectionView.dataSource = self
        collectionView.delegate = self
        collectionView.isScrollEnabled = false
        collectionView.register(TagsCollectionViewCell.self)
        return collectionView
    }()

    private func setupSubviews() {
        contentView.addSubview(collectionView)
        collectionView.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(90)
            $0.trailing.equalToSuperview().inset(16)
            $0.top.bottom.equalToSuperview()
        }
    }
}

extension SessionCalculateHeightTableViewCell: UICollectionViewDataSource {

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return tagContents.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell: TagsCollectionViewCell = collectionView.dequeueReusableCell(forIndexPath: indexPath)
        cell.tagContent = tagContents[indexPath.item]
        return cell
    }
}

extension SessionCalculateHeightTableViewCell: UICollectionViewDelegateFlowLayout {

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        struct Static {
            static let cell = TagsCollectionViewCell()
        }
        let cell = Static.cell
        cell.tagContent = tagContents[indexPath.item]
        let cellSize = cell.label.intrinsicContentSize
        let width = cellSize.width > collectionView.bounds.size.width - 20 ? collectionView.bounds.size.width - 20 : cellSize.width
        return CGSize(width: width, height: cellSize.height)
    }
}
