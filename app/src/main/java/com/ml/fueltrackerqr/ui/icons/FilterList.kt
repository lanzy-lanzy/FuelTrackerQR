package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * FilterList icon for use in the app
 */
public val Icons.Filled.FilterList: ImageVector
    get() {
        if (_filterList != null) {
            return _filterList!!
        }
        _filterList = materialIcon(name = "Filled.FilterList") {
            materialPath {
                moveTo(10.0f, 18.0f)
                horizontalLineTo(14.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(10.0f)
                verticalLineTo(18.0f)
                close()

                moveTo(3.0f, 6.0f)
                verticalLineTo(8.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(3.0f)
                close()

                moveTo(6.0f, 13.0f)
                horizontalLineTo(18.0f)
                verticalLineTo(11.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(13.0f)
                close()
            }
        }
        return _filterList!!
    }

private var _filterList: ImageVector? = null
