#!/bin/bash

# Script to validate GitHub API responses against expected format
# Uses real GitHub API to fetch data and validate structure
# 
# Usage: ./scripts/validate_api_responses.sh
# 
# Requirements:
# - curl and jq must be installed
# - GITHUB_TOKEN environment variable or .env file with token

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Load .env file if exists
if [ -f .env ]; then
    echo -e "${YELLOW}Loading environment variables from .env file${NC}"
    export $(grep -v '^#' .env | xargs)
fi

# Check if GITHUB_TOKEN is set
if [ -z "$GITHUB_TOKEN" ]; then
    echo -e "${RED}ERROR: GITHUB_TOKEN environment variable not set${NC}"
    echo "Please set GITHUB_TOKEN or create a .env file with your token"
    echo "See .env_sample for reference"
    exit 1
fi

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${RED}ERROR: jq is not installed${NC}"
    echo "Please install jq: https://stedolan.github.io/jq/download/"
    exit 1
fi

# Set defaults
REPO_OWNER="${TEST_REPO_OWNER:-lstasi}"
REPO_NAME="${TEST_REPO_NAME:-issuetrax}"
PR_NUMBER="${TEST_PR_NUMBER:-1}"

GITHUB_API="https://api.github.com"
AUTH_HEADER="Authorization: Bearer $GITHUB_TOKEN"

echo -e "${GREEN}Validating GitHub API Responses${NC}"
echo "Repository: $REPO_OWNER/$REPO_NAME"
echo "PR Number: $PR_NUMBER"
echo ""

# Function to validate JSON structure
validate_json() {
    local endpoint=$1
    local expected_fields=$2
    local response=$3
    
    echo -n "Validating $endpoint... "
    
    # Check if response is valid JSON
    if ! echo "$response" | jq empty 2>/dev/null; then
        echo -e "${RED}FAILED${NC}"
        echo "Invalid JSON response"
        return 1
    fi
    
    # Check for required fields
    local missing_fields=""
    for field in $expected_fields; do
        if ! echo "$response" | jq -e ".$field" > /dev/null 2>&1; then
            # For arrays, check first element
            if ! echo "$response" | jq -e ".[0].$field" > /dev/null 2>&1; then
                missing_fields="$missing_fields $field"
            fi
        fi
    done
    
    if [ -n "$missing_fields" ]; then
        echo -e "${RED}FAILED${NC}"
        echo "Missing fields:$missing_fields"
        return 1
    fi
    
    echo -e "${GREEN}OK${NC}"
    return 0
}

# Function to save response to file
save_response() {
    local filename=$1
    local response=$2
    local output_dir="app/src/test/resources/api_responses"
    
    mkdir -p "$output_dir"
    echo "$response" | jq '.' > "${output_dir}/${filename}"
    echo -e "  ${YELLOW}→${NC} Saved to ${output_dir}/${filename}"
}

# Test 1: Get Current User
echo "1. Testing GET /user"
USER_RESPONSE=$(curl -s -H "$AUTH_HEADER" "$GITHUB_API/user")
validate_json "user" "id login avatar_url html_url type" "$USER_RESPONSE"
save_response "user_live.json" "$USER_RESPONSE"
echo ""

# Test 2: Get User Repositories
echo "2. Testing GET /user/repos"
REPOS_RESPONSE=$(curl -s -H "$AUTH_HEADER" "$GITHUB_API/user/repos?sort=updated&per_page=5")
validate_json "repositories" "id name full_name owner description html_url default_branch" "$REPOS_RESPONSE"
save_response "repositories_live.json" "$REPOS_RESPONSE"
echo ""

# Test 3: Get Pull Requests
echo "3. Testing GET /repos/$REPO_OWNER/$REPO_NAME/pulls"
PRS_RESPONSE=$(curl -s -H "$AUTH_HEADER" "$GITHUB_API/repos/$REPO_OWNER/$REPO_NAME/pulls?state=all&per_page=5")
validate_json "pull_requests" "id number title state user created_at updated_at head base html_url" "$PRS_RESPONSE"
save_response "pull_requests_live.json" "$PRS_RESPONSE"
echo ""

# Test 4: Get Pull Request Details (only if PRs exist)
if echo "$PRS_RESPONSE" | jq -e '.[0]' > /dev/null 2>&1; then
    ACTUAL_PR_NUMBER=$(echo "$PRS_RESPONSE" | jq -r '.[0].number')
    echo "4. Testing GET /repos/$REPO_OWNER/$REPO_NAME/pulls/$ACTUAL_PR_NUMBER"
    PR_DETAIL_RESPONSE=$(curl -s -H "$AUTH_HEADER" "$GITHUB_API/repos/$REPO_OWNER/$REPO_NAME/pulls/$ACTUAL_PR_NUMBER")
    validate_json "pull_request_detail" "id number title body state merged draft changed_files additions deletions commits" "$PR_DETAIL_RESPONSE"
    save_response "pull_request_detail_live.json" "$PR_DETAIL_RESPONSE"
    echo ""
    
    # Test 5: Get Pull Request Files
    echo "5. Testing GET /repos/$REPO_OWNER/$REPO_NAME/pulls/$ACTUAL_PR_NUMBER/files"
    PR_FILES_RESPONSE=$(curl -s -H "$AUTH_HEADER" "$GITHUB_API/repos/$REPO_OWNER/$REPO_NAME/pulls/$ACTUAL_PR_NUMBER/files?per_page=5")
    validate_json "pull_request_files" "filename status additions deletions changes" "$PR_FILES_RESPONSE"
    save_response "pull_request_files_live.json" "$PR_FILES_RESPONSE"
    echo ""
else
    echo -e "${YELLOW}Skipping PR detail and files tests (no PRs found)${NC}"
    echo ""
fi

# Summary
echo -e "${GREEN}✓ API Response Validation Complete${NC}"
echo ""
echo "Live responses saved to app/src/test/resources/api_responses/*_live.json"
echo "Compare these with the mock responses (*_live.json vs *.json) to ensure compatibility"
