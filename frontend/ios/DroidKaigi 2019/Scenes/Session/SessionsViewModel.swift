//
//  SessionsViewModel.swift
//  DroidKaigi 2019
//
//  Created by Akira Iwaya on 2019/01/14.
//

import Foundation
import ioscombined
import RxSwift
import RxCocoa

final class SessionsViewModel {

    private let sessionRepository = SessionRepository()
    private let favoriteRepository = FavoriteRepository()
    private let bag = DisposeBag()
    private let day: Day
    init(day: Day) {
        self.day = day
    }
    private let _error = BehaviorRelay<String?>(value: nil)
}

extension SessionsViewModel {

    struct Input {
        let viewWillAppear: Observable<Void>
        let toggleFavorite: Observable<Session>
    }

    struct Output {
        let sessions: Driver<[SessionByStartTime]>
        let error: Driver<String?>
    }
    
    func transform(input: Input) -> Output {
        input.toggleFavorite
            .subscribe(onNext: { [weak self] in self?.favoriteRepository.toggle(sessionId: $0.id_) })
            .disposed(by: bag)
        
        let sessionContents = input.viewWillAppear
                .flatMap { [weak self] (_) -> Observable<SessionContents> in
                    guard let `self` = self else { return Observable.empty() }
                    return self.sessionRepository.fetch()
                        .asObservable()
                        .catchError { error in
                            self._error.accept(error.localizedDescription)
                            return Observable.empty()
                        }
                }
        
        let favoriteSessionIds = favoriteRepository.sessionIdsDidChanged
        
        let sessions = Observable.combineLatest(sessionContents, favoriteSessionIds)
            .map { [weak self] (sessionContents, favoriteSessionIds) -> [SessionByStartTime] in
                guard let `self` = self else { return [] }
                return sessionContents.sessions
                    .filter { $0.dayNumber == self.day.day }
                    .map { (session: Session) -> Session in
                        if let session = session as? ServiceSession {
                            return session.doCopy(isFavorited: favoriteSessionIds.contains(session.id_))
                        } else if let session = session as? SpeechSession {
                            return session.doCopy(isFavorited: favoriteSessionIds.contains(session.id_))
                        } else {
                            return session
                        }
                    }
                    .reduce(into: [SessionByStartTime]()) { result, session in
                        if let index = result.firstIndex(where: { $0.startTime == session.startTime }) {
                            result[index].sessions.append(session)
                            return
                        }
                        result.append(SessionByStartTime(startDayText: session.startDayText,
                                                         startTimeText: session.startTimeText,
                                                         startTime: session.startTime,
                                                         sessions: [session]))
                    }
            }
            .asDriver(onErrorJustReturn: [])
        let error = _error.asDriver()
        return Output(sessions: sessions, error: error)
    }
}
