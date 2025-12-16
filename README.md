
## CHOUQUET Romain PIEDEULEU Robin ZANGUILO Chris
#
# Catch me if you can — TP 3D
#
## Introduction

L’objectif est de développer une **application de visualisation 3D du trafic aérien** à l’aide de **JavaFX** et d’une **sphère texturée représentant la Terre**.

L’application permet de :
- Lire et exploiter une base de données d’aéroports (fichier CSV).
- Représenter les aéroports sur une sphère texturée (Terre).
- Identifier l’aéroport le plus proche d’un clic sur la carte.
- Récupérer et afficher les vols à destination de cet aéroport via l’API *AviationStack*.
- Visualiser les origines de ces vols par des sphères colorées.

Le projet combine **lecture de fichiers**, **parsing JSON**, **calculs géographiques**, et **modélisation 3D interactive** en JavaFX.

---
## 1ère Séance : Structure du projet


Catch_me/
┣ src/
┃ ┣ Aeroport.java
┃ ┣ World.java
┃ ┣ Flight.java
┃ ┣ JsonFlightFiller.java
┃ ┣ Earth.java
┃ ┗ Interface.java ← point d’entrée (Application JavaFX)
┣ lib/ ← bibliothèques JavaFX (.jar)
┣ data/ ← fichiers CSV et JSON de test

Lors de cette première séance, on a mis en place **`World.java`**, qui gère la lecture d’un document CSV contenant différentes caractéristiques décrites dans **`Aeroport.java`**.

Fonctionnalités implémentées :
- Extraction des coordonnées, du code IATA et du nom depuis le CSV  
- Filtrage des aéroports de type `large_airport`  
- Calcul de distance géographique simplifiée entre deux aéroports  

Par exemple :
#
<img width="608" height="65" alt="image" src="https://github.com/user-attachments/assets/a295344a-80e4-406f-a3d4-6be8039b6399" />
#

D’où viennent ces chiffres ?
Nombre d'aéroports trouvés : 606
    Correspond au nombre de lignes dans le CSV qui :
        sont de type "large_airport",
        ont des coordonnées valides.
#
Aéroport le plus proche de Paris
    Le programme crée un point à la position de Paris (lat=48.8566, lon=2.3522),
    puis compare la distance entre ce point et tous les aéroports de la liste.
#
Distance (approx)
    C'est la norme de distance :
    norme = (Θ2 − Θ1)^2 + ((Φ2 − Φ1) * cos((Θ2 + Θ1)/2))^2
#
Distance Paris-CDG
    On cherche ensuite l’aéroport CDG dans la liste et on calcule la distance simplifiée entre Paris et CDG 
    CDG est plus loin que Orly du centre de Paris

## Suite

On est entrain de mettre en place javafx
On a dû mettre a jours jdk

cependant stage est manquant.

## 2ème Séance : Visualisation et Environnement 3D

L'objectif de cette étape était de passer du traitement de données (console) à une interface graphique interactive en 3D. Nous avons utilisé la bibliothèque **JavaFX** pour modéliser la Terre et gérer les interactions utilisateur.

### 2.1. Modélisation de la Terre (`Earth.java`)

Nous avons créé une classe héritant de `Group` pour gérer l'objet 3D :

-   **Création de la sphère :** Utilisation de la classe `Sphere` de JavaFX avec un rayon défini (300).
    
-   **Texturage :** Application d'une `PhongMaterial` avec une _Diffuse Map_ (image `earth.png`) pour donner l'apparence de la Terre.
    
-   **Animation :** Ajout d'une `RotateTransition` sur l'axe Y pour simuler la rotation de la Terre sur elle-même.
    

### 2.2. Gestion de la Scène et Caméra (`Interface.java`)

La classe principale étend `Application`. Elle configure :

-   **La Scène 3D :** Activation du _depth buffer_ (`true`) pour gérer la profondeur.
    
-   **La Caméra :** Utilisation d'une `PerspectiveCamera`.
    
-   **Le Zoom :** Gestion de l'événement `ScrollEvent` pour déplacer la caméra sur l'axe Z, permettant de se rapprocher ou de s'éloigner du globe.
    

### 2.3. Interaction : Du clic 2D aux coordonnées 3D

Une difficulté majeure a été de traduire un clic de souris sur l'écran (2D) en coordonnées géographiques (Latitude/Longitude).

-   **Ray Picking :** Utilisation de `PickResult` pour obtenir les coordonnées de texture (u, v) au point d'impact sur la sphère.
    
-   **Conversion Mathématique :**
    
    -   `Latitude = 180 * (0.5 - v)`
        
    -   `Longitude = 360 * (u - 0.5)`
        
-   **Recherche :** Ces coordonnées sont envoyées à `World.findNearestAirport` pour identifier l'aéroport le plus proche.
    
-   **Marquage :** Une petite sphère rouge est ajoutée dynamiquement sur la position 3D de l'aéroport trouvé (conversion inverse Lat/Lon vers x,y,z).
    

----------

## Difficultés Techniques et Résolutions

La mise en place de l'environnement de développement a posé plusieurs problèmes bloquants liés à l'évolution de Java et JavaFX.

### 1. Configuration du JDK et JavaFX

JavaFX ne faisant plus partie du JDK standard (depuis Java 11), nous avons dû l'intégrer manuellement.

-   **Problème :** `Stage est manquant` / `No toolkit found`.
    
-   **Cause :** Le projet ne trouvait pas les modules graphiques au démarrage et l'erreur de compil ne redirigeais pas sur les bon problèmes : une erreur concernant un jar manquant, des erreurs concernant le main et des compatibilité entre javafx, jdk et java ou encore des chemins d'accès manquant hors c'était un .dll manquant qui posait problème

On a ensuite ajouté l'Interaction Utilisateur:

-   **Navigation :**
    
    -   **Rotation du Globe :** Clic gauche maintenu + glissement de la souris (`MouseDragged`).
        
    -   **Zoom :** Molette de la souris (`ScrollEvent`) pour avancer/reculer la caméra.
    - **Click de position** affiche les coordonnées
      ###
<img width="242" height="91" alt="image" src="https://github.com/user-attachments/assets/212334c2-c405-4f28-ac52-aa2f81a47355" />
###
<img width="1252" height="914" alt="image" src="https://github.com/user-attachments/assets/81c4b93f-da2d-4ea7-8546-25a8a86acd88" />
###
J'ai essayé d'ajouter les aéroport du csvs
###
<img width="323" height="293" alt="image" src="https://github.com/user-attachments/assets/7aa4f8b6-6a5d-4f7d-b2d4-e6c66bf2d2db" />








