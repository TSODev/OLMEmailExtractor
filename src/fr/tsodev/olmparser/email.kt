package fr.tsodev.olmparser

data class Email (val emailAddress: String, val type: String) {

    var domain: String = "domain"

    init {
        val regex_domain = "(?<=@)[^.]*.[^.]*(?=\\.)".toRegex()
        this.domain = regex_domain.find(this.emailAddress)?.value.toString().toLowerCase()
    }


}

open class XMLZone (val zone: String, val type: String) {

}