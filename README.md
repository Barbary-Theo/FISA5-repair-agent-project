
# Programmation par agents avec Jade

### 👥 Groupe projet

Le groupe du projet est composé de :

- BARBARY Théo
- COGO Clément
- THIRY Basile


### 👨🏻‍💻 Lancement du programme 

La base du projet est la même que celle récupéré via le template de Monsieur Adam.

Le projet se lance via la classe LaunchSimu dans le package launch.

Au lancement, les classes des agents s'initialisent :

- UserAgent : Création de tous les users avec un en plus du template un montent aléatoire dans le porte monaie.
- RepairCoffeeAgent : Création de tous les repairCoffees et aucun ajout comparé au template 
- PartStoreAgent : Création de tous les PartStors avec en plus du template définition si spécialiste en seconde-main ou en neuf, ajout de 10 parts au aléatoire dans le magasin.


### 🗄️ Structure du projet 

La communication par les agents se fait via la class UtilsMessage étendu par tous les agents. Elle implémente la méthode "send" pour envoyer un message à un autre agent.
Cette méthode "send" prend en paramètre l'agent target, le contenu du message sous l'objet MessageContent (j'y reviens ci-après) le message ID et le type de message (REQUEST, INFORM, etc.).

L'interface MessageContent permet est implémenté par tous les objets d'envoie (présent dans le package DataTransfert). Ces classes permettent d'échanger des informations entre les agents de façon simple est clair. Chaque objet est utilisé lors de la communication entre les agents.
La classe MessageID correspond simplement à une genre d'enum contentant tous les ID de messages possibles, elle permet l'uniformité des ID entre les agents.


### 🗂️ Diagrammes

Tous les diagrammes et des explications plus précises du fonctionnement du projet sont présents dans le rapport.