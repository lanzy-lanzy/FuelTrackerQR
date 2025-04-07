package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * LocalGasStation icon for use in the app
 */
public val Icons.Filled.LocalGasStation: ImageVector
    get() {
        if (_localGasStation != null) {
            return _localGasStation!!
        }
        _localGasStation = materialIcon(name = "Filled.LocalGasStation") {
            materialPath {
                // Main pump body
                moveTo(19.77f, 7.23f)
                lineTo(19.78f, 7.22f)
                lineTo(16.06f, 3.5f)
                lineTo(15.0f, 4.56f)
                lineTo(17.11f, 6.67f)
                curveTo(16.17f, 7.03f, 15.5f, 7.93f, 15.5f, 9.0f)
                curveTo(15.5f, 10.38f, 16.62f, 11.5f, 18.0f, 11.5f)
                curveTo(18.36f, 11.5f, 18.69f, 11.42f, 19.0f, 11.29f)
                verticalLineTo(18.5f)
                curveTo(19.0f, 19.05f, 18.55f, 19.5f, 18.0f, 19.5f)
                curveTo(17.45f, 19.5f, 17.0f, 19.05f, 17.0f, 18.5f)
                verticalLineTo(14.0f)
                curveTo(17.0f, 12.9f, 16.1f, 12.0f, 15.0f, 12.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(5.0f)
                curveTo(14.0f, 3.9f, 13.1f, 3.0f, 12.0f, 3.0f)
                horizontalLineTo(6.0f)
                curveTo(4.9f, 3.0f, 4.0f, 3.9f, 4.0f, 5.0f)
                verticalLineTo(21.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(13.5f)
                horizontalLineTo(15.5f)
                verticalLineTo(18.5f)
                curveTo(15.5f, 19.88f, 16.62f, 21.0f, 18.0f, 21.0f)
                curveTo(19.38f, 21.0f, 20.5f, 19.88f, 20.5f, 18.5f)
                verticalLineTo(9.0f)
                curveTo(20.5f, 8.31f, 20.22f, 7.68f, 19.77f, 7.23f)
                close()
                
                // Fuel window
                moveTo(12.0f, 10.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(5.0f)
                horizontalLineTo(12.0f)
                verticalLineTo(10.0f)
                close()
            }
        }
        return _localGasStation!!
    }

private var _localGasStation: ImageVector? = null
