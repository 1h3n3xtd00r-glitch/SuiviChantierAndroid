# Suivi de Chantier Routier — Android prototype

Prototype natif Android en Kotlin avec SQLite local. Langue française, devise MAD, interface simple adaptée au téléphone.

## Inclus
- Projet Android Studio / Gradle Kotlin DSL
- SQLite : tables `records` et `chantiers`, index par catégorie/date/chantier
- Formulaire générique : catégorie, date, chantier, désignation, fournisseur, quantité, tarif, montant, notes
- Historique et suppression de saisies
- Tableau de bord avec compteurs et coûts estimés
- Fonctionnement hors ligne, sans permissions réseau

## Limites actuelles
- Le formulaire est générique : les champs métier détaillés de chaque catégorie doivent être ajoutés progressivement.
- La table `chantiers` existe, mais l'écran CRUD des chantiers reste à construire.
- Export CSV/PDF, modification des saisies, pièces jointes, authentification et synchronisation distante ne sont pas encore inclus.
- La synchronisation nécessite un backend sécurisé (par exemple API + base distante) et une stratégie de résolution des conflits. Elle ne peut pas être activée avec SQLite seul.

## Construire l'APK
1. Installez Android Studio sur un ordinateur.
2. Ouvrez ce dossier comme projet Gradle.
3. Utilisez JDK 17 et installez Android SDK Platform 35 (ou adaptez compileSdk à une plateforme installée).
4. Attendez la synchronisation Gradle.
5. Menu **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. APK debug attendu : `app/build/outputs/apk/debug/app-debug.apk`.

Pour une compilation en ligne de commande avec Gradle installé : `gradle assembleDebug` depuis la racine du projet. Le fichier APK signé pour diffusion nécessite une configuration de signature.
