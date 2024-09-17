package com.example.fastcalculation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.fastcalculation.databinding.FragmentGameBinding


class GameFragment : Fragment(), OnResults {
    private lateinit var fragmentGameBinding: FragmentGameBinding

    private lateinit var settings: Settings
    private lateinit var calculationGame: CalculationGame
    private var currentRound : CalculationGame.Round? = null
    private var startRoundTime = 0L
    private var totalGameTime = 0L
    private var hits = 0
    private var roundDeadlineHandler = object : Handler (Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            totalGameTime += settings.roundInterval
            play()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            settings = it.getParcelable(Extras.EXTRA_SETTINGS) ?: Settings()
        }

        calculationGame = CalculationGame(settings.rounds)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentGameBinding = FragmentGameBinding.inflate(inflater, container, false)

        val onClickListener = View.OnClickListener {
            val value = (it as Button).text.toString().toInt()
            if (value == currentRound?.answer) {
                totalGameTime += System.currentTimeMillis() - startRoundTime
                hits++
            } else {
                totalGameTime += settings.roundInterval
                hits--
            }
            roundDeadlineHandler.removeMessages(MSG_ROUND_DEADLINE)
            play()
        }

        fragmentGameBinding.apply {
            alternativeOneButton.setOnClickListener(onClickListener)
            alternativeTwoButton.setOnClickListener(onClickListener)
            alternativeThreeButton.setOnClickListener(onClickListener)
        }

        play()

        return fragmentGameBinding.root
    }

    companion object {
        private const val MSG_ROUND_DEADLINE : Int = 0
        @JvmStatic
        fun newInstance(settings: Settings) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Extras.EXTRA_SETTINGS, settings)
                }
            }
    }

    private fun play() {
        currentRound = calculationGame.nextRound()

        if (currentRound != null) {
            fragmentGameBinding.apply {
                "Round: ${currentRound!!.round}/${settings.rounds}".also {
                    roundsTextView.text = it
                }
                questionTextView.text = currentRound!!.question
                alternativeOneButton.text = currentRound!!.alt1.toString()
                alternativeTwoButton.text = currentRound!!.alt2.toString()
                alternativeThreeButton.text = currentRound!!.alt3.toString()
            }
            startRoundTime = System.currentTimeMillis()
            roundDeadlineHandler.sendEmptyMessageDelayed(MSG_ROUND_DEADLINE, settings.roundInterval)
        } else {
            with(fragmentGameBinding) {
                onResults()
            }
        }
    }

    override fun onResults() {
        val points = hits * 10f / (totalGameTime / 1000L)
        getParentFragmentManager()
            .beginTransaction()
            .replace(R.id.gameFrameLayout, GameResultFragment.newInstance(settings, points)).commit()
    }
}