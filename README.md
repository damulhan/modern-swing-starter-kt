# Modern Swing Starter (Kotlin Edition)

Java Swing + FlatLaf + Kotlin Flow/Coroutines 기반의 현대적인 데스크톱 GUI 아키텍처 스타터 템플릿입니다.

> 💡 **Java 버전**: [damulhan/modern-swing-starter](https://github.com/damulhan/modern-swing-starter) (Java + JGoodies/FlatLaf 기반)

**Kotlin Data Class**, **StateFlow**, **kotlinx-coroutines-swing** (`Dispatchers.Swing`)을 적용했습니다.

## 핵심 기능 및 구성
- Kotlin 2.0.20 + JDK 17
- FlatLaf (Light/Dark 테마 스위처)
- Kotlin StateFlow 기반의 MVVM 상태 관리 및 Swing EDT 바인딩 (`bindToSwing`)
- Gradle Kotlin DSL (`build.gradle.kts`) + ShadowJar 지원

## 프로젝트 구조
- `com.example.swingapp.model`: Models.kt (Customer, Order)
- `com.example.swingapp.service`: CustomerService.kt
- `com.example.swingapp.viewmodel`: SwingBinding.kt, CustomerViewModel.kt, DashboardViewModel.kt
- `com.example.swingapp.view`: MainFrame.kt, DashboardPanel.kt, CustomerPanel.kt, SettingsPanel.kt
- `com.example.swingapp`: Application.kt

## License
[MIT License](./LICENSE)