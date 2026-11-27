package com.wdiodemoapp.ui.swipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.wdiodemoapp.IdlingHooks
import com.wdiodemoapp.R
import com.wdiodemoapp.databinding.FragmentSwipeBinding
import com.wdiodemoapp.databinding.ItemCarouselCardBinding
import com.wdiodemoapp.ui.applyTopThirdOffset

class SwipeFragment : Fragment() {

    private var _binding: FragmentSwipeBinding? = null
    private val binding get() = _binding!!

    private val cards = listOf(
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_0__,
            accessibilityId = "__CAROUSEL_ITEM_0__",
            title = "FULLY OPEN SOURCE",
            description = "WebdriverIO is fully open source and can be found on GitHub.",
        ),
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_1__,
            accessibilityId = "__CAROUSEL_ITEM_1__",
            title = "GREAT COMMUNITY",
            description = "WebdriverIO has a great community that supports all members.",
        ),
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_2__,
            accessibilityId = "__CAROUSEL_ITEM_2__",
            title = "JS.FOUNDATION",
            description = "The JS Foundation empowers projects with neutral governance.",
        ),
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_3__,
            accessibilityId = "__CAROUSEL_ITEM_3__",
            title = "SUPPORT VIDEOS",
            description = "The community has great YouTube videos for getting started.",
        ),
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_4__,
            accessibilityId = "__CAROUSEL_ITEM_4__",
            title = "EXTENDABLE",
            description = "Adding helper functions or complex setups is fast and clean.",
        ),
        CarouselCard(
            id = R.id.__CAROUSEL_ITEM_5__,
            accessibilityId = "__CAROUSEL_ITEM_5__",
            title = "COMPATIBLE",
            description = "WebdriverIO works seamlessly across web and mobile automation.",
        ),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSwipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyTopThirdOffset(binding.swipeScreen, binding.swipeContent)

        setResourceIdName(binding.Carousel, "Carousel")
        binding.Carousel.apply {
            adapter = CarouselAdapter(cards)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    _binding?.carouselIndicator?.text = "${position + 1} / ${cards.size}"
                }

                override fun onPageScrollStateChanged(state: Int) {
                    IdlingHooks.onCarouselScrolling(state != ViewPager2.SCROLL_STATE_IDLE)
                }
            })
        }
    }

    override fun onDestroyView() {
        IdlingHooks.onCarouselScrolling(false)
        super.onDestroyView()
        _binding = null
    }

    private data class CarouselCard(
        @param:IdRes val id: Int,
        val accessibilityId: String,
        val title: String,
        val description: String,
    )

    private class CarouselAdapter(
        private val items: List<CarouselCard>,
    ) : RecyclerView.Adapter<CarouselAdapter.CardViewHolder>() {

        class CardViewHolder(
            val binding: ItemCarouselCardBinding,
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
            val binding = ItemCarouselCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
            return CardViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
            val item = items[position]
            holder.itemView.id = item.id
            holder.itemView.contentDescription = item.accessibilityId
            setResourceIdName(holder.itemView, item.accessibilityId)
            holder.binding.cardTitle.text = item.title
            holder.binding.cardDescription.text = item.description
        }

        override fun getItemCount(): Int = items.size
    }

    companion object {
        private fun setResourceIdName(view: View, resourceIdName: String) {
            ViewCompat.setAccessibilityDelegate(
                view,
                object : AccessibilityDelegateCompat() {
                    override fun onInitializeAccessibilityNodeInfo(
                        host: View,
                        info: AccessibilityNodeInfoCompat,
                    ) {
                        super.onInitializeAccessibilityNodeInfo(host, info)
                        info.viewIdResourceName = resourceIdName
                    }
                },
            )
        }
    }
}
