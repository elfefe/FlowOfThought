package com.contour.flowofthought.activity.fragment

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.contour.flowofthought.R
import com.contour.flowofthought.activity.MainActivity
import com.contour.flowofthought.custom.Argument.THOUGHT_KEY
import com.contour.flowofthought.custom.Pager
import com.contour.flowofthought.custom.State.COLLAPSED
import com.contour.flowofthought.custom.State.EXPANDED
import com.contour.flowofthought.custom.name
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.mvvm.ViewModelFactory
import com.contour.flowofthought.mvvm.viewmodel.MainDbViewModel
import com.contour.flowofthought.custom.theme.FlowOfThoughtTheme
import java.util.*

class HistoryFragment : Fragment() {
    companion object {
        fun newInstance(args: Bundle = Bundle()): HistoryFragment {
            val fragment = HistoryFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val mainDbViewModel by lazy { ViewModelFactory.PROVIDER(this)[MainDbViewModel::class.java] }

    private var messages: SnapshotStateList<Message>? = null

    private lateinit var activity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity() as MainActivity
        return ComposeView(requireContext()).apply {
            setContent {
                messages = remember {
                    mutableStateListOf()
                }

                mainDbViewModel
                    .observeFirstMessagesByThoughtId()
                    .observe(viewLifecycleOwner) {
                        messages?.clear()
                        messages?.addAll(it)
                    }
                Container()
            }
        }
    }

    @Preview
    @Composable
    fun Container() {
        FlowOfThoughtTheme {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.primary)
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consumeAllChanges()
                            if (dragAmount > Pager.SLIDE_PAGE) {
                                activity.adapter.slideTo(EditFragment.name)
                            }
                        }
                    },
                verticalArrangement = Arrangement.Bottom
            ) {
                History()
            }
        }
    }

    @Composable
    fun History() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary),
            verticalArrangement = Arrangement.spacedBy(1f.dp)
        ) {
            messages?.forEach {
                item {
                    Message(message = it)
                }
            }
        }
    }

    @Composable
    fun Message( message: Message) {
        var clicks = 0

        var toState by remember { mutableStateOf(COLLAPSED) }
        val transition = updateTransition(targetState = toState, label = "")
        val widthScale: Float by transition.animateFloat(label = "") { state ->
            if (state == EXPANDED) .7f else {
                clicks = 0
                1f
            }
        }

        val title = remember {
            mutableStateOf("No title")
        }

        mainDbViewModel
            .observeThoughtById(message.thoughtId)
            .observe(viewLifecycleOwner) {
                it?.run {
                    if (this.title.isNotBlank())
                        title.value = this.title
                }
            }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    activity.run {
                        adapter.fragments[0].second.arguments?.let {
                            it.putLong(THOUGHT_KEY, message.thoughtId)
                        }
                        binding.mainViewpager.currentItem = 0
                    }
                },
                shape = RoundedCornerShape(10),
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consumeAllChanges()

                            toState = if (dragAmount > 0) COLLAPSED else EXPANDED
                        }
                    }
                    .fillMaxWidth(widthScale)
                    .align(Alignment.CenterVertically)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${
                                Calendar.getInstance().run {
                                    timeInMillis = message.id
                                    DateFormat.format("dd/MM/yyy hh:mm:ss", time)
                                }
                            }",
                            color = MaterialTheme.colors.primaryVariant,
                            maxLines = 1,
                            textAlign = TextAlign.Start
                        )

                        Icon(
                            imageVector = Icons.Rounded.NotificationsActive,
                            contentDescription = "",
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }

                    Text(
                        text = title.value,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier
                            .fillMaxWidth(),
                        maxLines = 1,
                        textAlign = TextAlign.Start
                    )
                }
            }

            IconButton(
                onClick = {
                }
            ) {
                Icon(imageVector = Icons.Rounded.EditNotifications, contentDescription = "")
            }

            IconButton(
                onClick = {
                    if (clicks++ == 0) {
                        Toast
                            .makeText(
                                requireContext(),
                                resources.getString(R.string.doubletap_to_delete_thought),
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    } else {
                        mainDbViewModel.removeThoughtById(message.thoughtId)

                        toState = COLLAPSED
                        clicks = 0
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "",
                    tint = MaterialTheme.colors.error
                )
            }
        }
    }
}