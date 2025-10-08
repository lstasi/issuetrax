# API Response Samples

This directory contains sample GitHub API responses used for unit testing.

## Files

- **user.json** - Sample response from `GET /user`
- **repositories.json** - Sample response from `GET /user/repos`
- **pull_requests.json** - Sample response from `GET /repos/{owner}/{repo}/pulls`
- **pull_request_files.json** - Sample response from `GET /repos/{owner}/{repo}/pulls/{number}/files`

## Live Responses

When you run the validation script (`../../../../../../scripts/validate_api_responses.sh`), it will create `*_live.json` files with real API responses for comparison.

## Usage in Tests

Load these JSON files in your tests to simulate API responses:

```kotlin
val jsonString = javaClass.classLoader
    ?.getResourceAsStream("api_responses/pull_requests.json")
    ?.bufferedReader()
    ?.use { it.readText() }
```

## Validation

Run the validation script to ensure mock responses match real API format:

```bash
./scripts/validate_api_responses.sh
```

This will:
1. Fetch live data from GitHub API
2. Validate structure matches DTOs
3. Save as `*_live.json` for comparison
4. Report any discrepancies
