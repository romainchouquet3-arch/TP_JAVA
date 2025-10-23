# CHOUQUET Romain
# PIEDEULEU Robin
# ZANGUILO Chris


# Catch me if you can — TP 3D

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

## 🧩 Structure du projet

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

## 1ère séance

Lors de cette première séance, on a mis en place **`World.java`**, qui gère la lecture d’un document CSV contenant différentes caractéristiques décrites dans **`Aeroport.java`**.

Fonctionnalités implémentées :
- Extraction des coordonnées, du code IATA et du nom depuis le CSV  
- Filtrage des aéroports de type `large_airport`  
- Calcul de distance géographique simplifiée entre deux aéroports  

Par exemple :

Nombre d'aéroports trouvés : 606
Aéroport le plus proche de Paris : Aeroport[iata=ORY, name=Paris-Orly Airport, country=FR, lat=48,723333, lon=2,379444]
Distance (approx) : 6.7322204235215755E-6
CDG : Aeroport[iata=CDG, name=Charles de Gaulle International Airport, country=FR, lat=49,012798, lon=2,550000]
Distance Paris-CDG : 0.0876713951059423

D’où viennent ces chiffres ?
Nombre d'aéroports trouvés : 606
    Correspond au nombre de lignes dans le CSV qui :
        sont de type "large_airport",
        ont des coordonnées valides.

Aéroport le plus proche de Paris
    Le programme crée un point à la position de Paris (lat=48.8566, lon=2.3522),
    puis compare la distance entre ce point et tous les aéroports de la liste.

Distance (approx)
    C'est la norme de distance :
    norme = (Θ2 − Θ1)^2 + ((Φ2 − Φ1) * cos((Θ2 + Θ1)/2))^2

Distance Paris-CDG
    On cherche ensuite l’aéroport CDG dans la liste et on calcule la distance simplifiée entre Paris et CDG 
    CDG est plus loin que Orly du centre de Paris

## Suite

On est entrain de mettre en place javafx
On a dû mettre a jours jdk

cependant stage est manquant
