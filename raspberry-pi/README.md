# Raspberry Pi 4

Runs on a Raspberry Pi 4 with Camera Module 2 and an HC-05 Bluetooth module.

- `testScript.sh` loops forever: takes a photo with `rpicam-jpeg` (which waits about 5 seconds by
  default), classifies it with `label_image.py`, and writes the result to `recordFile.txt`.
- `label_image.py` runs `model_unquant.tflite` on one image and prints the score for the first label.
- `model_unquant.tflite` is the distracted-driving model.

```bash
pip install tflite-runtime pillow numpy
./testScript.sh
```

## Missing files

- **`labels.txt`**: the label file for `model_unquant.tflite`. `testScript.sh` expects it here.
- **HC-05 Bluetooth script**: the Python script that reads `recordFile.txt` and talks to the phone
  app over the HC-05.

Both should still be on the Pi's SD card. Copy them into this folder.
