package stupidrepo.classuncharted.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import stupidrepo.classuncharted.ui.theme.ClassUnchartedTheme

@Composable
private fun IconRow(icon: @Composable () -> Unit, content: @Composable () -> Unit) {
    Row {
        icon()

        Spacer(modifier = Modifier.padding(4.dp))

        Column {
            content()
        }
    }
}

@Composable
fun IconRowMipmap(mipmap: Int, content: @Composable () -> Unit) {
    IconRow(
        icon = { MipmapImage(mipmap) },
        content = content
    )
}

@Composable
fun IconRowDrawable(drawable: Int, content: @Composable () -> Unit, colourFilter: ColorFilter? = null) {
    IconRow(
        icon = { DrawableImage(drawable, colourFilter = colourFilter) },
        content = content
    )
}

@Composable
fun IconRowImageVector(imageVector: ImageVector, content: @Composable () -> Unit) {
    IconRow(
        icon = { ImageVectorImage(imageVector) },
        content = content
    )
}

@Composable
fun AndroidAnnoyance(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    ClassUnchartedTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = topBar,
                bottomBar = bottomBar
            ) { value ->
                Box(modifier = modifier
                    .fillMaxSize()
                    .padding(value)
                ) {
                    content()
                }
            }
        }
    }
}