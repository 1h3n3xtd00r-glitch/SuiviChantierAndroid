# Build the APK using only an Android phone

This project includes a GitHub Actions workflow that builds a debug APK in the cloud.

1. On your phone, open https://github.com and sign in or create an account.
2. Create a new repository. For easiest setup, make it **Private** and initialize it without a README.
3. Extract this ZIP using a file manager that supports ZIP archives.
4. Upload the *contents* of the `SuiviChantierAndroid` folder to the repository's root (including the hidden `.github` folder). If the mobile upload interface does not show hidden folders, use GitHub's web editor to create `.github/workflows/build-apk.yml` and paste the workflow from this project.
5. Commit the files to the `main` branch.
6. Open the repository's **Actions** tab, choose **Build Android APK**, then tap **Run workflow** if it has not started automatically.
7. Wait until the run is green, open the completed run, scroll to **Artifacts**, and download `SuiviChantier-debug-apk`.
8. Extract the artifact ZIP and tap `app-debug.apk` to install it. Android may ask you to allow installs from your browser or file manager; only enable this for the app you are using, then turn it off again after installation.

Notes:
- The APK is a debug build for testing, not a Play Store release.
- The app stores data locally on the phone. Uninstalling it may erase its data, so export/backup support should be added before relying on it for important records.
- Cloud build requires internet and a GitHub account. App use is designed to work offline after installation.
