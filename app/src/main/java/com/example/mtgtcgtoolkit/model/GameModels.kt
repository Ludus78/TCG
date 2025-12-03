package com.example.mtgtcgtoolkit.model

enum class GameType(val defaultLife: Int) {
    MTG(40),
    YGO(8000)
}

data class CommanderDamage(
    val fromCommanderId: Int,
    val damage: Int
)

data class PlayerState(
    val id: Int,
    val name: String,
    val life: Int,
    val commanderDamages: List<CommanderDamage> = emptyList()
)

data class GameState(
    val gameType: GameType = GameType.MTG,
    val players: List<PlayerState> = emptyList()
) {
    companion object {
        fun newFourPlayer(gameType: GameType): GameState {
            val life = gameType.defaultLife
            val players = List(4) { index ->
                PlayerState(
                    id = index,
                    name = "J${index + 1}",
                    life = life
                )
            }
            return GameState(gameType = gameType, players = players)
        }
    }
}



