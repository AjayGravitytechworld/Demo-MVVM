# 🧠 SmartClassifier

> An on-device **computer-vision playground** for Android — image labeling, face analysis & face swap, sticker cut-outs, ARCore 3D doodles, air-writing, an AR emoji avatar, and hand-gesture experiences.
> Everything runs **on the phone** (ML Kit / MediaPipe / ARCore). No backend, no API keys.

Built with **Kotlin · Jetpack Compose · Clean Architecture · Hilt · Coroutines/Flow · CameraX · ML Kit · MediaPipe · ARCore · OpenGL ES**.

---

## 📑 Table of contents

1. [Feature overview](#-feature-overview)
2. [Tech stack](#-tech-stack)
3. [Getting started](#-getting-started)
4. [Architecture & project structure](#-architecture--project-structure)
5. [Feature deep-dives & function reference](#-feature-deep-dives--function-reference)
   - [5.1 Face Analyze & Swap](#51--face-analyze--swap)
   - [5.2 Sticker](#52--sticker)
   - [5.3 AR Doodle](#53--ar-doodle-arcore)
   - [5.4 Air-Write (3D text)](#54--air-write-3d-text)
   - [5.5 AR Emoji](#55--ar-emoji)
   - [5.6 Hand Air-Write](#56--hand-air-write-gesture-handwriting)
6. [Add-on feature modules](#-add-on-feature-modules)
   - [6.1 Gesture Selfie](#61--gesture-selfie)
   - [6.2 Air Theremin](#62--air-theremin)
   - [6.3 Blink Morse](#63--blink-morse)
   - [6.4 Air Pictionary](#64--air-pictionary)
   - [6.5 Chomp Monster](#65--chomp-monster)
   - [6.6 Pet Buddies](#66--pet-buddies)
7. [Shared building blocks](#-shared-building-blocks)
8. [Permissions](#-permissions)
9. [How to add a new feature screen](#-how-to-add-a-new-feature-screen)
10. [Tuning cheat-sheet](#-tuning-cheat-sheet)
11. [Troubleshooting](#-troubleshooting)
12. [Roadmap](#-roadmap)

---

## ✨ Feature overview

| # | Screen | What it does | Main tech |
|---|--------|--------------|-----------|
| 1 | **Face Analyze & Swap** 🧠 | Pick/take a photo → image labels + per-face **mood**, **body pose** (standing / sitting / lying), and **face swap** between two faces | ML Kit Image Labeling, Face Detection, Pose Detection |
| 2 | **Sticker** ✂ | Long-press an object/person → it is lifted out as a white-bordered sticker | ML Kit Subject Segmentation |
| 3 | **AR Doodle** ✏ | Draw lines or **string lights** that stay fixed in 3D space while you walk around; record video | ARCore, OpenGL ES, CameraX |
| 4 | **Air-Write** 🔤 | Write in the air with your finger on screen → recognised text becomes **3D labels** pinned in the world | ML Kit Digital Ink, ARCore |
| 5 | **AR Emoji** 😀 | Cartoon avatar (Classic / Cat / Panda / Robot) that copies your face — blink, mouth, brows, head tilt; photo + video | ML Kit Face Detection, CameraX `OverlayEffect` |
| 6 | **Hand Air-Write** 🖐 | Write in mid-air with **hand gestures** (pinch / point / hold button) → text recognised and shown as floating labels | MediaPipe Hand Landmarker, ML Kit Digital Ink |
| + | *Add-on modules* | Gesture Selfie, Air Theremin, Blink Morse, Air Pictionary, Chomp Monster, Pet Buddies — see [§6](#-add-on-feature-modules) | MediaPipe, ML Kit, Canvas |

---

## 🧰 Tech stack

| Area | Library / version |
|------|-------------------|
| Language / UI | Kotlin, Jetpack Compose (BOM `2024.06.00`), Material 3 |
| DI | Hilt `2.51.1` + `hilt-navigation-compose` |
| Async | Kotlin Coroutines `1.8.1`, `StateFlow` |
| Camera | CameraX `1.3.4` (+ `camera-effects 1.4.2` for `OverlayEffect`) |
| ML Kit | Image Labeling `17.0.9`, Face Detection `16.1.6`, Pose Detection `18.0.0-beta4`, Subject Segmentation, Digital Ink `18.1.0` |
| AR | ARCore `1.44.0` + raw OpenGL ES renderers |
| Hand tracking (add) | MediaPipe Tasks Vision `0.10.14` |
| Images | Coil `2.6.0` |
| Build | AGP, KSP, Kotlin compiler extension `1.5.14`, **minSdk 24**, target/compile **36**, Java 17 |

---

## 🚀 Getting started

### Requirements
- Android Studio **Hedgehog** or newer, JDK 17
- A **physical device** with Google Play Services (AR Doodle / Air-Write need an **ARCore-supported** phone)
- Internet **once** (to download on-device models — see below)

### Setup
```bash
git clone <your-repo-url>
cd SmartClassifier
# open in Android Studio → Sync Gradle → Run ▶
```

### Add the MediaPipe dependency (needed by Hand Air-Write and all hand-gesture add-ons)
```kotlin
// app/build.gradle.kts → dependencies { }
implementation("com.google.mediapipe:tasks-vision:0.10.14")
```

### Models that download on first use
| Model | Used by | Size / note |
|-------|---------|-------------|
| `hand_landmarker.task` (MediaPipe) | Hand Air-Write + all hand add-ons | ~7.5 MB, saved to app `filesDir` once |
| Digital Ink language model (e.g. `en-US`) | Air-Write, Hand Air-Write | downloaded by ML Kit on first recognise |
| Doodle model `zxx-Zsym-x-autodraw` | Air Pictionary | downloaded by ML Kit on first guess |

After the first download everything works **offline**.

---

## 🏗 Architecture & project structure

Clean Architecture, three layers, unidirectional data flow (`UI → ViewModel → UseCase → Repository`, state flows back as `StateFlow`).

```
                ┌────────────────────────────┐
                │ presentation (Compose + VM) │  ← UI, ViewModels (StateFlow), AR/camera controllers
                └──────────────┬─────────────┘
                               │ calls
                ┌──────────────▼─────────────┐
                │ domain (pure Kotlin)        │  ← models, repository interfaces, use cases
                └──────────────┬─────────────┘
                               │ implemented by
                ┌──────────────▼─────────────┐
                │ data                        │  ← ML Kit repositories, bitmap utils
                └────────────────────────────┘
        di/ (Hilt) wires interfaces → implementations
```

```
app/src/main/java/com/example/smartclassifier/
├── SmartClassifierApp.kt        # @HiltAndroidApp Application
├── MainActivity.kt              # Hosts HomeScreen + feature screens, back handling
├── di/AppModule.kt              # Hilt bindings (repository interface → impl)
│
├── domain/
│   ├── model/                   # ClassificationResult, FaceInfo (Mood, BodyPose), Sticker,
│   │                            # DoodleStroke/DoodlePoint/FaceAnchor, EmojiFace/EmojiStyle
│   ├── repository/              # ImageClassifierRepository, FaceRepository, StickerRepository
│   └── usecase/                 # ClassifyImageUseCase, ExtractStickerUseCase,
│                                # Load/Analyze/Swap/SaveImage use cases (FaceUseCases.kt)
│
├── data/
│   ├── repository/              # ImageClassifierRepositoryImpl, FaceRepositoryImpl, StickerRepositoryImpl
│   └── util/FaceSwapper.kt      # pure-bitmap face swap
│
└── presentation/
    ├── HomeScreen.kt            # AppScreen enum + home cards
    ├── *Screen.kt / *ViewModel.kt   # one pair per feature
    ├── ar/                      # camera/AR controllers + OpenGL/Canvas renderers + InkTextRecognizer
    └── util/                    # ArPermissionGate, FaceOverlay, ImageSourcePicker
```

---

## 🔬 Feature deep-dives & function reference

### 5.1 🧠 Face Analyze & Swap

**Flow:** `ImageSourcePicker` (camera or gallery) → `ClassifierViewModel.onImageSelected()` → runs **labeling and face analysis in parallel** → UI shows labels + numbered face boxes → tap two faces → **Swap**.

#### `ImageClassifierRepositoryImpl`
| Function | Description |
|----------|-------------|
| `classifyImage(uri): List<ClassificationResult>` | Loads the image, runs ML Kit **Image Labeling** (confidence threshold **0.5**), returns `(label, confidence)` pairs. Wraps the callback API in `suspendCancellableCoroutine`. |

#### `FaceRepositoryImpl`
| Function | Description |
|----------|-------------|
| `loadBitmap(uri)` | Decodes with `inSampleSize` so the longest side ≤ `MAX_DIMENSION` (prevents OOM), then **applies EXIF rotation/flip** so faces are upright. |
| `detectFaces(bitmap, analyzePose)` | Runs the **accurate** face detector (classification on, min face 5 %). Sorts faces left→right, assigns ids `1..n`, computes `mood` from smile probability and (optionally) `pose`. |
| `moodOf(smile)` *(private)* | `≥ 0.65 → HAPPY`, `≥ 0.20 → NEUTRAL`, else `SAD`, `null → UNKNOWN`. |
| `detectPose(bitmap, face, wholeImage)` *(private)* | For a single face uses the whole image; for groups it **crops a body region under each face** (±2 face-widths, down 7 face-heights) so every person gets their own pose. |
| `classifyPose(pose)` *(private)* | Landmark geometry: torso more horizontal than vertical → `LYING`; no hips → `UPPER_BODY_ONLY`; thigh vertical (`≥ 0.75`) **and** knee angle ≥ 135° → `STANDING`; otherwise `SITTING`. |
| `angleAt(a, b, c)` *(private)* | Angle at point *b* in degrees (used for the knee). |
| `swapFaces` / `replaceFace` / `saveBitmap` | Delegate to `FaceSwapper` / write the result to MediaStore. |

#### `FaceSwapper` (pure Kotlin, no ML)
Pipeline: **crop expanded face (×1.3) → feathered oval mask → colour match → rotate by roll difference → paste.**

| Function | Description |
|----------|-------------|
| `swap(base, first, firstRoll, second, secondRoll)` | Swaps two faces in the same bitmap. |
| `replace(base, target, targetRoll, source, sourceBox, sourceRoll)` | Pastes a face from **another photo** onto a target face. |
| `extractPatch` / `drawPatch` | Crop a 256×256 patch; draw it with an oval radial-gradient mask. |
| `colorMatch` / `averageColor` | Per-channel gain (clamped 0.8–1.25) so skin tones blend. |

#### `ClassifierViewModel`
| Function | Description |
|----------|-------------|
| `onImageSelected(uri)` | Loads bitmap, then `async` labeling + face analysis, builds the overlay image, auto-selects the face if only one. |
| `toggleFace(id)` | Keeps at most **2** selected faces (oldest dropped). |
| `swapSelectedFaces()` / `swapWithPhoto(uri)` | Swap two faces / replace the selected face with one from a second photo. Pushes a snapshot for undo. |
| `undo()` | Restores the previous snapshot. |
| `saveImage()` | Saves the edited bitmap to the gallery. |

---

### 5.2 ✂ Sticker

`StickerViewModel.liftSubject(x, y)` → `ExtractStickerUseCase` → `StickerRepositoryImpl.extractSubject(bitmap, touchX, touchY)`.

| Function | Description |
|----------|-------------|
| `extractSubject` | ML Kit **Subject Segmentation** (multiple subjects). Picks the **smallest** cut-out under the finger, so tapping a person vs. the object they hold selects the right one. |
| `normalize(subject, w, h)` | Handles both mask styles (full-frame mask vs. cropped mask) and returns a `Cutout(bitmap, left, top)`. |
| `Cutout.hit(x, y, r)` | Samples 9 points around the touch (radius `max(w,h)/60`) and checks alpha > 40, so you don't have to hit pixel-perfect. |
| `addOutline(cutout, stroke, pad)` | Draws a **white sticker border** by stamping a white-tinted copy around a 24-step ring (two rings). |

---

### 5.3 ✏ AR Doodle (ARCore)

Strokes are stored **in world space** with ARCore anchors, so they stay on the real object while you move (6-DoF). Recording re-renders the same scene into a MediaRecorder surface.

| Class | Responsibility |
|-------|----------------|
| `ArDoodleController` | Owns the ARCore `Session` + `GLSurfaceView.Renderer`. `resume()` returns an error string or `null`; `pause()` / `release()` manage lifecycle. `onDrawFrame` draws on screen **and** into the recorder. Handles brush, colour, "string lights" bulbs (`addBulbs`, `bulbIntensity` blink patterns). |
| `StrokeRenderer` | Draws strokes as **camera-facing ribbons** (triangle strips). API: `beginFrame()` → `addStroke(world, camPos, right…)` → `draw(vp)`. |
| `LightRenderer` | Glowing bulbs as additive-blended quads. `enum LightPattern { CHASE, ALTERNATE, TWINKLE }`. |
| `LabelRenderer` / `LabelInstance` | Text as **stacked textured quads** (darker back layers fake 3D extrusion). |
| `BackgroundRenderer` | Draws the ARCore camera image as a full-screen quad. |
| `ArVideoRecorder` | `start(w, h, withAudio)`, `render(timestampNs, draw)`, `stop(): File?` — records via a second EGL surface. |
| `CameraProjection` | `sensorToWorld(...)`, `worldToSensor(...)` using the camera intrinsics. |
| `OrientationTracker` | Rotation-vector sensor → device→world matrix. |
| `GlUtil`, `TextBitmap` | Shader helpers; text → alpha-mask bitmap. |
| `ArDoodleViewModel` | UI state: colour, brush, lights on/off + pattern, stroke count, tracking, elapsed time, errors. |

---

### 5.4 🔤 Air-Write (3D text)

Finger-write on the AR view → `AirWriteViewModel.recognize(strokes, onText)` → **`InkTextRecognizer`** → text → pinned as a 3D `LabelInstance`.

#### `InkTextRecognizer`
| Function | Description |
|----------|-------------|
| `recognize(strokes, languageTag): String` | Best candidate only. |
| `recognizeCandidates(strokes, languageTag): List<String>` | All candidates. Builds an `Ink` from your strokes with **strictly increasing timestamps**, passes a `RecognitionContext` with the real **writing area** (bounding box ×1.2) so ML Kit judges letter size correctly. |
| `clientFor(tag)` *(private)* | Downloads the language model if missing, caches the recogniser per language. |
| `close()` | Releases the recogniser. |

---

### 5.5 😀 AR Emoji

An avatar that mirrors your face; the **same `OverlayEffect`** draws on preview, photo and video, so what you see is exactly what is saved.

| Class | Responsibility |
|-------|----------------|
| `EmojiCameraController` | `bind(owner, front)`, `takePhoto()`, `startRecording()/stop`, face analysis. `buildFace(face, matrix)` converts ML Kit output into an `EmojiFace`; `smooth(prev, new)` applies light smoothing (blinks/mouth react faster than position). `uprightToSensorMatrix(...)` maps analysis-image coords to sensor coords. |
| `EmojiAvatarRenderer` | Canvas drawing: `drawHead`, `drawBrow`, `drawEye(open)`, `drawNose`, `drawMouth`; styles from `EmojiStyle { CLASSIC, CAT, PANDA, ROBOT }`. Everything is drawn in a head-local, roll-rotated frame via `at(...)`. |
| `EmojiFace` (model) | Head box, roll, eye centres + openness (0–1), brows, nose, mouth centre/width/**open**, **smile**, timestamp. |
| `EmojiViewModel` | Style, video-vs-photo mode, flip camera, recording state/time. |

---

### 5.6 🖐 Hand Air-Write (gesture handwriting)

Write in mid-air using **real hand landmarks** (MediaPipe, 21 points) — not the coarse pose-model fingers.

**Gesture modes** (`GestureMode`):

| Mode | Pen down when… | Pen position |
|------|----------------|--------------|
| `PINCH` | thumb tip ↔ index tip distance (÷ hand size) `< 0.35` (release `> 0.60`) | midpoint of the two tips (very stable) |
| `INDEX_POINT` | index straight (`> 1.30`) **and** other fingers curled (`< 1.15`) | index fingertip |
| `HOLD_BUTTON` | on-screen button held | index fingertip |
| `GRAB` | pinch **on a text label** → label follows your hand | pinch midpoint |

#### `HandTracker` (`presentation/ar/HandTracker.kt`)
| Function | Description |
|----------|-------------|
| `prepare(): String?` | **Blocking** (call on IO). Downloads `hand_landmarker.task` once, builds a `HandLandmarker` (`VIDEO` mode, 1 hand). Returns an error text or `null`. |
| `detect(bitmap, timestampMs): HandPose?` | Runs detection on an **upright ARGB_8888** bitmap, returns landmarks in **pixels**. Forces strictly increasing timestamps. |
| `close()` | Releases the model. |

#### `HandPose`
| Member | Meaning |
|--------|---------|
| `x[21]`, `y[21]`, `imgW/imgH` | landmark pixels in the upright analysis image |
| `handSize` | wrist→middle-MCP distance (normaliser for every ratio) |
| `pinchRatio` | thumb tip ↔ index tip ÷ `handSize` (≈0.1 pinched, ≈1.0 open) |
| `indexExt`, `maxOtherExt` | tip-to-wrist ÷ pip-to-wrist (`>1.3` straight, `<1.1` curled) |
| `pinchCenterX/Y` | midpoint of thumb & index tips |

#### `HandAirWriteScreen` — key local functions
| Function | Description |
|----------|-------------|
| `handleHand(pose)` | **Runs on the main thread for each frame.** Maps landmarks → screen (FILL_CENTER crop + front-camera mirror), smooths the pen with a **speed-adaptive filter** (slow = strong smoothing, fast = low lag), decides pen state with **hysteresis + 3-frame debounce**, and appends points (≥ 5 px apart). Pre-roll `pending` points are kept so stroke starts aren't clipped. |
| `handleGrab(cur, pinching)` | GRAB mode: picks the nearest label within 200 px on pinch, moves it via `viewModel.moveLabel(...)`, drops it on release / hand lost. |
| `endStroke()` | Commits `currentLiveStroke` to `completedStrokes` and resets debounce counters. |
| Camera `DisposableEffect` | Binds Preview + `ImageAnalysis` (RGBA) → `toBitmap()` → rotate upright → `tracker.detect()` → posts to main. Detector/executor are released **only when leaving the screen** (not on camera flip). |
| `renderStroke` | Draws strokes as quadratic-Bézier curves with glow. |

#### `HandAirWriteViewModel`
| Function | Description |
|----------|-------------|
| `recognize(strokes, cx, cy)` | Runs ink recognition (guarded against double-runs), adds a `FloatingLabel` at the centre, stores alternative `candidates`. |
| `selectCandidate(text)` | Tapping a suggestion replaces the last label's text. |
| `moveLabel(id, x, y)` | Moves a label (normalised 0–1 coords). |
| `setGestureMode`, `setColor`, `setLanguage`, `undoLabel`, `clearLabels`, … | UI state setters. |

---

## 🧩 Add-on feature modules

Each add-on is a **self-contained screen** (drop-in file + 2 lines of wiring, see [§9](#-how-to-add-a-new-feature-screen)). All hand-based ones reuse `HandTracker`.

### 6.1 🤳 Gesture Selfie
`GestureSelfieScreen.kt` — hands-free camera.
- **✌️ peace sign held ~0.9 s** → 3-2-1 countdown → photo saved to `Pictures/SmartClassifier`. **🖐 open palm** during the countdown cancels.
- `classify(pose): Sign` — `PEACE` (index+middle straight, ring+pinky curled) / `PALM` (all straight) / `NONE`, using finger-extension ratios.
- `onFrame(pose)` — hold timer with 3-frame miss tolerance, cool-down after capture.
- `takePhoto()` — CameraX `ImageCapture` → MediaStore; mirrors the saved image for the front camera (`isReversedHorizontal`).

### 6.2 🎶 Air Theremin
`ThereminScreen.kt` + `ar/ThereminEngine.kt` — an instrument with no instrument.
- Hand **← →** = pitch (C3–C6), **↑ ↓** = volume, **🤏 pinch** = mute.
- Scales: Smooth glide, Major, Pentatonic, **Raag Yaman**, **Raag Bhairavi**; waves: Sine / Square / Saw / Triangle.
- `ThereminEngine.start()/stop()` — a streaming `AudioTrack` thread: per-sample **portamento** (pitch glide), smoothed amplitude, subtle 5.5 Hz vibrato, low-pass on harsh waves. `targetFreq`, `targetAmp`, `wave` are `@Volatile` and set by the UI.
- `buildNotes(scale)`, `midiToFreq(m)`, lane **hysteresis** to prevent flicker on lane borders.

### 6.3 👁 Blink Morse
`BlinkMorseScreen.kt` — type with your **eyes** (accessibility).
- Slow blink 0.2–0.65 s = `•`, long close 0.65–2.5 s = `—`, 2.5 s+ = delete; eyes open 1.4 s = letter done, 3.4 s = space. Natural fast blinks are ignored.
- `onFaces(faces)` — eye-open probability average with hysteresis (`<0.30` closed, `>0.60` open) measures closure time.
- `onBlinkFinished(ms)`, `addSymbol`, `backspace`, a 100 ms ticker that commits letters/spaces, `MORSE` table (A–Z, 0–9), TTS **Speak**, Copy, Share.
- Tune with the constants at the top: `DOT_MS`, `DASH_MS`, `DELETE_MS`, `LETTER_GAP_MS`, `WORD_GAP_MS`.

### 6.4 🎨 Air Pictionary
`AirPictionaryScreen.kt` + `ar/DoodleRecognizer.kt` + `ar/DoodleAnimator.kt` — draw in the air, **AI guesses it, then your drawing comes alive**.
- Draw by **pinch** or **finger**. **Challenge** mode (word + 30 s timer, score, streak, best) or **Free** mode.
- `DoodleRecognizer.recognize(strokes)` — ML Kit Digital Ink with the AutoDraw model (`zxx-Zsym-x-autodraw`) → candidate labels.
- `themeFor(label)` — keyword → `DoodleTheme(motion, palette, fx, scene)`. Motions: `WALK, FLY, SWIM, SPIN, SWAY, DRIVE, FLOAT, BOUNCE, WOBBLE`.
- `buildDoodle(rawStrokes)` — normalises strokes, detects **closed shapes** (get filled with palette colours) and **tiny closed blobs → googly eyes**.
- `drawAliveDoodle(...)` — per-point "vertex shader" displacement (legs shuffle, wings flap, tail waves, tree sways) + whole-body pose (walk, swim, spin, bounce-squash) + pop-in/colour fade.
- `drawScene` / `drawFx` — stateless backgrounds and particles (bubbles, rain, sparkles, hearts, leaves, speed lines).
- Tap another guess chip to re-theme if the AI was wrong.
- ⚠ The model's exact label vocabulary isn't documented; matching is keyword-based. Check the candidate chips and adjust `WORDS` / `RULES` if a word never matches.

### 6.5 🐲 Chomp Monster
`ChompMonsterScreen.kt` — your **hand is the monster's mouth**.
- Open thumb + fingers wide, then **close** over falling food to chomp. Avoid 💣 (−1 life) and 🧦 (−10). 60 s, 3 lives, combo multiplier, best score saved in `SharedPreferences`.
- `handleHand(pose)` — builds the mouth: hinge = knuckle centre, axis = wrist→knuckles, **jaw side** from where the thumb is, `openness = (dist(thumb, middle tip)/handSize − 0.25)/0.8`.
- Chomp = openness was `> 0.55` (**armed**) then drops `< 0.28`; `chomp()` eats items within `0.7 × size` of the mouth centre.
- `step(dt)` game loop (spawn, fall speed ramps with time, popups), `drawMonster(...)` (jaws, teeth, tongue, eyes).

### 6.6 🐾 Pet Buddies
`PetBuddiesScreen.kt` + `ar/AnimalDrawing.kt` — cute **procedural animals sit on your head, glasses and shoulders** and live their own life.
- 10 species (Cat, Dog, Bunny, Panda, Duckling, Penguin, Fox, Bear, Owl, Piglet), random per person, up to 3 people.
- **Anchors:** `onFrame(faces, pose, w, h)` — head = top of face box along head roll; glasses = top rim of one lens from eye landmarks (optional virtual glasses frame); shoulders = real **pose** landmarks (fallback: estimated from the face box).
- **Seated pose:** pear-shaped body, thighs, and **dangling, swinging legs**; shoulder animals sit towards the neck and lean inwards.
- **Behaviour:** `updateAnim` (spring smoothing, action scheduler, **random eye glances & random blinks — no fixed loop**), `computePose` (per-action parameters: wave, yawn, look, sleep, eat, dance, kiss, ear-wiggle, jump).
- **Reacts to you:** smile → they dance/kiss; eyes closed > 1.2 s → they sleep; fast head movement → they hold on; tap an animal → boop.
- 🎲 shuffle, 🔄 flip camera, 📸 photo (via `PixelCopy`, Android 8+).

---

## 🧱 Shared building blocks

| Component | Where | Purpose |
|-----------|-------|---------|
| `ImageSourcePicker` | `presentation/util` | One "Camera or Gallery" entry point: launchers + CAMERA permission + dialogs. `rememberImageSourcePicker(onImagePicked)` returns a lambda for a single button. |
| `ArPermissionGate` | `presentation/util` | Asks for CAMERA + RECORD_AUDIO (+ storage ≤ Android 9) and shows content only when the camera is allowed. |
| `FaceOverlay.draw` | `presentation/util` | Returns a copy of a bitmap with numbered face boxes (selected = orange). |
| `InkTextRecognizer` | `presentation/ar` | Handwriting → text (shared by Air-Write & Hand Air-Write). |
| `HandTracker` / `HandPose` | `presentation/ar` | MediaPipe hand landmarks (shared by all hand features). |

---

## 🔐 Permissions

| Permission | Why |
|-----------|-----|
| `CAMERA` | all camera / AR / gesture features |
| `RECORD_AUDIO` | video with sound (AR Doodle, AR Emoji) |
| `INTERNET` | one-time model downloads (hand model, ink models, doodle model) |
| `WRITE_EXTERNAL_STORAGE` (≤ API 28) | saving photos/videos on old Android |

Saving uses **MediaStore** (`Pictures/SmartClassifier`), so no storage permission is needed on Android 10+.

---

## ➕ How to add a new feature screen

1. Create `presentation/MyFeatureScreen.kt` with `@Composable fun MyFeatureScreen()`.
2. Register it in `HomeScreen.kt`:
   ```kotlin
   enum class AppScreen(val title: String, val subtitle: String, val icon: String) {
       // ...existing...
       MY_FEATURE("My Feature", "One-line description", "🚀")
   }
   ```
3. Route it in `MainActivity.kt`:
   ```kotlin
   when (current) {
       // ...existing...
       AppScreen.MY_FEATURE -> MyFeatureScreen()
   }
   ```
4. Need a ViewModel? Annotate with `@HiltViewModel`, inject with `hiltViewModel()`.
5. Need hand tracking? Create a `HandTracker`, call `prepare()` on `Dispatchers.IO`, feed `detect()` from a CameraX `ImageAnalysis` (RGBA, rotate bitmap upright).

> 💡 **Previews:** `HomeScreen.kt` contains `@Preview` composables (light/dark). Feature screens that need a camera or Hilt can't be previewed — run them on a device.

---

## 🎚 Tuning cheat-sheet

| What feels wrong | Where to change |
|------------------|-----------------|
| Hand writes when it shouldn't / won't start | `HandAirWriteScreen.handleHand` → pinch `0.35f` (start) / `0.60f` (release), debounce `need` |
| Line is jittery or laggy | smoothing `0.25f + speed / 90f` in `handleHand` |
| Written shape looks off | min point spacing (`>= 5f`), `renderStroke` curve smoothing |
| Peace sign not recognised (Selfie) | `classify()` thresholds `1.30f` / `1.15f`, hold time `900f` |
| Morse blinks mis-read | constants at the top of `BlinkMorseScreen.kt` |
| Chomp too hard/easy | `0.55f` / `0.28f` thresholds, reach `mouth.s * 0.7f` in `ChompMonsterScreen` |
| Animals too big/small | `head.tsize`, `gl.tsize`, `ss` in `PetBuddiesScreen.onFrame` |
| Animal tilts the wrong way | sign of `face.headEulerAngleZ` in `PetBuddiesScreen.onFrame` |
| Theremin range | `buildNotes()` octave count, volume range in `ThereminScreen.onFrame` |

---

## 🩺 Troubleshooting

| Problem | Fix |
|---------|-----|
| "Hand model load failed" | Connect to the internet once; the model is cached afterwards. |
| Hand features never start | Make sure the `tasks-vision` dependency is added and the camera permission is granted. |
| AR Doodle says AR unsupported | Device must support **ARCore** (install/update *Google Play Services for AR*). |
| Text recognition returns nothing | The Digital Ink language model must download once (needs internet). |
| Camera preview rebinding issues after flipping | Camera is rebound in `DisposableEffect(lifecycleOwner, isFront)`; detectors must **not** be closed there. |
| Photo save fails on Android ≤ 9 | Grant storage permission. |

---

## 🗺 Roadmap

- Replace the default labeler with a custom **TensorFlow Lite** model
- **Room** database for history (classifications, saved stickers, high scores)
- Unit tests: `ClassifyImageUseCase`, `ClassifierViewModel` (`runTest` + Turbine), pure functions like `themeFor`, `classify`, `buildDoodle`
- Multiplayer / leaderboard for Air Pictionary and Chomp Monster
- Export Pet Buddies / Emoji clips as GIF

---

## 📄 License

Add your license here (e.g. MIT).
