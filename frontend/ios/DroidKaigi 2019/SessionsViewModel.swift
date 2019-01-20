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
    
    struct Input {
        let viewDidLoad: Driver<Void>
    }
    
    // MARK: Output
    let titles: Observable<[String]>

    // MARK: Output
    let error: Observable<String?>

    // MARK: Self state
    private let _titles = BehaviorRelay<[String]>(value: [])

    // MARK: Self state
    private let _error = BehaviorRelay<String?>(value: nil)

    private let disposeBag = DisposeBag()
    private let repository =  SessionRepository()
    
    init(input: Input) {
        titles = _titles.asObservable()
        error = _error.asObservable()

        input.viewDidLoad
            .asObservable()
            .flatMap { [weak self] (_: Void) -> Observable<SessionContents> in
                self?.repository.fetch().asObservable().catchError { err in
                        self?._error.accept(err.localizedDescription)
                        return .empty()
                    } ?? .empty()
            }
            .map { $0.sessions }
            .map { sessions in
                return sessions.map {
                    let title: LocaledString?
                    switch $0 {
                    case is ServiceSession:
                        title = ($0 as! ServiceSession).title
                    case is SpeechSession:
                        title = ($0 as! SpeechSession).title
                    default:
                        title = nil
                    }
                    return title?.getByLang(lang: LangKt.defaultLang())
                    }.compactMap { $0 }
            }
            .bind(to: _titles)
            .disposed(by: disposeBag)
    }
}
