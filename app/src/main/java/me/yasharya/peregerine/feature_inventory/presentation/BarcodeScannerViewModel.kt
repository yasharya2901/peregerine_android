package me.yasharya.peregerine.feature_inventory.presentation

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class BarcodeScannerViewModel: ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    private val _scannedBarcode = Channel<String>(Channel.CONFLATED)
    val scannedBarcode = _scannedBarcode.receiveAsFlow()

    private var cameraProvider: ProcessCameraProvider? = null

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
            )
            .build()
    )

    private var isBound = false

    private var imageAnalysis: ImageAnalysis? = null
    private var hasScanned = false

    fun bindCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        if (isBound) return

        val preview = Preview.Builder().build().also { preview ->
            preview.setSurfaceProvider { request ->
                _surfaceRequest.value = request
            }
        }

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    MlKitAnalyzer(
                        listOf(barcodeScanner),
                        ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
                        ContextCompat.getMainExecutor(context)
                    ) { result ->
                        if (hasScanned) return@MlKitAnalyzer  // ← gate
                        val barcodes = result?.getValue(barcodeScanner) ?: return@MlKitAnalyzer
                        barcodes.firstOrNull()?.rawValue?.let { value ->
                            hasScanned = true                  // ← set before sending
                            _scannedBarcode.trySend(value)
                        }
                    }
                )
            }

        ProcessCameraProvider.getInstance(context).also { future ->
            future.addListener({
                cameraProvider = future.get().also { provider ->
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis!!
                    )
                    isBound = true
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    override fun onCleared() {
        cameraProvider?.unbindAll()
        barcodeScanner.close()
        _scannedBarcode.close()
    }
}