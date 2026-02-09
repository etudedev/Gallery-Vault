# PROJECT_GUIDELINES.md  
**Secure Gallery Vault – Open-Source Android App**  
*(Living reference document – last major update: February 2026)*

This document serves as the canonical guide for the project. It defines goals, decisions, tech choices, security posture, and UX priorities. Refer back to this file for consistency.

## 1. Project Overview

- **Name**: Secure Gallery Vault (working title)
- **Type**: Open-source, completely offline Android gallery vault app
- **Core Philosophy**:
  - 100% offline – no internet, no cloud sync, no telemetry
  - Strong, auditable encryption
  - Minimal permissions
  - Excellent, low-friction user experience (especially import/export & vault switching)
  - Free and open-source forever
- **Target users**: Privacy-conscious people who want real encryption (not just file hiding/renaming)
- **Compatibility**: Android 13+ (API level 33+), target latest stable API

## 2. License

- **MIT License** (permissive, contributor-friendly)

## 3. Tech Stack

- Language: **Kotlin** (100%)
- IDE: Android Studio (latest stable)
- UI: **Jetpack Compose**
- Architecture: MVVM + modular + feature-based organization
- Minimum/Target SDK: API 33+ (target latest)
- Key libraries:
  - Jetpack: Compose, Navigation Compose, ViewModel, Lifecycle, Room, Activity Result API
  - Security: AndroidX Security Crypto, Android Keystore
  - Images: Coil
  - Async: Kotlin Coroutines + Flow
  - Biometrics: androidx.biometric:biometric

## 4. Folder Structure (Fixed – Use This)
app/
├── src/main/java/com/example/vault/      ← Root package (to be decided)
│   ├── MainActivity.kt
│   ├── VaultApplication.kt
│   ├── core/                             ← Shared utilities
│   │   ├── common/
│   │   ├── di/
│   │   ├── security/
│   │   ├── ui/theme/
│   │   └── util/
│   ├── data/                             ← Data layer (Room, repositories)
│   ├── domain/                           ← Models & use cases (optional for small scope)
│   ├── features/                         ← Feature-based screens
│   │   ├── auth/
│   │   ├── vault_list/                   ← Vault management / switcher
│   │   ├── vault_detail/                 ← Inside active vault
│   │   ├── settings/
│   │   └── import_export/                ← Dedicated for import/export flows
│   └── navigation/
└── res/

## 5. Encryption & Security (unchanged)

- Algorithm: **ChaCha20-Poly1305** (256-bit key)
- Key derivation: Argon2id (preferred) or PBKDF2-HMAC-SHA512
- Master keys: Wrapped in **Android Keystore**
- Each file + thumbnail encrypted individually
- Thumbnails: Encrypted (same scheme), decrypted in memory only for display
- No plaintext thumbnails or unencrypted hashes

## 6. Authentication & Lock Behavior

- **PIN requirement rules**:
  - **Creating a new vault** → PIN only (fingerprint/biometric NOT allowed at this step)
  - **Importing a vault** (from encrypted backup) → PIN only (fingerprint NOT allowed at this step)
  - **Opening the app** (first unlock after launch) → Biometric (fingerprint/face) preferred + PIN fallback
  - **Switching to an already imported/existing vault** → Biometric (fingerprint/face) preferred + PIN fallback
- Primary unlock method (after initial setup): BiometricPrompt (fingerprint/face – strong Class 3 preferred)
- Fallback: Minimum 6-digit PIN (enforce basic complexity)
- **Default lock**: Vault stays unlocked until screen turns off (lock button or timeout)
- Session persists across app background/resume (via Keystore validity duration)
- Configurable shorter timeouts in settings

## 7. Import / Export (UX Priority)

### Import
- Primary: **Android Photo Picker** (no persistent permissions for selected media)
- Fallback: Temporary READ_MEDIA_* only if absolutely needed

### Export – High Priority Feature
- Goal: Return files to **original location/folder/filename** as seamlessly as possible
- Capture original metadata during import
- Use Storage Access Framework (SAF) to suggest/restore original path
- Fallback: App-specific external storage or SAF picker if original unavailable
- This will be tackled with high priority

## 8. UI & Navigation Decisions

### General
- Dark mode by default (system-following); light/other themes low priority
- Use **ModalNavigationDrawer** (or Permanent on larger screens) for sidebar

### Vault Management / Switching
- Located in the **bottom section** of the hamburger sidebar (Navigation Drawer)
- **Switcher flow**:
  1. Open drawer (hamburger icon)
  2. Bottom part shows dynamic list of vaults
     - Each vault entry shows name (+ optional icon/color)
     - List includes all existing vaults
     - At bottom/bottom-adjacent: "Create vault" and "Import vault" actions/buttons
  3. Tap any vault → navigate to authentication screen
     - For already imported/existing vaults: Biometric (fingerprint/face) preferred + PIN fallback
  4. Successful auth → switch to that vault's content (reload vault_detail screen)

### Create / Import Vault
- Separate full page (not inline in drawer) with back button in header
- **Create vault flow**:
  - Fields: Vault name (required), Description (optional), PIN setup (with confirmation)
  - Create button
  - Authentication: PIN only (no biometric option during creation)
- **Import vault flow**:
  - Divider + **Import vault** button (uses Android built-in file/document picker for encrypted backup file)
  - After file selection: PIN entry screen (PIN only – no biometric during import)
  - Successful PIN → decrypt/validate backup → add as new vault
- After create/import success → return to drawer or main screen + auto-switch to new vault

### Main Screen Layout (Vault Detail)
- Top: App bar / header (with hamburger icon to open drawer)
- Just below header: Small breadcrumb / current path indicator
  - Format: `VaultName / folder1 / folder2 / …`
  - Shows current directory location inside the active vault
  - Tap-able segments to navigate up (optional, decide later)
- Below breadcrumb: Main content (folders list + media grid of current directory)

## 9. Features Roadmap

### MVP
- Vault creation (PIN only) & import (PIN only)
- Multi-vault support with drawer-based switching (biometric preferred for existing vaults)
- Current path breadcrumb on vault detail screen
- Import via Photo Picker
- Folder organization inside vault
- Encrypted thumbnails in grid
- View decrypted media (in-memory streams)
- Export with original-location priority
- Delete from vault
- Dark mode default

### Post-MVP
- Vault encrypted backup & restore (requires original PIN)
- Search inside vault
- Video playback
- More auto-lock options
- Vault icons/colors
- Export UX refinements

## 10. Distribution

- GitHub Releases (APK)
- F-Droid (priority)
- Optional: Google Play (if compliant)
- Offline app – updates via store or sideload

## 11. Development Notes

- Heavy unit testing for encryption/decryption
- Test on real Android 13+ devices
- Document threat model in README
- Encourage community audits

---

This document is the single source of truth.  
Future changes will be proposed and folded in explicitly.