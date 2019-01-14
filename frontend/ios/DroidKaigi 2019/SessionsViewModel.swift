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
    
    // MARK: Self state
    private let _titles = BehaviorRelay<[String]>(value: [])
    
    private let disposeBag = DisposeBag()
    private let repository =  SessionRepository()
    
    init(input: Input) {
        titles = _titles.asObservable()
        
        input.viewDidLoad
            .asObservable()
            .flatMap { [weak self] (_: Void) -> Observable<SessionContents> in
                self?.repository.fetch().asObservable() ?? .empty()
            }
            .map { $0.sessions }
            .map { sessions in
                return sessions.map {
                    let title: LocaledString?
                    switch $0 {
                    case is Session.ServiceSession:
                        title = ($0 as! Session.ServiceSession).title
                    case is Session.SpeechSession:
                        title = ($0 as! Session.SpeechSession).title
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
