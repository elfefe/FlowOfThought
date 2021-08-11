package com.contour.flowofthought.mvvm.viewmodel

import androidx.lifecycle.*
import com.contour.flowofthought.oltp.model.Image
import com.contour.flowofthought.oltp.model.Message
import com.contour.flowofthought.oltp.model.Thought
import com.contour.flowofthought.mvvm.repository.MainDbRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainDbViewModel(
    private val mainDbRepository: MainDbRepository
): ViewModel() {

    private var _thoughtById: MutableLiveData<Thought?> = MutableLiveData<Thought?>()
    val thoughtById: LiveData<Thought?>
        get() = _thoughtById

    fun saveThought(vararg thought: Thought) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDbRepository.saveThought(*thought)
        }
    }

    fun observeThought(): LiveData<List<Thought>> = mainDbRepository.observeThoughts()

    fun observeThoughtById(id: Long) = mainDbRepository.observeThoughtById(id)

    fun queryThoughtById(id: Long): LiveData<Thought?> {
        viewModelScope.launch(Dispatchers.IO) {
            _thoughtById.postValue(mainDbRepository.queryThoughtById(id))
        }
        return thoughtById
    }

    fun removeThought(vararg thought: Thought) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDbRepository.removeThought(*thought)
        }
    }

    fun removeThoughtById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDbRepository.removeThoughtById(id)
        }
    }

    fun saveMessage(vararg message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDbRepository.saveMessage(*message)
        }
    }

    fun observeMessage(): LiveData<List<Message>> = mainDbRepository.observeMessages()

    fun observeMessageByThoughtId(id: Long): LiveData<List<Message>> = mainDbRepository.observeMessagesByThoughtId(id)

    fun observeFirstMessagesByThoughtId(): LiveData<List<Message>> = mainDbRepository.observeFirstMessagesByThoughtId()

    fun removeMessage(vararg message: Message) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDbRepository.removeMessage(*message)
        }
    }
}