## MTG & TCG Toolkit – MVP1 (Android / Kotlin / Jetpack Compose)

Application de compteur de points de vie pour TCG (MTG, YGO, etc.) avec :
- **Quick Start 4 joueurs** (J1–J4, PV par défaut selon le jeu)
- **Gestes swipe haut/bas** pour \\(+/-1\\) PV
- **Dégâts de commandant MTG** par joueur
- **Sauvegarde automatique** de la partie en cours
- **Tutoriel overlay** au premier lancement
- **Mode veille désactivé** (écran toujours allumé pendant la partie)

### Ouvrir le projet

- **1.** Ouvre **Android Studio**.
- **2.** Choisis **“Open an existing project”**.
- **3.** Sélectionne le dossier `MTGTCGToolkit` (racine du repo).
- **4.** Laisse Android Studio télécharger le SDK/Gradle si nécessaire.

### Lancer l’application

- **1.** Branche un téléphone Android (mode développeur + débogage USB) ou lance un **émulateur**.
- **2.** Dans Android Studio, sélectionne une configuration `app`.
- **3.** Clique sur **Run ▶**.
- L’appli démarre directement sur l’écran **Quick Start 4 joueurs MTG**.

### Fonctionnalités MVP

- **Quick Start 4 joueurs**
  - 4 zones : `J1`, `J2`, `J3`, `J4`.
  - PV initiaux définis par le type de jeu (`MTG` 40, `YGO` 8000 – logique dans `GameType`).

- **Gestes de swipe PV**
  - **Swipe vers le haut** sur une zone joueur : **+1 PV**.
  - **Swipe vers le bas** : **-1 PV**.

- **Dégâts de Commandant (MTG)**
  - Icône de commandant dans chaque carte joueur.
  - Tap sur l’icône : ajoute 1 dégât de commandant (stocké par joueur).

- **Sauvegarde automatique**
  - L’état de la partie (`PV`, dégâts de commandant, jeu) est sérialisé en JSON dans `SharedPreferences`.
  - Sauvegarde :
    - Périodique (toutes les 5 secondes).
    - Sur `onPause()`.
  - Restauration au démarrage si un état existe.

- **Tutoriel interactif (premier lancement)**
  - Overlay sombre expliquant les gestes de swipe et l’icône de commandant.
  - Affiché uniquement si le flag `first_launch` est vrai dans `SharedPreferences`.
  - Dismiss par interaction et le flag est mis à `false`.

- **Orientation dynamique**
  - Disposition en **grille 2x2**, gérée via `LocalConfiguration.orientation`.
  - Fonctionne en **portrait** et **paysage**.

- **Écran toujours allumé**
  - Utilisation de `FLAG_KEEP_SCREEN_ON` dans `MainActivity`.

### Fichiers importants

- **`MainActivity.kt`** : point d’entrée, gestion du cycle de vie, auto-save, écran toujours allumé.
- **`GameModels.kt`** : modèles `GameType`, `GameState`, `PlayerState`, `CommanderDamage`.
- **`GamePreferences.kt`** : persistance simple via `SharedPreferences` + JSON.
- **`GameScreen.kt`** : UI Compose principale (compteur 4 joueurs, gestes, overlay tutoriel).

### Prochaines étapes possibles (Phase 2+)

- **Sélection explicite du jeu/mode** (MTG, YGO, autres TCG) avec différents presets.
- **Historique des parties** (utiliser Room).
- **Modes 1v1, 2v2, Commander multi-pods**, personnalisation des noms/couleurs des joueurs.
- **Thèmes personnalisables** (dark/bright, skins par jeu).



