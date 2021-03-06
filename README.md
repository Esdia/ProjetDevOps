[![View site - GH Pages](https://img.shields.io/badge/View_site-GH_Pages-2ea44f?style=for-the-badge)](https://esdia.github.io/ProjetDevOps/)

[![Java CI with Maven](https://github.com/Esdia/ProjetDevOps/actions/workflows/pull-request.yml/badge.svg)](https://github.com/Esdia/ProjetDevOps/actions/workflows/pull-request.yml)
[![Java CD with Maven](https://github.com/Esdia/ProjetDevOps/actions/workflows/merge.yml/badge.svg)](https://github.com/Esdia/ProjetDevOps/actions/workflows/merge.yml)
[![pages-build-deployment](https://github.com/Esdia/ProjetDevOps/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/Esdia/ProjetDevOps/actions/workflows/pages/pages-build-deployment)

![Coverage](../badges/jacoco.svg)
![Branches](../badges/branches.svg)
[![CodeFactor](https://www.codefactor.io/repository/github/esdia/projetdevops/badge/master)](https://www.codefactor.io/repository/github/esdia/projetdevops/overview/master)

***
# Projet de DevOps : Panda en Java
***
##### Groupe:
HERMITTE Théo - TOURNON Pierre - BERNABE Aurélien - M1 INFO - 2021/2022

***
## Nos fonctionnalités fournies
### Les constructeurs
Notre bibliothèque dispose de deux constructeurs permettant de
créer des dataframes soit en prenant en paramètre un tableau simple à
deux dimensions, soit en prenant en paramètre le chemin d'un fichier
csv contenant des données du type Integer, String ou Boolean.
### L'affichage
Concernant l'affichage, notre bibliothèque permet d'afficher l'intégralité d'un dataframe grâce à la ré-implémentation de la méthode toString(), l’affichage est soigné avec des espaces permettant de bien aligner les lignes et les colonnes. Nous avons aussi deux méthodes qui permettent d’afficher les premières lignes ou les dernières lignes d’un dataframe.
### La sélection
Nous avons implémenté diverses méthodes de sélection basiques et avancées pour sélectionner des sous-ensembles de colonnes et de lignes. Il est alors possible de sélectionner des lignes : avec un intervalle d’index, une suite de booléens corrélés aux index ou encore avec un prédicat. Il est possible de sélectionner des colonnes avec une suite de labels indiquant les colonnes à renvoyer. Un mode de sélection avancé permet également de sélectionner un sous-ensemble de lignes et de colonnes en fonction d’index pour les lignes et de labels pour les colonnes.

### Les statistiques
Nous avons implémenté les méthodes Sum, Mean, Min et Max permettant d'exécuter des calculs sur les colonnes d’un dataframe. Ainsi nous pouvons effectuer tous ces calculs sur des colonnes contenant des valeurs du type Number (Integer, Float, …). Concernant les valeurs du type String et Char nous ne pouvons pas calculer leur somme ni leur moyenne (pas trop de sens) mais nous pouvons calculer le min et le max suivant l’ordre alphabétique.

***
## Nos outils utilisés
Nous avons utilisé divers outils pour mener à bien le projet :
* JUnit 5 : pour les tests unitaires
* GitHub : comme gestionnaire de versions
* Maven : pour faciliter l’intégration continue
* [jacoco-badge-generator](https://github.com/cicirello/jacoco-badge-generator) : Pour générer les badges indiquant la couverture des tests
* [github-pages-deploy-action](https://github.com/JamesIves/github-pages-deploy-action) : Pour déployer la Javadoc générée par Maven sur GitHub Pages
* [codefactor](https://www.codefactor.io/repository/github/esdia/projetdevops/overview/master) : Pour évaluer la qualité du code

***
## Notre Workflow Git
Nous avons choisi d'utiliser le workflow Feature Branch. Ainsi durant la phase de développement de notre projet nous avons pu expérimenter ce workflow et nous l’avons trouvé très pratique. Il nous a permis d’avoir le code dans master bien sécurisé et dès lors que l’on avait à apporter des modifications à master on devait passer par plusieurs étapes : 
* Merge la branche master dans notre branche afin d’avoir une branche n’ayant aucun conflit avec master. 
* Créer une pull request. Il fallait qu’un autre contributeur du projet approuve notre requête après avoir analysé les changements des fichiers de la branche feature pour que le merge soit possible.
* Merge la pull request dans master
* Supprimer la feature branch

***
## Notre intégration et notre déploiement continus
Nous avons développé deux actions GitHub. 
La première, déclenchée lors de la création d'une pull request, exécute l'ensemble de nos tests unitaires.

La seconde action est déclenchée lors d'un push sur master. Puisque la branche master est protégée, cette action n'est déclenchée que lorsqu'une branche est merge dans master. Cette action a plusieurs effets :
* Elle déploie notre projet sous forme de dépôt Maven hébergé sur GitHub Packages
* Elle génère la Javadoc et la déploie sur GitHub Pages
* Elle met à jour les badges liés à la couverture de tests et les affiche sur le README

De plus, nous avons ajouté plusieurs badges au README pour afficher le status des actions, la qualité du code et la couverture des tests
***
## Feedback
Pour conclure, tous les membres du groupe ont eu l’occasion pour la première fois d’utiliser des technologies permettant l’intégration et le développement continus. Nous avons donc éprouvé des difficultés à mettre en place et à utiliser ces outils qui une fois bien compris nous ont permis d’avoir un code avec une bien meilleure qualité. Nous n’étions par exemple pas très à l’aise avec le workflow feature branch au début car on était tenté d’écrire plus de fonctionnalités que ce à quoi elles étaient destinées. </br>
Ces outils DevOps d’intégration continue, de déploiement continu et de workflow étant désormais indispensable en entreprise, nous sommes certains que les connaissances acquises durant ce projet seront de véritables atouts lors de notre insertion en entreprise.










