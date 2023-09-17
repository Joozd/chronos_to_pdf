package email

import data.Config
import data.R
import data.logindata.LoginWithKey
import global.Values

class Mailer {
    private val config get() = Config.getInstance()
    fun sendNewUserMail(emailAddress: String, loginWithKey: LoginWithKey){
        EmailSender.sendEmail(
            emailAddress,
            "Chronos to PDF login Link",
            addLinkToTemplate(R.textFile("login_template.html") ?: error ("Email template not found!"), makeLoginLink(loginWithKey)),
            config["password"]!!
        )
    }

    fun sendResetMail(emailAddress: String, loginWithKey: LoginWithKey){
        EmailSender.sendEmail(
            emailAddress,
            "Chronos to PDF Account Reset Link",
            addLinkToTemplate(R.textFile("reset_template.html") ?: error ("Email template not found!"), makeResetLink(loginWithKey)),
            config["password"]!!
        )
    }

    private fun addLinkToTemplate(template: String, link: String): String =
        template.replace(TEMPLATE_PLACEHOLDER_TEXT, link)

    private fun makeLoginLink(loginWithKey: LoginWithKey): String =
        "${config["hostname"]}/?${Values.UID}=${loginWithKey.uid}&${Values.KEY}=${loginWithKey.base64Key}"

    private fun makeResetLink(loginWithKey: LoginWithKey): String =
        "${config["hostname"]}/?${Values.RESET}=true&${Values.UID}=${loginWithKey.uid}&${Values.KEY}=${loginWithKey.base64Key}"

    companion object{
        private const val TEMPLATE_PLACEHOLDER_TEXT = "[PLACEHOLDER_LOGIN_LINK_GOES_HERE]"
    }
}