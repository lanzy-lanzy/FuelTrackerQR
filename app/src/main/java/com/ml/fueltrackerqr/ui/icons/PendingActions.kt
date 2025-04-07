package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * PendingActions icon for use in the app
 */
public val Icons.Filled.PendingActions: ImageVector
    get() {
        if (_pendingActions != null) {
            return _pendingActions!!
        }
        _pendingActions = materialIcon(name = "Filled.PendingActions") {
            materialPath {
                // Clipboard
                moveTo(17.0f, 12.0f)
                horizontalLineTo(7.0f)
                curveTo(6.45f, 12.0f, 6.0f, 12.45f, 6.0f, 13.0f)
                curveTo(6.0f, 13.55f, 6.45f, 14.0f, 7.0f, 14.0f)
                horizontalLineTo(17.0f)
                curveTo(17.55f, 14.0f, 18.0f, 13.55f, 18.0f, 13.0f)
                curveTo(18.0f, 12.45f, 17.55f, 12.0f, 17.0f, 12.0f)
                close()
                
                moveTo(17.0f, 16.0f)
                horizontalLineTo(7.0f)
                curveTo(6.45f, 16.0f, 6.0f, 16.45f, 6.0f, 17.0f)
                curveTo(6.0f, 17.55f, 6.45f, 18.0f, 7.0f, 18.0f)
                horizontalLineTo(17.0f)
                curveTo(17.55f, 18.0f, 18.0f, 17.55f, 18.0f, 17.0f)
                curveTo(18.0f, 16.45f, 17.55f, 16.0f, 17.0f, 16.0f)
                close()
                
                moveTo(19.0f, 3.0f)
                horizontalLineTo(14.82f)
                curveTo(14.4f, 1.84f, 13.3f, 1.0f, 12.0f, 1.0f)
                curveTo(10.7f, 1.0f, 9.6f, 1.84f, 9.18f, 3.0f)
                horizontalLineTo(5.0f)
                curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                verticalLineTo(19.0f)
                curveTo(3.0f, 20.1f, 3.9f, 21.0f, 5.0f, 21.0f)
                horizontalLineTo(19.0f)
                curveTo(20.1f, 21.0f, 21.0f, 20.1f, 21.0f, 19.0f)
                verticalLineTo(5.0f)
                curveTo(21.0f, 3.9f, 20.1f, 3.0f, 19.0f, 3.0f)
                close()
                
                // Clock face
                moveTo(12.0f, 3.0f)
                curveTo(12.55f, 3.0f, 13.0f, 3.45f, 13.0f, 4.0f)
                curveTo(13.0f, 4.55f, 12.55f, 5.0f, 12.0f, 5.0f)
                curveTo(11.45f, 5.0f, 11.0f, 4.55f, 11.0f, 4.0f)
                curveTo(11.0f, 3.45f, 11.45f, 3.0f, 12.0f, 3.0f)
                close()
                
                moveTo(17.0f, 8.0f)
                horizontalLineTo(7.0f)
                curveTo(6.45f, 8.0f, 6.0f, 8.45f, 6.0f, 9.0f)
                curveTo(6.0f, 9.55f, 6.45f, 10.0f, 7.0f, 10.0f)
                horizontalLineTo(17.0f)
                curveTo(17.55f, 10.0f, 18.0f, 9.55f, 18.0f, 9.0f)
                curveTo(18.0f, 8.45f, 17.55f, 8.0f, 17.0f, 8.0f)
                close()
            }
        }
        return _pendingActions!!
    }

private var _pendingActions: ImageVector? = null
