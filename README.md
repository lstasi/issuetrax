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

Built with modern Android development practices and **minimal essential dependencies**:

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Local Storage**: DataStore (preferences only)
- **Authentication**: GitHub OAuth 2.0 with Custom Tabs
- **Target**: Android 14+ (API 34) - No backward compatibility
- **Philosophy**: Simple UI, minimal dependencies, no unnecessary animations

## 📚 Documentation

This repository contains comprehensive definition documents for the Issuetrax project:

### 📋 Core Documents

- **[CLASS_ARCHITECTURE.md](doc/CLASS_ARCHITECTURE.md)**: Complete class reference with all classes, methods, and relationships ⭐ **NEW**
- **[PROJECT_DEFINITION.md](doc/PROJECT_DEFINITION.md)**: Complete project overview, requirements, and implementation roadmap
- **[ARCHITECTURE.md](doc/ARCHITECTURE.md)**: Technical architecture, patterns, and implementation details
- **[UI_UX_DESIGN.md](doc/UI_UX_DESIGN.md)**: Detailed UI/UX specifications with gesture system design
- **[PROJECT_SETUP.md](doc/PROJECT_SETUP.md)**: Step-by-step development environment and project setup guide
- **[copilot-instructions.md](.github/copilot-instructions.md)**: GitHub Copilot configuration and development guidelines

### 🎯 Quick Navigation

| Document | Purpose | Target Audience |
|----------|---------|-----------------|
| [Class Architecture](doc/CLASS_ARCHITECTURE.md) | **Detailed class reference and implementation** | **Developers, Code Reviewers** |
| [Project Definition](doc/PROJECT_DEFINITION.md) | High-level overview and requirements | Product Managers, Stakeholders |
| [Architecture](doc/ARCHITECTURE.md) | Technical implementation details | Developers, Architects |
| [UI/UX Design](doc/UI_UX_DESIGN.md) | Design specifications and user experience | Designers, Frontend Developers |
| [Project Setup](doc/PROJECT_SETUP.md) | Development environment setup | Developers, DevOps |
| [Copilot Instructions](.github/copilot-instructions.md) | AI coding assistant configuration | Developers using GitHub Copilot |

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

Active development is underway with core functionality being implemented.

### Development Phases

- [x] **Phase 0**: Requirements analysis and definition documents (simplified workflow)
- [x] **Phase 1**: Current Work Screen implementation (100% complete)
- [x] **Phase 2**: PR Review Screen - Basic Display (100% complete)
- [x] **Phase 3.1**: Diff Parser implementation (100% complete)
- [x] **Phase 3.2**: Diff Display Components (100% complete)
- [x] **Phase 3.3**: Inline Diff View optimization for mobile (100% complete)
- [x] **Phase 3.4**: Diff Viewer testing and validation (100% complete)
- [ ] **Phase 4**: Advanced gesture navigation system
- [ ] **Phase 5**: Enhanced PR visualization with action toolbar and compact layout
- [ ] **Phase 6**: Syntax highlighting for improved code readability
- [ ] **Phase 7**: Review submission functionality
- [ ] **Phase 8**: Inline comments and advanced features
- [ ] **Phase 9**: Polish, optimization, and APK release preparation

## 🎯 Target Output

The final deliverable will be a **minimal, focused installable APK** that provides:
- Native Android 14+ performance
- Simplified single-repository workflow
- Gesture-optimized PR review experience with inline diff views
- Secure GitHub authentication
- Focused issue and PR management
- **Minimal APK size** with only essential dependencies
- **Simple, clean UI** without unnecessary animations

## 🤝 Contributing

This project follows clean architecture principles and emphasizes:
- **Minimal changes**: Surgical, precise modifications
- **Test-driven development**: Comprehensive testing strategy
- **Performance optimization**: 60fps gesture recognition and smooth animations
- **Accessibility**: Full support for screen readers and alternative navigation

### Pull Request Workflow

When submitting a pull request, please note that workflows require manual approval for first-time contributors and forks for security reasons. See [.github/WORKFLOW_APPROVAL.md](.github/WORKFLOW_APPROVAL.md) for details on the workflow approval process.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related Links

- [GitHub API Documentation](https://docs.github.com/en/rest)
- [Android Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

## 🔐 Signing (release builds)

Release signing uses a keystore file on disk. Provide the keystore via either:

- `SIGNING_KEY_FILE` environment variable (absolute or relative path), or
- Place a file named `signing-key.jks` in the repository root.

Also set these environment variables:

- `KEY_ALIAS`
- `KEY_STORE_PASSWORD`
- `KEY_PASSWORD`

See `doc/KEYSTORE_SETUP.md` for full instructions and security notes.

---

**Issuetrax** - Revolutionizing mobile code reviews with simplified workflow and gesture-based navigation. 
