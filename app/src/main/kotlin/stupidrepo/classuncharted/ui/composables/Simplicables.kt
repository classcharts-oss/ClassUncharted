package stupidrepo.classuncharted.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import stupidrepo.classuncharted.HomeActivity
import stupidrepo.classuncharted.isContentLoading
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

@Composable
fun BradTabSwitcher(
    tabs: List<HomeActivity.SimpleTabItem>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyRow {
            tabs.forEachIndexed { index, tabItem ->
                val shape = when (index) {
                    0 -> RoundedCornerShape(topStart = 54.dp, bottomStart = 54.dp)
                    tabs.size - 1 -> RoundedCornerShape(topEnd = 54.dp, bottomEnd = 54.dp)
                    else -> RoundedCornerShape(0.dp)
                }
                item {
                    Button(
                        onClick = { onTabSelected(index) },
                        shape = shape,
                        colors = ButtonDefaults.buttonColors(
                            if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .widthIn(min = 10.dp)
                            .height(32.dp)
                    ) {
                        Text(tabItem.title, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HWSwitcherPreview() {
    BradTabSwitcher(
        tabs = listOf(
            HomeActivity.SimpleTabItem("To-do"),
            HomeActivity.SimpleTabItem("Completed")
        ),
        selectedTab = 0,
        onTabSelected = {}
    )
}

@Preview
@Composable
fun ShortSwitcherPreview() {
    BradTabSwitcher(
        tabs = listOf(
            HomeActivity.SimpleTabItem("Past"),
            HomeActivity.SimpleTabItem("Today"),
            HomeActivity.SimpleTabItem("Future")
        ),
        selectedTab = 0,
        onTabSelected = {}
    )
}

@Preview
@Composable
fun LongSwitcherPreview() {
    BradTabSwitcher(
        tabs = (1..10 step 1).map { HomeActivity.SimpleTabItem("Tab Item $it") },
        selectedTab = 0,
        onTabSelected = {}
    )
}

@Composable
fun LoadingIndicator(size: Dp, modifier: Modifier = Modifier) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = LinearEasing),
            )
        )
    }

    Box(
        Modifier
            .padding(0.dp, 12.dp)
            .fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(
            visible = isContentLoading(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(size * 2)
                    )
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = "Rotating Icon",
                        modifier = modifier
                            .rotate(rotation.value)
                            .size(size),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text("Loading data, please wait!")
            }
        }
    }
}

@Composable
fun IconTabSwitcher(
    icons: List<ImageVector>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        Modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = RoundedCornerShape(999.dp),
            )
            .padding(4.dp, 6.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyRow {
            icons.forEachIndexed { index, icon ->
                item {
                    IconButton(
                        onClick = { onTabSelected(index) },
                        colors = IconButtonColors(
                            containerColor = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTab == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(48.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = if (selectedTab == index) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun IconTabSwitcherPreview() {
    IconTabSwitcher(
        icons = listOf(
            Icons.Rounded.Home,
            Icons.Rounded.Search,
            Icons.Rounded.Notifications,
            Icons.Rounded.Settings
        ),
        selectedTab = 0,
        onTabSelected = {}
    )
}