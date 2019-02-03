//
//  DrawerMenuTableViewCell.swift
//  DroidKaigi 2019
//
//  Created by ShoMasegi on 2019/02/03.
//

import UIKit
import SnapKit
import ioscombined
import MaterialComponents.MDCInkTouchController

class DrawerMenuTableViewCell: UITableViewCell, Reusable {

    var menuItem: MenuItem? {
        didSet {
            guard let menuItem = menuItem else { return }
            iconImageView.image = menuItem.icon
            label.text = menuItem.title.getByLang(lang: LangKt.defaultLang())
        }
    }

    override init(style: CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        [iconImageView, label].forEach(contentView.addSubview)
        iconImageView.snp.makeConstraints {
            $0.leading.equalToSuperview().inset(20)
            $0.top.bottom.equalToSuperview().inset(12)
            $0.height.width.equalTo(24)
        }
        label.snp.makeConstraints {
            $0.centerY.equalTo(iconImageView)
            $0.leading.equalTo(iconImageView.snp.trailing).offset(8)
            $0.trailing.equalToSuperview().inset(16)
        }
        inkTouchController.addInkView()
    }
    required init?(coder aDecoder: NSCoder) { fatalError() }

    override func prepareForReuse() {
        super.prepareForReuse()
        iconImageView.image = nil
        label.text = ""
    }

    private lazy var inkTouchController: MDCInkTouchController = {
        return MDCInkTouchController(view: self)
    }()
    private lazy var label: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: 16)
        label.textColor = UIColor.black.withAlphaComponent(0.8)
        label.numberOfLines = 1
        return label
    }()
    private lazy var iconImageView: UIImageView = {
        let imageView = UIImageView()
        imageView.tintColor = .gray
        imageView.contentMode = .scaleAspectFit
        imageView.clipsToBounds = true
        return imageView
    }()
}
