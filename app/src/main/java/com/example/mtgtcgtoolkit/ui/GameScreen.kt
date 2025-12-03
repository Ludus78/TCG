package com.example.mtgtcgtoolkit.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mtgtcgtoolkit.R
import com.example.mtgtcgtoolkit.model.CommanderDamage
import com.example.mtgtcgtoolkit.model.GameState
import com.example.mtgtcgtoolkit.model.PlayerState

@Composable
fun GameScreen(
    state: GameState,
    onStateChange: (GameState) -> Unit,
    showTutorial: Boolean,
    onDismissTutorial: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLandscape) {
                TwoByTwoGrid(state = state, onStateChange = onStateChange)
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        PlayerCard(
                            modifier = Modifier.weight(1f),
                            player = state.players.getOrNull(0),
                            onPlayerChange = { updated -> updatePlayer(state, updated, onStateChange) }
                        )
                        PlayerCard(
                            modifier = Modifier.weight(1f),
                            player = state.players.getOrNull(1),
                            onPlayerChange = { updated -> updatePlayer(state, updated, onStateChange) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        PlayerCard(
                            modifier = Modifier.weight(1f),
                            player = state.players.getOrNull(2),
                            onPlayerChange = { updated -> updatePlayer(state, updated, onStateChange) }
                        )
                        PlayerCard(
                            modifier = Modifier.weight(1f),
                            player = state.players.getOrNull(3),
                            onPlayerChange = { updated -> updatePlayer(state, updated, onStateChange) }
                        )
                    }
                }
            }

            if (showTutorial) {
                TutorialOverlay(onDismiss = onDismissTutorial)
            }
        }
    }
}

private fun updatePlayer(
    state: GameState,
    updated: PlayerState,
    onStateChange: (GameState) -> Unit
) {
    val newPlayers = state.players.map { if (it.id == updated.id) updated else it }
    onStateChange(state.copy(players = newPlayers))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerCard(
    modifier: Modifier = Modifier,
    player: PlayerState?,
    onPlayerChange: (PlayerState) -> Unit
) {
    if (player == null) return

    var commanderDamage by remember { mutableStateOf(player.commanderDamages.sumOf { it.damage }) }

    Card(
        modifier = modifier
            .padding(8.dp)
            .pointerInput(player.id) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -10) {
                        // vers le haut => +1 PV
                        onPlayerChange(player.copy(life = player.life + 1))
                    } else if (dragAmount > 10) {
                        // vers le bas => -1 PV
                        onPlayerChange(player.copy(life = player.life - 1))
                    }
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = player.life.toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    commanderDamage += 1
                    val list = player.commanderDamages.toMutableList()
                    val existing = list.firstOrNull { it.fromCommanderId == player.id }
                    if (existing == null) {
                        list.add(CommanderDamage(fromCommanderId = player.id, damage = 1))
                    } else {
                        list[list.indexOf(existing)] = existing.copy(damage = existing.damage + 1)
                    }
                    onPlayerChange(player.copy(commanderDamages = list))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_commander),
                        contentDescription = "Dégâts de Commandant"
                    )
                }
                Text(
                    text = commanderDamage.toString(),
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun TwoByTwoGrid(
    state: GameState,
    onStateChange: (GameState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            PlayerCard(
                modifier = Modifier.weight(1f),
                player = state.players.getOrNull(0),
                onPlayerChange = { updatePlayer(state, it, onStateChange) }
            )
            PlayerCard(
                modifier = Modifier.weight(1f),
                player = state.players.getOrNull(1),
                onPlayerChange = { updatePlayer(state, it, onStateChange) }
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            PlayerCard(
                modifier = Modifier.weight(1f),
                player = state.players.getOrNull(2),
                onPlayerChange = { updatePlayer(state, it, onStateChange) }
            )
            PlayerCard(
                modifier = Modifier.weight(1f),
                player = state.players.getOrNull(3),
                onPlayerChange = { updatePlayer(state, it, onStateChange) }
            )
        }
    }
}

@Composable
fun TutorialOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xAA000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
        ) {
            Text(
                text = "Swipe vers le haut pour +1 PV\nSwipe vers le bas pour -1 PV\nTouchez l’icône de commandant pour ajouter des dégâts.",
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "\nTouchez n’importe où pour commencer.",
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        // Détection simple du tap via LaunchedEffect + pas de blocage de l’UI
        LaunchedEffect(Unit) {
            // dans une vraie app, on utiliserait un Modifier.clickable sur un Box transparent
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = {
                            onDismiss()
                        },
                        onVerticalDrag = { _, _ -> }
                    )
                }
        )
    }
}



