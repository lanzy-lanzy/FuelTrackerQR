package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * QR Code Scanner icon for use in the app
 */
public val Icons.Filled.QrCodeScanner: ImageVector
    get() {
        if (_qrCodeScanner != null) {
            return _qrCodeScanner!!
        }
        _qrCodeScanner = materialIcon(name = "Default.QrCodeScanner") {
            materialPath {
                // This is a simplified QR code scanner icon path
                moveTo(9.5f, 6.5f)
                verticalLineTo(9.5f)
                horizontalLineTo(6.5f)
                verticalLineTo(6.5f)
                horizontalLineTo(9.5f)

                moveTo(9.5f, 14.5f)
                verticalLineTo(17.5f)
                horizontalLineTo(6.5f)
                verticalLineTo(14.5f)
                horizontalLineTo(9.5f)

                moveTo(17.5f, 6.5f)
                verticalLineTo(9.5f)
                horizontalLineTo(14.5f)
                verticalLineTo(6.5f)
                horizontalLineTo(17.5f)

                moveTo(12f, 12f)
                horizontalLineTo(14f)
                verticalLineTo(14f)
                horizontalLineTo(12f)
                verticalLineTo(12f)

                moveTo(14f, 14f)
                horizontalLineTo(16f)
                verticalLineTo(16f)
                horizontalLineTo(14f)
                verticalLineTo(14f)

                moveTo(12f, 14f)
                horizontalLineTo(14f)
                verticalLineTo(16f)
                horizontalLineTo(12f)
                verticalLineTo(14f)

                moveTo(16f, 14f)
                horizontalLineTo(18f)
                verticalLineTo(16f)
                horizontalLineTo(16f)
                verticalLineTo(14f)

                moveTo(16f, 12f)
                horizontalLineTo(18f)
                verticalLineTo(14f)
                horizontalLineTo(16f)
                verticalLineTo(12f)

                moveTo(3f, 3f)
                horizontalLineTo(11f)
                verticalLineTo(5f)
                horizontalLineTo(5f)
                verticalLineTo(11f)
                horizontalLineTo(3f)
                verticalLineTo(3f)

                moveTo(21f, 3f)
                verticalLineTo(11f)
                horizontalLineTo(19f)
                verticalLineTo(5f)
                horizontalLineTo(13f)
                verticalLineTo(3f)
                horizontalLineTo(21f)

                moveTo(3f, 13f)
                horizontalLineTo(5f)
                verticalLineTo(19f)
                horizontalLineTo(11f)
                verticalLineTo(21f)
                horizontalLineTo(3f)
                verticalLineTo(13f)

                moveTo(19f, 19f)
                verticalLineTo(13f)
                horizontalLineTo(21f)
                verticalLineTo(21f)
                horizontalLineTo(13f)
                verticalLineTo(19f)
                horizontalLineTo(19f)

                close()
            }
        }
        return _qrCodeScanner!!
    }

private var _qrCodeScanner: ImageVector? = null
