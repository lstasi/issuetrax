# Keystore Setup for Local Builds


This document explains how to configure your local environment to sign release builds using a keystore file located on disk.

## Overview

The project now uses a file-based signing approach. You can provide the keystore file via:

- `SIGNING_KEY_FILE` environment variable (absolute or relative path), or
- Place a file named `signing-key.jks` in the repository root.

Required additional environment variables:

- `KEY_ALIAS` — the key alias inside the keystore
- `KEY_STORE_PASSWORD` — the keystore password
- `KEY_PASSWORD` — the key password

This approach is simpler for local development and straightforward to use in CI: the CI job can place the keystore file at the repo root or set `SIGNING_KEY_FILE`.

## Setup Process

This method matches exactly what GitHub Actions does using the `r0adkll/sign-android-release` action.


### Provide or create a keystore file

If you already have a keystore file, copy it to the repository root and name it `signing-key.jks`, or set `SIGNING_KEY_FILE` to its path.

If you need to create a new keystore:

```bash
keytool -genkey -v -keystore signing-key.jks \
   -alias issuetrax-sign-keys \
   -keyalg RSA \
   -keysize 2048 \
   -validity 10000
```

Remember the passwords you enter; they will be used as `KEY_STORE_PASSWORD` and `KEY_PASSWORD`.


### Step 2: Set Environment Variables

   **Option A: Create a local `.env` file** (recommended for development)
   ```bash
   cat > .env << 'EOF'
   export SIGNING_KEY_FILE='<path-to-your-keystore-file>'
   export KEY_ALIAS='<your-key-alias>'
   export KEY_STORE_PASSWORD='<your-keystore-password>'
   export KEY_PASSWORD='<your-key-password>'
   EOF
   ```

   Then source it before building:
   ```bash
   source .env
   ./gradlew assembleRelease
   ```

   **Option B: Add to shell profile** (for persistent setup)
   
   Add to `~/.zshrc` or `~/.bashrc`:
   ```bash
   export SIGNING_KEY_FILE='<path-to-your-keystore-file>'
   export KEY_ALIAS='<your-key-alias>'
   export KEY_STORE_PASSWORD='<your-keystore-password>'
   export KEY_PASSWORD='<your-key-password>'
   ```

   Then reload your shell:
   ```bash
   source ~/.zshrc  # or source ~/.bashrc
   ```

### Step 3: Set Environment Variables

   **Option A: Create a local `.env` file** (recommended for development)
   ```bash
   cat > .env << 'EOF'
   export SIGNING_KEY_BASE64='<your-base64-encoded-keystore>'
   export KEY_ALIAS='<your-key-alias>'
   export KEY_STORE_PASSWORD='<your-keystore-password>'
   export KEY_PASSWORD='<your-key-password>'
   EOF
   ```

   Then source it before building:
   ```bash
   source .env
   ./gradlew assembleRelease
   ```

   **Option B: Add to shell profile** (for persistent setup)
   
   Add to `~/.zshrc` or `~/.bashrc`:
   ```bash
   export SIGNING_KEY_BASE64='<your-base64-encoded-keystore>'
   export KEY_ALIAS='<your-key-alias>'
   export KEY_STORE_PASSWORD='<your-keystore-password>'
   export KEY_PASSWORD='<your-key-password>'
   ```

   Then reload your shell:
   ```bash
   source ~/.zshrc  # or source ~/.bashrc
   ```

### Step 3: Build the Release APK

```bash
./gradlew assembleRelease
```

You should see the message: `✓ Using keystore from environment variable or repository root`

### How It Works

The `build.gradle.kts` checks for these environment variables:
- `SIGNING_KEY_FILE` - Path to the keystore file
- `KEY_ALIAS` - Key alias
- `KEY_STORE_PASSWORD` - Keystore password
- `KEY_PASSWORD` - Key password

If found, it:
1. Uses the specified keystore file to sign the release APK.

This is **identical** to what GitHub Actions does, ensuring build consistency.

## Security Best Practices

⚠️ **Never commit keystore files or credentials to version control!**

✅ Store `.env` file locally (git-ignored)  
✅ Use different keystores for debug and release builds  
✅ Backup your keystore securely (losing it means you can't update your app!)  
✅ Use strong passwords for keystore and key  
✅ Limit access to production keystores

## GitHub Actions Setup

For GitHub Actions to sign releases, set these **repository secrets**:

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add these secrets:
   - `SIGNING_KEY` - Base64-encoded keystore (get from `./scripts/encode-keystore.sh`)
   - `ALIAS` - Key alias
   - `KEY_STORE_PASSWORD` - Keystore password
   - `KEY_PASSWORD` - Key password

The `.github/workflows/release.yml` workflow uses these secrets automatically.

## Quick Reference

### Encode an existing keystore to Base64

```bash
base64 < your-keystore.jks | tr -d '\n'
```

### Decode Base64 back to keystore file

```bash
echo "$SIGNING_KEY_BASE64" | base64 -d > issuetrax-release.keystore
```

### Check if environment variables are set

```bash
env | grep -E '(SIGNING_KEY_BASE64|KEY_ALIAS|KEY_STORE_PASSWORD|KEY_PASSWORD)'
```

## Troubleshooting

### "WARNING: No signing configuration found"

Environment variables are not set. The build will continue but use debug signing.

**Solution:** 
1. Check if `.env` file exists: `ls -la .env`
2. Source it: `source .env`
3. Verify variables are set: `echo $SIGNING_KEY_BASE64`

### "Keystore was tampered with, or password was incorrect"

The password in your environment variables is incorrect.

**Solution:** 
1. Verify the correct password for your keystore
2. Update the environment variables with the correct password
3. Re-source your `.env` file

### Environment variables not persisting

If variables disappear after closing the terminal:

**Solution:** Either:
- Source `.env` in each terminal session: `source .env`
- Add the exports to your shell profile (`~/.zshrc` or `~/.bashrc`)

### Base64 string too long for terminal

When copying the Base64 string, it may be very long.

**Solution:** 
- Use the script which creates the `.env` file directly
- Or save to file: `base64 < keystore.jks | tr -d '\n' > keystore.b64.txt`


## Related Files

- `.env` - Optional local environment variables (git-ignored)
- `app/build.gradle.kts` - Build configuration with signing setup

## References

- GitHub Actions workflow: `.github/workflows/release.yml`
- Build configuration: `app/build.gradle.kts`
- Sign Android Release Action: https://github.com/r0adkll/sign-android-release
