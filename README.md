# 🏝️ Atomix Island

> The most advanced Dynamic Island ever built for Android.
> Apple-level quality. Premium glassmorphism. 120 FPS animations.

---

## Overview

Atomix Island is a fully featured, production-ready Android Dynamic Island implementation built with Jetpack Compose, MVVM architecture, and a complete overlay service stack. It runs as a persistent foreground service and reacts to every meaningful system event with premium animations.

---

## Features

### Island States
| State | Description |
|---|---|
| `Compact` | Minimal pill — camera dot indicator |
| `Mini` | Small pill with icon + label |
| `Expanded` | Standard bar (music, calls, nav) |
| `FullIsland` | Large card (incoming call, assistant) |
| `SplitIsland` | Two events side by side |
| `DualIsland` | Two separate floating pills |

### Events Supported
- 🎵 Music playback (title, artist, artwork, progress, controls)
- 📞 Incoming / active / missed calls with caller photo
- 🤖 Voice assistants (Idle → Listening → Thinking → Responding)
- 🔔 Notifications (WhatsApp, Telegram, Discord, SMS, Instagram)
- 🔋 Battery (charging, low, full)
- 🔊 Volume changes (per stream type)
- 🌐 WiFi connect/disconnect
- 🎧 Bluetooth / headphone events
- 📥 Download progress
- 🗺️ Turn-by-turn navigation
- 📸 Screenshot / screen recording
- 📷 Camera / microphone active indicators
- ⏰ Alarms, timers
- 📦 App installs / updates

### Animations
- **Spring-based** size morphing (dampingRatio: 0.72, stiffness: 500)
- **Liquid morphing** shape transitions with mid-transit wobble
- **Breathing pulse** for active states
- **Ripple rings** for voice listening
- **Equalizer bars** for music
- **Waveform visualizer** for voice assistant
- **Particle system** for ambient glow
- **60–120 FPS** via Compose's hardware-accelerated renderer

---

## Architecture

```
AtomixIsland/
├── app/src/main/java/com/atomix/island/
│   ├── AtomixApp.kt                    # Application + notification channels
│   ├── di/                             # Hilt DI modules
│   │   └── AppModule.kt
│   ├── ui/
│   │   ├── components/
│   │   │   ├── IslandState.kt          # All states + event sealed classes
│   │   │   ├── IslandComposable.kt     # Main AtomixIsland() composable
│   │   │   └── IslandShape.kt          # Glass modifiers, glow, morph
│   │   ├── widgets/
│   │   │   ├── MusicIsland.kt          # Music player widget
│   │   │   ├── CallIsland.kt           # Incoming / active call
│   │   │   ├── VoiceAssistantIsland.kt # AI assistant + waveform
│   │   │   ├── NotificationIsland.kt   # Notification + system events
│   │   │   ├── MultiWidget.kt          # Clock, Calendar, Weather, AI, Notes
│   │   │   └── SystemMonitorWidget.kt  # CPU/RAM/Battery arc gauges
│   │   ├── screens/
│   │   │   └── PermissionActivity.kt   # Permission setup flow
│   │   └── theme/
│   │       └── Theme.kt                # Colors, typography, design tokens
│   ├── services/
│   │   ├── IslandOverlayService.kt     # Core floating window + Compose
│   │   ├── AtomixNotificationService.kt# Notification listener
│   │   ├── AtomixMediaService.kt       # MediaSession API watcher
│   │   ├── PhoneStateService.kt        # Call state monitor + timer
│   │   └── SystemEventReceiver.kt      # Battery/WiFi/BT/Volume receiver
│   ├── viewmodels/
│   │   └── IslandViewModel.kt          # State orchestration + event routing
│   ├── settings/
│   │   ├── SettingsActivity.kt         # Settings app entry
│   │   ├── SettingsViewModel.kt        # Settings state + preview
│   │   └── SettingsScreen.kt           # Full settings UI with live preview
│   ├── animations/
│   │   ├── IslandAnimations.kt         # Spring specs, breathing, wave
│   │   ├── LiquidMorphTransition.kt    # Squircle morph engine
│   │   └── ParticleEffect.kt           # Particle + sparkle + glow ring
│   └── utils/
│       ├── IslandPreferences.kt        # DataStore settings model
│       ├── IslandEventMapper.kt        # System API → IslandEvent
│       ├── IslandGestureHandler.kt     # Tap/swipe gesture state machine
│       ├── WallpaperColorExtractor.kt  # Dynamic accent from wallpaper
│       └── AdaptiveLayout.kt           # Phone/tablet/foldable sizing
├── app/src/main/res/
│   ├── drawable/
│   │   ├── ic_launcher.xml
│   │   └── ic_island_notification.xml
│   ├── values/
│   │   ├── themes.xml
│   │   └── strings.xml
│   └── xml/
│       └── accessibility_service_config.xml
└── app/src/main/assets/svg/           # 18 original SVG icon assets
    ├── ic_battery.svg  ic_music.svg   ic_ai.svg
    ├── ic_phone.svg    ic_wifi.svg    ic_bluetooth.svg
    ├── ic_microphone.svg ic_camera.svg ic_download.svg
    ├── ic_upload.svg   ic_clock.svg   ic_calendar.svg
    ├── ic_storage.svg  ic_cpu.svg     ic_ram.svg
    ├── ic_performance.svg ic_settings.svg ic_gamepad.svg
    ├── ic_flashlight.svg ic_messages.svg
    └── ...
```

---

## Setup

### 1. Clone / Open in Android Studio
```
Android Studio Hedgehog or later
AGP 8.5 · Kotlin 2.0 · minSdk 31
```

### 2. Build
```bash
./gradlew assembleDebug
```

### 3. Install
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 4. Grant Permissions
Open **Atomix Island Settings** → tap:
- **Grant Overlay Permission** — required for the floating island
- **Grant Notification Access** — for notification mirroring
- **Start Atomix Island**

---

## Customization

All settings are persisted via **DataStore** and applied live:

| Setting | Range | Default |
|---|---|---|
| Island Width | 100 – 360 dp | 340 dp |
| Island Height | 30 – 160 dp | 82 dp |
| Corner Radius | 0 – 60 dp | 44 dp |
| Glow Intensity | 0 – 100% | 35% |
| Animation Speed | 0.5× – 2× | 1× |
| Accent Color | 6 presets + wallpaper | Electric Blue |
| Auto Hide Delay | 1 – 10 s | 5 s |

---

## Color System

```kotlin
PureBlack    = #000000   // Island background
DeepSpace    = #0D1117   // Settings / cards
Graphite     = #1C1F26   // Secondary surfaces
ElectricBlue = #00A3FF   // Primary accent
PurpleGlow   = #7B61FF   // AI / assistant
MintGreen    = #00E5A0   // Calls / battery OK
GoldenAmber  = #FFB800   // Charging / warnings
RoseRed      = #FF3B5C   // Decline / error
SunriseOrange= #FF6B35   // Music
```

---

## Android Version Support

| Version | API | Status |
|---|---|---|
| Android 12 | 31 | ✅ Full support |
| Android 13 | 33 | ✅ Full support |
| Android 14 | 34 | ✅ Full support |
| Android 15 | 35 | ✅ Full support |
| Android 16+ | 36+ | ✅ Forward compatible |

Adaptive layout for:
- Phones (portrait + landscape)
- Tablets (≥ 600 dp)
- Foldables (≥ 840 dp unfolded)

---

## License

MIT © Atomix Island
