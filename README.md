# PROJET-JAVA
Projet Java par Peter et Younes

Projet de synchronisation de dossiers en java, sans l'aide d'API extérieures.


La synchronisation de dossier est possible sur un seul ordinateur, et en réseau, à l'aide de sockets. Cette synchronisation est possible dans les deux sens : si un dossier A est modifié, alors le dossier B est modifié, et inversement.


Pour l'envoi de données par les sockets, on serialize les données en String, puis on les deserialize. Dans l'ordre, on envoie d'abord 1 si c'est un dossier, 0 si c'est un fichier, puis le chemin relatif du fichier, puis son contenu si c'est un fichier. Les données sont séparées par un séparateur " || ".


La lecture et l'envoi de sous dossiers est fait par récursivité, récursivité qui est beaucoup présente dans le projet.

Execution : java -cp bin sync.Gui
Le serveur doit être lancé en premier pour que le client puisse s'y connecter.

UML : erreurs à lister :
    -suppression source/destination
    -champs vides*
    -bouton désactivé*
    -exceptions faux chemin*
implémenter rename
implémenter backup
implémenter logfile