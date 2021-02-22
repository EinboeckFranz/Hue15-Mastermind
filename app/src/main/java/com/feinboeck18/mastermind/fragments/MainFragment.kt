package com.feinboeck18.mastermind.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feinboeck18.mastermind.MainActivity
import com.feinboeck18.mastermind.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    lateinit var gameAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, MainActivity.guesses)
        listView.adapter = gameAdapter

        newGameBtn.setOnClickListener {
            startNewGame()
        }
        submitBtn.setOnClickListener {
            evaluateGuess()
        }
        loadBtn.setOnClickListener {
            loadGameState()
        }
        saveBtn.setOnClickListener {
            saveGameState()
        }
        scoreBtn.setOnClickListener {
            switchToScoreFragment()
        }
        settingsBtn.setOnClickListener {
            switchToSettingsFragment()
        }

        nextGuess.requestFocus()
    }

    private fun startNewGame() {
        MainActivity.resetGame()
        gameAdapter.notifyDataSetChanged()
        showToast("New Game has been started!")
    }

    private fun evaluateGuess() {
        val guess = nextGuess.text.toString()

        if(MainActivity.gameOver)
            showToast("Game is already over!\nPlease start a new Game!")
        else if(!MainActivity.guessIsLongEnough(guess))
            showToast("Invalid guess length!")
        else if(!MainActivity.allCharsAreValid(guess))
            showToast("Invalid chars found!")
        else {
            nextGuess.text.clear()
            val text: String = MainActivity.addGuess(guess)

            showToast(text)
            if(!text.contains("lost"))
                MainActivity.addScoreToList(
                    MainActivity.guesses.size,
                    System.currentTimeMillis() - MainActivity.startTime
                )

            gameAdapter.notifyDataSetChanged()
        }
    }

    private fun loadGameState() {
        MainActivity.loadGame()
        gameAdapter.notifyDataSetChanged()
    }

    private fun saveGameState() {
        MainActivity.saveGame()
    }

    private fun switchToScoreFragment() {
        nav.findNavController().navigate(R.id.fragmentMain_to_fragmentScore)
    }

    private fun switchToSettingsFragment() {
        nav.findNavController().navigate(R.id.fragmentMain_to_fragmentSettings)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}