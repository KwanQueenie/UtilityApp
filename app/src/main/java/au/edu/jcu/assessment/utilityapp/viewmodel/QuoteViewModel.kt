package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



//ViewModel: store and manage UI-related data that survives configuration changes, such as screen rotations
class QuoteViewModel : ViewModel() {
    //MutableStateFlow: manage state changes in a reactive way.
    private val _quote = MutableStateFlow("Click button to load quote")
    val quote: StateFlow<String> = _quote //StateFlow: read-only version of MutableStateFlow.
    //viewModelScope.launch is to start a Kotlin coroutine within a ViewModel
    // that is automatically canceled when the ViewModel is destroyed (when its onCleared() method is called).
    fun loadQuote() {
        viewModelScope.launch {
            try {
                val quotes = RetrofitInstance.api.getQuotes()
                if (quotes.isNotEmpty()) {
                    val randomQuote = quotes.random()
                    _quote.value = "\"${randomQuote.q}\" — ${randomQuote.a}"
                }
            } catch (e: Exception) {
                _quote.value = "Error: ${e.message}"
            }
        }
    }
}