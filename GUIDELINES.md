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
  - Excellent, low-friction user experience (especially import/export)
  - Free and open-source forever
- **Target users**: Privacy-conscious people who want real encryption (not just file hiding/renaming)
- **Compatibility**: Android 13+ (API level 33+), target latest stable API

## 2. License

- **MIT License** (permissive, contributor-friendly)

## 3. Tech Stack

- Language: **Kotlin** (100%)
- IDE: Android Studio (latest stable)
- UI: **Jetpack Compose**
- Architecture: MVVM + modular
- Minimum/Target SDK: API 33+ (target latest)
- Key libraries:
  - Jetpack: Compose, Navigation Compose, ViewModel, Lifecycle, Room, Activity Result API
  - Security: AndroidX Security Crypto, Android Keystore
  - Images: Coil
  - Async: Kotlin Coroutines + Flow
  - Biometrics: androidx.biometric:biometric

## 4. Encryption & Security

### Algorithm
- **ChaCha20-Poly1305** (256-bit key)
  - Reasons: excellent software performance, battery efficiency, constant-time safety, large nonce space, mobile-optimized

### Key Derivation
- Argon2id (preferred) or PBKDF2-HMAC-SHA512 (high iterations + unique per-vault salt)
- Master keys stored/wrapped in **Android Keystore** (hardware-backed where available)

### File & Thumbnail Encryption
- Each file encrypted individually with ChaCha20-Poly1305
- Random nonce per file (never reused)
- Nonce + Poly1305 tag prepended to ciphertext
- **Thumbnails**: low-res versions generated → encrypted with same algorithm → stored beside main file (e.g. `file_thumb.enc`)
  - Decrypt thumbnails only in memory for grid view
  - **No plaintext thumbnails, no unencrypted hashes** – full encryption required

### Authentication
- Primary: BiometricPrompt (fingerprint/face – strong auth preferred)
- Fallback: Minimum 6-digit PIN (enforce basic complexity)
- Rate limiting + exponential backoff on failed attempts

### Lock Behavior (Default)
- Vault stays **unlocked** until the **screen turns off** (lock button pressed or screen timeout)
- Session persists across app background/resume (using Keystore validity duration)
- Configurable in settings: shorter timeouts (e.g. 5 min, on app background, etc.)

## 5. Import / Export (UX Priority)

### Import
- **Primary**: Android **Photo Picker** (privacy-first, no persistent permissions needed for selected media)
- Supports single/multiple images/videos
- Fallback: only if absolutely necessary (temporary READ_MEDIA_IMAGES/VIDEO)

### Export – High Priority Feature
- Goal: Return files to their **original location/folder/filename** as seamlessly as possible
- Avoid forcing manual selection every time – this is a noticeable UX win
- Planned (to be implemented early):
  - Capture original filename + path hint/metadata during import
  - On export: attempt to suggest/restore original location via Storage Access Framework (SAF)
  - If original location unavailable: create similar structure in app-specific external storage or fallback to SAF picker
- **This feature will be tackled with high priority** – detailed implementation strategy/code to be discussed soon

## 6. Storage & Organization

- Encrypted files + thumbnails stored in app-private internal storage (`context.filesDir`)
- User-created folders mirrored inside vault
- **Multi-vault support**: separate subfolders + independent master keys/salts per vault

## 7. Permissions (Minimal)

- No CAMERA, no unnecessary permissions
- Photo Picker → often zero runtime permissions
- Biometric → USE_BIOMETRIC
- Temporary media read only if Photo Picker fallback needed

## 8. Features Roadmap

### MVP
- Create / unlock vault (biometric + PIN)
- Multi-vault support (basic)
- Import via Photo Picker
- Folder organization
- Encrypted thumbnails in grid
- View decrypted media (in-memory)
- Export with original-location priority
- Delete from vault
- Dark mode by default (system-following; light/other themes low priority)

### Post-MVP
- Vault transfer / encrypted backup & restore (requires original PIN)
- Search inside vault
- Video playback
- More auto-lock options
- Export UX refinements

## 9. Distribution

- GitHub Releases (APK)
- F-Droid (priority for privacy users)
- Optional: Google Play (if compliant)
- Offline app – updates via store or manual sideload

## 10. Development Notes

- Heavy unit testing for encryption/decryption
- Test on real Android 13+ devices (Scoped Storage, Photo Picker, Keystore)
- Document threat model in README
- Encourage community audits & contributions

---

This document is the single source of truth for the project direction.  
Any future changes will be explicitly proposed and folded into updated versions of this file.