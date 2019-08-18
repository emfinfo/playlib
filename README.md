# playlib 1.1.0 - august 2019
A small library to help use of Play framwork with session and return of results.

You can download and open this project in NetBeans 8.x or 9.x. It's a Java 8 maven project. So, dependencies are loaded automaticly from maven central.

Project documentation here :<br>
    http://jcstritt.emf-informatique.ch/doc/playlib<br>

New in release 1.1.0 (15.08.2019) :
* Adapted to Play 2.7.x
* In Utils, new name for method validCrossDomainContext -> validCrossDomainRequest
* In SessionUtils, new name for method getUserPersonneId -> getUserPersonId
* In SessionUtils, most setters methods have disappeared, because the data of a request are now immutable throughout the life of the request.
* New model class "ReleaseInfo" to store release informations of a Play application (application version, server version and data date version)

New in release 1.0.2 (15.11.2018) :
* Add support to personId in the session

New in release 1.0.1 (15.11.2018) :
* Add method "toXml"
* Returns of getUserName is now "?" and not "?name?"
* Returns of getUserprofile is now "?" and not "?profil?"

New in release 1.0.0 (12.11.2018) :
* First commit
