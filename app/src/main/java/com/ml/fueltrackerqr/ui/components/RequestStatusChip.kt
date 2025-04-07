package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.theme.StatusApproved
import com.ml.fueltrackerqr.ui.theme.StatusDeclined
import com.ml.fueltrackerqr.ui.theme.StatusDispensed
import com.ml.fueltrackerqr.ui.theme.StatusPending

/**
 * Chip displaying the status of a request
 * 
 * @param status Status of the request
 */
@Composable
fun RequestStatusChip(status: RequestStatus) {
    val (backgroundColor, textColor) = when (status) {
        RequestStatus.PENDING -> Pair(Color(0xFFFFF9C4), Color(0xFF8C6D1F))
        RequestStatus.APPROVED -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        RequestStatus.DECLINED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        RequestStatus.DISPENSED -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
    }
    
    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
                    .padding(end = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(color = textColor)
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = status.name,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}
