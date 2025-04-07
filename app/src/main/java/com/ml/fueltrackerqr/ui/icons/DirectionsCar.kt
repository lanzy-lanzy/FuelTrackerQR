package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * DirectionsCar icon for use in the app
 */
public val Icons.Filled.DirectionsCar: ImageVector
    get() {
        if (_directionsCar != null) {
            return _directionsCar!!
        }
        _directionsCar = materialIcon(name = "Filled.DirectionsCar") {
            materialPath {
                // Car body
                moveTo(18.92f, 6.01f)
                curveTo(18.72f, 5.42f, 18.16f, 5.0f, 17.5f, 5.0f)
                horizontalLineTo(6.5f)
                curveTo(5.84f, 5.0f, 5.29f, 5.42f, 5.08f, 6.01f)
                lineTo(3.0f, 12.0f)
                verticalLineTo(19.0f)
                curveTo(3.0f, 19.55f, 3.45f, 20.0f, 4.0f, 20.0f)
                horizontalLineTo(5.0f)
                curveTo(5.55f, 20.0f, 6.0f, 19.55f, 6.0f, 19.0f)
                verticalLineTo(18.0f)
                horizontalLineTo(18.0f)
                verticalLineTo(19.0f)
                curveTo(18.0f, 19.55f, 18.45f, 20.0f, 19.0f, 20.0f)
                horizontalLineTo(20.0f)
                curveTo(20.55f, 20.0f, 21.0f, 19.55f, 21.0f, 19.0f)
                verticalLineTo(12.0f)
                lineTo(18.92f, 6.01f)
                close()
                
                // Left wheel
                moveTo(6.5f, 16.0f)
                curveTo(5.67f, 16.0f, 5.0f, 15.33f, 5.0f, 14.5f)
                curveTo(5.0f, 13.67f, 5.67f, 13.0f, 6.5f, 13.0f)
                curveTo(7.33f, 13.0f, 8.0f, 13.67f, 8.0f, 14.5f)
                curveTo(8.0f, 15.33f, 7.33f, 16.0f, 6.5f, 16.0f)
                close()
                
                // Right wheel
                moveTo(17.5f, 16.0f)
                curveTo(16.67f, 16.0f, 16.0f, 15.33f, 16.0f, 14.5f)
                curveTo(16.0f, 13.67f, 16.67f, 13.0f, 17.5f, 13.0f)
                curveTo(18.33f, 13.0f, 19.0f, 13.67f, 19.0f, 14.5f)
                curveTo(19.0f, 15.33f, 18.33f, 16.0f, 17.5f, 16.0f)
                close()
                
                // Front window
                moveTo(5.0f, 10.0f)
                lineTo(6.0f, 6.0f)
                horizontalLineTo(18.0f)
                lineTo(19.0f, 10.0f)
                horizontalLineTo(5.0f)
                close()
            }
        }
        return _directionsCar!!
    }

private var _directionsCar: ImageVector? = null
