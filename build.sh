#!/bin/bash

# Issuetrax Build Script
# Provides convenient commands for building the Android app

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_usage() {
    echo "Issuetrax Build Script"
    echo
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo
    echo "Commands:"
    echo "  clean           Clean build artifacts"
    echo "  debug           Build debug APK"
    echo "  release         Build release APK"
    echo "  test            Run unit tests"
    echo "  lint            Run lint checks"
    echo "  docker-build    Build using Docker"
    echo "  docker-serve    Build and serve APKs via web interface"
    echo "  all             Run clean, lint, test, and build debug"
    echo
    echo "Options:"
    echo "  -h, --help      Show this help message"
    echo
    echo "Examples:"
    echo "  $0 debug                 # Build debug APK"
    echo "  $0 docker-serve          # Build with Docker and serve via web"
    echo "  $0 all                   # Full build pipeline"
}

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

ensure_gradlew() {
    if [[ ! -f "./gradlew" ]]; then
        log_error "gradlew not found. Make sure you're in the project root directory."
        exit 1
    fi
    chmod +x ./gradlew
}

cmd_clean() {
    log_info "Cleaning build artifacts..."
    ensure_gradlew
    ./gradlew clean
    log_success "Clean completed"
}

cmd_debug() {
    log_info "Building debug APK..."
    ensure_gradlew
    ./gradlew assembleDebug
    log_success "Debug APK built successfully"
    log_info "APK location: app/build/outputs/apk/debug/"
}

cmd_release() {
    log_info "Building release APK..."
    ensure_gradlew
    ./gradlew assembleRelease
    log_success "Release APK built successfully"
    log_info "APK location: app/build/outputs/apk/release/"
}

cmd_test() {
    log_info "Running unit tests..."
    ensure_gradlew
    ./gradlew test
    log_success "Tests completed"
}

cmd_lint() {
    log_info "Running lint checks..."
    ensure_gradlew
    ./gradlew lintDebug
    log_success "Lint completed"
}

cmd_docker_build() {
    log_info "Building with Docker..."
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    mkdir -p build/docker-output
    docker build -f Dockerfile.build -t issuetrax-builder .
    docker run --rm -v "$(pwd)/build/docker-output:/app/app/build/outputs/apk" issuetrax-builder
    log_success "Docker build completed"
    log_info "APKs available in: build/docker-output/"
}

cmd_docker_serve() {
    log_info "Building and serving with Docker..."
    if ! command -v docker-compose &> /dev/null; then
        log_error "docker-compose is not installed or not in PATH"
        exit 1
    fi
    
    docker-compose up --build
}

cmd_all() {
    log_info "Running full build pipeline..."
    cmd_clean
    cmd_lint
    cmd_test
    cmd_debug
    log_success "Full build pipeline completed successfully"
}

# Main script logic
case "${1:-}" in
    clean)
        cmd_clean
        ;;
    debug)
        cmd_debug
        ;;
    release)
        cmd_release
        ;;
    test)
        cmd_test
        ;;
    lint)
        cmd_lint
        ;;
    docker-build)
        cmd_docker_build
        ;;
    docker-serve)
        cmd_docker_serve
        ;;
    all)
        cmd_all
        ;;
    -h|--help|help)
        print_usage
        ;;
    "")
        log_warning "No command specified"
        print_usage
        exit 1
        ;;
    *)
        log_error "Unknown command: $1"
        print_usage
        exit 1
        ;;
esac