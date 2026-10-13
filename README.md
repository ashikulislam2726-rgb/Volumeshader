# Volume Shader — Neon Gaming Edition

Native Android app prototype with music volume control, in-app brightness, a floating neon overlay, and system permission flows.

## Build the APK
1. Open the **Actions** tab in this repository.
2. Choose **Build Volume Shader APK** and press **Run workflow**.
3. When it succeeds, open the run and download the `VolumeShader-debug-apk` artifact.
4. Extract `app-debug.apk` and install it on your Android phone.

The workflow builds an unsigned debug APK for testing. Android may ask you to allow installs from your browser or file manager.

## Permissions
- Display over other apps: required for the floating panel.
- Modify system settings: required for the overlay to change system brightness.
- No root access is required.

This app provides its own floating panel; it does not replace the manufacturer's system volume panel.