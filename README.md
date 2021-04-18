# ITmitSchmiss-MockServers

## Überblick

Der Sinn dieses Programms ist, für ein paar grundsätzliche Netzwerkdienste (wie etwa Daytime, Echo, POP3) „Fake“-Server zu bauen, die Ihr auf Eurem PC laufen lassen könnt, ohne weitere Pakete und Abhängigkeiten zu installieren. Der POP3-Server zeigt zur Übersiccht außerdem die vorhandenen Konten und deren Mails an, die Ihr über Telnet oder einen selbst geschriebenen Mail-Client abrufen könnt.

## Download und Installation

Auf der rechten Seite seht Ihr die "Releases". Klickt das neuste davon an und ladet die passende JAR-Datei herunter. Wenn Ihr eine Java-Runtime oder ein JDK installiert habt, solltet Ihr die JAR-Datei mit Doppelklick starten können. 

Wenn Ihr bereits BlueJ, IntelliJ oder Visual Studio Code mit Java-Paket besitzt, sollte das der Fall sein.
Ansonsten müsst Ihr z.B. auf [AdoptOpenJDK](https://adoptopenjdk.net/) eins herunterladen und installieren. Ich empfehle ein JDK der Version 11 mit HotSpot-JVM. Eventuell müsst Ihr Euch nach der Installtion des JDK einmal ab- und wieder anmelden.

Achtung: Beim Start der JAR-Datei kommt hoffentlich ein Firewall-Hinweis. Das ist auch richtig so, denn die Mock-Server öffnen (während der Laufzeit des Programms) Netzwerkschnittstellen auf Eurem PC. Wenn Ihr auch von anderen Geräten in Eurem Heimnetz auf die Server zugreifen wollt, müsst Ihr den Zugriff von *privaten* Netzwerken erlauben.
