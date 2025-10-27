# Issuetrax - UI/UX Design Specification

## Design Philosophy

Issuetrax embraces a **simplified, single-context workflow** design philosophy that focuses on one repository and one issue/PR at a time. The app prioritizes the PR review experience with **inline diff views optimized for mobile screens** and **gesture-based navigation** that reduces UI complexity while maximizing efficiency.

## Related Documentation

- **[CLASS_ARCHITECTURE.md](CLASS_ARCHITECTURE.md)** - Complete class reference for implementation details
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Technical architecture patterns
- **[PROJECT_DEFINITION.md](PROJECT_DEFINITION.md)** - Project requirements and scope
- **[PROJECT_SETUP.md](PROJECT_SETUP.md)** - Development environment setup

## Visual Design System

### Color Palette

```
Primary Colors:
- Primary: #1976D2 (GitHub Blue variant)
- On Primary: #FFFFFF
- Primary Container: #E3F2FD
- On Primary Container: #0D47A1

Secondary Colors:
- Secondary: #455A64
- On Secondary: #FFFFFF
- Secondary Container: #CFD8DC
- On Secondary Container: #263238

Surface Colors:
- Surface: #FAFAFA
- On Surface: #212121
- Surface Variant: #F5F5F5
- On Surface Variant: #757575

Status Colors:
- Success: #4CAF50 (PR approved, tests passing)
- Warning: #FF9800 (Changes requested, pending review)
- Error: #F44336 (Failed tests, conflicts)
- Info: #2196F3 (Draft, WIP)
```

### Typography

```
Display Large: Roboto 57sp/64sp (App title)
Display Medium: Roboto 45sp/52sp (Screen headers)
Display Small: Roboto 36sp/44sp (Section headers)

Headline Large: Roboto 32sp/40sp (PR titles)
Headline Medium: Roboto 28sp/36sp (File names)
Headline Small: Roboto 24sp/32sp (Dialog titles)

Title Large: Roboto 22sp/28sp (Card titles)
Title Medium: Roboto 16sp/24sp (List items)
Title Small: Roboto 14sp/20sp (Buttons)

Body Large: Roboto 16sp/24sp (Main content)
Body Medium: Roboto 14sp/20sp (Secondary content)
Body Small: Roboto 12sp/16sp (Captions, metadata)

Code Font: JetBrains Mono 14sp/20sp (Code blocks, diffs)
```

### Iconography

- **System Icons**: Material Design Icons 3
- **Custom Icons**: GitHub-specific icons (PR, commit, branch, etc.)
- **Gesture Indicators**: Custom animated icons for swipe hints
- **Status Icons**: Clear visual indicators for PR states, CI/CD status

## Screen Designs

### 1. Login & Repository Selection Screen

```
┌─────────────────────────────────────┐
│              Status Bar              │
├─────────────────────────────────────┤
│                                     │
│        [Issuetrax Logo]             │
│                                     │
│     "Connect to GitHub"             │
│                                     │
│    ┌─────────────────────────────┐  │
│    │    🐙 Sign in with GitHub   │  │
│    └─────────────────────────────┘  │
│                                     │
│    "Select working repository:"     │
│                                     │
│    ┌─────────────────────────────┐  │
│    │ 📁 user/current-project     │  │
│    │ Active: 2 PRs • 5 issues   │  │
│    │ [Select Repository]         │  │
│    └─────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘
```

**Key Features**:
- Combined authentication and repository selection
- Single-repository focus
- Repository context preview
- Streamlined workflow

### 2. Current Work Screen

```
┌─────────────────────────────────────┐
│  [⚙] user/repo-name        [👤][⋮]  │
├─────────────────────────────────────┤
│  📂 Current Work Context            │
├─────────────────────────────────────┤
│                                     │
│  🔄 Active PR #42                   │
│  ┌─────────────────────────────────┐│
│  │ "Add gesture navigation system" ││
│  │ feature/gestures → main         ││
│  │ ✅ Ready for review             ││
│  │ 👀 2/3 approvals needed         ││
│  │ +234 -89 lines • 8 files       ││
│  │                                 ││
│  │    [Continue Review]            ││
│  └─────────────────────────────────┘│
│                                     │
│  📝 Or create new issue:            │
│  ┌─────────────────────────────────┐│
│  │ Enter issue title...            ││
│  └─────────────────────────────────┘│
│  [Create Issue]                     │
│                                     │
└─────────────────────────────────────┘
```

**Key Features**:
- Single active work context
- Direct PR review access
- Simple issue creation
- Focus on current task

### 3. PR Review Screen (Primary Focus)

The PR Review screen is the core of the application, optimized for mobile code review with a simplified navigation pattern focused on file list → file diff → chunk detail.

#### Main View: File List

The default view shows a list of all changed files in the pull request.

```
┌─────────────────────────────────────┐
│  [←]  #47: Add gesture navigation   │
│                              [⋮][✓] │
├─────────────────────────────────────┤
│  📋 Files Changed (8)               │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 📄 GestureDetector.kt       │   │
│  │ +45 -12 • Modified          │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 📄 PRReviewScreen.kt        │   │
│  │ +23 -8 • Modified           │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ 📄 MainActivity.kt          │   │
│  │ +10 -5 • Modified           │   │
│  └─────────────────────────────┘   │
│                                     │
│  ... (+5 more files)               │
│                                     │
└─────────────────────────────────────┘
```

**Navigation:**
- Tap on a file → View file diff
- Pull request metadata is shown in the top bar

#### File Diff View

When a file is selected, the full diff is displayed.

```
┌─────────────────────────────────────┐
│  [←]  #47: Add gesture navigation   │
│                              [⋮][✓] │
├─────────────────────────────────────┤
│  📂 GestureDetector.kt              │
│  +45 -12 lines • Modified           │
├─────────────────────────────────────┤
│  Hunk 1/5: @@ -15,3 +15,6 @@       │
│                                     │
│ 15  class GestureDetector {         │
│ 16  private val threshold = 50.dp   │ 
│ 17 -fun detectSwipe() {             │
│ 18 +fun detectSwipe(                │
│ 19 +    sensitivity: Float = 1.0f   │
│ 20 +) {                             │
│ 21      if (dragAmount > threshold) │
│ 22          onSwipeDetected()       │
│ 23  }                               │
│                                     │
├─────────────────────────────────────┤
│  Hunk 2/5: @@ -45,2 +48,8 @@       │
│  ...                                │
└─────────────────────────────────────┘
```

**Navigation:**
- Swipe right (→) → Return to file list
- Tap on a chunk → View chunk in full screen

#### Chunk Detail View

When a chunk is selected, it's displayed in full screen.

```
┌─────────────────────────────────────┐
│  Chunk 2 of 5                  [✕]  │
│  GestureDetector.kt                 │
├─────────────────────────────────────┤
│                                     │
│  @@ -45,2 +48,8 @@                  │
│                                     │
│ 45  fun handleGesture(              │
│ 46      event: MotionEvent          │
│ 47  ): Boolean {                    │
│ 48 -    return false                │
│ 49 +    val handled = detector.     │
│ 50 +        onTouchEvent(event)     │
│ 51 +    if (handled) {               │
│ 52 +        triggerHaptic()         │
│ 53 +    }                            │
│ 54 +    return handled               │
│ 55  }                                │
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Navigation:**
- Tap close button (✕) → Return to file diff

#### Gesture Support

```
Gesture navigation:
- Swipe right in file diff view → Return to file list
- No other swipe gestures (removed for simplification)
```

#### Source Code Browser

```
┌─────────────────────────────────────┐
│  📁 Browse Repository Files         │
├─────────────────────────────────────┤
│  🔍 Search files...                 │
├─────────────────────────────────────┤
│  📂 src/main/kotlin/                │
│  │  📄 MainActivity.kt              │
│  │  📄 IssuetraxApplication.kt     │
│  │  📂 presentation/                │
│  │  │  📄 PRReviewScreen.kt 🔄     │
│  │  │  📄 LoginScreen.kt           │
│  │  📂 data/                       │
│  │  │  📄 GitHubRepository.kt      │
│  │                                 │
│  [View Selected File]              │
└─────────────────────────────────────┘
```

#### Comment & Review Interface

```
┌─────────────────────────────────────┐
│  💬 Add Comment                     │
├─────────────────────────────────────┤
│  📍 Line 18: detectSwipe(           │
│                                     │
│  ┌─────────────────────────────────┐│
│  │ Consider adding parameter       ││
│  │ validation for sensitivity      ││
│  │ value to prevent negative       ││
│  │ values...                       ││
│  └─────────────────────────────────┘│
│                                     │
│  💡 Suggestion   📝 General Comment │
│                                     │
│  [Cancel]              [Add Comment]│
└─────────────────────────────────────┘

Review Submission:
┌─────────────────────────────────────┐
│  📝 Submit Review                   │
├─────────────────────────────────────┤
│  ✅ Approve                         │
│  🟡 Request Changes                 │
│  💬 Comment Only                    │
│                                     │
│  ┌─────────────────────────────────┐│
│  │ Overall feedback:               ││
│  │ Good implementation! Just add   ││
│  │ some unit tests and handle edge ││
│  │ cases better.                   ││
│  └─────────────────────────────────┘│
│                                     │
│  [Cancel]                [Submit]   │
└─────────────────────────────────────┘
```

## Gesture System Detailed Design

### 1. Swipe Sensitivity & Thresholds

```
Horizontal Swipes (File Navigation):
- Minimum Distance: 100dp
- Minimum Velocity: 500dp/s
- Maximum Angle Deviation: 30°

Vertical Swipes (Hunk Navigation): 
- Minimum Distance: 80dp
- Minimum Velocity: 400dp/s
- Maximum Angle Deviation: 30°

Double Tap:
- Max Time Between Taps: 300ms
- Max Distance Between Taps: 40dp

Long Press:
- Duration: 500ms
- Movement Tolerance: 10dp
```

### 2. Visual Feedback System

```
Gesture Start:
- Subtle vibration (10ms)
- Overlay appearance (150ms fade-in)
- Direction indicators

Gesture Progress:
- Real-time visual feedback
- Progress threshold indicators
- Dynamic arrow scaling

Gesture Completion:
- Success animation (200ms)
- Stronger vibration (20ms)
- Content transition (300ms)

Gesture Cancellation:
- Fade-out overlay (100ms)
- Gentle vibration (5ms)
- Return to original state
```

### 3. Accessibility Features

```
Voice Accessibility:
- "Swipe right to next file"
- "Swipe left to previous file"
- "Swipe up to next change"
- "Double tap to expand hunk"

Alternative Navigation:
- Hardware buttons support
- Keyboard shortcuts (Bluetooth)
- Voice commands
- Traditional tap navigation fallback

Visual Indicators:
- High contrast mode support
- Large text support
- Motion reduction options
- Screen reader compatibility
```

## Responsive Design

### Phone Layouts (320dp - 600dp)

```
Portrait Mode:
- Full-screen inline diff view
- Collapsible file list
- Bottom sheet for comments
- Gesture-primary navigation

Landscape Mode:
- Persistent file list sidebar
- Wider inline diff view
- Enhanced gesture zones
- Optimized for one-handed use
```

### Tablet Layouts (600dp+)

```
Master-Detail Layout:
- Persistent file navigation panel
- Large inline diff viewing area
- Side panel for comments
- Enhanced gesture recognition
- Expanded code context display
```

## Dark Mode Support

```
Dark Theme Colors:
- Surface: #121212
- On Surface: #FFFFFF
- Primary: #BB86FC
- Secondary: #03DAC6
- Error: #CF6679

Code Syntax Highlighting:
- Keywords: #CC7832
- Strings: #6A8759
- Comments: #808080
- Added Lines: #365B3D
- Removed Lines: #5C2727
```

## Animation & Transitions

### Page Transitions

```
File Navigation:
- Slide animation (300ms)
- Material motion curves
- Gesture-driven progress

Hunk Expansion:
- Smooth expand/collapse (200ms)
- Preserved scroll position
- Progressive content loading

Comment Actions:
- Bottom sheet slide-up (250ms)
- Keyboard-aware animations
- Focus management
```

### Micro-interactions

```
Gesture Feedback:
- Ripple effects
- Scale animations
- Color transitions
- Haptic feedback synchronization

Loading States:
- Skeleton screens
- Progressive loading
- Shimmer effects
- Error state transitions
```

## Performance Considerations

### Rendering Optimization

```
Diff Rendering:
- Virtual scrolling for large files
- Lazy loading of file content
- Efficient syntax highlighting
- Optimized layout calculations

Memory Management:
- Image caching strategies
- Diff data pagination
- Background processing
- Garbage collection optimization
```

### Gesture Performance

```
Touch Response:
- 60fps gesture tracking
- Low-latency feedback
- Efficient hit testing
- Optimized drawing operations
```

This UI/UX design specification ensures that the Issuetrax app provides an intuitive, efficient, and accessible experience for mobile code reviews, with the gesture-based navigation system as its standout feature.