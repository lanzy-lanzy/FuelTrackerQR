package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * History icon for use in the app
 */
public val Icons.Filled.History: ImageVector
    get() {
        if (_history != null) {
            return _history!!
        }
        _history = materialIcon(name = "Filled.History") {
            materialPath {
                // Clock face
                moveTo(13.0f, 3.0f)
                curveTo(8.03f, 3.0f, 4.0f, 7.03f, 4.0f, 12.0f)
                horizontalLineTo(1.0f)
                lineTo(4.89f, 15.89f)
                lineTo(4.96f, 16.03f)
                lineTo(9.0f, 12.0f)
                horizontalLineTo(6.0f)
                curveTo(6.0f, 8.13f, 9.13f, 5.0f, 13.0f, 5.0f)
                curveTo(16.87f, 5.0f, 20.0f, 8.13f, 20.0f, 12.0f)
                curveTo(20.0f, 15.87f, 16.87f, 19.0f, 13.0f, 19.0f)
                curveTo(11.07f, 19.0f, 9.32f, 18.21f, 8.06f, 16.94f)
                lineTo(6.64f, 18.36f)
                curveTo(8.27f, 19.99f, 10.51f, 21.0f, 13.0f, 21.0f)
                curveTo(17.97f, 21.0f, 22.0f, 16.97f, 22.0f, 12.0f)
                curveTo(22.0f, 7.03f, 17.97f, 3.0f, 13.0f, 3.0f)
                close()
                
                // Clock hands
                moveTo(12.5f, 8.0f)
                verticalLineTo(12.25f)
                lineTo(16.0f, 14.33f)
                lineTo(15.28f, 15.54f)
                lineTo(11.0f, 13.0f)
                verticalLineTo(8.0f)
                horizontalLineTo(12.5f)
                close()
            }
        }
        return _history!!
    }

private var _history: ImageVector? = null
