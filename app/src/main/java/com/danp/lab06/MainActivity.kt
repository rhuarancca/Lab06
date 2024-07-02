package com.danp.lab06

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danp.lab06.ui.theme.DanpTheme

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var rotation: Float by mutableStateOf(0f)
    private var isRotationActive by mutableStateOf(false)

    private val TAG: String = "Lab-ActivityOne"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        setContent {
            DanpTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DrawTriangle(rotation)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            isRotationActive = !isRotationActive
                            if (isRotationActive) {
                                sensorManager.registerListener(this@MainActivity, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
                            } else {
                                sensorManager.unregisterListener(this@MainActivity)
                            }
                        }) {
                            Text(if (isRotationActive) "Desactivar Rotación" else "Activar Rotación")
                        }
                    }
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            rotation = orientation[0] // Azimuth
            Log.d(TAG, rotation.toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do something if sensor accuracy changes
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun DrawTriangle(rotation: Float) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, size.height * 0.1f) // Top point
            lineTo(size.width * 0.1f, size.height * 0.9f) // Bottom left point
            lineTo(size.width * 0.9f, size.height * 0.9f) // Bottom right point
            close() // Close the path to form the triangle
        }

        drawPath(
            path = path,
            color = Color.LightGray
        )
    }
}