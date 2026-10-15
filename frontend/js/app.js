/**
 * Zenith '26 College Fest Management System
 * Student Portal JavaScript (app.js)
 */

document.addEventListener('DOMContentLoaded', () => {
  // DOM Elements
  const eventsGrid = document.getElementById('events-grid');
  const loadingSpinner = document.getElementById('loading-spinner');
  const errorState = document.getElementById('error-state');
  const errorMessage = document.getElementById('error-message');
  const emptyState = document.getElementById('empty-state');
  const resultsCount = document.getElementById('results-count');
  const searchInput = document.getElementById('search-input');
  const clearSearchBtn = document.getElementById('clear-search-btn');
  const categoryFilters = document.getElementById('category-filters');
  const retryBtn = document.getElementById('retry-btn');
  const resetFilterBtn = document.getElementById('reset-filter-btn');
  const mobileMenuBtn = document.getElementById('mobile-menu-btn');
  const mobileMenu = document.getElementById('mobile-menu');

  // Modal Elements
  const eventModal = document.getElementById('event-modal');
  const closeModalBtn = document.getElementById('close-modal-btn');
  const modalEventIcon = document.getElementById('modal-event-icon');
  const modalCategoryBadge = document.getElementById('modal-category-badge');
  const modalFeeBadge = document.getElementById('modal-fee-badge');
  const modalEventTitle = document.getElementById('modal-event-title');
  const modalDatetime = document.getElementById('modal-datetime');
  const modalVenue = document.getElementById('modal-venue');
  const modalSeats = document.getElementById('modal-seats');
  const modalFeeText = document.getElementById('modal-fee-text');
  const modalDescription = document.getElementById('modal-description');
  const registrationForm = document.getElementById('registration-form');
  const formEventId = document.getElementById('form-event-id');
  const submitRegisterBtn = document.getElementById('submit-register-btn');
  const btnText = document.getElementById('btn-text');
  const btnSpinner = document.getElementById('btn-spinner');
  const modalSuccessBanner = document.getElementById('modal-success-banner');
  const modalErrorBanner = document.getElementById('modal-error-banner');
  const modalErrorText = document.getElementById('modal-error-text');

  // Inline Validation Field Elements
  const studentNameInput = document.getElementById('student-name');
  const rollNumberInput = document.getElementById('roll-number');
  const studentEmailInput = document.getElementById('student-email');
  const studentPhoneInput = document.getElementById('student-phone');
  const errName = document.getElementById('err-name');
  const errRoll = document.getElementById('err-roll');
  const errEmail = document.getElementById('err-email');

  // Current State
  let currentCategory = 'All';
  let currentSearch = '';
  let searchDebounceTimeout = null;
  let activeEventData = null;

  // Toggle Mobile Menu
  if (mobileMenuBtn && mobileMenu) {
    mobileMenuBtn.addEventListener('click', () => {
      mobileMenu.classList.toggle('hidden');
    });
  }

  // Category Badge Colors Map
  const categoryBadgeClasses = {
    'Technical': 'bg-blue-100 text-blue-800 border-blue-200',
    'Cultural': 'bg-pink-100 text-pink-800 border-pink-200',
    'Sports': 'bg-emerald-100 text-emerald-800 border-emerald-200',
    'Gaming': 'bg-purple-100 text-purple-800 border-purple-200',
    'Workshop': 'bg-amber-100 text-amber-800 border-amber-200',
  };

  /**
   * Fetch events from backend API using current filters
   */
  async function fetchEvents() {
    showLoading();

    try {
      const params = new URLSearchParams();
      if (currentCategory && currentCategory !== 'All') {
        params.append('category', currentCategory);
      }
      if (currentSearch && currentSearch.trim()) {
        params.append('search', currentSearch.trim());
      }

      const url = `/api/events${params.toString() ? '?' + params.toString() : ''}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new Error(`API returned HTTP ${response.status}: ${response.statusText}`);
      }

      const events = await response.json();
      renderEvents(events);
    } catch (err) {
      console.error('Error fetching events:', err);
      showError(err.message || 'Unable to load events.');
    }
  }

  /**
   * Render list of events into grid
   */
  function renderEvents(events) {
    hideAllStates();

    if (!events || events.length === 0) {
      emptyState.classList.remove('hidden');
      resultsCount.textContent = '0 events found';
      return;
    }

    resultsCount.textContent = `Showing ${events.length} event${events.length === 1 ? '' : 's'}`;
    eventsGrid.innerHTML = '';

    events.forEach((event, index) => {
      const isFull = event.available_seats <= 0;
      const feeLabel = event.fee === 0 ? 'Free' : `₹${event.fee}`;
      const catClass = categoryBadgeClasses[event.category] || 'bg-slate-100 text-slate-800 border-slate-200';

      const card = document.createElement('div');
      card.className = 'group bg-white rounded-2xl p-5 shadow-sm hover:shadow-xl border border-slate-200/80 hover:border-brand-500/40 transition-all duration-300 flex flex-col justify-between animate-fade-in relative';
      card.style.animationDelay = `${index * 40}ms`;

      card.innerHTML = `
        <div>
          <!-- Header: Category & Capacity Badges -->
          <div class="flex items-center justify-between gap-2 mb-3">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${catClass}">
              ${event.category}
            </span>
            ${isFull ? `
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold bg-rose-100 text-rose-800 border border-rose-300 shadow-sm animate-pulse">
                FULL
              </span>
            ` : `
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${event.available_seats <= 10 ? 'bg-amber-100 text-amber-800 border border-amber-200' : 'bg-slate-100 text-slate-600'}">
                ${event.available_seats} / ${event.capacity} seats left
              </span>
            `}
          </div>

          <!-- Icon & Title -->
          <div class="flex items-start gap-3 mb-3">
            <span class="text-3xl p-2 rounded-xl bg-slate-50 group-hover:scale-110 group-hover:bg-brand-50 transition-all shrink-0">
              ${event.icon || '⚡'}
            </span>
            <div>
              <h3 class="font-bold text-base text-slate-900 group-hover:text-brand-600 transition-colors leading-snug line-clamp-1">
                ${escapeHtml(event.title)}
              </h3>
              <p class="text-xs text-slate-500 line-clamp-2 mt-1">
                ${escapeHtml(event.description || 'Join this exciting fest competition!')}
              </p>
            </div>
          </div>

          <!-- Details Info Box -->
          <div class="space-y-1.5 py-3 border-t border-slate-100 text-xs text-slate-600">
            <div class="flex items-center gap-2">
              <span class="text-slate-400">📅</span>
              <span class="font-medium">${formatDate(event.event_date)} • ${escapeHtml(event.event_time)}</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-slate-400">📍</span>
              <span class="truncate">${escapeHtml(event.venue)}</span>
            </div>
          </div>
        </div>

        <!-- Footer: Fee and Action Button -->
        <div class="pt-3 border-t border-slate-100 flex items-center justify-between mt-2">
          <div>
            <span class="text-[10px] uppercase font-bold text-slate-400 block tracking-wider">Entry Fee</span>
            <span class="text-sm font-extrabold ${event.fee === 0 ? 'text-emerald-600' : 'text-slate-900'}">
              ${feeLabel}
            </span>
          </div>

          <button 
            type="button" 
            data-event-id="${event.id}" 
            class="view-details-btn px-4 py-2 rounded-xl text-xs font-bold transition-all shadow-sm ${isFull 
              ? 'bg-slate-100 text-slate-400 cursor-not-allowed hover:bg-slate-200' 
              : 'bg-brand-600 hover:bg-brand-700 active:bg-brand-800 text-white shadow-brand-500/20 hover:shadow-md'}"
          >
            ${isFull ? 'View Info' : 'View & Register →'}
          </button>
        </div>
      `;

      eventsGrid.appendChild(card);
    });

    // Attach click listeners to "View Details" buttons
    document.querySelectorAll('.view-details-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const eventId = btn.getAttribute('data-event-id');
        openEventDetailModal(eventId);
      });
    });
  }

  /**
   * Open Event Detail & Registration Modal
   */
  async function openEventDetailModal(eventId) {
    resetModalForm();
    eventModal.classList.remove('hidden');
    document.body.classList.add('overflow-hidden');

    try {
      const response = await fetch(`/api/events/${eventId}`);
      if (!response.ok) {
        throw new Error('Event could not be found.');
      }

      const event = await response.json();
      activeEventData = event;

      // Populate Modal Fields
      formEventId.value = event.id;
      modalEventIcon.textContent = event.icon || '⚡';
      modalCategoryBadge.textContent = event.category;
      modalEventTitle.textContent = event.title;
      modalDatetime.textContent = `${formatDate(event.event_date)}, ${event.event_time}`;
      modalVenue.textContent = event.venue;
      modalSeats.textContent = `${event.available_seats} seats left (out of ${event.capacity})`;
      modalFeeBadge.textContent = event.fee === 0 ? 'Free' : `₹${event.fee}`;
      modalFeeText.textContent = event.fee === 0 ? 'Free Entry' : `₹${event.fee} per participant`;
      modalDescription.textContent = event.description || 'No additional details provided for this event.';

      // Check if event is full
      if (event.available_seats <= 0) {
        modalSeats.className = 'font-bold text-rose-600';
        modalSeats.textContent = 'SOLD OUT (0 seats left)';
        submitRegisterBtn.disabled = true;
        submitRegisterBtn.classList.add('bg-slate-300', 'text-slate-500', 'cursor-not-allowed');
        submitRegisterBtn.classList.remove('bg-brand-600', 'hover:bg-brand-700');
        btnText.textContent = 'Event is Full';
      } else {
        modalSeats.className = 'font-bold text-brand-600';
        submitRegisterBtn.disabled = false;
        submitRegisterBtn.classList.remove('bg-slate-300', 'text-slate-500', 'cursor-not-allowed');
        submitRegisterBtn.classList.add('bg-brand-600', 'hover:bg-brand-700');
        btnText.textContent = 'Confirm Registration';
      }
    } catch (err) {
      console.error(err);
      modalErrorBanner.classList.remove('hidden');
      modalErrorText.textContent = err.message || 'Failed to load event details.';
    }
  }

  /**
   * Close Modal
   */
  function closeEventModal() {
    eventModal.classList.add('hidden');
    document.body.classList.remove('overflow-hidden');
    resetModalForm();
  }

  function resetModalForm() {
    registrationForm.reset();
    modalSuccessBanner.classList.add('hidden');
    modalErrorBanner.classList.add('hidden');
    errName.classList.add('hidden');
    errRoll.classList.add('hidden');
    errEmail.classList.add('hidden');
    btnSpinner.classList.add('hidden');
    submitRegisterBtn.disabled = false;
  }

  /**
   * Handle Registration Submission (Client checks + Server POST)
   */
  registrationForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const eventId = formEventId.value;
    const name = studentNameInput.value.trim();
    const roll = rollNumberInput.value.trim();
    const email = studentEmailInput.value.trim();
    const phone = studentPhoneInput.value.trim();

    // Client-side validation checks
    let hasClientError = false;

    if (!name) {
      errName.classList.remove('hidden');
      hasClientError = true;
    } else {
      errName.classList.add('hidden');
    }

    if (!roll) {
      errRoll.classList.remove('hidden');
      hasClientError = true;
    } else {
      errRoll.classList.add('hidden');
    }

    if (!email || !email.includes('@')) {
      errEmail.classList.remove('hidden');
      hasClientError = true;
    } else {
      errEmail.classList.add('hidden');
    }

    if (hasClientError) {
      return;
    }

    // Submit to server
    modalErrorBanner.classList.add('hidden');
    modalSuccessBanner.classList.add('hidden');
    submitRegisterBtn.disabled = true;
    btnSpinner.classList.remove('hidden');
    btnText.textContent = 'Registering...';

    try {
      const response = await fetch(`/api/events/${eventId}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          student_name: name,
          roll_number: roll,
          email: email,
          phone: phone || null,
        }),
      });

      const data = await response.json();

      if (response.status === 201) {
        // SUCCESS (201)
        modalSuccessBanner.classList.remove('hidden');
        document.getElementById('success-title').textContent = `Registered for ${activeEventData ? activeEventData.title : 'Event'}!`;
        document.getElementById('success-message').textContent = `Registration ID #${data.id} confirmed for ${data.student_name}.`;
        btnText.textContent = 'Registered!';

        // Re-fetch events so seat count updates on grid immediately
        await fetchEvents();

        // Close modal after 1.8 seconds
        setTimeout(() => {
          closeEventModal();
        }, 1800);
      } else {
        // FAILURE (400, 404, 409, etc.)
        modalErrorBanner.classList.remove('hidden');
        modalErrorText.textContent = data.detail || 'Registration failed. Please check the information provided.';
        submitRegisterBtn.disabled = false;
        btnText.textContent = 'Try Again';
      }
    } catch (err) {
      console.error('Registration network error:', err);
      modalErrorBanner.classList.remove('hidden');
      modalErrorText.textContent = 'Network error: could not connect to server.';
      submitRegisterBtn.disabled = false;
      btnText.textContent = 'Confirm Registration';
    } finally {
      btnSpinner.classList.add('hidden');
    }
  });

  // Modal Close Events
  closeModalBtn.addEventListener('click', closeEventModal);
  eventModal.addEventListener('click', (e) => {
    if (e.target === eventModal) {
      closeEventModal();
    }
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !eventModal.classList.contains('hidden')) {
      closeEventModal();
    }
  });

  // Category Filter Click Handler
  categoryFilters.querySelectorAll('.cat-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      categoryFilters.querySelectorAll('.cat-btn').forEach(b => {
        b.classList.remove('active', 'bg-brand-600', 'text-white', 'shadow-sm');
        b.classList.add('bg-slate-100', 'text-slate-700');
      });
      btn.classList.add('active', 'bg-brand-600', 'text-white', 'shadow-sm');
      btn.classList.remove('bg-slate-100', 'text-slate-700');

      currentCategory = btn.getAttribute('data-category');
      fetchEvents();
    });
  });

  // Search Input Handler with Debounce
  searchInput.addEventListener('input', (e) => {
    currentSearch = e.target.value;
    clearSearchBtn.classList.toggle('hidden', !currentSearch);

    if (searchDebounceTimeout) clearTimeout(searchDebounceTimeout);
    searchDebounceTimeout = setTimeout(() => {
      fetchEvents();
    }, 250);
  });

  // Clear Search Button
  clearSearchBtn.addEventListener('click', () => {
    searchInput.value = '';
    currentSearch = '';
    clearSearchBtn.classList.add('hidden');
    fetchEvents();
  });

  // Reset Filters from Empty State
  resetFilterBtn.addEventListener('click', () => {
    searchInput.value = '';
    currentSearch = '';
    clearSearchBtn.classList.add('hidden');

    const allBtn = categoryFilters.querySelector('[data-category="All"]');
    if (allBtn) allBtn.click();
  });

  // Retry Button from Error State
  retryBtn.addEventListener('click', fetchEvents);

  // Helper States
  function showLoading() {
    loadingSpinner.classList.remove('hidden');
    errorState.classList.add('hidden');
    emptyState.classList.add('hidden');
    eventsGrid.innerHTML = '';
  }

  function showError(msg) {
    loadingSpinner.classList.add('hidden');
    errorState.classList.remove('hidden');
    emptyState.classList.add('hidden');
    errorMessage.textContent = msg;
    resultsCount.textContent = 'Error connecting to API';
  }

  function hideAllStates() {
    loadingSpinner.classList.add('hidden');
    errorState.classList.add('hidden');
    emptyState.classList.add('hidden');
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
  }

  function formatDate(isoDate) {
    if (!isoDate) return '';
    // Format YYYY-MM-DD into readable Day Month
    try {
      const parts = isoDate.split('-');
      if (parts.length === 3) {
        const day = parts[2];
        const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
        const month = monthNames[parseInt(parts[1], 10) - 1] || parts[1];
        return `${day} ${month}`;
      }
    } catch (_) {}
    return isoDate;
  }

  // Initial fetch on page load
  fetchEvents();
});
