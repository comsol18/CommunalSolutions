package comcomsol.wixsite.communalsolutions1.communalsolutions.Managers

import android.text.TextUtils
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class AuthManager {
    var mAuth: FirebaseAuth? = null

    private fun checkValidEmail(eText: EditText): Boolean {
        val email: EditText = eText
        val emailText: String = email.text.toString()

        // list of most common email addresses
        val validEmails: Array<String> = arrayOf("aol.com", "att.net", "comcast.net", "facebook.com", "gmail.com", "gmx.com", "googlemail.com",
                "google.com", "hotmail.com", "hotmail.co.uk", "mac.com", "me.com", "mail.com", "msn.com",
                "live.com", "sbcglobal.net", "verizon.net", "yahoo.com", "yahoo.co.uk", "email.com",
                "fastmail.fm", "games.com", "gmx.net", "hush.com", "hushmail.com", "icloud.com",
                "iname.com", "inbox.com", "lavabit.com", "love.com", "outlook.com", "pobox.com",
                "protonmail.com", "rocketmail.com", "safe-mail.net", "wow.com", "ygm.com", "ymail.com",
                "zoho.com", "yandex.com", "bellsouth.net", "charter.net", "cox.net", "earthlink.net",
                "juno.com", "btinternet.com", "virginmedia.com", "blueyonder.co.uk", "freeserve.co.uk",
                "live.co.uk", "ntlworld.com", "o2.co.uk", "orange.net", "sky.com", "talktalk.co.uk",
                "tiscali.co.uk", "virgin.net", "wanadoo.co.uk", "bt.com", "sina.com", "qq.com",
                "naver.com", "hanmail.net", "daum.net", "nate.com", "yahoo.co.jp", "yahoo.co.kr",
                "yahoo.co.id", "yahoo.co.in", "yahoo.com.sg", "yahoo.com.ph", "hotmail.fr", "live.fr",
                "laposte.net", "yahoo.fr", "wanadoo.fr", "orange.fr", "gmx.fr", "sfr.fr", "neuf.fr",
                "free.fr", "gmx.de", "hotmail.de", "live.de", "online.de", "t-online.de", "web.de",
                "yahoo.de", "libero.it", "virgilio.it", "hotmail.it", "aol.it", "tiscali.it", "alice.it",
                "live.it", "yahoo.it", "email.it", "tin.it", "poste.it", "teletu.it", "mail.ru",
                "rambler.ru", "yandex.ru", "ya.ru", "list.ru", "hotmail.be", "live.be", "skynet.be",
                "voo.be", "tvcablenet.be", "telenet.be", "hotmail.com.ar", "live.com.ar", "yahoo.com.ar",
                "fibertel.com.ar", "speedy.com.ar", "arnet.com.ar", "yahoo.com.mx", "live.com.mx",
                "hotmail.es", "hotmail.com.mx", "prodigy.net.mx", "yahoo.com.br", "hotmail.com.br",
                "outlook.com.br", "uol.com.br", "bol.com.br", "terra.com.br", "ig.com.br",
                "itelefonica.com.br", "r7.com", "zipmail.com.br", "globo.com", "globomail.com", "oi.com.br")

        // Check if field is empty
        if (TextUtils.isEmpty(emailText)) {
            email.error = "Field must not be empty."
            return false
        } // check if text entered is in email format
        else if (emailText.contains("@")) {
            // validate a university email if possible else check other email types
            if (emailText.contains(".edu")) return true
        } // Text is not in email format
        else {
            email.error = "Email must contain the '@' symbol."
            return false
        }

        // check if email is one of the most common types after confirming @ exists else not a valid email address
        // since these emails will cover most people, temporary spam emails can be avoided
        for (emailType in validEmails) {
            // if the email matches, return true confirming it is a valid email
            if (emailText.contains(emailType)) return true
        }

        // if after all checks, default to an invalid email
        email.error = "Please use an email from a more common domain."
        return false
    }

    private fun checkValidPassword(pText: EditText): Boolean {
        val pass: EditText = pText
        val passText: String = pass.text.toString()

        when {
        // Checks that the field isn't left empty
            TextUtils.isEmpty(passText) -> {
                pass.error = "Field cannot be empty."
                return false
            } // Checks that the password given is long enough
            passText.length < 6 -> {
                pass.error = "Password must be at least 6 characters in length."
                return false
            } // Checks password requirements
            else -> {
                val reqs = arrayOf(false, false, false)

                // Searches password for alphanumeric characters
                for (x in passText) {
                    when (x) {
                        in '0'..'9' -> reqs[0] = true
                        in 'a'..'z' -> reqs[1] = true
                        in 'A'..'Z' -> reqs[2] = true
                    }
                }

                // Checks that all requirements were met and gives an error otherwise
                for (req in reqs) {
                    if (!req) {
                        pass.error = "Password must contain upper and lower case\ncharacters and numbers."
                        return false
                    }
                }

                // Returns true when requirements met
                return true
            }
        }
    }

    fun checkCreds(eText: EditText, pText: EditText): Boolean {
        // Check to see if email and password are valid
        return checkValidEmail(eText) && checkValidPassword(pText) && isAuth()
    }

    fun isAuth(): Boolean { return mAuth != null }
}