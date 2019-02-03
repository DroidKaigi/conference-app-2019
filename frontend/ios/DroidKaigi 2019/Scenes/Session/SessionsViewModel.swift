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

enum SectionType {
    case day1
    case day2
    case myPlan

    var text: String {
        switch self {
        case .day1: return "Day1"
        case .day2: return "Day2"
        case .myPlan: return "MyPlan"
        }
    }
}

final class SessionsViewModel {

    let type: SectionType
    private let sessionRepository = SessionRepository()
    private let favoriteRepository = FavoriteRepository()
    private let bag = DisposeBag()
    init(type: SectionType) {
        self.type = type
    }
    private let _error = BehaviorRelay<String?>(value: nil)
}

extension SessionsViewModel {

    struct Input {
        let viewWillAppear: Observable<Void>
        let topVisibleSession: Observable<Session>
        let toggleFavorite: Observable<Session>
    }

    struct Output {
        let startDayText: Driver<String>
        let sessions: Driver<[SessionByStartTime]>
        let error: Driver<String?>
    }
    
    func transform(input: Input) -> Output {
        input.toggleFavorite
            .subscribe(onNext: { [weak self] in self?.favoriteRepository.toggle(sessionId: $0.id_) })
            .disposed(by: bag)
        
        let startDayText = input.topVisibleSession
            .map { $0.startDayText }
            .distinctUntilChanged()
            .asDriver(onErrorJustReturn: "")
        
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
                    .filter {
                        switch self.type {
                        case .day1: return $0.dayNumber == 1
                        case .day2: return $0.dayNumber == 2
                        case .myPlan: return favoriteSessionIds.contains($0.id_)
                        }
                    }
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
        return Output(startDayText: startDayText, sessions: sessions, error: error)
    }
}
