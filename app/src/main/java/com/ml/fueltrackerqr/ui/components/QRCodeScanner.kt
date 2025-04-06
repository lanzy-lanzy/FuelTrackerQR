package com.ml.fueltrackerqr.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.PrimaryLight
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * A stylized QR code scanner component with animation
 */
@Composable
fun StylizedQRScanner(
    modifier: Modifier = Modifier,
    title: String = "Scan QR Code",
    subtitle: String = "Position the QR code within the frame",
    scannerContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Scanner frame with animation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundDark
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Scanner content (camera view)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    scannerContent()
                }
                
                // Animated scanner line
                ScannerAnimation()
                
                // Corner markers
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = 40f
                    val strokeWidth = 4f
                    val cornerRadius = 8f
                    
                    // Top-left corner
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(Primary, PrimaryLight)),
                        start = Offset(0f, cornerRadius),
                        end = Offset(0f, cornerLength),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(Primary, PrimaryLight)),
                        start = Offset(cornerRadius, 0f),
                        end = Offset(cornerLength, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    
                    // Top-right corner
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(PrimaryLight, Primary)),
                        start = Offset(size.width, cornerRadius),
                        end = Offset(size.width, cornerLength),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(PrimaryLight, Primary)),
                        start = Offset(size.width - cornerRadius, 0f),
                        end = Offset(size.width - cornerLength, 0f),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    
                    // Bottom-left corner
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(Primary, PrimaryLight)),
                        start = Offset(0f, size.height - cornerRadius),
                        end = Offset(0f, size.height - cornerLength),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(Primary, PrimaryLight)),
                        start = Offset(cornerRadius, size.height),
                        end = Offset(cornerLength, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    
                    // Bottom-right corner
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(PrimaryLight, Primary)),
                        start = Offset(size.width, size.height - cornerRadius),
                        end = Offset(size.width, size.height - cornerLength),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(listOf(PrimaryLight, Primary)),
                        start = Offset(size.width - cornerRadius, size.height),
                        end = Offset(size.width - cornerLength, size.height),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * Animated scanner line
 */
@Composable
private fun ScannerAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scannerPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner position"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val y = scannerPosition * size.height
        
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Primary.copy(alpha = 0.0f),
                    Primary.copy(alpha = 0.8f),
                    Primary.copy(alpha = 0.8f),
                    Primary.copy(alpha = 0.0f)
                )
            ),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}
