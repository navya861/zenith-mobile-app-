package com.zenith26.fest.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zenith26.fest.ui.components.BackendConfigDialog
import com.zenith26.fest.ui.components.EmptyView
import com.zenith26.fest.ui.components.ErrorView
import com.zenith26.fest.ui.components.EventCard
import com.zenith26.fest.ui.components.EventDetailModal
import com.zenith26.fest.ui.components.Footer
import com.zenith26.fest.ui.components.HeroSection
import com.zenith26.fest.ui.components.LoadingView
import com.zenith26.fest.ui.components.SearchAndFilterBar
import com.zenith26.fest.ui.components.TopHeader
import com.zenith26.fest.ui.theme.Slate50
import com.zenith26.fest.viewmodel.EventViewModel

/**
 * Main Event Portal Screen reproducing the web student experience:
 * Header, Hero Section, Search and Category Filters, Event Cards,
 * Loading/Error/Empty states, Modal dialog, and Footer.
 */
@Composable
fun EventScreen(
    viewModel: EventViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val resultsCountText = when {
        uiState.isLoading -> "Loading events..."
        uiState.errorMessage != null -> "Error connecting to API"
        uiState.events.isEmpty() -> "0 events found"
        else -> "Showing ${uiState.events.size} event${if (uiState.events.size == 1) "" else "s"}"
    }

    Scaffold(
        topBar = {
            TopHeader(
                onConfigClick = { viewModel.showConfigDialog(true) }
            )
        },
        containerColor = Slate50,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate50),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            // 1. HERO SECTION
            item {
                HeroSection()
            }

            // 2. SEARCH & CATEGORY FILTER BAR
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                    SearchAndFilterBar(
                        searchQuery = uiState.searchQuery,
                        onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                        onClearSearch = { viewModel.clearSearch() },
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { viewModel.selectCategory(it) },
                        resultsCountText = resultsCountText
                    )
                }
            }

            // 3. MAIN CONTENT: Loading, Error, Empty, or Event Cards Grid
            when {
                uiState.isLoading -> {
                    item {
                        LoadingView(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
                uiState.errorMessage != null -> {
                    item {
                        ErrorView(
                            message = uiState.errorMessage ?: "",
                            onRetry = { viewModel.retryConnection() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                uiState.events.isEmpty() -> {
                    item {
                        EmptyView(
                            onResetFilters = { viewModel.resetFilters() },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                else -> {
                    items(
                        items = uiState.events,
                        key = { it.id }
                    ) { event ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            EventCard(
                                event = event,
                                onViewClick = { viewModel.openEventModal(it) }
                            )
                        }
                    }
                }
            }

            // Spacer before footer
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. FOOTER
            item {
                Footer()
            }
        }

        // EVENT DETAIL & REGISTRATION MODAL
        if (uiState.isModalVisible && uiState.activeModalEvent != null) {
            EventDetailModal(
                event = uiState.activeModalEvent!!,
                studentName = uiState.formStudentName,
                onNameChange = { viewModel.onNameChanged(it) },
                nameError = uiState.errName,
                rollNumber = uiState.formRollNumber,
                onRollChange = { viewModel.onRollChanged(it) },
                rollError = uiState.errRoll,
                email = uiState.formEmail,
                onEmailChange = { viewModel.onEmailChanged(it) },
                emailError = uiState.errEmail,
                phone = uiState.formPhone,
                onPhoneChange = { viewModel.onPhoneChanged(it) },
                isRegistering = uiState.isRegistering,
                registrationSuccess = uiState.registrationSuccess,
                registrationError = uiState.registrationError,
                onSubmit = { viewModel.submitRegistration() },
                onDismiss = { viewModel.closeEventModal() }
            )
        }

        // BACKEND BASE URL CONFIG DIALOG
        if (uiState.isConfigDialogVisible) {
            BackendConfigDialog(
                currentUrl = uiState.backendUrl,
                onSave = { viewModel.updateBackendUrl(it) },
                onDismiss = { viewModel.showConfigDialog(false) }
            )
        }
    }
}
