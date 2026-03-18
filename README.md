# Peregerine Android

An Android app for offline inventory management, built with Kotlin + Jetpack Compose.

This README reflects the current codebase and also highlights planned work.

## Project Overview

Peregerine is a local-first inventory app with product catalog, batch-level stock handling, stock ledger tracking, and barcode-assisted lookup.

## Implemented Features (Current)

- Product management: create, edit, activate/deactivate products.
- Opening stock flow on product creation.
- Batch management: add batch, edit batch, activate/deactivate batch.
- Stock adjustment flow (with validation to prevent invalid negative adjustments).
- Product-level stock ledger support and recent activity in product detail.
- Inventory dashboard with counts and filters (`All`, `Low Stock`, `Out of Stock`, `Inactive`, `Not Stocked`).
- Product search and barcode scanner integration.
- Local persistence with Room (SQLite), Paging, and reactive flows.

## Planned Features (Roadmap)

- Complete purchase-order workflow (currently the tab is placeholder UI).
- Full ledger screen/navigation (currently wired as a TODO from product detail).
- Add-to-PO flow from inventory list.
- Settings feature implementation.
- Additional inventory and performance hardening.

## Tech Stack

- Kotlin (JDK 17 target)
- Jetpack Compose (Material 3)
- AndroidX Navigation 3
- Room (SQLite)
- Paging 3
- Kotlin Coroutines + Flow
- CameraX + ML Kit (barcode scanning)
- Gradle Kotlin DSL + Version Catalog

## Architecture (High-Level)

Code is organized by feature with layered boundaries:

- `app/src/main/java/me/yasharya/peregerine/feature_inventory/`
  - `data/` (Room entities, DAO usage, repository impl)
  - `domain/` (models, repository contracts, use cases)
  - `presentation/` (ViewModels + Compose screens)
- `app/src/main/java/me/yasharya/peregerine/feature_purchase_order/`
  - early scaffolding for purchase-order functionality
- `app/src/main/java/me/yasharya/peregerine/core/`
  - shared database, navigation, utilities, and common UI components
- `app/src/main/java/me/yasharya/peregerine/di/AppContainer.kt`
  - manual dependency wiring

## Setup Prerequisites

- Android Studio (latest stable recommended)
- Android SDK installed (project uses `compileSdk = 36`, `targetSdk = 35`, `minSdk = 26`)
- JDK 17
- A connected Android device or emulator

> Note: Barcode scanning uses camera APIs; test on an emulator/device with camera support for that flow.

## Quick Start (Android Studio)

1. Clone the repository.
2. Open the project folder in Android Studio.
3. Let Gradle sync finish.
4. Select an emulator/device.
5. Run the `app` configuration.

## Quick Start (Gradle CLI)

From the repository root:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Optional (run checks):

```powershell
.\gradlew.bat test
```

## Roadmap

Near-term focus:

- Purchase-order end-to-end flow (create, item management, receive stock).
- Full stock-ledger listing and drill-down navigation.
- Settings tab implementation.
