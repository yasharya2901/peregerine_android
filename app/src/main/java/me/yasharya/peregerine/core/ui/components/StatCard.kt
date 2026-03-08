package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StatCard (
    label: String,
    count: Int,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .align(Alignment.Start),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = dotColor
                ) { }
            }


            Text(text = count.toString(), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = dotColor)

            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)


        }
    }
}


@Preview
@Composable
fun StatCardPreview() {
    Row(
        modifier = Modifier
            .height(200.dp)
            .width(800.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCard(
            label = "Total Products",
            count = 120,
            dotColor = Color.Green,
            modifier = Modifier.size(120.dp)
        )

        StatCard(
            label = "Low Stock",
            count = 30,
            dotColor = Color.Yellow,
            modifier = Modifier.size(120.dp)
        )

        StatCard(
            label = "Out of Stock",
            count = 12,
            dotColor = Color.Red,
            modifier = Modifier.size(120.dp)
        )
    }

}
