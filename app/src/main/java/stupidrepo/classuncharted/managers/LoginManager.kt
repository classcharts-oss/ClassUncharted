package stupidrepo.classuncharted.managers

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.FormBody
import stupidrepo.classuncharted.HomeActivity
import stupidrepo.classuncharted.data.api.User
import stupidrepo.classuncharted.data.mine.Account
import stupidrepo.classuncharted.data.mine.SavedAccount
import stupidrepo.classuncharted.service.NotificationService
import stupidrepo.classuncharted.utils.JSONUtils

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
            JSONUtils.gson.fromJson(saved, Array<SavedAccount>::class.java)
                .toList()
        } else {
            listOf()
        }
    }

    fun getLoginDetails(context: Context): Account? {
        val sharedPreferences = context.getSharedPreferences(
            CURRENT_LOGIN_PREFS,
            Context.MODE_PRIVATE
        )

        val current: String? = sharedPreferences.getString(CURRENT_LOGIN_KEY, null)

        if (current != null) {
            return JSONUtils.gson.fromJson(current, Account::class.java)
        }

        return null
    }

    private fun addUserToSavedLogins(context: Context) {
        val loginDetails = getLoginDetails(context)!!

        val sharedPreferences = context.getSharedPreferences(
            SAVED_LOGINS_PREFS,
            Context.MODE_PRIVATE
        )

        val saved: String? = sharedPreferences.getString(SAVED_LOGINS_KEY, null)

        val savedAccounts = if(saved != null)
            JSONUtils.gson.fromJson(saved, Array<SavedAccount>::class.java)
                .toMutableList()
        else mutableListOf()

        if(savedAccounts.any { it.account == loginDetails }) {
            throw Exception("Account already saved.")
        }

        savedAccounts.add(SavedAccount(loginDetails, user!!.name))
        sharedPreferences.edit().putString(SAVED_LOGINS_KEY, JSONUtils.gson.toJson(savedAccounts)).apply()

        Log.d(TAG, "addUserToSavedLogins: Account saved.")
    }

    fun addUserToSavedLogins(context: Context, onComplete: () -> Unit, onError: (String) -> Unit) {
        runBlocking {
            launch(Dispatchers.IO) {
                try {
                    addUserToSavedLogins(context)
                    onComplete()
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

        val saved: String? = sharedPreferences.getString(SAVED_LOGINS_KEY, null)

        val savedAccounts = if(saved != null)
            JSONUtils.gson.fromJson(saved, Array<SavedAccount>::class.java)
                .toMutableList()
        else mutableListOf()

        savedAccounts.removeIf { it.account == account }
        sharedPreferences.edit().putString(SAVED_LOGINS_KEY, JSONUtils.gson.toJson(savedAccounts)).apply()

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

    suspend fun logInUser(
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
                    APIManager.POST("login", body)?.let { response ->
                        if (response.has("error")) {
                            onError(response.get("error").asString)
                            onEnd()

                            return@launch
                        }

                        context.getSharedPreferences(CURRENT_LOGIN_PREFS, Context.MODE_PRIVATE)
                            .edit().apply {
                                putString(CURRENT_LOGIN_KEY, JSONUtils.gson.toJson(account))

                                apply()
                            }

                        user = JSONUtils.gson.fromJson(
                            response.get("data").asJsonObject,
                            User::class.java
                        )
                        user?.session_id =
                            response.get("meta").asJsonObject.get("session_id").asString
                        user?.account = account

                        onComplete()
                        onEnd()
                    } ?: run {
                        onError("Couldn't log in. Check your internet connection.")
                    }
                } catch (e: Exception) {
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

        context.stopService(Intent(context, NotificationService::class.java))
    }

    suspend fun switchAccount(context: Context, account: SavedAccount, onComplete: () -> Unit, onError: () -> Unit) {
        context.getSharedPreferences(
            CURRENT_LOGIN_PREFS,
            Context.MODE_PRIVATE
        ).edit().apply {
            putString(CURRENT_LOGIN_KEY, JSONUtils.gson.toJson(account.account))
        }.apply()

        logInUser(context, account.account, {
            context.startActivity(Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            onComplete()
        }, {
            onError()
        }, logInAnyway = true)
    }
}