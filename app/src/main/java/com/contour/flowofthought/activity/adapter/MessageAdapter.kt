package com.contour.flowofthought.activity.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contour.flowofthought.oltp.model.Message

class MessageAdapter(
    private val context: Context,
    var messages: ArrayList<Message>
) : RecyclerView.Adapter<MessageAdapter.Holder>() {
//
//    var currentRichtextFocus: MutableLiveData<RichEditText> = MutableLiveData()
//    var currentIndexFocus: Int = 0
//
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
//        val view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
//        return Holder(view, this)
        return Holder(View(context), this)
    }
//
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.update(position)
    }

    override fun getItemCount(): Int = messages.size
//
    class Holder(private val view: View, private val adapter: MessageAdapter) :
        RecyclerView.ViewHolder(view) {

        fun update(position: Int) {
            val message = adapter.messages[position]
//            val messageRichtext = view.findViewById<RichEditText>(R.id.textview_message).apply {
//                setText(message.message)
//                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
//                    if (hasFocus) {
//                        adapter.currentRichtextFocus.value = this
//                        adapter.currentIndexFocus = position
//                    }
//                }
//            }
        }
    }
}