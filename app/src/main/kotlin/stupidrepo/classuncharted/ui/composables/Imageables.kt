package stupidrepo.classuncharted.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap

@Composable
fun MipmapImage(id: Int) {
    ResourcesCompat.getDrawable(
        LocalContext.current.resources,
        id, LocalContext.current.theme
    )?.toBitmap()?.asImageBitmap()?.let {
        Image(
            bitmap = it,
            contentDescription = "No content description",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun DrawableImage(id: Int, colourFilter: ColorFilter? = null) {
    Image(
        painter = painterResource(id),
        contentDescription = "No content description",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        colorFilter = colourFilter
    )
}

@Composable
fun ImageVectorImage(imageVector: ImageVector) {
    Image(
        imageVector = imageVector,
        contentDescription = "No content description",
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}