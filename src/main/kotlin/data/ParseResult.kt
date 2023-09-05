package data

data class ParseResult(val filesUploaded: Int, val filesParsed: Int, val flightsParsed: Int){
    val filesFailed = filesUploaded - filesParsed
}
