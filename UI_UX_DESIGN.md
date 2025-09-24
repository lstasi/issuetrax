# Issuetrax - UI/UX Design Specification

## Design Philosophy

Issuetrax embraces a **gesture-first, clean interface** design philosophy that prioritizes efficiency and reduces cognitive load during code reviews. The app follows Material Design 3 principles while introducing innovative gesture-based navigation patterns specifically optimized for mobile PR reviews.

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

### 1. Login Screen

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
│    "Secure OAuth 2.0 authentication"│
│                                     │
│                                     │
│                                     │
└─────────────────────────────────────┘
```

**Key Features**:
- Minimalist design with focus on single action
- GitHub branding integration
- Security assurance messaging
- Loading states during OAuth flow

### 2. Repository Selection Screen

```
┌─────────────────────────────────────┐
│  [≡] Repositories        [👤][⚙]   │
├─────────────────────────────────────┤
│  🔍 Search repositories...          │
├─────────────────────────────────────┤
│  📌 Pinned                          │
│  ┌─────────────────────────────────┐│
│  │ 📁 user/important-repo         ⭐││
│  │ Last updated: 2 hours ago       ││
│  │ 🟢 4 open PRs • 12 issues      ││
│  └─────────────────────────────────┘│
│                                     │
│  📂 Your Repositories               │
│  ┌─────────────────────────────────┐│
│  │ 📁 user/mobile-app             ⭐││
│  │ Kotlin • Updated 1 day ago      ││
│  │ 🟡 2 open PRs • 8 issues       ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 📁 user/web-service            ⭐││
│  │ TypeScript • Updated 3 days ago ││
│  │ 🔴 1 open PR • 15 issues       ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

**Key Features**:
- Search functionality with instant filtering
- Repository categorization (Pinned, Owned, Starred)
- Visual status indicators for PRs and issues
- Pull-to-refresh gesture support

### 3. Issue List Screen

```
┌─────────────────────────────────────┐
│  [←] user/repo-name        [🔍][⋮]  │
├─────────────────────────────────────┤
│  Issues (24) • PRs (8) • Code      │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
├─────────────────────────────────────┤
│  🔍 Filter: Open • Sort: Updated   │
├─────────────────────────────────────┤
│  ┌─────────────────────────────────┐│
│  │ 🟢 #156 Feature: Add dark mode  ││
│  │ 🏷️ enhancement 🏷️ ui/ux          ││
│  │ @user • 2 hours ago             ││
│  │ 💬 3  👍 5                       ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 🟢 #155 Bug: Login not working  ││
│  │ 🏷️ bug 🏷️ critical              ││
│  │ @contributor • 4 hours ago      ││
│  │ 💬 8  👍 2                       ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 🟡 #154 Update documentation    ││
│  │ 🏷️ documentation                ││
│  │ @maintainer • 1 day ago         ││
│  │ 💬 1  👍 1                       ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

**Key Features**:
- Tab-based navigation between Issues and PRs
- Advanced filtering and sorting options
- Rich metadata display (labels, assignees, reactions)
- Swipe actions for quick operations

### 4. PR Reviews Screen (Primary Focus)

#### PR List View

```
┌─────────────────────────────────────┐
│  [←] Pull Requests        [🔍][⋮]   │
├─────────────────────────────────────┤
│  Open (8) • Closed (45) • Draft (2)│
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
├─────────────────────────────────────┤
│  ┌─────────────────────────────────┐│
│  │ 🟢 #47 Add gesture navigation   ││
│  │ feature/gestures → main         ││
│  │ @developer • 3 hours ago        ││
│  │ ✅ All checks passed            ││
│  │ 👀 2/3 approvals • 📝 5 comments││
│  │ +234 -89 lines • 8 files       ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 🟡 #46 Refactor API client      ││
│  │ refactor/api → main             ││
│  │ @senior-dev • 1 day ago         ││
│  │ 🟠 Changes requested            ││
│  │ 👀 1/2 approvals • 📝 12 comments│
│  │ +89 -156 lines • 12 files      ││
│  └─────────────────────────────────┘│
│  ┌─────────────────────────────────┐│
│  │ 🔴 #45 Fix critical bug         ││
│  │ hotfix/auth → main              ││
│  │ @contributor • 2 days ago       ││
│  │ ❌ Tests failing                ││
│  │ 👀 0/3 approvals • 📝 8 comments││  
│  │ +12 -34 lines • 3 files        ││
│  └─────────────────────────────────┘│
└─────────────────────────────────────┘
```

#### PR Detail View with Gesture Navigation

```
┌─────────────────────────────────────┐
│  [←]  #47: Add gesture navigation   │
│                              [⋮][✓] │
├─────────────────────────────────────┤
│  📊 Overview • 📁 Files • 💬 Conv   │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │
├─────────────────────────────────────┤
│  📂 GestureDetector.kt (1/8)       │
│  +45 -12 lines                     │
├─────────────────────────────────────┤
│  1  │ class GestureDetector {      │
│  2  │ +   private val threshold =  │
│  3  │ +       100.dp              │
│  4  │                             │
│  5  │ -   fun detectSwipe() {     │
│  6  │ +   fun detectSwipe(        │
│  7  │ +       sensitivity: Float  │
│  8  │ +   ) {                     │
│  9  │         // Implementation  │
│ 10  │     }                       │
│     │                             │
│     │ 💬 @reviewer: "Great improv-│
│     │ ement! Consider adding unit │
│     │ tests for edge cases."      │
│     │ 👍 2 • 👀 1 • ↩️ Reply       │
│     │                             │
├─────────────────────────────────────┤
│  📱 Swipe Guide:                   │
│  ← → Next/Prev File                │
│  ↑ ↓ Next/Prev Hunk                │
│  ⚫⚫ Toggle View • 👆 Long Comment  │
└─────────────────────────────────────┘
```

#### Gesture Overlay System

```
When user starts swiping:

┌─────────────────────────────────────┐
│                                     │
│           ←  📁  →                  │
│        Prev    Next                 │
│         File   File                 │
│                                     │
│                ↑                    │
│           Next Hunk                 │
│                                     │
│           🔍 Current                 │
│                                     │
│                ↓                    │
│          Prev Hunk                  │
│                                     │
└─────────────────────────────────────┘

Visual feedback during gestures:
- Semi-transparent overlay
- Directional arrows with labels
- Progress indicators
- Haptic feedback on action completion
```

#### Side-by-Side Diff View

```
┌─────────────────────────────────────┐
│  GestureDetector.kt                 │
│  [Unified] [Side-by-Side] • 45/8   │
├─────────────┬───────────────────────┤
│   Before    │        After          │
├─────────────┼───────────────────────┤
│ 1 │ class   │ 1 │ class             │
│ 2 │         │ 2 │ private val       │
│ 3 │         │ 3 │   threshold =     │
│ 4 │         │ 4 │     100.dp        │
│ 5 │ fun det-│ 5 │                   │
│ 6 │   ectSw-│ 6 │ fun detectSwipe(  │
│ 7 │   ipe() │ 7 │   sensitivity:    │
│ 8 │ {       │ 8 │   Float           │
│ 9 │   //    │ 9 │ ) {               │
│10 │ }       │10 │   // Impl         │
│   │         │11 │ }                 │
├─────────────┴───────────────────────┤
│  💬 Add comment on this hunk        │
│  ✅ Approve   🟡 Request   💬 Comment│
└─────────────────────────────────────┘
```

#### Comment & Review Interface

```
┌─────────────────────────────────────┐
│  💬 Add Comment                     │
├─────────────────────────────────────┤
│  📍 Line 6: detectSwipe(            │
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
- "Double tap to toggle view"

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
- Full-screen diff view
- Collapsible file list
- Bottom sheet for comments
- Gesture-primary navigation

Landscape Mode:
- Side-by-side file list + diff
- Persistent comment panel
- Enhanced gesture zones
- Optimized for one-handed use
```

### Tablet Layouts (600dp+)

```
Master-Detail Layout:
- Persistent file navigation
- Large diff viewing area
- Side panel for comments
- Enhanced gesture recognition
- Multi-pane PR overview
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

View Mode Toggle:
- Crossfade transition (200ms)
- Smooth layout changes
- Preserved scroll position

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