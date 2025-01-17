package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    var userGuess by mutableStateOf(" ")

    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()

    private fun pickUpAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickUpAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        tempWord.shuffle()
        while (word.equals(String(tempWord))) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentWordCount = currentState.currentWordCount.inc(),
                    currentScrambledWorld = pickUpAndShuffle(),
                    score = updatedScore
                )
            }
        }
    }

        fun skipWord() {
            updateGameState(_uiState.value.score)
            updateUserGuess("")
        }


        fun checkUserGuess() {
            if (userGuess.equals(currentWord, ignoreCase = true)) {
                val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
                updateGameState(updatedScore)
            } else {
                _uiState.update { currentState -> currentState.copy(isGuessedWordWrong = true) }
            }
            updateUserGuess("")
        }

        fun resetGame() {
            usedWords.clear()
            _uiState.value = GameUiState(currentScrambledWorld = pickUpAndShuffle())
        }

    init {
        resetGame()
    }
}