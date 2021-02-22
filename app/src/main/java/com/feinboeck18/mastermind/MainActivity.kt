package com.feinboeck18.mastermind

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.io.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var currentActivity: MainActivity

        var guesses: ArrayList<String> = ArrayList()
        var scores: ArrayList<String> = ArrayList()
        var startTime: Long = System.currentTimeMillis()
        var gameOver: Boolean = false
        private var currentCode: String = ""

        //Settings inside of a List
        lateinit var settings: ArrayList<Setting>

        //SETTINGS
        lateinit var alphabet: List<Char>
        var codeLength: Int = 0
        var guessRounds: Int = 0
        var sameCharsAllowedTwice: Boolean = true
        var correctPositionSign: Char = ' '
        var correctCodeElementSign: Char = ' '

        private fun startNewGame() {
            currentCode = getNewCode()
            gameOver = false
            startTime = System.currentTimeMillis()
        }

        fun resetGame() {
            startNewGame()
            guesses.clear()
        }

        private fun getNewCode(): String {
            val newCode: StringBuilder = StringBuilder()

            for(i in 1 .. codeLength) {
                var charToAdd = alphabet[(Math.random()*alphabet.size).toInt()]
                while(!sameCharsAllowedTwice && newCode.contains(charToAdd))
                    charToAdd = alphabet[(Math.random()*alphabet.size).toInt()]

                newCode.append(charToAdd)
            }
            println(newCode.toString())
            return newCode.toString()
        }

        fun guessIsLongEnough(guess: String): Boolean {
            return currentCode.length == guess.length
        }

        fun allCharsAreValid(guess: String): Boolean {
            return alphabet.toList().containsAll(guess.toCharArray().toList())
        }

        fun addGuess(guess: String): String {
            return when {
                currentCode == guess -> {
                    guesses.add("$guess|SOLVED")
                    gameOver = true
                    "You won!"
                }
                guesses.size+1 == guessRounds -> {
                    guesses.add("$guess|NOT SOLVED")
                    gameOver = true
                    "You lost!"
                }
                else -> {
                    guesses.add("$guess|${evaluateGuess(guess)}")
                    "GO AHEAD"
                }
            }
        }

        //Found this stuff in the Internet
        private fun evaluateGuess(guess: String): String {
            val output: StringBuilder = StringBuilder()
            val usedIndexes = ArrayList<Int>()

            //Check if it's at a Correct Position
            for((i, char) in guess.toCharArray().withIndex()) {
                if(currentCode[i] == char) {
                    output.append(correctPositionSign)
                    usedIndexes.add(i)
                }
            }

            //Check if it's a correct Code Element
            for((indexAtGuess, charAtGuess) in guess.toCharArray().withIndex()) {
                if(currentCode[indexAtGuess] != charAtGuess && currentCode.contains(charAtGuess)) {
                    for((indexAtCode, charAtCode) in currentCode.toCharArray().withIndex()) {
                        if(!usedIndexes.contains(indexAtCode) && charAtCode == charAtGuess) {
                            output.append(correctCodeElementSign)
                            usedIndexes.add(indexAtCode)
                            break
                        }
                    }
                }
            }

            return output.toString()
        }

        fun loadGame() {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(currentActivity.openFileInput("savestate.sav")))
                val rawXML: StringBuilder = StringBuilder()

                var currentLine = bufferedReader.readLine()
                while(currentLine != null) {
                    rawXML.append("$currentLine\n")
                    currentLine = bufferedReader.readLine()
                }
                bufferedReader.close()

                convertFromXML(rawXML.toString().substring(0, rawXML.toString().length - 1))
            } catch(exception: FileNotFoundException) {
                Toast.makeText(currentActivity, "No available save state!", Toast.LENGTH_SHORT).show()
            } catch(exception: IllegalArgumentException) {
                Toast.makeText(currentActivity, "Invalid save state!", Toast.LENGTH_SHORT).show()
            }
        }

        //Bei den XML-Zeug Hilfe von Mitschüler.
        private fun convertFromXML(xmlCode: String) {
            val tempCode: String
            val tempDuration: Long
            val tempGuesses: ArrayList<String> = ArrayList()
            var tempGameOver = false

            val linesOfCode = xmlCode.replace("\t", "").split("\n")
            if(linesOfCode.size >= 4) {
                if("<savestate>" == linesOfCode.first() && linesOfCode.last() == "</savestate>") {
                    //CODE
                    if(linesOfCode[1].contains("<code>") && linesOfCode.contains("</code>"))
                        tempCode = linesOfCode[1].replace("<code>", "").replace("</code>", "").trim()
                    else
                        throw IllegalArgumentException("No Code found in XML")

                    //DURATION
                    if(linesOfCode[2].contains("<duration>") && linesOfCode[2].contains("</duration>"))
                        tempDuration = linesOfCode[2].replace("<duration>", "")
                                .replace("</duration>", "")
                                .trim()
                                .toLong()
                    else
                        throw IllegalArgumentException("No duration found in XML")

                    //GUESSES
                    if(linesOfCode.size > 4) {
                        var guessLines = linesOfCode.subList(3, linesOfCode.size - 1)
                        var index = 1
                        while(guessLines.isNotEmpty()) {
                            if (guessLines.size >= 4 && guessLines[0] == "<guess$index>" && guessLines[3] == "</guess$index>") {

                                //guess
                                val userInput: String
                                if (guessLines[1].contains("<userInput>") && guessLines[1].contains("</userInput>"))
                                    userInput = guessLines[1].replace("<userInput>", "")
                                            .replace("</userInput>", "")
                                            .trim()
                                else
                                    throw IllegalArgumentException("No UserInput found in XML")

                                val result: String
                                if (guessLines[2].contains("<result>") && guessLines[2].contains("</result>"))
                                    result = guessLines[2].replace("result>", "")
                                            .replace("</result>", "")
                                            .replace(", ", "")
                                            .trim()
                                else
                                    throw IllegalArgumentException("No Result found in XML")


                                if (result == "SOLVED" || result == "NOT SOLVED")
                                    tempGameOver = true
                                tempGuesses.add("$userInput|$result")

                                guessLines = if(guessLines.size == 4) ArrayList() else guessLines.subList(4, guessLines.size)

                                //incorrect number of lines
                            } else
                                throw IllegalArgumentException("wrong number of lines")
                            index++
                        }
                    }
                } else
                    throw IllegalArgumentException("Start and/or End is incorrect")
                this.currentCode = tempCode
                this.startTime = System.currentTimeMillis() - tempDuration
                this.guesses.clear()
                this.guesses = tempGuesses
                this.gameOver = tempGameOver
            } else
                throw IllegalArgumentException("This XML-Code is not long enough")
        }

        fun saveGame() {
            val writer = PrintWriter(OutputStreamWriter(currentActivity.openFileOutput("savestate.sav", Context.MODE_PRIVATE)))
            writer.print(convertToXML())
            writer.flush()
            writer.close()
        }

        private fun convertToXML(): String {
            val outputString: StringBuilder = StringBuilder("<savestate>\n")
                    .append("\t<code>$currentCode</code>\n")
                    .append("\t<duration>${(System.currentTimeMillis()- startTime)}</duration>\n")

            for((i, guess) in guesses.withIndex()) {
                val toAppend: StringBuilder = StringBuilder("\t<guess${i+1}>\n")
                val args = guess.split("|")

                toAppend.append("\t\t<userInput>")
                var userInput = ""
                for(element in args[0].split(""))
                    if(element != "") userInput += "$element, "
                toAppend.append("${userInput.substring(0, userInput.length-2)}</userInput>")

                toAppend.append("\t\t<result>")
                var result = ""
                if(args[1] == "SOLVED" || args[1] == "NOT SOLVED")
                    result += args[1]
                else {
                    for(element in args[1].split(""))
                        if(element != "") result += "$element, "
                    if(result.length >= 3)
                        result.substring(0, result.length - 2)
                }
                toAppend.append("$result</result>\n")
                        .append("\t</guess${i+1}>\n")

                outputString.append(toAppend)
            }
            return outputString.append("</savestate>").toString()
        }

        fun addScoreToList(size: Int, duration: Long) {
            val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            val currentDateTime: LocalDateTime = LocalDateTime.now()
            val seconds = (duration/1000)%60
            val minutes = (duration/1000)/60

            scores.add("${dtf.format(currentDateTime)} | $size Rounds | $minutes min $seconds sec")
            saveScores()
        }

        private fun saveScores() {
            val writer = PrintWriter(OutputStreamWriter(currentActivity.openFileOutput("scores.sav", Context.MODE_PRIVATE)))
            scores.forEach { game ->
                writer.println(game)
            }
            writer.flush()
            writer.close()
        }

        fun loadScores() {
            try {
                val bufferedReader = BufferedReader(InputStreamReader(currentActivity.openFileInput("scores.sav")))
                var currentLine: String? = bufferedReader.readLine()
                while(currentLine != null) {
                    this.scores.add(currentLine)
                    currentLine = bufferedReader.readLine()
                }
                bufferedReader.close()
            } catch (fnfe: FileNotFoundException) {
                Toast.makeText(currentActivity, "No Scores found.", Toast.LENGTH_SHORT).show()
                this.scores = ArrayList()
            }
        }

        private fun initSettings() {
            for(setting in settings) {
                val settingDescription: String = setting.settingDescription
                when(setting.settingName) {
                    "alphabet" -> alphabet = settingDescription.split(",").map { char -> char[0] }
                    "codeLength" -> codeLength = settingDescription.toInt()
                    "doubleAllowed" -> sameCharsAllowedTwice = settingDescription.toBoolean()
                    "guessRounds" -> guessRounds = settingDescription.toInt()
                    "correctPositionSign" -> correctPositionSign = settingDescription[0]
                    "correctCodeElementSign" -> correctCodeElementSign = settingDescription[0]
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings = loadSettingsToList(assets.open("config.conf"))
        initSettings()
        currentActivity = this
        loadScores()
        startNewGame()
    }

    private fun loadSettingsToList(inputStream: InputStream): ArrayList<Setting> {
        val tempList = ArrayList<Setting>()

        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var currentLine = bufferedReader.readLine()
        while(currentLine != null) {
            val settingArgs = currentLine.split("=")
            if(settingArgs.size == 2) {
                tempList.add(Setting(settingArgs[0].trim(), settingArgs[1].trim()))
                currentLine = bufferedReader.readLine()
            }
        }

        return tempList
    }
}