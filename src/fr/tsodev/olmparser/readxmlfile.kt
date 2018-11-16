package fr.tsodev.olmparser

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

val FILE_REPOSITORY = "/Volumes/TOSHIBA 1T/Record Mgmt/Outlook Archive 2016-2.olm Folder/Local/com.microsoft.__Messages/Aug2Dec2018/"
val OUTPUT_FILE = "/Users/thierry/Desktop/OLMAddresses.csv"

object EMAIL_FROM: XMLZone("OPFMessageCopyFromAddresses","From" )
object EMAIL_REPLY: XMLZone("OPFMessageCopyReplyToAddresses","ReplyTo" )
object EMAIL_SENDER: XMLZone("OPFMessageCopySenderAddress","Sender" )
object EMAIL_COPY: XMLZone("OPFMessageCopyToAddresses","CopyTo" )
val EMAIL_ZONES = arrayOf(EMAIL_FROM, EMAIL_REPLY, EMAIL_SENDER, EMAIL_COPY)
val HTML_BODY = "OPFMessageCopyHTMLBody"


// TODO - Replace Command Line Argument Parser library
class MyArgs(parser: ArgParser) {



    val InFolder by parser.positional(
            "SOURCE",
            "source folder name")
    val OutFile by parser.positional(
            "DEST",
            "destination filename")
}

fun main(args: Array<String>) = mainBody {
    ArgParser(args).parseInto(::MyArgs).run {

        val source = InFolder.substringAfter('=')
        val dest = OutFile.substringAfter('=')

        println("Source : ${source}")
        println("Destination : ${dest}")
        println("-----")

        val CSV_File_Path = dest
        val writer = Files.newBufferedWriter(Paths.get(CSV_File_Path))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Domain", "EmailAddress", "Zone"))


        val EmailAddressList: MutableList<Email> = mutableListOf<Email>()

        println("Working on so many files that you need to be patient...")
        println("I will create a CSV file with the result at the end of this process.")

        println("[1 of 2] Extracting Email Addresses from the files")
        for (file in File(source).walk()) {
            if (file.extension == "xml") {
                AddToListIfNotAlreadyIn(EmailAddressList, ExtractEmailAddressesFromXmlFile(file.absolutePath))
            }
        }

        println("[2 of 2 ] Writing Addresses in CSV file")
//    val FilteredEmailAddressList = EmailAddressList.filter { it.domain == "bmc"}
        val FilteredEmailAddressList = EmailAddressList.filter { true }
        for (email in FilteredEmailAddressList) {
            csvPrinter.printRecord(email.domain, email.emailAddress, email.type)
        }

        csvPrinter.flush()
        csvPrinter.close()

        println("Done")
    }
}

fun readXml(filename: String): Document {
    val xmlFile = File(filename)
    val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
    return xmlDoc
}

fun readFile(filename: String): String{
    val file = File(filename)
    return file.readText(Charsets.UTF_8)
}

fun ExtractEmailAddressesFromXmlFile(filename: String): MutableList<Email> {

    val EmailAddressList: MutableList<Email> = mutableListOf<Email>()
    val doc = readXml(filename)

   for (xmlzone in EMAIL_ZONES){
        AddEmailAddressFromOLMNodeList(EmailAddressList = EmailAddressList, OLMNodeList = getAddresses(doc, zone = xmlzone.zone), type = xmlzone.type)
    }

    return EmailAddressList

}

fun AddEmailAddressFromOLMNodeList(EmailAddressList: MutableList<Email>, OLMNodeList: NodeList, type: String) {
    for (i in 0 until OLMNodeList.length) {
        val AddrNode = getEmailAddressesNode(OLMNodeList,i)
        EmailAddressList.add(Email(AddrNode.nodeValue, type))
    }
}

fun getEmailAddressesNode(nodelist: NodeList, index: Int): Node {
        val child = nodelist.item(index).firstChild
        val childattr = child.attributes
        if (childattr.getNamedItem("OPFContactEmailAddressAddress") != null) {
            return childattr.getNamedItem("OPFContactEmailAddressAddress")
        } else {
            return childattr.getNamedItem("OPFContactEmailAddressName")
        }

}

fun AddToListIfNotAlreadyIn(listDest: MutableList<Email>, listSrc: MutableList<Email>){
    listSrc.forEach {
        if (listDest.contains(it)){
            }
        else
            listDest.add(it)
    }
}

fun getAddresses(doc: Document, zone: String): NodeList = doc.getElementsByTagName(zone)

