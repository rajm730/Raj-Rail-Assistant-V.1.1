# 🚆 Raju Rail Assistant (Luminous Transit v2.0)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Design](https://img.shields.io/badge/Design-Luminous%20Transit%20Dark%20Glass-cyan.svg)](https://railradar.in)
[![API](https://img.shields.io/badge/Backend-RailRadar%20%26%20RailKit%20API-blue.svg)](https://railradar.in)

**Raju Rail Assistant** is an advanced, dark glassmorphic Indian Railways transit companion built with Google Stitch UI designs and backed by real-time RailRadar / RailKit APIs.

---

## ✨ Key Features

- **🔍 Comprehensive Station Search & Autocomplete:**
  - 200+ major Indian Railway stations with 0ms instant autocomplete.
  - Supports Station Codes (`NDLS`, `KNE`, `PRNA`, `KIR`, `GKP`, `PNBE`, `CNB`, `HWH`, `CSMT`), English, Hindi, and city aliases (e.g., Purnea ➔ PRNA).
- **🚆 Train Search & Live Seat Availability:**
  - Direct trains between stations with duration, halts, and running days.
  - Live seat availability check across classes (`SL`, `3A`, `2A`, `1A`, `CC`, `2S`, `3E`, `EC`) and quotas (`GN`, `TQ`, `PT`, `LD`) with live fare in ₹.
- **📅 Full Train Schedule (शेड्यूल) Modal:**
  - Station-by-station stoppage timetable with arrival, departure, halt minutes, platform number, distance (km), and day.
- **📊 IRCTC COA (Live Charting):**
  - Interactive swipeable coach rake miniature (`WAP-7 • SLR • H1 • A1 • B1 • B2 • S1 • S2`).
  - Click-to-scroll coach highlighting and vacant berth filters.
- **🎫 PNR Status Enquiry:**
  - 10-digit PNR lookup with passenger list, coach, berth, RAC/CNF status, and chart indicator.
- **🗺️ 3D Live Train Location:**
  - 3D isometric train canvas, speedometer (`km/h`), upcoming station flag with platform & arrival time, and live GPS timeline.
- **🎙️ AI Voice Assistant:**
  - Speech recognition supporting natural routes (*"किशनगंज से एनजीपी"*), train status (*"ट्रेन 12004"*), and PNR status (*"PNR 2415896321"*).
  - Multi-engine support (Sarvam AI, Android Native, Google Cloud TTS).
- **🌐 16-in-1 RailRadar Tools Suite:**
  - Interactive tools for all 16 RailRadar / RailKit API endpoints.

---

## 📱 Build & Installation

The pre-built APK is included directly in the repository:
- **[`RajuRailAssistant-v1.0.apk`](RajuRailAssistant-v1.0.apk)** (5.68 MB)

To build from source:
```bash
./gradlew assembleDebug
```
The output APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.
