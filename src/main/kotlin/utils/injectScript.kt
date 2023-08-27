package utils

/**
 * inject code into a html page.
 * example: ctx.html(injectScript("initializePolling(\"$sessionId\");"))
 */
fun injectScript(injectedCode: String): String = "<script>\n" +
        injectedCode +
        "</script>"