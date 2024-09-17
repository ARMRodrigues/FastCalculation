package com.example.fastcalculation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.example.fastcalculation.databinding.FragmentGameResultBinding


class GameResultFragment : Fragment() {
    private lateinit var fragmentGameResultBinding: FragmentGameResultBinding

    private lateinit var settings: Settings

    private var points: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            settings = it.getParcelable(Extras.EXTRA_SETTINGS) ?: Settings()
            points = it.getFloat("points")
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentGameResultBinding = FragmentGameResultBinding.inflate(inflater, container, false)
        "%.1f".format(points).also {
            fragmentGameResultBinding.resultsPointsTextView.text = it
        }
        fragmentGameResultBinding.replayButton.setOnClickListener {
            (context as OnPlayGame).onPlayGame()
        }
        return fragmentGameResultBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(settings: Settings, points : Float) =
            GameResultFragment().apply {
                arguments = Bundle().apply {
                    putFloat("points", points)
                    putParcelable(Extras.EXTRA_SETTINGS, settings)
                }
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.restartGameMenuItem).isVisible = false
    }
}