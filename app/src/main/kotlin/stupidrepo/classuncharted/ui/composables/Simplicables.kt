package stupidrepo.classuncharted.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import stupidrepo.classuncharted.HomeActivity
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
    modifier: Modifier = Modifier,
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
        LazyRow(
            modifier
                .background(Color.Transparent)
        ) {
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
    Box(modifier = Modifier.fillMaxWidth().height((31 + 16).dp).background(Color.Gray)) {}

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
fun DTSwitcherPreview() {
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
        tabs = (1..10 step 1).map { HomeActivity.SimpleTabItem(it.toString()) },
        selectedTab = 0,
        onTabSelected = {}
    )
}