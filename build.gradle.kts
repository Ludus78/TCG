// Racine du projet MTG & TCG Toolkit

plugins {
    // vide, les plugins sont déclarés dans les modules
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}



