package com.example.unscramble.ui

data class GameUiState(
    val currentScrambledWorld: String = "",
    val currentWordCount: Int = 1,
    val isGameOver: Boolean = false,
    val isGuessedWordWrong: Boolean = false,
    val score: Int = 0
)
