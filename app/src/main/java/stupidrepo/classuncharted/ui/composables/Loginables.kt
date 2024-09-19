package stupidrepo.classuncharted.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoginButton(onSubmit: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onSubmit,
        modifier = modifier,
        enabled = enabled
    ) {
        Text(text = "Log In")
    }
}

@Composable
fun LoginForm(onSubmit: (code: String, dob: String) -> Unit, buttonEnabled: Boolean = true) {
    val code = remember { mutableStateOf("") }
    val dob = remember { mutableStateOf("") }

    StatusCard(false) {
            OutlinedTextField(
                value = code.value,
                onValueChange = { code.value = it },
                label = {
                    Text("ClassCharts Code")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dob.value,
                onValueChange = { dob.value = it },
                label = {
                    Text("Date of Birth (e.g. 30/09/2011)")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LoginButton({
                    onSubmit(code.value, dob.value)
                },
                enabled = buttonEnabled,
                modifier = Modifier
                    .fillMaxWidth()
            )
    }
}

@Preview
@Composable
fun LoginFormPreview() {
    LoginForm({ _, _ -> })
}