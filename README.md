# League Mobile Challenge

An Android application built with **Clean Architecture** and modern Android development technologies to display posts with user information from a remote API.

## 📱 Project Description

This application demonstrates modern Android development best practices, including clean architecture, dependency injection, secure storage, comprehensive testing, and code quality tools.

### Main Features
- 🔐 **Authentication** with secure token management
- 📝 **Posts Feed** with user avatars and names
- 👤 **User Details** with complete profile
- 💾 **Offline Support** with Room database caching
- 🔒 **Secure Storage** with Tink encryption
- 🎨 **Material Design 3** with dark/light theme support

## 🏗️ Architecture

### Clean Architecture in 3 Layers

```
📱 Presentation Layer (UI)
    ↓
🧠 Domain Layer (Business Logic)
    ↓
💾 Data Layer (Data Sources)
```

#### **Presentation Layer** (`/presentation`)
- **Pattern**: MVI with Jetpack Compose
- **Components**: Screens, ViewModels, UI Models
- **State Management**: StateFlow with unidirectional data flow
- **Navigation**: Type-safe Navigation Compose

#### **Domain Layer** (`/domain`)
- **Pattern**: Use Cases + Repository Pattern
- **Components**: Domain models, Use Cases, Repository interfaces
- **Independent**: No Android Framework dependencies

#### **Data Layer** (`/data`, `/network`, `/database`)
- **Repository Implementations**: Implementation of domain contracts
- **Data Sources**: Remote (API) and local (Room + DataStore) sources
- **Mappers**: Transformation between DTOs/Entities and Domain Models

## 📁 Module Structure

```
LeagueMobileChallenge/
├── app/                    # Main application module
├── presentation/           # UI layer (Compose + ViewModels)
├── domain/                 # Business logic layer
├── data/                   # Repository implementations
├── network/                # HTTP client and APIs
├── database/               # Local persistence and secure storage
├── di/                     # Centralized DI configuration
├── config/                 # Code quality tools configuration
└── scripts/                # Setup scripts
```

### **App Module** 🚀
- **Purpose**: Application entry point
- **Technologies**: Single Activity + Compose, Hilt Application
- **Configuration**: ProGuard, R8, release optimization

### **Presentation Module** 🎨
- **UI Framework**: Jetpack Compose with Material 3
- **State Management**: StateFlow + Compose State
- **Navigation**: Navigation Compose type-safe
- **Image Loading**: Coil with automatic caching
- **Testing**: ViewModel tests with MockK

### **Domain Module** 🧠
- **Use Cases**: `AuthUserCase`, `GetPostsWithUsersUseCase`, `GetUserByIdUseCase`
- **Models**: `UserModel`, `PostModel`, `PostWithUserModel`
- **Interfaces**: Repository contracts without external dependencies
- **Testing**: Comprehensive unit tests with edge cases

### **Data Module** 💾
- **Repository Implementations**: Bridge between domain and data sources
- **Mappers**: DTO ↔ Domain Models conversion
- **Dependencies**: Network + Database modules
- **Testing**: Integration tests with MockK

### **Network Module** 🌐
- **HTTP Client**: Retrofit 3.0.0 + OkHttp 5.1.0
- **Serialization**: Kotlinx Serialization (JSON)
- **APIs**: AuthApi, PostApi, UserApi
- **Configuration**: 30s timeouts, logging interceptor
- **Base URL**: `https://engineering.league.dev/challenge/api/`

### **Database Module** 🗄️
- **ORM**: Room 2.7.2 for SQLite
- **Storage**: DataStore for preferences
- **Security**: Google Tink (AES-256-GCM encryption)
- **Entities**: UserEntity, PostEntity with relationships
- **Features**: Flow-based reactive queries

### **DI Module** 🔗
- **Framework**: Hilt (Dagger) 2.57.1
- **Structure**: Centralized AppModule that includes all modules
- **Scope**: SingletonComponent for shared dependencies

## 🛠️ Technologies and Libraries

### **Core Android**
| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.2.10 | Main programming language |
| Android Gradle Plugin | 8.12.1 | Build system |
| Jetpack Compose | BOM 2024.09.00 | Declarative UI |
| Material 3 | Latest | Design system |

### **Architecture & DI**
| Library | Version | Purpose |
|---------|---------|---------|
| Hilt | 2.57.1 | Dependency injection |
| Navigation Compose | 2.9.3 | Type-safe navigation |
| Lifecycle | 2.9.2 | ViewModels and lifecycle-aware |
| Hilt Navigation Compose | 1.2.0 | Hilt + Navigation integration |

### **Networking**
| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 3.0.0 | REST HTTP client |
| OkHttp | 5.1.0 | HTTP client with interceptors |
| Kotlinx Serialization | 1.9.0 | JSON parsing |

### **Database & Storage**
| Library | Version | Purpose |
|---------|---------|---------|
| Room | 2.7.2 | SQLite ORM |
| DataStore | 1.1.1 | Preferences storage |
| Google Tink | 1.16.0 | AES-256-GCM encryption |

### **UI & Media**
| Library | Version | Purpose |
|---------|---------|---------|
| Coil | 3.3.0 | Async image loading |
| Google Fonts | 1.9.0 | Google Fonts typography |

### **Testing**
| Library | Version | Purpose |
|---------|---------|---------|
| JUnit | 4.13.2 | Base testing framework |
| MockK | 1.13.14 | Kotlin mocking |
| Coroutines Test | 1.9.0 | Coroutines testing |

### **Code Quality**
| Tool | Version | Purpose |
|------|---------|---------|
| Detekt | 1.23.8 | Static code analysis |
| KtLint | 13.1.0 | Code formatting |
| Pre-commit | Latest | Validation hooks |

## 🚀 Setup and Installation

### **Prerequisites**
- **Android Studio**: Flamingo 2022.2.1 or higher
- **JDK**: 17 or higher
- **Android SDK**: API 26-36
- **Git**: To clone the repository

### **Installation**

1. **Clone the repository**
```bash
git clone <repository-url>
cd LeagueMobileChallenge
```

2. **Setup pre-commit hooks** (Recommended)
```bash
./scripts/setup-pre-commit.sh
```

3. **Sync the project**
```bash
./gradlew sync
```

4. **Run tests**
```bash
./gradlew test
```

5. **Build the application**
```bash
./gradlew build
```

## 🔧 Special Configurations

### **Pre-commit Hooks**
Automatic validations before each commit:
- ✅ **Unit Tests**: All tests must pass
- ✅ **KtLint Check**: Code must follow formatting standards
- ✅ **Detekt**: Static analysis must pass

### **Quality Tools**
- **Detekt**: Custom configuration in `config/detekt/detekt.yml`
- **KtLint**: Version 1.5.0 with Android configuration
- **Combined Task**: `qualityCheck` runs ktlint + detekt

### **Build Configuration**
- **Multi-module setup** with version catalogs
- **ProGuard/R8** optimization for release
- **Kotlin DSL** for build scripts
- **Consistent versioning** through `libs.versions.toml`

### **Security**
- **Token Authentication**: Automatic Authorization headers
- **Encrypted Storage**: Tokens encrypted with Tink
- **Secure HTTP**: SSL/TLS certificates
- **ProGuard Rules**: Obfuscation for release

## 🧪 Testing Strategy

### **Test Coverage**
- ✅ **Unit Tests**: Use Cases, ViewModels, Repositories (98 tests)
- ✅ **Integration Tests**: API calls, Database operations
- ✅ **UI Tests**: Compose screens and navigation
- ✅ **Edge Cases**: Error handling, empty states, loading states

## 📖 Technical Documentation

### **API Endpoints**
- **Base URL**: `https://engineering.league.dev/challenge/api/`
- **Authentication**: `/auth` - POST with username/password
- **Posts**: `/posts` - GET with Authorization header  
- **Users**: `/users` - GET individual user data

### **Database Schema**
- **Users Table**: Complete user information
- **Posts Table**: Posts with user relationships
- **Encrypted Storage**: Tokens and sensitive data

### **Navigation**
```kotlin
// Available routes
Screen.Splash -> Screen.Login -> Screen.Home -> Screen.Detail
```

## 📝 Additional Notes

### **Performance**
- **LazyColumn** for large lists
- **Image caching** with Coil
- **Database indexing** in Room
- **ProGuard optimization** for release

### **Monitoring**
- **Logging** with interceptors
- **Error tracking** ready for Crashlytics
- **Performance** metrics ready

---

**Built with** ❤️ **using Android Modern Development Stack**
