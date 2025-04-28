# 🎨 Drawing Canvas App

## Overview

A simple Android app built with **Jetpack Compose** and **SurfaceView** that allows you to:

- Pick an image from the gallery
- Move, zoom, and rotate it
- Insert it onto a drawing canvas
- Draw freely over it
- Clear the canvas anytime

---

## Features

- Compose UI + SurfaceView integration
- Image selection from gallery
- Interactive transformations (Pan, Zoom, Rotate)
- Insert transformed image onto canvas
- Freehand drawing on canvas
- Clear the entire canvas
- Smooth background operations with Kotlin Coroutines

---

## How It Works

1. **Pick Image** → Select an image from gallery.
2. **Move, Zoom, Rotate** → Adjust using touch gestures.
3. **Insert** → Permanently place the image on the canvas.
4. **Draw** → Draw freely with touch.
5. **Clear** → Reset canvas completely.

---

## Technical Details

| Aspect                    | Implementation                            |
|:--------------------------|:------------------------------------------|
| **SurfaceView**           | Hosted inside Compose with `AndroidView`. |
| **Drawing**               | Using `Bitmap` and `Canvas`.              |
| **Image Transformations** | Managed with `Matrix`.                    |
| **Background Work**       | Done using Kotlin Coroutines.             |
| **Orientation**           | Fixed; no handling of rotation.           |

---

## Requirements

- Android Studio (Giraffe or later)
- Minimum SDK 24+
- Kotlin + Jetpack Compose
- Permission: Read external storage

---

## Notes

- Simple gallery access; no advanced picker
- All image adjustments happen **before** insertion
- No rotation handling (fixed orientation)
- Focused on clean, maintainable code

---

## Apk

* Debug APK: `app\build\outputs\apk\debug\app-debug.apk`

---