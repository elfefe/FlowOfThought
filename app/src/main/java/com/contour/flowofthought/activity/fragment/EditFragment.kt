package com.contour.flowofthought.activity.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.contour.flowofthought.R
import com.contour.flowofthought.activity.MainActivity
import com.contour.flowofthought.custom.*
import com.contour.flowofthought.custom.Argument.THOUGHT_KEY
import com.contour.flowofthought.custom.State.COLLAPSED
import com.contour.flowofthought.custom.State.EXPANDED
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.oltp.model.Thought
import com.contour.flowofthought.mvvm.ViewModelFactory
import com.contour.flowofthought.mvvm.viewmodel.MainDbViewModel
import com.contour.flowofthought.custom.theme.*
import com.contour.richtext.RichEditText
import com.contour.richtext.parser.HtmlParser
import org.joda.time.DateTime

class EditFragment : Fragment() {
    companion object {
        fun newInstance(args: Bundle = Bundle()): EditFragment {
            val fragment = EditFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var activity: MainActivity

    private val mainDbViewModel by lazy { ViewModelFactory.PROVIDER(this)[MainDbViewModel::class.java] }

    private lateinit var messagesState: SnapshotStateList<Message>
    private var thought = Thought(DateTime.now().millis, "")

    private var currentMessage: Message? = null
    private var currentEditText: RichEditText? = null

    private val getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentEditText?.image(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity = requireActivity() as MainActivity
        return ComposeView(requireContext()).apply {
            setContent {
                FlowOfThoughtTheme {
                    Container(
                        Modifier
                            .background(MaterialTheme.colors.primary)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.run {
            val id = getLong(THOUGHT_KEY, thought.id)
            mainDbViewModel
                .queryThoughtById(id)
                .observe(viewLifecycleOwner) { thought ->
                    thought?.let {
                        this@EditFragment.thought = it
                    }?: mainDbViewModel.saveThought(this@EditFragment.thought)
                }

            mainDbViewModel
                .observeMessageByThoughtIdOnThoughtById()
                .observe(viewLifecycleOwner) {
                    currentMessage = null
                    addToMessagesState(it)
                }
        }

    }

    @Preview
    @Composable
    fun Container(modifier: Modifier = Modifier) {
        messagesState = remember {
            mutableStateListOf()
        }

        val newThoughtState = remember {
            mutableStateOf(COLLAPSED)
        }

        isDarkTheme = isSystemInDarkTheme()

        ConstraintLayout(
            modifier = modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        if (dragAmount > 0 && newThoughtState.value == COLLAPSED) {
                            newThoughtState.value = EXPANDED
                        } else if (dragAmount < -Pager.SLIDE_PAGE && newThoughtState.value == COLLAPSED) {
                            activity.adapter.slideTo(HistoryFragment.name)
                        }
                    }
                }
        ) {
            val (
                edit,
                markdown,
                titleField,
                buttonsContainer,
                buttonNewThought
            ) = createRefs()

            Edit(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(edit) {
                    top.linkTo(anchor = parent.top)
                    bottom.linkTo(anchor = markdown.top)
                    start.linkTo(anchor = parent.start)
                    end.linkTo(anchor = parent.end)
                    height = Dimension.fillToConstraints
                }
            )

            Markdown(
                Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .constrainAs(markdown) {
                        bottom.linkTo(anchor = parent.bottom)
                        start.linkTo(anchor = parent.start)
                        end.linkTo(anchor = parent.end)
                    }
            )

            ManagementButtons(
                titleField,
                buttonsContainer,
                buttonNewThought,
                newThoughtState
            )
        }
    }

    @Composable
    fun Edit(
        modifier: Modifier = Modifier
    ) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            contentPadding = PaddingValues(8.dp, 4.dp)
        ) {
            items(items = messagesState) { message ->
                log("Item ${messagesState.indexOf(message)}: $message")
                AndroidView(
                    {
                        FotEditText(requireContext()).apply {
                            currentEditText = this

                            fromHtml(message.text)

                            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                                if (hasFocus) {
                                    currentMessage = message
                                    currentEditText = this
                                }
                            }

                            doAfterTextChanged { text ->
                                message.text = HtmlParser.toHtml(text)

                                messagesState.run {
                                    if (isEmpty())
                                        mainDbViewModel.saveThought(thought)

                                    val filteredMessages = filter { it.id == message.id }
                                    if (filteredMessages.isNotEmpty())
                                        set(indexOf(filteredMessages[0]), message)
                                }

                                mainDbViewModel.run {
                                    saveMessage(message)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                )
            }
        }
    }

    @Composable
    fun Markdown(modifier: Modifier = Modifier) {
        Row(
            modifier = modifier
        ) {
            IconButton(
                onClick = {
                    currentEditText?.bullet(!currentEditText!!.contains(RichEditText.FORMAT_BULLET))
                },
                content = {
                    Icon(
                        imageVector = Icons.Rounded.List,
                        contentDescription = "List",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            )
            IconButton(
                onClick = {
                    currentEditText?.quote(!currentEditText!!.contains(RichEditText.FORMAT_QUOTE))
                },
                content = {
                    Icon(
                        imageVector = Icons.Rounded.FormatQuote,
                        contentDescription = "List",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            )
            IconButton(
                onClick = {
                    if (currentEditText != null)
                        getImage.launch("image/*")
                },
                content = {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        currentEditText?.undo()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Undo,
                            contentDescription = "List",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                )
                IconButton(
                    onClick = {
                        currentEditText?.redo()
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Redo,
                            contentDescription = "List",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                )
                IconButton(
                    onClick = {

                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "List",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun ConstraintLayoutScope.ManagementButtons(
        titleField: ConstrainedLayoutReference,
        buttonsContainer: ConstrainedLayoutReference,
        buttonNewThought: ConstrainedLayoutReference,
        newThoughtState: MutableState<Boolean>
    ) {
        var columnState by remember { mutableStateOf(COLLAPSED) }
        var titleState by remember { mutableStateOf(COLLAPSED) }

        val columnTransition = updateTransition(targetState = columnState, label = "")
        val titleTransition = updateTransition(targetState = titleState, label = "")
        val newThoughtTransition = updateTransition(targetState = newThoughtState, label = "")

        val rotation: Float by columnTransition.animateFloat(label = "") { state ->
            if (state == EXPANDED) 45f else 0f
        }
        val heightScale: Float by columnTransition.animateFloat(label = "") { state ->
            if (state == EXPANDED) 1f else 0f
        }
        val titleScale: Float by titleTransition.animateFloat(label = "") { state ->
            if (state == EXPANDED) .75f else 0f
        }
        val titleColor: Color by titleTransition.animateColor(label = "") { state ->
            if (state == EXPANDED) MaterialTheme.colors.onPrimary else Color.Transparent
        }
        val newThoughtSize: Dp by newThoughtTransition.animateDp(label = "") { state ->
            if (state.value == EXPANDED) 128.dp else 0.dp
        }
        val newThoughtPosition: Float by newThoughtTransition.animateFloat(label = "") { state ->
            if (state.value == EXPANDED) .5f else 0f
        }

        var title by remember {
            mutableStateOf(thought.title)
        }

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                thought.title = it
                mainDbViewModel.saveThought(thought)
            },
            modifier = Modifier
                .fillMaxWidth(titleScale)
                .constrainAs(titleField) {
                    top.linkTo(buttonsContainer.top)
                    end.linkTo(buttonsContainer.start, margin = 10.dp)
                },
            placeholder = {
                Text(
                    text = stringResource(R.string.edit_title_hint),
                    modifier = Modifier
                        .fillMaxWidth(titleScale * 1.14f),
                    color = MaterialTheme.colors.primaryVariant,
                    maxLines = 1
                )
            },
            shape = RoundedCornerShape(10),
            maxLines = 1,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = titleColor,
                backgroundColor = MaterialTheme.colors.primary,
                cursorColor = MaterialTheme.colors.onPrimary,
                focusedBorderColor = MaterialTheme.colors.primaryVariant,
                unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
            )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(.5f)
                .constrainAs(buttonsContainer) {
                    top.linkTo(parent.top, margin = 10.dp)
                    end.linkTo(parent.end, margin = 10.dp)
                }
        ) {
            FloatingActionButton(
                onClick = {
                    columnState =
                        if (columnState == COLLAPSED) EXPANDED
                        else COLLAPSED
                },
                modifier = Modifier
                    .size(64.dp),
                shape = CircleShape,
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onSecondary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "",
                    modifier = Modifier
                        .rotate(rotation)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(heightScale)
                    .verticalScroll(
                        state = ScrollState(0),
                        enabled = true
                    )
                    .padding(5f.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        val message = Message(DateTime.now().millis, thought.id, "")
                        messagesState.run {
                            add(message)
                        }
                        arguments?.putLong(THOUGHT_KEY, thought.id)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.PostAdd,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                )
                IconButton(
                    onClick = {
                        titleState =
                            if (titleState == COLLAPSED) EXPANDED
                            else COLLAPSED
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Title,
                            contentDescription = "List",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                )
                IconButton(
                    onClick = {

                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.NotificationAdd,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                )
                IconButton(
                    onClick = {
                        currentMessage?.let {
                            removeMessageState(it)
                            mainDbViewModel.removeMessage(it)
                        }
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.RemoveCircleOutline,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                thought = Thought(DateTime.now().millis, "")
                mainDbViewModel.saveThought(thought)
                title = thought.title
                messagesState.clear()
                newThoughtState.value = COLLAPSED
            },
            modifier = Modifier
                .size(newThoughtSize)
                .constrainAs(buttonNewThought) {
                    centerVerticallyTo(parent)
                    linkTo(start = parent.start, end = parent.end, bias = newThoughtPosition)
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        if (dragAmount < 0 && newThoughtState.value == EXPANDED) {
                            newThoughtState.value = COLLAPSED
                        }
                    }
                },
            shape = CircleShape,
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onSecondary
        ) {
            Icon(
                imageVector = Icons.Rounded.NoteAdd,
                contentDescription = "new",
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }

    private fun addToMessagesState(messages: List<Message>) {
        val empty = messagesState.filter {
            it.text.isEmpty()
        }
        messagesState.clear()
        messagesState.addAll(messages)
        messagesState.addAll(empty)
        messagesState.sortBy { it.id }
    }

    private fun removeMessageState(message: Message) {
        currentEditText?.clearFocus()
        messagesState.remove(message)
    }
}