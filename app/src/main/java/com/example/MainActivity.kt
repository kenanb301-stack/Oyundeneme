package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.GameRepository
import com.example.ui.components.GameDialogs
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel

import com.example.ui.audio.SoundPlayer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SoundPlayer.startMusicLoop()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Access database on current context
                    val context = LocalContext.current
                    val db = remember { AppDatabase.getDatabase(context) }
                    val repository = remember {
                        GameRepository(db.playerProgressDao(), db.levelHistoryDao())
                    }

                    // Create ViewModel with custom Factory injecting the repository
                    val factory = remember {
                        object : ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return GameViewModel(repository) as T
                            }
                        }
                    }

                    val viewModel: GameViewModel = viewModel(factory = factory)
                    val isGameActive by viewModel.isGameActive.collectAsState()

                    Box(modifier = Modifier.fillMaxSize()) {
                        if (isGameActive) {
                            GameScreen(viewModel = viewModel)
                        } else {
                            HomeScreen(viewModel = viewModel)
                        }

                        // Always render Dialog Overlays on top of active states
                        GameDialogs(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundPlayer.stopMusicLoop()
    }
}
