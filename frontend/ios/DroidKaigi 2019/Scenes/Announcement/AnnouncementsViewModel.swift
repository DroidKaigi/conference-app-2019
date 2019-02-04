//
//  AnnouncementsViewModel.swift
//  DroidKaigi 2019
//
//  Created by 佐々木美穂 on 2019/02/03.
//

import Foundation
import ioscombined
import RxSwift
import RxCocoa

final class AnnouncementsViewModel {
    
    private let announcementRepository = AnnouncementRepository()
    private let bag = DisposeBag()
    private let _error = BehaviorRelay<String?>(value: nil)
}

extension AnnouncementsViewModel {
    struct Input {
        let viewWillAppear: Observable<Void>
    }

    struct Output {
        let announcements: Driver<[AnnouncementResponse]>
        let error: Driver<String?>
    }

    func transform(input: Input) -> Output {
        let announcementContents = input.viewWillAppear
            .flatMap { [weak self] (_) -> Observable<[AnnouncementResponse]> in
                guard let `self` = self else { return Observable.empty() }
                return self.announcementRepository.fetch()
                .asObservable()
                    .catchError { error in
                        self._error.accept(error.localizedDescription)
                        return Observable.empty()
                }
        }
        
        let announcements = announcementContents
            .asDriver(onErrorJustReturn: [])
        let error = _error.asDriver()
        return Output(announcements: announcements, error: error)
    }
}
