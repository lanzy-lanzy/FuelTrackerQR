package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.RequestStatus

/**
 * A chip component to display request status
 */
@Composable
fun RequestStatusChip(
    status: RequestStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        RequestStatus.PENDING -> Pair(Color(0xFF3E2723).copy(alpha = 0.7f), Color.White)
        RequestStatus.APPROVED -> Pair(Color(0xFF1B5E20).copy(alpha = 0.7f), Color.White)
        RequestStatus.DISPENSED -> Pair(Color(0xFF0D47A1).copy(alpha = 0.7f), Color.White)
        RequestStatus.DECLINED -> Pair(Color(0xFFB71C1C).copy(alpha = 0.7f), Color.White)
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
