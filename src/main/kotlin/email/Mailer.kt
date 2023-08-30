package email

import data.Config
import data.R
import data.flightsdata.EncryptedUserData
import data.logindata.LoginWithKey

class Mailer {
    suspend fun sendNewUserMail(emailAddress: String, loginWithKey: LoginWithKey){
        EmailSender.sendEmail(
            emailAddress,
            "Chronos to PDF login Link",
            addLinkToTemplate(R.textFile("email_template.html")!!, loginWithKey),
            Config.getInstance()["password"]!!
        )
    }

    private fun addLinkToTemplate(template: String, loginWithKey: LoginWithKey): String =
        template.replace(TEMPLATE_PLACEHOLDER_TEXT, makeLoginLink(loginWithKey))

    private fun makeLoginLink(loginWithKey: LoginWithKey): String =
        "http://127.0.0.1:7070/?UID=${loginWithKey.uid}&KEY=${loginWithKey.base64Key}"

    companion object{
        private const val TEMPLATE_PLACEHOLDER_TEXT = "[PLACEHOLDER_LOGIN_LINK_GOES_HERE]"
    }
}