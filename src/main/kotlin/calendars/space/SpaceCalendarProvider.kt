package calendars.space

import calendars.CalendarProvider
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.OneTimeString
import com.intellij.ide.passwordSafe.PasswordSafe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class SpaceCalendarProvider: CalendarProvider {
    override val isLoggedIn: Boolean
        get() = getCredentials() != null

    override fun load() {
        if (!isLoggedIn) { return }

        TODO("Not yet implemented")
    }

    private fun createCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes("IdeaMeetReminder::SpaceCalendar")
    }

    internal fun login(url: URL, token: String, callback: (String?) -> Unit) {
        val request = Request.Builder()
            .url("$url/api/http/team-directory/profiles/me")
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()

        val body = OkHttpClient().newCall(request).execute().use {
            try {
                if (it.isSuccessful && it.body != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<UsernameDto>(it.body?.string() ?: "")
                }
                else null
            } catch (e: Throwable) {
                null
            }
        }

        if (body?.username != null)
            PasswordSafe.instance.set(createCredentialAttributes(), Credentials(url.toString(), token))

        callback(body?.username)
    }

    private fun getCredentials() : OneTimeString? {
        return PasswordSafe.instance.get(createCredentialAttributes())?.password
    }

    @Serializable
    data class UsernameDto(val username: String)
}