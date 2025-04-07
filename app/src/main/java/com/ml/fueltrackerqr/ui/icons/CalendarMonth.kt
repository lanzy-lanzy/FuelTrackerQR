package com.ml.fueltrackerqr.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * CalendarMonth icon for use in the app
 */
public val Icons.Filled.CalendarMonth: ImageVector
    get() {
        if (_calendarMonth != null) {
            return _calendarMonth!!
        }
        _calendarMonth = materialIcon(name = "Filled.CalendarMonth") {
            materialPath {
                // Calendar outline
                moveTo(19.0f, 4.0f)
                horizontalLineTo(18.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(16.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(2.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(5.0f)
                curveTo(3.9f, 4.0f, 3.0f, 4.9f, 3.0f, 6.0f)
                verticalLineTo(20.0f)
                curveTo(3.0f, 21.1f, 3.9f, 22.0f, 5.0f, 22.0f)
                horizontalLineTo(19.0f)
                curveTo(20.1f, 22.0f, 21.0f, 21.1f, 21.0f, 20.0f)
                verticalLineTo(6.0f)
                curveTo(21.0f, 4.9f, 20.1f, 4.0f, 19.0f, 4.0f)
                close()

                // Calendar body
                moveTo(19.0f, 20.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(10.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(20.0f)
                close()

                // Calendar header
                moveTo(19.0f, 8.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(6.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(8.0f)
                close()

                // Calendar content (month view)
                moveTo(9.0f, 14.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(12.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(14.0f)
                close()

                moveTo(13.0f, 14.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(12.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(14.0f)
                close()

                moveTo(17.0f, 14.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(12.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(14.0f)
                close()

                moveTo(9.0f, 18.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(9.0f)
                verticalLineTo(18.0f)
                close()

                moveTo(13.0f, 18.0f)
                horizontalLineTo(11.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(18.0f)
                close()

                moveTo(17.0f, 18.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(18.0f)
                close()
            }
        }
        return _calendarMonth!!
    }

private var _calendarMonth: ImageVector? = null
