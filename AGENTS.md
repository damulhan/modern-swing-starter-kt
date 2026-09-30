# AGENTS.md

## 프로젝트 개요
이 프로젝트는 **Kotlin + Java Swing + FlatLaf + Kotlin Flow / Coroutines**를 조합하여 현대적인 룩앤필과 견고한 아키텍처(MVVM 패턴)를 갖춘 데스크톱 GUI 애플리케이션의 모범 사례를 구현한 레퍼런스 스타터 프로젝트입니다.

JGoodies(Forms & Binding) 대신 **Kotlin Coroutines (`kotlinx-coroutines-swing`)** 및 **`StateFlow`**를 활용하여 상태 관리 및 Swing EDT 바인딩을 구현했습니다.

---

## 핵심 기술 제약 및 규칙 (Strict Constraints)
1. **기술 스택**
   - **Kotlin** (JVM 2.0.20 / JDK 17)
   - **Swing** (`java.desktop`)
   - **FlatLaf**: 3.5.4 (Light/Dark 런타임 테마 전환 및 컴포넌트 스타일링)
   - **비동기/상태 관리**: `kotlinx-coroutines-core` & `kotlinx-coroutines-swing` (`Dispatchers.Swing`)
   - **빌드 도구**: Gradle Kotlin DSL (`build.gradle.kts`) + ShadowJar

2. **사용 금지 기술**
   - JGoodies (Forms & Binding 일체 제외)
   - JavaFX, SWT, Compose Desktop
   - Electron, CEF, WebView, Tauri 등 웹 기반 런타임

3. **Swing EDT 준수**
   - 모든 UI 변경 및 StateFlow 반영은 `Flow.bindToSwing(scope)`를 통해 `Dispatchers.Swing`에서 처리합니다.
   - 무거운 작업이나 I/O는 `Dispatchers.IO` 또는 백그라운드 코루틴에서 실행합니다.

---

## 아키텍처 및 디렉토리 구조
```
src/main/kotlin/com/example/swingapp/
├── Application.kt                 # FlatLaf 초기화 및 엔트리포인트
├── model/
│   └── Models.kt                  # Customer, Order 데이터 클래스
├── service/
│   └── CustomerService.kt         # 인메모리 비즈니스 로직 및 샘플 데이터
├── viewmodel/
│   ├── SwingBinding.kt            # Flow.bindToSwing (EDT 안전 바인딩 확장 함수)
│   ├── CustomerViewModel.kt       # 고객 목록/상세 StateFlow 상태 및 액션
│   └── DashboardViewModel.kt      # 대시보드 통계 StateFlow 상태
└── view/
    ├── MainFrame.kt               # 상단바, 사이드바 네비게이션, CardLayout 화면 전환
    ├── DashboardPanel.kt          # KPI 카드 및 최근 주문 테이블
    ├── CustomerPanel.kt           # 마스터-디테일 테이블 및 폼 에디터
    └── SettingsPanel.kt           # FlatLaf 실시간 테마 전환기
```

---

## 빌드 및 실행 명령어
- 애플리케이션 실행: `./gradlew run`
- 팻 JAR 생성: `./gradlew shadowJar`
- 테스트 실행: `./gradlew test`

---

## 라이선스
- **MIT License** ([LICENSE](./LICENSE)) 준수