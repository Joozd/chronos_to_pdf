package email

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import java.util.Properties

object EmailSender {
    @Throws(MessagingException::class)
    fun sendEmail(to: String, subject: String, htmlContent: String, password: String) {
        // Setup mail server
        val properties = Properties()
        properties["mail.smtp.host"] = "smtp.hostnet.nl"
        properties["mail.smtp.port"] = "587"
        properties["mail.smtp.starttls.enable"] = "true"
        properties["mail.smtp.auth"] = "true"

        // Authentication
        val auth = object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("noreply@joozd.nl", password)
            }
        }

        // Create session
        val session = Session.getInstance(properties, auth)

        // Create email
        val message = MimeMessage(session)
        message.setFrom(InternetAddress("noreply@joozd.nl"))
        message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
        message.subject = subject
        message.setContent(htmlContent, "text/html")

        // Send email
        Transport.send(message)
    }
}