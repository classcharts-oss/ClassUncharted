package stupidrepo.classuncharted

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import stupidrepo.classuncharted.data.mine.Account
import stupidrepo.classuncharted.data.mine.SavedAccount
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.AccountCard
import stupidrepo.classuncharted.ui.composables.AndroidAnnoyance
import stupidrepo.classuncharted.ui.composables.IconRowImageVector
import stupidrepo.classuncharted.ui.composables.IconRowMipmap
import stupidrepo.classuncharted.ui.composables.LoginButton
import stupidrepo.classuncharted.ui.composables.LoginForm
import stupidrepo.classuncharted.utils.DialogUtils

class MainActivity : FragmentActivity() {
    private var noCachedLogin: MutableState<Boolean> = mutableStateOf(false)
    private var showLoading = mutableStateOf(false)

    private var accounts by mutableStateOf(listOf<SavedAccount>())

//    private var activeDialog: AlertDialog? = null

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun LogInTopBar() {
        TopAppBar(title = {
            Column {
                Text(if (showLoading.value) "Logging in..." else "Log in")
                Text(
                    text = "You'll need your ClassCharts code for this.",
                    style = typography.bodySmall
                )
            }
        })
    }

    @Composable
    private fun LoginCard() {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            if(noCachedLogin.value)
                IconRowImageVector(
                    Icons.Rounded.ErrorOutline
                ) {
                    Text(
                        text = "Input your login details below to continue.",
                        style = typography.bodyMedium
                    )

                    LoginForm({ code, dob ->
                        doLogin(Account(code, dob))
                    }, buttonEnabled = !showLoading.value)
                }
            else IconRowMipmap(R.mipmap.ic_launcher) {
                    Text(
                        text = "Please press 'Log In' to continue.",
                        style = typography.bodyMedium
                    )

                    if(!noCachedLogin.value) LoginButton({
                        doCachedLogin()
                    }, enabled = !showLoading.value)
                }

            if(accounts.isNotEmpty() && !showLoading.value) {
                IconRowImageVector(Icons.Filled.Save) {
                    Text(
                        text = "Or, log in with a saved account:",
                        style = typography.bodyMedium
                    )

                    accounts.forEach {
                        AccountCard(it, "Log in") {
                            LoginManager.removeUserFromSavedLogins(this@MainActivity, it.account, {
                                DialogUtils.showDialog(
                                    this@MainActivity,
                                    "Account removed!",
                                    "Your account has been removed."
                                )
                                accounts -= it
                            }, { DialogUtils.showErrorDialog(this@MainActivity, it) })
                        }
                    }
                }
            }
        }
    }

    private fun doLogin(account: Account) {
        showLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            LoginManager.logInUser(
                this@MainActivity,
                account,
                onComplete = {
                    showLoading.value = false

                    startActivity(
                        Intent(this@MainActivity, HomeActivity::class.java)
                        .addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    or Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                    )
                },
                onError = {
                    DialogUtils.showErrorDialog(this@MainActivity, it)
                    showLoading.value = false
                }
            )
        }
    }

    private fun doCachedLogin() {
        LoginManager.getLoginDetails(this)?.let {
            doLogin(it)
        } ?: run {
            noCachedLogin.value = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        accounts = LoginManager.getSavedLogins(this)

        setContent {
            AndroidAnnoyance(
                modifier = Modifier
                    .padding(8.dp),
                topBar = {
                    LogInTopBar()
                }
            ) {
                LoginCard()

                if(showLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }

        doCachedLogin()
    }

//    override fun onDestroy() {
//        activeDialog?.dismiss()
//        super.onDestroy()
//    }

    @Preview
    @Composable
    private fun Previewable() {
        AndroidAnnoyance(
            modifier = Modifier.padding(8.dp),
            topBar = {
                LogInTopBar()
            }
        ) {
            LoginCard()
        }
    }
}