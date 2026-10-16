# DriverSafe: A Machine-Learning Based Device to Prevent Distracted Driving

Science fair project (Trabuco Hills High School, 2024) by Hussein Hirani.
The full project board is in [`docs/DriverSafe-Project-Board.pdf`](docs/DriverSafe-Project-Board.pdf).

The project has two parts:

1. **DriverSafe**: A Raspberry Pi 4 with a camera watches the driver and runs a TFLite model trained
   to detect distracted driving. A companion Android app runs a second model that recognises the
   driver's side of the car. When both agree, the Pi tells the phone over Bluetooth (HC-05) to power
   off, so only the driver's phone is disabled.
2. **Heads-up display (HUD)**: A Raspberry Pi Pico W with an ILI9341 display and push buttons,
   projected onto the windshield with reflective film. A companion app sends the phone's
   notifications and contacts to the HUD over BLE, and can place calls chosen from the HUD.

## Repository layout

| Folder | What it is | Built with |
| --- | --- | --- |
| [`driversafe-app/`](driversafe-app) | DriverSafe Android app: camera + driver's-side model + HC-05 link to the Pi | Android Studio (Kotlin) |
| [`raspberry-pi/`](raspberry-pi) | Raspberry Pi 4 capture-and-classify loop and distracted-driving model | Bash, Python, TFLite |
| [`hud/pico-w/`](hud/pico-w) | Pico W firmware for the heads-up display | MicroPython (Thonny) |
| [`hud/companion-app/`](hud/companion-app) | Android app that syncs notifications and contacts to the HUD | MIT App Inventor |
| [`docs/`](docs) | Project board | |

## How DriverSafe works

```
 Raspberry Pi 4                                   Android phone (driversafe-app)
 ─────────────                                    ──────────────────────────────
 camera photo every ~5 s                          camera frame
   → model_unquant.tflite                           → DistractionModel.tflite
   → distracted? (recordFile.txt)                   → driver's side of the car?
                                                       │ yes: send '0' over HC-05
 HC-05  ◄──────────────────────────────────────────────┘
   if the Pi is also seeing distraction,
   it replies  ──────────────────────────────────►  phone powers off
```

## Building and running

- **Android app**: open `driversafe-app/` in Android Studio, or run `./gradlew assembleDebug` with
  JDK 17 or 18. The HC-05's MAC address is set in `HudBluetooth.kt`.
- **Raspberry Pi**: see [`raspberry-pi/README.md`](raspberry-pi/README.md).
- **HUD**: see [`hud/pico-w/README.md`](hud/pico-w/README.md).

## Credits

- Android app based on Google's
  [Recognize Flowers with TensorFlow Lite on Android](https://codelabs.developers.google.com/codelabs/recognize-flowers-with-tensorflow-on-android)
  codelab (Apache 2.0).
- `raspberry-pi/label_image.py` is adapted from the TensorFlow Lite Python example (Apache 2.0).
- ILI9341 driver and fonts from [jeffmer/micropython-ili9341](https://github.com/jeffmer/micropython-ili9341).
- BLE helpers from the MicroPython Bluetooth examples.
- Distracted-driving training data from the
  [State Farm Distracted Driver Detection](https://www.kaggle.com/c/state-farm-distracted-driver-detection) dataset.
