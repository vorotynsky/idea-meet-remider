package calendars.space

import calendars.CalendarItem
import calendars.CalendarProvider
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.OneTimeString
import com.intellij.ide.passwordSafe.PasswordSafe
import calendars.executeAsync
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class SpaceCalendarProvider: CalendarProvider {
    override val isLoggedIn: Boolean
        get() = getCredentials() != null

    override suspend fun load() : List<CalendarItem> {
        val url = getBaseUrl()
        val token = getCredentials()
        if (!isLoggedIn || (url == null || token == null)) { return listOf() }

        val profile = me(URL(url), token)?.id
        val now = Clock.System.now().toString()

        val request = Request.Builder()
            .url("$url/api/http/calendars/meetings?profiles=$profile&startingAfter=$now")
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()

        val body = OkHttpClient().newCall(request).executeAsync().use {
            runCatching {
                if (it.isSuccessful && it.body != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<SpaceCalendarItemsDto>(it.body?.string() ?: "")
                }
                else null.also { _ -> System.err.println(it.message) }
            }.onFailure { it.printStackTrace() }.getOrNull()
        } ?: return listOf()

        return body.data.map {
            val startDateTime = Instant.parse(it.occurrenceRule.start.iso)
                .toLocalDateTime(TimeZone.of(it.occurrenceRule.timezone.id))
            val finishDateTime = Instant.parse(it.occurrenceRule.end.iso)
                .toLocalDateTime(TimeZone.of(it.occurrenceRule.timezone.id))

            SpaceCalendarItem(
                title = it.summary,
                startDateTime = startDateTime,
                finishDateTime = finishDateTime,
                url = url + "/meetings/" + it.id
            )
        }
    }

    private fun createCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes("IdeaMeetReminder::SpaceCalendar")
    }

    internal suspend fun login(url: URL, token: String, callback: (String?) -> Unit) {
        val body = me(url, OneTimeString(token))

        if (body?.username != null)
            PasswordSafe.instance.set(createCredentialAttributes(), Credentials(url.toString(), token))

        callback(body?.username)
    }

    private suspend fun me(url: URL, token: OneTimeString): UsernameDto? {
        val request = Request.Builder()
            .url("$url/api/http/team-directory/profiles/me")
            .header("Authorization", "Bearer $token")
            .header("Accept", "application/json")
            .build()

        val body = OkHttpClient().newCall(request).executeAsync().use {
            runCatching {
                if (it.isSuccessful && it.body != null) {
                    val json = Json { ignoreUnknownKeys = true }
                    json.decodeFromString<UsernameDto>(it.body?.string() ?: "")
                } else null.also { _ -> System.err.println(it.message) }
            }.onFailure { it.printStackTrace() }.getOrNull()
        }
        return body
    }

    private fun getCredentials() : OneTimeString? {
        return PasswordSafe.instance.get(createCredentialAttributes())?.password
    }

   private fun getBaseUrl() : String? {
        return PasswordSafe.instance.get(createCredentialAttributes())?.userName
    }

    @Serializable
    data class UsernameDto(val id: String, val username: String)
}