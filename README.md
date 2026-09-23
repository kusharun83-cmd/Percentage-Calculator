# InvestCalc

A simple Android app (Kotlin + Jetpack Compose) with two calculators:

1. **Return on %** — enter capital invested and expected return %, get the return amount and total value.
2. **Profit / Loss** — enter invested value and current value, get profit/loss amount and profit/loss %.

## Get the APK without installing Android Studio (recommended)

This project includes a GitHub Actions workflow that builds the APK automatically.

1. Create a new repository on GitHub (public or private).
2. Upload/push everything in this folder to that repository, keeping the folder structure exactly as is (including the hidden `.github` folder).
   - Easiest way on phone/web: on your new repo page, use "Add file → Upload files", drag this whole folder's contents in, and commit to the `main` branch.
   - Or from a computer:
     ```
     git init
     git add .
     git commit -m "Initial commit"
     git branch -M main
     git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
     git push -u origin main
     ```
3. On GitHub, open the **Actions** tab of your repo. A workflow called "Build APK" will run automatically on push (takes a couple of minutes).
4. Once it finishes (green check), open that workflow run and scroll to **Artifacts** — download `InvestCalc-debug-apk`. Unzip it to get `app-debug.apk`.
5. Transfer `app-debug.apk` to your Android phone and open it to install (you'll need to allow "install unknown apps" for that source in Android settings).

## Build locally instead (if you have Android Studio)

1. Open Android Studio → Open → select this project folder.
2. Let Gradle sync.
3. Run ▶ on an emulator or your connected phone, or Build → Build APK(s).

## Notes / next steps

- This is a debug build for personal use, not signed for the Play Store.
- Easy things to add later: a CAGR / annualized-return calculator, a SIP calculator, saving past calculations.
