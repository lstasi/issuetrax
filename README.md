# Issuetrax

A Kotlin mobile application for focused GitHub issue management and Pull Request reviews with innovative gesture-based navigation and simplified single-repository workflow.

## 🚀 Overview

Issuetrax is designed to revolutionize mobile code reviews by providing a clean, gesture-driven interface that makes reviewing Pull Requests on mobile devices intuitive and efficient. The app operates on a **simplified workflow model**: one repository, one issue/PR at a time, with primary focus on creating the best possible PR review experience using **inline diff views optimized for mobile screens**.

## ✨ Key Features

- **Simplified Workflow**: One repository, one issue/PR focus at a time
- **Gesture-Based Navigation**: Revolutionary swipe controls for PR navigation
  - Swipe left/right: Navigate between files
  - Swipe up/down: Navigate between code hunks
  - Double tap: Expand/collapse code sections
  - Long press: Quick comment mode

- **Mobile-Optimized PR Reviews**: Inline diff views designed for mobile screens
- **GitHub Integration**: Seamless OAuth authentication and full API integration
- **Source Code Browser**: Navigate repository files beyond the current PR
- **Clean Architecture**: MVVM with Clean Architecture principles

## 📱 Screens

1. **Login & Repository Selection**: Combined GitHub OAuth authentication and single repository selection
2. **Current Work**: Display active issue/PR or provide simple creation interface
3. **PR Reviews**: Advanced gesture-based review interface with inline diff viewing (primary focus)

## 🏗️ Architecture

Built with modern Android development practices:

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Storage**: Room + DataStore
- **Authentication**: GitHub OAuth 2.0

## 📚 Documentation

This repository contains comprehensive definition documents for the Issuetrax project:

### 📋 Core Documents

- **[PROJECT_DEFINITION.md](PROJECT_DEFINITION.md)**: Complete project overview, requirements, and implementation roadmap
- **[ARCHITECTURE.md](ARCHITECTURE.md)**: Technical architecture, patterns, and implementation details
- **[UI_UX_DESIGN.md](UI_UX_DESIGN.md)**: Detailed UI/UX specifications with gesture system design
- **[PROJECT_SETUP.md](PROJECT_SETUP.md)**: Step-by-step development environment and project setup guide

### 🎯 Quick Navigation

| Document | Purpose | Target Audience |
|----------|---------|-----------------|
| [Project Definition](PROJECT_DEFINITION.md) | High-level overview and requirements | Product Managers, Stakeholders |
| [Architecture](ARCHITECTURE.md) | Technical implementation details | Developers, Architects |
| [UI/UX Design](UI_UX_DESIGN.md) | Design specifications and user experience | Designers, Frontend Developers |
| [Project Setup](PROJECT_SETUP.md) | Development environment setup | Developers, DevOps |

## 🎨 Design Highlights

### Gesture Navigation System
The core innovation of Issuetrax is its gesture-based navigation system optimized for mobile PR reviews:

```
Horizontal Gestures:
← → Navigate between PR files

Vertical Gestures:
↑ ↓ Navigate between code hunks

Special Gestures:
⚫⚫ Double tap to expand/collapse sections
👆 Long press for quick comments
```

### PR Review Interface
- **Inline diff view** optimized for mobile screens
- **Syntax highlighting** for various programming languages
- **Inline commenting** with suggestion support
- **Review submission** with approval workflow
- **File tree navigation** with quick search
- **Source code browser** for additional context

## 🛠️ Development Status

This repository currently contains the **definition and design phase** documentation. The implementation phase will follow the roadmap outlined in the project definition document.

### Development Phases

- [x] **Phase 0**: Requirements analysis and definition documents (simplified workflow)
- [ ] **Phase 1**: Foundation and GitHub OAuth implementation with repository selection
- [ ] **Phase 2**: Core screens (Auth, Current Work screen)
- [ ] **Phase 3**: PR Review core functionality with inline diff viewer
- [ ] **Phase 4**: Advanced gesture navigation system
- [ ] **Phase 5**: Polish, optimization, and source code browser
- [ ] **Phase 6**: APK generation and release preparation

## 🎯 Target Output

The final deliverable will be an **installable APK** that provides:
- Native Android performance
- Simplified single-repository workflow
- Gesture-optimized PR review experience with inline diff views
- Offline-capable operation
- Secure GitHub authentication
- Focused issue and PR management

## 🤝 Contributing

This project follows clean architecture principles and emphasizes:
- **Minimal changes**: Surgical, precise modifications
- **Test-driven development**: Comprehensive testing strategy
- **Performance optimization**: 60fps gesture recognition and smooth animations
- **Accessibility**: Full support for screen readers and alternative navigation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related Links

- [GitHub API Documentation](https://docs.github.com/en/rest)
- [Android Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

**Issuetrax** - Revolutionizing mobile code reviews with simplified workflow and gesture-based navigation. 
