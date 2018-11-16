#OLMEmailExtractor

Extract all the email addresses from an OLM Archive.
OLM Archive has to be unzipped first.

Email Addresses are extracted from the following tags:
+ From
+ Reply To
+ Sender
+ Copy To

duplicates (with a same tag) are removed

CSV output provides :
domain , email address , tag

-----

usage :
java -jar **OLMEmailExtractor.jar** InFolder="*Source Files Folder*" OutFile="*Output CSV filename*"

-----

_please let me a message @ **thierry.soulie@tsodev.fr** if you use and like it !_