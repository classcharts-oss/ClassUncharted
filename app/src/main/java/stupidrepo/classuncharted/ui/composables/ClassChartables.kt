package stupidrepo.classuncharted.ui.composables

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.R
import stupidrepo.classuncharted.data.api.Activity
import stupidrepo.classuncharted.data.api.Announcement
import stupidrepo.classuncharted.data.api.Attachment
import stupidrepo.classuncharted.data.api.Detention
import stupidrepo.classuncharted.data.api.DetentionAttended
import stupidrepo.classuncharted.data.api.DetentionType
import stupidrepo.classuncharted.data.api.Lesson
import stupidrepo.classuncharted.data.api.LessonPupilBehaviour
import stupidrepo.classuncharted.data.api.Teacher
import stupidrepo.classuncharted.data.mine.SavedAccount
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.activities.AnnouncementActivity
import stupidrepo.classuncharted.utils.AuthUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

var extra = mutableStateOf(false)

@OptIn(ObsoleteCoroutinesApi::class)
@Composable
fun ProgressCard(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    modifier: Modifier = Modifier,
    content: @Composable (@Composable () -> Unit) -> Unit
) {
    val currentTime = remember { mutableStateOf(LocalDateTime.now()) }
    val progressAnimated by animateFloatAsState(
        targetValue = (ChronoUnit.SECONDS.between(startTime, currentTime.value).toFloat() / ChronoUnit.SECONDS.between(startTime, endTime).toFloat()).coerceIn(0f, 1f),
        animationSpec = tween(500, 0, EaseInOut), label = "Progress Animation"
    )

    LaunchedEffect(Unit) {
        val ticker = ticker(delayMillis = 500)
        for (event in ticker) {
            currentTime.value = LocalDateTime.now()
        }
    }

    content {
        AnimatedVisibility(visible = !(currentTime.value.isBefore(startTime) || currentTime.value.isAfter(endTime))) {
            Column {
                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressAnimated },
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
fun StatusCard(
    clickable: Boolean = true,
    onClick: (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    left: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    OutlinedCard(
        modifier = if (clickable)
            modifier
                .padding(vertical = 4.dp)
                .clickable {
                    if (onClick != null) onClick()
                    else extra.value = !extra.value
                }
        else modifier.padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
            Row(
                modifier = Modifier
                    .padding(if (left == null) 0.dp else 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(left != null) {
                    left()
                    Spacer(modifier = Modifier.padding(8.dp))
                }

                Column(
                    modifier = Modifier
                        .padding(if (left != null) 0.dp else 16.dp)
                        .fillMaxWidth()
                ) {
                    content()
                }
            }
    }
}

@Composable
fun LessonCard(lesson: Lesson) {
    ProgressCard(
        startTime = lesson.format_start_time.atDate(LocalDateTime.now().toLocalDate()),
        endTime = lesson.format_end_time.atDate(LocalDateTime.now().toLocalDate())
    ) {
        StatusCard {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = lesson.subject_name,
                        style = typography.titleMedium
                    )

                    // FIXME: Ugly :P
                    Text(
                        text = if (extra.value) "${
                            lesson.format_start_time.format(
                                DateTimeFormatter.ofPattern(
                                    "HH:mm"
                                )
                            )
                        } - ${lesson.format_end_time.format(DateTimeFormatter.ofPattern("HH:mm"))}" else lesson.format_start_time.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        style = typography.bodyMedium
                    )
                }

                Text(
                    text = "with ${lesson.teacher_name} in ${lesson.room_name}",
                    style = typography.bodyMedium
                )

                it()
            }
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    StatusCard(
        left = {
            ScoreIcon(
                with(activity.polarity) {
                    when {
                        contains("positive") -> R.color.green
                        contains("negative") -> R.color.red
                        else -> android.R.color.holo_purple
                    }
                },
                activity.score.toString()
            )
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = activity.reason,
                style = typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = activity.format_timestamp.format(DateTimeFormatter.ofPattern(if (extra.value) "HH:mm:ss" else "HH:mm")),
                style = typography.bodyMedium,
                maxLines = 1,
            )
        }

        Text(
            text = "Awarded by ${activity.teacher_name ?: "nobody"}${if (activity.lesson_name != null) " in ${activity.lesson_name}" else ""}",
            style = typography.bodyMedium
        )

        if(activity.type == "detention") {
            val teacher = activity.teacher_name!!.split(" ")

            DetentionCard(
                detention = Detention(
                    -1,
                    activity.format_detention_date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    activity.detention_time ?: "Unknown",
                    0,
                    activity.detention_location!!,
                    Teacher(-1, teacher[0], teacher[1], teacher[2]),
                    LessonPupilBehaviour(activity.reason),
                    DetentionType(activity.detention_type!!),
                    DetentionAttended.unknown
                ),
                clickable = false
            )
        }
    }
}

@Composable
fun DetentionCard(detention: Detention, clickable: Boolean = true) {
    StatusCard(clickable) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = detention.lesson_pupil_behaviour.reason,
                style = typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = if (extra.value) 2 else 1,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = detention.formatted_date_time,
                style = typography.bodyMedium,
                maxLines = 1
            )
        }

        Text(
            text = detention.detention_type?.name ?: "Unknown type",
            style = typography.bodySmall
        )

        Text(
            text = "Set by ${detention.teacher.full_name} in ${detention.location}.",
            style = typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        DetentionStatusBadge(detention)
    }
}

@Composable
fun AnnouncementCard(announcement: Announcement) {
    val context = LocalContext.current

    StatusCard(true, {
        context.startActivity(
            Intent(context, AnnouncementActivity::class.java).apply {
                putExtra("announcement", announcement)
            },
        )
    }) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = announcement.title,
                style = typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = if(!extra.value) 1 else 2,
                modifier = Modifier
                    .weight(1f)
            )

            Spacer(
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = announcement.format_timestamp.format(DateTimeFormatter.ofPattern(if (extra.value) "HH:mm:ss" else "HH:mm")),
                style = typography.bodyMedium,
                maxLines = 1
            )
        }

        Text(
            text = "${announcement.format_timestamp.format(DateTimeFormatter.ofPattern("d MMM uuuu"))} - ${announcement.teacher_name}",
            style = typography.bodySmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinkText(text = "View") {
            context.startActivity(
                Intent(context, AnnouncementActivity::class.java).apply {
                    putExtra("announcement", announcement)
                },
            )
        }
//        LinkText(text = if(!extra.value) "Preview More" else "Preview Less") {
//            extra.value = !extra.value
//        }
    }
}

@Composable
fun AccountCard(account: SavedAccount, buttonText : String = "Switch", onRemove: () -> Unit = {}) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current as FragmentActivity

    val enabled = remember { mutableStateOf(true) }
    val shown = remember { mutableStateOf(false) }

    StatusCard(false) {
        Text(
            text = account.name,
            style = typography.titleMedium
        )

        ClickableText(
            text = buildAnnotatedString {
                if(!shown.value) {
                    withStyle(
                        SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("Show ClassCharts Code")
                    }
                } else {
                    withStyle(SpanStyle(MaterialTheme.colorScheme.onSurface)) {
                        append(account.account.code)
                        append(" ")
                    }

                    withStyle(SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.primary
                    )) {
                        append("Copy & Hide")
                    }
                }
            },
            style = typography.bodyMedium,
            onClick = {
                if(!shown.value) {
                    AuthUtils.showBiometricPrompt(context, { shown.value = true }, "Reveal Code", "You need to authenticate to\nreveal this login code.")
                } else {
                    shown.value = false

                    clipboardManager.setText(AnnotatedString(account.account.code))
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Button(
                onClick = {
                    AuthUtils.showBiometricPrompt(context, {
                        CoroutineScope(Dispatchers.Main).launch {
                            LoginManager.switchAccount(context, account, {}, { enabled.value = true })
                            enabled.value = false
                        }
                    }, buttonText, "You need to authenticate to ${buttonText.lowercase()} to this account.")
                },
                enabled = enabled.value && account.account != LoginManager.user?.account,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
            ) {
                Text(buttonText)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    AuthUtils.showBiometricPrompt(context, {
                        onRemove()
                    }, "Remove Account", "You need to authenticate to remove this account.")
                },
                enabled = enabled.value && account.account != LoginManager.user?.account,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.5f)
            ) {
                Text("Remove")
            }
        }
    }
}

@Composable
fun AttachmentsList(attachments: List<Attachment>) {
    val context = LocalContext.current

    StatusCard(false) {
        Text("Attachments", style = typography.titleMedium)

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = if (attachments.size > 1) Modifier.height(128.dp) else Modifier
        ) {
            items(attachments.size) { index ->
                val attachment = attachments[index]

                StatusCard(false) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = attachment.filename,
                            style = typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        LinkText(text = "Download") {
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(attachment.url)
                                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreIcon(colour: Int = android.R.color.holo_purple, text: String = "?") {
    val backgroundColor = colorResource(id = colour)

    Box(
        modifier = Modifier
            .size(38.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color.White,
            style = typography.titleMedium
        )
    }
}

@Composable
fun DetentionStatusBadge(detention: Detention) {
    val backgroundColor = when (detention.attended) {
        DetentionAttended.yes -> R.color.green
        DetentionAttended.no -> R.color.red
        DetentionAttended.pending -> R.color.yellow
        DetentionAttended.upscaled -> android.R.color.black

        else -> android.R.color.holo_purple
    }

    Box(
        modifier = Modifier
            .background(colorResource(backgroundColor), shape = RoundedCornerShape(25))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = detention.getAttendedString(),
            color = Color.White,
            style = typography.bodySmall
        )
    }
}

@Preview
@Composable
fun LessonCardPreview() {
    LessonCard(Lesson("Mr. Smith", "Maths", "X18", "2024-01-01T08:50:00+01:00", "2024-07-18T09:00:00+01:00"))
}

@Preview
@Composable
fun ActivityCardPreview() {
    Column {
        ActivityCard(
            Activity(
                123,
                3,
                "behaviour",
                "positive",
                "Good",
                "2024-01-01 15:21:03",
                "Maths",
                "Mr J Smith"
            )
        )

        ActivityCard(
            Activity(
                123,
                3,
                "detention",
                "negative",
                "Naughty!",
                "2024-01-01 15:21:03",
                "Maths",
                "Mr J Smith",
                "2024-01-01",
                "14:05",
                "X21",
                "Bullying"
            )
        )
    }
}

@Preview
@Composable
fun DetentionCardPreview() {
    DetentionCard(Detention(
        123,
        "01/01/2000",
        "14:05",
        30,
        "X21",
        Teacher(123, "Mr", "J", "Smith"),
        LessonPupilBehaviour("Chewing in Class"),
        DetentionType("Lunch Time Detention"),
        DetentionAttended.yes
    ))
}

@Preview
@Composable
fun AttachmentsListPreview() {
    AttachmentsList(
        listOf(
            Attachment("file1.pdf", "https://example.com/file1.pdf"),
            Attachment( "file2.pdf", "https://example.com/file2.pdf"),
        )
    )
}