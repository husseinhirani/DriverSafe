# Heads-up display firmware (Raspberry Pi Pico W)

MicroPython firmware for the HUD: a Pico W driving a 320x240 ILI9341 display over SPI, with push
buttons for switching between the notification and phone screens. It talks to the companion app
over BLE.

Display wiring from the display test script (SPI0); check it against `main.py` once recovered:

| ILI9341 | Pico W GPIO |
| --- | --- |
| CLK | 6 |
| MOSI | 7 |
| MISO | 4 |
| CS | 13 |
| RST | 14 |
| DC | 15 |

## Files

- `lib/ili934xnew.py`, `lib/glcdfont.py`, `lib/tt14.py`, `lib/tt24.py`, `lib/tt32.py`: display
  driver and fonts from [jeffmer/micropython-ili9341](https://github.com/jeffmer/micropython-ili9341).
- `lib/ble_simple_peripheral.py`, `lib/ble_advertising.py`: BLE UART peripheral helpers.

Copy everything in `lib/` to the root of the Pico W, alongside `main.py`.

## Missing: `main.py`

The final HUD program (screens, buttons, notification and contact handling) was saved directly to
the Pico W from Thonny and isn't on this computer. To recover it, plug in the Pico W, open Thonny,
choose **View → Files**, and download `main.py` (and any other `.py` files on the board) into this
folder.
