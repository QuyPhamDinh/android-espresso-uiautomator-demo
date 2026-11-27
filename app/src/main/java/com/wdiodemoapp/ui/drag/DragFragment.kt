package com.wdiodemoapp.ui.drag

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wdiodemoapp.R
import com.wdiodemoapp.databinding.FragmentDragBinding
import com.wdiodemoapp.ui.applyTopThirdOffset

class DragFragment : Fragment() {

    private var _binding: FragmentDragBinding? = null
    private val binding get() = _binding!!

    private data class PuzzlePair(
        val key: String,
        val dragView: TextView,
        val dropView: TextView,
    )

    private lateinit var pairs: List<PuzzlePair>
    private val solvedKeys = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTopThirdOffset(
            container = binding.dragDropScreen,
            target = binding.puzzleContainer,
            reservedContentHeightDp = 580,
        )

        pairs = listOf(
            PuzzlePair("L1", binding.dragL1, binding.dropL1),
            PuzzlePair("C1", binding.dragC1, binding.dropC1),
            PuzzlePair("R1", binding.dragR1, binding.dropR1),
            PuzzlePair("L2", binding.dragL2, binding.dropL2),
            PuzzlePair("C2", binding.dragC2, binding.dropC2),
            PuzzlePair("R2", binding.dragR2, binding.dropR2),
            PuzzlePair("L3", binding.dragL3, binding.dropL3),
            PuzzlePair("C3", binding.dragC3, binding.dropC3),
            PuzzlePair("R3", binding.dragR3, binding.dropR3),
        )

        pairs.forEach { attachDragBehavior(it) }

        binding.renewButton.setOnClickListener { resetPuzzle() }
        binding.buttonRetry.setOnClickListener { resetPuzzle() }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun attachDragBehavior(pair: PuzzlePair) {
        val dragView = pair.dragView
        var startRawX = 0f
        var startRawY = 0f
        var startTranslationX = 0f
        var startTranslationY = 0f

        dragView.setOnTouchListener { v, event ->
            if (solvedKeys.contains(pair.key)) {
                return@setOnTouchListener false
            }

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    v.translationZ = 24f
                    binding.dragTray.translationZ = 24f
                    startRawX = event.rawX
                    startRawY = event.rawY
                    startTranslationX = v.translationX
                    startTranslationY = v.translationY
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    v.translationX = startTranslationX + (event.rawX - startRawX)
                    v.translationY = startTranslationY + (event.rawY - startRawY)
                    true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    v.translationZ = 0f
                    binding.dragTray.translationZ = 0f
                    if (isDroppedOnTarget(v, pair.dropView)) {
                        markSolved(pair)
                    } else {
                        v.animate()
                            .translationX(0f)
                            .translationY(0f)
                            .setDuration(150)
                            .start()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun isDroppedOnTarget(dragView: View, dropView: View): Boolean {
        val dragLoc = IntArray(2)
        val dropLoc = IntArray(2)
        dragView.getLocationOnScreen(dragLoc)
        dropView.getLocationOnScreen(dropLoc)

        val centerX = dragLoc[0] + dragView.width / 2
        val centerY = dragLoc[1] + dragView.height / 2

        val slopPx = (16 * resources.displayMetrics.density).toInt()
        val targetRect = Rect(
            dropLoc[0] - slopPx,
            dropLoc[1] - slopPx,
            dropLoc[0] + dropView.width + slopPx,
            dropLoc[1] + dropView.height + slopPx,
        )
        return targetRect.contains(centerX, centerY)
    }

    private fun markSolved(pair: PuzzlePair) {
        solvedKeys.add(pair.key)
        pair.dragView.visibility = View.INVISIBLE
        pair.dragView.translationX = 0f
        pair.dragView.translationY = 0f

        pair.dropView.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.drop_zone_filled),
        )
        pair.dropView.setTextColor(
            ContextCompat.getColor(requireContext(), R.color.text_primary),
        )

        if (solvedKeys.size == pairs.size) {
            binding.puzzleContainer.visibility = View.GONE
            binding.congratsContainer.visibility = View.VISIBLE
        }
    }

    private fun resetPuzzle() {
        solvedKeys.clear()
        pairs.forEach { pair ->
            pair.dragView.visibility = View.VISIBLE
            pair.dragView.translationX = 0f
            pair.dragView.translationY = 0f
            pair.dropView.setBackgroundResource(R.drawable.bg_drop_zone)
            pair.dropView.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.text_secondary),
            )
        }
        binding.congratsContainer.visibility = View.GONE
        binding.puzzleContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
