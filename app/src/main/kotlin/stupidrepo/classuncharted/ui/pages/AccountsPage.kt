package stupidrepo.classuncharted.ui.pages

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import stupidrepo.classuncharted.data.mine.SavedAccount
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.AccountCard
import stupidrepo.classuncharted.ui.composables.CenteredText
import stupidrepo.classuncharted.utils.DialogUtils

class AccountsPage : BradPage {
    private val TAG = "AccountsPage"

    private var accounts by mutableStateOf(listOf<SavedAccount>())

    @Composable
    override fun Content(modifier: Modifier, activity: Activity) {
        Column {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (accounts.isEmpty())
                    item { CenteredText("No saved accounts found!") }
                else items(accounts.size) { index ->
                    AccountCard(account = accounts[index]) {
                        LoginManager.removeUserFromSavedLogins(
                            activity,
                            accounts[index].account,
                            {
                                DialogUtils.showDialog(
                                    activity,
                                    "Account removed!",
                                    "Your account has been removed."
                                )
                                accounts -= accounts[index]
                            }, {
                                DialogUtils.showErrorDialog(activity, it)
                            }
                        )
                    }
                }
            }
            Button(
                onClick = {
                    LoginManager.addUserToSavedLogins(activity, {
                        DialogUtils.showDialog(
                            activity,
                            "Account saved!",
                            "Your account has been saved."
                        )
                        LoginManager.user!!.let { user ->
                            accounts += SavedAccount(user.account!!, user.name)
                        }
                    }, { DialogUtils.showErrorDialog(activity, it) })
                },
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text("Save this account")
            }
        }
    }

    override fun refresh(activity: Activity) {
        accounts = LoginManager.getSavedLogins(activity)
    }
}