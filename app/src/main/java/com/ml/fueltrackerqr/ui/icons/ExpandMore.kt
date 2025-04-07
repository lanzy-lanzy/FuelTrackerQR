package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * ExpandMore icon for use in the app
 */
public val Icons.Filled.ExpandMore: ImageVector
    get() {
        if (_expandMore != null) {
            return _expandMore!!
        }
        _expandMore = materialIcon(name = "Filled.ExpandMore") {
            materialPath {
                moveTo(16.6f, 8.6f)
                lineTo(12.0f, 13.2f)
                lineTo(7.4f, 8.6f)
                lineTo(6.0f, 10.0f)
                lineTo(12.0f, 16.0f)
                lineTo(18.0f, 10.0f)
                close()
            }
        }
        return _expandMore!!
    }

private var _expandMore: ImageVector? = null
