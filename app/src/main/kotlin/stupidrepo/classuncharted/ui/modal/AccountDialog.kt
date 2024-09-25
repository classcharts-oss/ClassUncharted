package stupidrepo.classuncharted.ui.modal

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import stupidrepo.classuncharted.managers.LoginManager
import stupidrepo.classuncharted.ui.composables.AccountCard
import stupidrepo.classuncharted.ui.theme.ClassUnchartedTheme
import stupidrepo.classuncharted.utils.DialogUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
open class AccountDialog: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.Default)
            setContent {
                val savedLogins = remember { mutableStateOf(LoginManager.getSavedLogins(requireContext())) }

                ClassUnchartedTheme {
                    Surface(modifier = Modifier.fillMaxSize(.75f)) {
                        Scaffold(
                            floatingActionButton = {
                                FloatingActionButton(onClick = {
                                    LoginManager.addUserToSavedLogins(requireContext(), {
                                        savedLogins.value = LoginManager.getSavedLogins(requireContext()) // note: I know calling this over and over is so bad, but for some reason it's the only way to make this stupid composable view will recompose itself afaik!!!
                                    }, { DialogUtils.showErrorDialog(requireContext(), it) })
                                }) {
                                    Icon(Icons.Rounded.Save, "Save")
                                }
                            }
                        ) { _ ->
                            Box(modifier = Modifier.padding(8.dp)) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    items(savedLogins.value.size) { login ->
                                        AccountCard(account = savedLogins.value[login], onRemove = {
                                            LoginManager.removeUserFromSavedLogins(requireContext(), savedLogins.value[login].account,
                                                {
                                                    savedLogins.value = LoginManager.getSavedLogins(requireContext())
                                                }, { DialogUtils.showErrorDialog(requireContext(), it) })
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(
            STYLE_NO_TITLE,
            R.style.Theme_Material_Dialog_Alert
        )
    }
}