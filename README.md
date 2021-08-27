# Notable

The obligatory note taking application.

It's nothing fancy and mostly used to automatically share notes between my many devices.

## Stack

For more information about each component take a look at their ReadMe's.

- [Client](/client/) The desktop client using Electron.
- [Server](/server/): The server written in Go.
- [App](/app/): The Android application.

## Features

- (Very) simple account system using token authentication.
- Note CRUD
- The Android application supports offline mode and data sync.
  The desktop client is always assumed to be connected to the internet.
- Notes can be written using [GitHub Flavored Markdown](https://guides.github.com/features/mastering-markdown/#GitHub-flavored-markdown).
- Tests for the server and app.

## Goals

The ultimate goal is a complete Kotlin powered [KMP](https://kotlinlang.org/docs/multiplatform.html) stack using [Ktor](https://ktor.io/) as both the server and client as well as [JetBrains Compose](https://github.com/JetBrains/compose-jb) for the desktop client once that's more mature. This would allow to share most of the business logic.
