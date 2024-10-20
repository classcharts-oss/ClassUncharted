package stupidrepo.classuncharted.managers

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import stupidrepo.classuncharted.HomeActivity
import stupidrepo.classuncharted.JSON
import stupidrepo.classuncharted.data.api.User
import stupidrepo.classuncharted.data.mine.Account
import stupidrepo.classuncharted.data.mine.SavedAccount

private const val SAVED_LOGINS_PREFS = "saved_logins"
private const val CURRENT_LOGIN_PREFS = "current_login"

private const val CURRENT_LOGIN_KEY = "current"
private const val SAVED_LOGINS_KEY = "saved"

object LoginManager {
    private const val TAG: String = "LoginManager"

    var user: User? = null

    fun getSavedLogins(context: Context) : List<SavedAccount> {
        val sharedPreferences = context.getSharedPreferences(
            SAVED_LOGINS_PREFS,
            Context.MODE_PRIVATE
        )

        val saved: String? = sharedPreferences.getString(SAVED_LOGINS_KEY, null)

        return if(saved != null) {
            JSON.decodeFromString<List<SavedAccount>>(saved)
        } else {
            listOf()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getLoginDetails(context: Context): Account? {
        val sharedPreferences = context.getSharedPreferences(
            CURRENT_LOGIN_PREFS,
            Context.MODE_PRIVATE
        )

        var current: Account? = null

        try {
            sharedPreferences.getString(CURRENT_LOGIN_KEY, null)?.let {
                current = JSON.decodeFromString<Account>(it)
            }
        } catch (e: MissingFieldException) {
            logOutUser(context)
        }

        return current
    }

    private fun addUserToSavedLogins(context: Context): SavedAccount {
        val loginDetails = getLoginDetails(context) ?: throw Exception("User not logged in.")

        val sharedPreferences = context.getSharedPreferences(
            SAVED_LOGINS_PREFS,
            Context.MODE_PRIVATE
        )

        val savedAccounts = getSavedLogins(context).toMutableList()

        if(savedAccounts.any { it.account == loginDetails }) {
            throw Exception("Account already saved.")
        }

        val acc = SavedAccount(loginDetails, user!!.name)
        savedAccounts.add(acc)
        sharedPreferences.edit().putString(SAVED_LOGINS_KEY, JSON.encodeToString(savedAccounts)).apply()

        Log.d(TAG, "addUserToSavedLogins: Account saved.")

        return acc
    }

    fun addUserToSavedLogins(context: Context, onComplete: (SavedAccount) -> Unit, onError: (String) -> Unit) {
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    onComplete(addUserToSavedLogins(context))
                } catch (e: Exception) {
                    onError(e.message!!)
                }
            }
        }
    }

    private fun removeUserFromSavedLogins(context: Context, account: Account) {
        val sharedPreferences = context.getSharedPreferences(
            SAVED_LOGINS_PREFS,
            Context.MODE_PRIVATE
        )

        val savedAccounts = getSavedLogins(context).toMutableList()

        savedAccounts.removeIf { it.account == account }
        sharedPreferences.edit().putString(SAVED_LOGINS_KEY, JSON.encodeToString(savedAccounts)).apply()

        Log.d(TAG, "removeUserFromSavedLogins: Account removed.")
    }

    fun removeUserFromSavedLogins(context: Context, account: Account, onComplete: () -> Unit, onError: (String) -> Unit) {
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    removeUserFromSavedLogins(context, account)
                    onComplete()
                } catch (e: Exception) {
                    onError(e.message!!)
                }
            }
        }
    }

    fun logInUser(
        context: Context,
        account: Account,
        onComplete: () -> Unit,
        onError: (String) -> Unit,
        onEnd: () -> Unit = {},
        logInAnyway: Boolean = false
    ) {
        if(user != null && !logInAnyway) {
            Log.e(TAG, "LogInUser: User is already logged in.")
            onComplete()

            return
        }

        account.code = account.code.trim()

        val body = FormBody.Builder()
            .add("code", account.code)
            .add("dob", account.dob)
            .build()

        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    APIManager.POST("login", body).let { response ->
                        context.getSharedPreferences(CURRENT_LOGIN_PREFS, Context.MODE_PRIVATE)
                            .edit().apply {
                                putString(CURRENT_LOGIN_KEY, JSON.encodeToString(account))
                                apply()
                            }

                        user = JSON.decodeFromString<User>(response.jsonObject["data"].toString())
                        user?.session_id = response.jsonObject["meta"]!!.jsonObject["session_id"]?.jsonPrimitive?.content
                        user?.account = account

                        onComplete()
                        onEnd()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "logInUser: Error!")
                    onError(e.message!!)
                    onEnd()
                }
            }
        }
    }

    fun logOutUser(context: Context) {
        context.getSharedPreferences(
            CURRENT_LOGIN_PREFS,
            Context.MODE_PRIVATE
        ).edit().clear().apply()

        user = null
    }

    fun switchAccount(context: Context, account: SavedAccount, onComplete: () -> Unit, onError: () -> Unit) {
        context.getSharedPreferences(
            CURRENT_LOGIN_PREFS,
            Context.MODE_PRIVATE
        ).edit().apply {
            putString(CURRENT_LOGIN_KEY, JSON.encodeToString(account.account))
        }.apply()

        logInUser(context, account.account, {
            context.startActivity(Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            onComplete()
        }, {
            onError()
        }, logInAnyway = true)
    }
}