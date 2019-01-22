//
//  SessionsViewModel.swift
//  DroidKaigi 2019
//
//  Created by Akira Iwaya on 2019/01/14.
//

import Foundation
import ios_combined
import RxSwift
import RxCocoa

final class SessionsViewModel {

    private let repository =  SessionRepository()
    private let bag = DisposeBag()
    init() {}
    private let _error = BehaviorRelay<String?>(value: nil)
}

extension SessionsViewModel {

    struct Input {
        let initTrigger: Observable<Void>
    }

    struct Output {
        let sessions: Driver<[Session]>
        let error: Driver<String?>
    }

    func transform(input: Input) -> Output {
        let sessionContents = input.initTrigger
                .flatMap { (_) -> Observable<SessionContents> in
                    return self.repository.fetch()
                        .asObservable()
                        .catchError { error in
                            self._error.accept(error.localizedDescription)
                            return Observable.empty()
                        }
                }
        let sessions = sessionContents.map { $0.sessions }.asDriver(onErrorJustReturn: [])
        let error = _error.asDriver()
        return Output(sessions: sessions, error: error)
    }
}
