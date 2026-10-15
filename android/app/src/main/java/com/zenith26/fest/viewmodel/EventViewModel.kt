package com.zenith26.fest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.model.RegistrationResponse
import com.zenith26.fest.data.repository.EventRepository
import com.zenith26.fest.data.repository.NetworkResult
import com.zenith26.fest.network.NetworkClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State representing the complete screen state.
 */
data class EventUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val activeModalEvent: Event? = null,
    val isModalVisible: Boolean = false,

    // Form inputs and validation state
    val formStudentName: String = "",
    val formRollNumber: String = "",
    val formEmail: String = "",
    val formPhone: String = "",
    val errName: String? = null,
    val errRoll: String? = null,
    val errEmail: String? = null,

    // Registration submission state
    val isRegistering: Boolean = false,
    val registrationSuccess: RegistrationResponse? = null,
    val registrationError: String? = null,

    // Settings / Backend URL config
    val backendUrl: String = NetworkClient.currentBaseUrl,
    val isConfigDialogVisible: Boolean = false
)

class EventViewModel(
    private val repository: EventRepository = EventRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        loadEvents()
    }

    /**
     * Fetch events from API matching current category and search query.
     */
    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = repository.getEvents(
                category = _uiState.value.selectedCategory,
                search = _uiState.value.searchQuery
            )

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            events = result.data,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.exception.localizedMessage ?: "Unable to connect to backend server."
                        )
                    }
                }
            }
        }
    }

    /**
     * Filter by category (e.g. All, Technical, Cultural, Sports, Gaming, Workshop)
     */
    fun selectCategory(category: String) {
        if (_uiState.value.selectedCategory != category) {
            _uiState.update { it.copy(selectedCategory = category) }
            loadEvents()
        }
    }

    /**
     * Update search query with 250ms debounce matching web frontend.
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(250)
            loadEvents()
        }
    }

    fun clearSearch() {
        searchDebounceJob?.cancel()
        _uiState.update { it.copy(searchQuery = "") }
        loadEvents()
    }

    fun resetFilters() {
        searchDebounceJob?.cancel()
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = "All"
            )
        }
        loadEvents()
    }

    fun retryConnection() {
        loadEvents()
    }

    /**
     * Open Event Detail and Registration Modal
     */
    fun openEventModal(event: Event) {
        // Reset modal form fields and banners
        _uiState.update {
            it.copy(
                activeModalEvent = event,
                isModalVisible = true,
                formStudentName = "",
                formRollNumber = "",
                formEmail = "",
                formPhone = "",
                errName = null,
                errRoll = null,
                errEmail = null,
                isRegistering = false,
                registrationSuccess = null,
                registrationError = null
            )
        }

        // Fetch fresh event details to ensure seat count is up-to-date
        viewModelScope.launch {
            val result = repository.getEvent(event.id)
            if (result is NetworkResult.Success) {
                _uiState.update {
                    if (it.isModalVisible && it.activeModalEvent?.id == event.id) {
                        it.copy(activeModalEvent = result.data)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun closeEventModal() {
        _uiState.update {
            it.copy(
                isModalVisible = false,
                activeModalEvent = null,
                isRegistering = false,
                registrationSuccess = null,
                registrationError = null,
                errName = null,
                errRoll = null,
                errEmail = null
            )
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(formStudentName = value, errName = null) }
    }

    fun onRollChanged(value: String) {
        _uiState.update { it.copy(formRollNumber = value, errRoll = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(formEmail = value, errEmail = null) }
    }

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(formPhone = value) }
    }

    /**
     * Validates client rules and submits registration to FastAPI backend.
     */
    fun submitRegistration() {
        val state = _uiState.value
        val event = state.activeModalEvent ?: return

        val name = state.formStudentName.trim()
        val roll = state.formRollNumber.trim()
        val email = state.formEmail.trim()
        val phone = state.formPhone.trim().ifEmpty { null }

        // Client-side validations matching app.js
        var hasClientError = false
        var nameErr: String? = null
        var rollErr: String? = null
        var emailErr: String? = null

        if (name.isEmpty()) {
            nameErr = "Please enter your name."
            hasClientError = true
        }

        if (roll.isEmpty()) {
            rollErr = "Roll number is required."
            hasClientError = true
        }

        if (email.isEmpty() || !email.contains("@")) {
            emailErr = "Valid email address containing '@' is required."
            hasClientError = true
        }

        if (hasClientError) {
            _uiState.update {
                it.copy(
                    errName = nameErr,
                    errRoll = rollErr,
                    errEmail = emailErr
                )
            }
            return
        }

        // Send registration request to server
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRegistering = true,
                    registrationError = null,
                    registrationSuccess = null
                )
            }

            val request = RegistrationRequest(
                studentName = name,
                rollNumber = roll,
                email = email,
                phone = phone
            )

            val result = repository.registerForEvent(event.id, request)

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            registrationSuccess = result.data,
                            registrationError = null
                        )
                    }

                    // Refresh event list to update seat count
                    loadEvents()

                    // Auto-close modal after 1.8s matching web frontend
                    delay(1800)
                    closeEventModal()
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = result.message
                        )
                    }
                }
                is NetworkResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isRegistering = false,
                            registrationError = "Network error: could not connect to server."
                        )
                    }
                }
            }
        }
    }

    // Config Dialog Controls for Development / Testing
    fun showConfigDialog(show: Boolean) {
        _uiState.update { it.copy(isConfigDialogVisible = show) }
    }

    fun updateBackendUrl(newUrl: String) {
        NetworkClient.updateBaseUrl(newUrl)
        _uiState.update {
            it.copy(
                backendUrl = NetworkClient.currentBaseUrl,
                isConfigDialogVisible = false
            )
        }
        loadEvents()
    }
}
