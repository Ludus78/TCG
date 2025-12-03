package com.example.mtgtcgtoolkit.data

import android.content.Context
import com.example.mtgtcgtoolkit.model.CommanderDamage
import com.example.mtgtcgtoolkit.model.GameState
import com.example.mtgtcgtoolkit.model.GameType
import com.example.mtgtcgtoolkit.model.PlayerState
import org.json.JSONArray
import org.json.JSONObject

class GamePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean = prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    fun setNotFirstLaunch() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun saveGameState(state: GameState) {
        val root = JSONObject().apply {
            put("gameType", state.gameType.name)
            val playersArray = JSONArray()
            state.players.forEach { p ->
                val playerObj = JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("life", p.life)
                    val cmdArray = JSONArray()
                    p.commanderDamages.forEach { cd ->
                        val cdObj = JSONObject().apply {
                            put("fromCommanderId", cd.fromCommanderId)
                            put("damage", cd.damage)
                        }
                        cmdArray.put(cdObj)
                    }
                    put("commanderDamages", cmdArray)
                }
                playersArray.put(playerObj)
            }
            put("players", playersArray)
        }

        prefs.edit().putString(KEY_GAME_STATE, root.toString()).apply()
    }

    fun loadGameState(): GameState? {
        val json = prefs.getString(KEY_GAME_STATE, null) ?: return null
        return try {
            val root = JSONObject(json)
            val gameType = GameType.valueOf(root.getString("gameType"))
            val playersArray = root.getJSONArray("players")
            val players = buildList {
                for (i in 0 until playersArray.length()) {
                    val pObj = playersArray.getJSONObject(i)
                    val cmdArray = pObj.getJSONArray("commanderDamages")
                    val commands = buildList {
                        for (j in 0 until cmdArray.length()) {
                            val cdObj = cmdArray.getJSONObject(j)
                            add(
                                CommanderDamage(
                                    fromCommanderId = cdObj.getInt("fromCommanderId"),
                                    damage = cdObj.getInt("damage")
                                )
                            )
                        }
                    }
                    add(
                        PlayerState(
                            id = pObj.getInt("id"),
                            name = pObj.getString("name"),
                            life = pObj.getInt("life"),
                            commanderDamages = commands
                        )
                    )
                }
            }
            GameState(gameType = gameType, players = players)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_GAME_STATE = "game_state"
    }
}



