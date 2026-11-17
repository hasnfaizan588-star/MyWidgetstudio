# My Widget Studio — native Android source, Stage 4 in progress

Existing project's Kotlin/AppWidget source, not a PWA or WebView. No APK has been built or installed here. Android compilation requires JDK 17 and Android SDK 35, unavailable in this sandbox. Source checks are not a phone test.

## Implemented in source
- Digital Clock AppWidget with launcher-managed TextClock (no polling alarms).
- Local single active design, colours, font size, optional date and explicit 12/24-hour time (12-hour includes AM/PM).
- Import bounded to 16 KiB with strict `mws-design` version 1 validation; rejects malformed/unsupported files without saving them.
- Digital Clock web exports only. Minimal Clock and Weather imports are intentionally unsupported rather than silently producing the wrong widget.
- GitHub Actions: JDK/SDK setup, wrapper validation, unit tests, lint, debug APK build, downloadable APK and reports.

## Phone-only build (no Codespaces or paid services required)
1. Download the final source ZIP from Files. Extract it on your phone with your file manager.
2. Create a GitHub repository in your phone's browser. A public repository avoids private-runner quotas if you are comfortable sharing these sources; never upload personal design files or credentials.
3. In the repository choose Add file → Upload files (enable Desktop site if needed). Upload the extracted project CONTENTS, not the ZIP and not an enclosing folder. Root must contain `app/`, `gradle/`, `gradlew`, `build.gradle.kts`, `settings.gradle.kts`. Preserve all subfolders and binary files.
4. If your mobile browser cannot upload folders or hidden files, upload individual files into matching directories using Add file. The essential hidden file is `.github/workflows/build-apk.yml`. Create that exact path with Add file → Create new file and paste its contents from the extracted source. Do not merely upload the ZIP: GitHub does not unpack ZIPs or detect nested workflow files.
5. Commit to `main` or `master`. Open Actions → Build debug APK. Enable Actions if asked. Alternatively tap Run workflow. The build needs network access; GitHub may impose account/usage restrictions. Do not purchase services for this task.
6. Wait for a green run. If red, read the failed step and download Android-check-reports; no APK is promised on failure.
7. Open the successful run's Artifacts → MyWidgetStudio-debug-apk. Download and extract that artifact ZIP. It should contain `app-debug.apk`.
8. Tap the APK. Allow installs from that browser/file manager only when Android prompts, then revoke that permission afterward. This is a debug-signed personal-use build, not a Play Store release. If a prior APK has a different debug signing key, uninstalling may be needed and clears its local settings.

## Transfer a design and verify on your phone
1. Add Digital Clock: long-press your launcher → Widgets → My Widget Studio → Digital Clock.
2. Web app → Designer → Digital Clock: choose distinct colours, size, date and 12/24-hour; Save.
3. Saved Widgets → Export for Android (.json). Keep the download on this phone.
4. Native app → Import design from web app (.json) → choose that JSON. It saves one active design and applies it to ALL existing Digital Clock widgets. If none exists, add one afterward.
5. Check colours, date visibility, size, and time format on the HOME SCREEN; also test with the phone's time preference set opposite to the imported format. Wait through a minute rollover and restart the phone. Reopen the native app to confirm persistence.
6. Resize the widget on your launcher and check clipping. Font sizes are interpreted as Android sp (not identical web pixels); radius is interpreted as dp and rounded to 0/12/24/36/48 buckets. Android 8–11 use square coloured backgrounds; Android 12+ permits rounded tinted backgrounds. Launcher minimum-size limits and font scaling can affect fit.
7. Try an invalid JSON and confirm the previous design remains. Source/unit checks alone do not complete this flow.

## Remaining work
Stages 3, 4 and 6 remain IN PROGRESS until cloud build and actual launcher tests pass. Native weather was deliberately deferred within the credit cap. No location permission or weather network code was added. Multiple separate native saved designs, native Minimal Clock and per-widget designs are not implemented.

## Local or cloud terminal checks
Requires JDK 17, Android SDK platform 35/build tools 35.0.0 and ANDROID_HOME:
`chmod +x gradlew && ./gradlew testDebugUnitTest lintDebug assembleDebug --no-daemon`
Unit tests cover concrete web import values, numeric clamping, invalid files, and clock formats. The CI workflow runs the same checks; no CI success is claimed here.

Doto font is redistributed under SIL Open Font License; see app/src/main/res/font/OFL.txt.
