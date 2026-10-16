package org.tensorflow.lite.examples.classification.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

private const val TAG = "HudBluetooth"

// MAC address of the HC-05 module on the DriverSafe heads-up display
private const val HC05_MAC_ADDRESS = "98:D3:02:96:83:14"

// Standard Serial Port Profile UUID used by the HC-05
private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

private const val CONNECT_ATTEMPTS = 3

// Byte sent to the HUD when the driver is distracted (ASCII '0')
private const val ALERT_BYTE = '0'.code

/**
 * Talks to the HC-05 Bluetooth module on the DriverSafe heads-up display.
 *
 * A new connection is opened for every analysed camera frame and closed afterwards.
 */
class HudBluetooth(private val context: Context) {

    /**
     * Connects to the HUD, sends the alert byte if the driver is distracted, then waits for
     * the HUD to reply before disconnecting. Does nothing if the HUD can't be reached.
     */
    fun exchange(distracted: Boolean) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val socket = connect() ?: return
        try {
            if (distracted) {
                socket.outputStream.write(ALERT_BYTE)
            }
            waitForReply(socket)
        } catch (e: IOException) {
            Log.e(TAG, "HUD communication failed", e)
        } finally {
            closeQuietly(socket)
        }
    }

    @SuppressLint("MissingPermission") // Checked in exchange()
    private fun connect(): BluetoothSocket? {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter ?: return null
        val hud = adapter.getRemoteDevice(HC05_MAC_ADDRESS)

        repeat(CONNECT_ATTEMPTS) { attempt ->
            var socket: BluetoothSocket? = null
            try {
                socket = hud.createRfcommSocketToServiceRecord(SPP_UUID)
                socket.connect()
                return socket
            } catch (e: IOException) {
                Log.w(TAG, "HUD connection attempt ${attempt + 1} failed", e)
                socket?.let { closeQuietly(it) }
            }
        }
        return null
    }

    private fun waitForReply(socket: BluetoothSocket) {
        val input = socket.inputStream
        // Drop anything left over so we only react to a fresh reply
        input.skip(input.available().toLong())

        val reply = input.read() // Blocks until the HUD sends a byte
        Log.d(TAG, "HUD replied with byte $reply")
        requestShutdown()
    }

    /**
     * Asks Android to power off the phone. Android only allows this for apps holding the
     * system-level SHUTDOWN permission.
     */
    private fun requestShutdown() {
        val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            .putExtra("android.intent.extra.KEY_CONFIRM", false)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun closeQuietly(socket: BluetoothSocket) {
        try {
            socket.close()
        } catch (e: IOException) {
            Log.w(TAG, "Failed to close HUD socket", e)
        }
    }
}
