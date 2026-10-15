/**
 * Zenith '26 College Fest Management System
 * Admin Dashboard JavaScript (admin.js)
 */

document.addEventListener('DOMContentLoaded', () => {
  // Stats Elements
  const statTotalEvents = document.getElementById('stat-total-events');
  const statTotalRegs = document.getElementById('stat-total-regs');
  const statTotalCapSub = document.getElementById('stat-total-cap-sub');
  const statUtilization = document.getElementById('stat-utilization');
  const statUtilizationBar = document.getElementById('stat-utilization-bar');
  const statUpcomingEvents = document.getElementById('stat-upcoming-events');
  const categoryBreakdownContainer = document.getElementById('category-breakdown-container');
  const lastUpdatedText = document.getElementById('last-updated-text');

  // Error Banner
  const adminErrorBanner = document.getElementById('admin-error-banner');
  const adminErrorText = document.getElementById('admin-error-text');
  const adminRetryBtn = document.getElementById('admin-retry-btn');
  const refreshDashboardBtn = document.getElementById('refresh-dashboard-btn');

  // Tabs & Panels
  const tabEventsBtn = document.getElementById('tab-events-btn');
  const tabRegsBtn = document.getElementById('tab-regs-btn');
  const tabEventsBadge = document.getElementById('tab-events-badge');
  const tabRegsBadge = document.getElementById('tab-regs-badge');
  const panelEvents = document.getElementById('panel-events');
  const panelRegistrations = document.getElementById('panel-registrations');

  // Tables
  const eventsTableBody = document.getElementById('events-table-body');
  const registrationsTableBody = document.getElementById('registrations-table-body');
  const filterRegEvent = document.getElementById('filter-reg-event');

  // Add Event Modal Elements
  const openAddEventBtn = document.getElementById('open-add-event-btn');
  const addEventModal = document.getElementById('add-event-modal');
  const closeAddEventBtn = document.getElementById('close-add-event-btn');
  const cancelAddEventBtn = document.getElementById('cancel-add-event-btn');
  const addEventForm = document.getElementById('add-event-form');
  const addEventError = document.getElementById('add-event-error');

  // Delete Registration Modal Elements
  const deleteRegModal = document.getElementById('delete-reg-modal');
  const cancelDeleteRegBtn = document.getElementById('cancel-delete-reg-btn');
  const confirmDeleteRegBtn = document.getElementById('confirm-delete-reg-btn');
  const deleteRegDesc = document.getElementById('delete-reg-desc');
  let pendingDeleteRegId = null;

  // Cached Data
  let allEventsList = [];

  // Tab Switching
  tabEventsBtn.addEventListener('click', () => {
    tabEventsBtn.className = 'dashboard-tab py-3 px-1 border-b-2 font-bold text-sm text-brand-600 border-brand-600 flex items-center gap-2';
    tabRegsBtn.className = 'dashboard-tab py-3 px-1 border-b-2 font-semibold text-sm text-slate-500 border-transparent hover:text-slate-700 flex items-center gap-2';
    panelEvents.classList.remove('hidden');
    panelRegistrations.classList.add('hidden');
  });

  tabRegsBtn.addEventListener('click', () => {
    tabRegsBtn.className = 'dashboard-tab py-3 px-1 border-b-2 font-bold text-sm text-brand-600 border-brand-600 flex items-center gap-2';
    tabEventsBtn.className = 'dashboard-tab py-3 px-1 border-b-2 font-semibold text-sm text-slate-500 border-transparent hover:text-slate-700 flex items-center gap-2';
    panelRegistrations.classList.remove('hidden');
    panelEvents.classList.add('hidden');
    fetchRegistrations();
  });

  // Sync / Refresh button
  refreshDashboardBtn.addEventListener('click', loadDashboardData);
  adminRetryBtn.addEventListener('click', loadDashboardData);

  /**
   * Main loader for Dashboard
   */
  async function loadDashboardData() {
    adminErrorBanner.classList.add('hidden');
    lastUpdatedText.textContent = 'Syncing...';

    try {
      await Promise.all([
        fetchStats(),
        fetchEventsTable(),
      ]);
      const now = new Date();
      lastUpdatedText.textContent = `Updated ${now.toLocaleTimeString()}`;
    } catch (err) {
      console.error('Error loading dashboard data:', err);
      adminErrorBanner.classList.remove('hidden');
      adminErrorText.textContent = err.message || 'Could not communicate with the backend server.';
      lastUpdatedText.textContent = 'Sync failed';
    }
  }

  /**
   * 1. Fetch live stats
   */
  async function fetchStats() {
    const res = await fetch('/api/stats');
    if (!res.ok) throw new Error(`Stats API failed (HTTP ${res.status})`);
    const stats = await res.json();

    statTotalEvents.textContent = stats.total_events;
    statTotalRegs.textContent = stats.total_registrations;
    statTotalCapSub.textContent = `Out of ${stats.total_capacity} total seats`;
    statUtilization.textContent = `${stats.overall_utilization_pct}%`;
    statUtilizationBar.style.width = `${Math.min(stats.overall_utilization_pct, 100)}%`;
    statUpcomingEvents.textContent = stats.upcoming_events_count;

    renderCategoryBreakdown(stats.events_by_category || {});
  }

  /**
   * 2. Render category breakdown bar/pills
   */
  function renderCategoryBreakdown(eventsByCategory) {
    categoryBreakdownContainer.innerHTML = '';
    const categories = Object.keys(eventsByCategory);

    if (categories.length === 0) {
      categoryBreakdownContainer.innerHTML = '<p class="text-xs text-slate-400 col-span-full">No event category data available.</p>';
      return;
    }

    const catIcons = {
      'Technical': '💻',
      'Cultural': '🎭',
      'Sports': '⚽',
      'Gaming': '🎮',
      'Workshop': '🛠️'
    };

    categories.forEach(cat => {
      const count = eventsByCategory[cat];
      const icon = catIcons[cat] || '📌';
      const el = document.createElement('div');
      el.className = 'bg-slate-50 border border-slate-200/80 rounded-xl p-3 flex items-center justify-between';
      el.innerHTML = `
        <div class="flex items-center gap-2">
          <span class="text-lg">${icon}</span>
          <span class="text-xs font-semibold text-slate-700">${cat}</span>
        </div>
        <span class="text-xs font-extrabold px-2 py-0.5 rounded-full bg-white text-slate-800 border border-slate-200 shadow-2xs">
          ${count}
        </span>
      `;
      categoryBreakdownContainer.appendChild(el);
    });
  }

  /**
   * 3. Fetch events list & populate table sorted by utilization descending
   */
  async function fetchEventsTable() {
    const res = await fetch('/api/events');
    if (!res.ok) throw new Error(`Events API failed (HTTP ${res.status})`);
    const events = await res.json();
    allEventsList = events;

    tabEventsBadge.textContent = events.length;

    // Populate dropdown in Registrations tab
    populateEventFilterDropdown(events);

    // Compute utilization % for each event and sort descending
    const eventsWithUtil = events.map(ev => {
      const registered = ev.capacity - ev.available_seats;
      const utilPct = ev.capacity > 0 ? (registered / ev.capacity) * 100 : 0;
      return {
        ...ev,
        registered,
        utilPct: Number(utilPct.toFixed(1)),
      };
    });

    // Sort by utilization percentage descending
    eventsWithUtil.sort((a, b) => b.utilPct - a.utilPct);

    // Render table
    eventsTableBody.innerHTML = '';
    if (eventsWithUtil.length === 0) {
      eventsTableBody.innerHTML = '<tr><td colspan="6" class="text-center py-6 text-slate-400">No events found in database.</td></tr>';
      return;
    }

    eventsWithUtil.forEach(ev => {
      const isOver90 = ev.utilPct >= 90.0;
      const row = document.createElement('tr');
      row.className = isOver90 
        ? 'bg-rose-50/70 border-l-4 border-rose-500 hover:bg-rose-100/60 transition-colors' 
        : 'hover:bg-slate-50 transition-colors';

      row.innerHTML = `
        <td class="py-3 pl-6 pr-3">
          <div class="flex items-center gap-2.5">
            <span class="text-lg shrink-0">${ev.icon || '⚡'}</span>
            <div>
              <span class="font-bold text-slate-900 block">${escapeHtml(ev.title)}</span>
              <span class="text-[10px] text-slate-400 font-mono">ID #${ev.id} • ${ev.fee === 0 ? 'Free' : '₹' + ev.fee}</span>
            </div>
          </div>
        </td>
        <td class="px-3 py-3">
          <span class="inline-flex px-2 py-0.5 rounded-full text-[11px] font-semibold bg-slate-100 text-slate-700 border border-slate-200">
            ${ev.category}
          </span>
        </td>
        <td class="px-3 py-3 text-slate-600">
          <div class="font-medium">${ev.event_date}</div>
          <div class="text-[10px] text-slate-400">${escapeHtml(ev.venue)}</div>
        </td>
        <td class="px-3 py-3 text-slate-800 font-semibold font-mono">
          ${ev.registered} / ${ev.capacity}
        </td>
        <td class="px-3 py-3">
          <span class="font-semibold ${ev.available_seats <= 0 ? 'text-rose-600 font-bold' : 'text-emerald-700'}">
            ${ev.available_seats <= 0 ? 'SOLD OUT' : `${ev.available_seats} seats`}
          </span>
        </td>
        <td class="py-3 pl-3 pr-6 text-right">
          <div class="inline-flex items-center gap-2">
            <div class="w-16 bg-slate-200 rounded-full h-1.5 overflow-hidden">
              <div class="h-1.5 rounded-full ${isOver90 ? 'bg-rose-500' : 'bg-brand-600'}" style="width: ${Math.min(ev.utilPct, 100)}%"></div>
            </div>
            <span class="font-bold font-mono text-xs ${isOver90 ? 'text-rose-700' : 'text-slate-800'}">
              ${ev.utilPct}%
            </span>
            ${isOver90 ? '<span class="text-xs" title="Over 90% Capacity!">⚠️</span>' : ''}
          </div>
        </td>
      `;

      eventsTableBody.appendChild(row);
    });
  }

  /**
   * 4. Populate Event Filter Dropdown
   */
  function populateEventFilterDropdown(events) {
    const selected = filterRegEvent.value;
    filterRegEvent.innerHTML = '<option value="">All Events</option>';
    events.forEach(ev => {
      const opt = document.createElement('option');
      opt.value = ev.id;
      opt.textContent = `[#${ev.id}] ${ev.title}`;
      filterRegEvent.appendChild(opt);
    });
    filterRegEvent.value = selected;
  }

  filterRegEvent.addEventListener('change', () => {
    fetchRegistrations();
  });

  /**
   * 5. Fetch Registrations List (Filterable by event_id)
   */
  async function fetchRegistrations() {
    registrationsTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-6 text-slate-400">Loading registrations...</td></tr>';

    try {
      const eventId = filterRegEvent.value;
      const url = eventId ? `/api/registrations?event_id=${eventId}` : '/api/registrations';
      const res = await fetch(url);
      if (!res.ok) throw new Error(`Registrations API failed (HTTP ${res.status})`);
      const regs = await res.json();

      tabRegsBadge.textContent = regs.length;
      registrationsTableBody.innerHTML = '';

      if (regs.length === 0) {
        registrationsTableBody.innerHTML = '<tr><td colspan="7" class="text-center py-8 text-slate-400">No student registrations found for this selection.</td></tr>';
        return;
      }

      regs.forEach(r => {
        const row = document.createElement('tr');
        row.className = 'hover:bg-slate-50 transition-colors';
        const formattedDate = new Date(r.registered_at).toLocaleString();

        row.innerHTML = `
          <td class="py-3 pl-6 pr-3 font-mono font-bold text-slate-700">
            #${r.id}
          </td>
          <td class="px-3 py-3 font-semibold text-slate-900">
            ${escapeHtml(r.student_name)}
          </td>
          <td class="px-3 py-3 font-mono text-slate-600">
            ${escapeHtml(r.roll_number)}
          </td>
          <td class="px-3 py-3 text-slate-600">
            <div>${escapeHtml(r.email)}</div>
            ${r.phone ? `<div class="text-[10px] text-slate-400 font-mono">${escapeHtml(r.phone)}</div>` : ''}
          </td>
          <td class="px-3 py-3">
            <span class="font-medium text-slate-800">${escapeHtml(r.event_title)}</span>
            <span class="text-[10px] text-slate-400 block font-mono">Event #${r.event_id}</span>
          </td>
          <td class="px-3 py-3 text-[11px] text-slate-500 whitespace-nowrap">
            ${formattedDate}
          </td>
          <td class="py-3 pl-3 pr-6 text-right">
            <button 
              type="button" 
              data-reg-id="${r.id}" 
              data-student-name="${escapeHtml(r.student_name)}" 
              data-event-title="${escapeHtml(r.event_title)}"
              class="cancel-reg-btn text-xs text-rose-600 hover:text-rose-800 font-semibold px-2 py-1 rounded hover:bg-rose-50 transition-colors"
            >
              Cancel
            </button>
          </td>
        `;

        registrationsTableBody.appendChild(row);
      });

      // Attach cancel listeners
      document.querySelectorAll('.cancel-reg-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          const regId = btn.getAttribute('data-reg-id');
          const studentName = btn.getAttribute('data-student-name');
          const eventTitle = btn.getAttribute('data-event-title');
          openDeleteRegModal(regId, studentName, eventTitle);
        });
      });
    } catch (err) {
      console.error(err);
      registrationsTableBody.innerHTML = `<tr><td colspan="7" class="text-center py-6 text-rose-600">Failed to load registrations: ${err.message}</td></tr>`;
    }
  }

  /**
   * Delete Registration Modal Logic (Stretch Goal 2)
   */
  function openDeleteRegModal(regId, studentName, eventTitle) {
    pendingDeleteRegId = regId;
    deleteRegDesc.textContent = `Are you sure you want to cancel registration #${regId} for ${studentName} (${eventTitle})? The seat will be released immediately.`;
    deleteRegModal.classList.remove('hidden');
  }

  function closeDeleteRegModal() {
    pendingDeleteRegId = null;
    deleteRegModal.classList.add('hidden');
  }

  cancelDeleteRegBtn.addEventListener('click', closeDeleteRegModal);

  confirmDeleteRegBtn.addEventListener('click', async () => {
    if (!pendingDeleteRegId) return;

    try {
      const res = await fetch(`/api/registrations/${pendingDeleteRegId}`, {
        method: 'DELETE',
      });

      if (!res.ok) {
        throw new Error('Failed to delete registration.');
      }

      closeDeleteRegModal();
      // Re-fetch all dashboard state and registrations list
      await loadDashboardData();
      await fetchRegistrations();
    } catch (err) {
      alert(`Error: ${err.message}`);
    }
  });

  /**
   * Add Event Modal Logic (Stretch Goal 1)
   */
  openAddEventBtn.addEventListener('click', () => {
    addEventForm.reset();
    addEventError.classList.add('hidden');
    addEventModal.classList.remove('hidden');
  });

  function closeAddEventModal() {
    addEventModal.classList.add('hidden');
  }

  closeAddEventBtn.addEventListener('click', closeAddEventModal);
  cancelAddEventBtn.addEventListener('click', closeAddEventModal);

  addEventForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    addEventError.classList.add('hidden');

    const title = document.getElementById('new-event-title').value.trim();
    const category = document.getElementById('new-event-category').value;
    const icon = document.getElementById('new-event-icon').value.trim() || '⚡';
    const date = document.getElementById('new-event-date').value.trim();
    const time = document.getElementById('new-event-time').value.trim();
    const venue = document.getElementById('new-event-venue').value.trim();
    const capacity = parseInt(document.getElementById('new-event-capacity').value, 10);
    const fee = parseInt(document.getElementById('new-event-fee').value, 10) || 0;
    const description = document.getElementById('new-event-desc').value.trim();

    try {
      const res = await fetch('/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title,
          category,
          icon,
          event_date: date,
          event_time: time,
          venue,
          capacity,
          fee,
          description,
        }),
      });

      const data = await res.json();
      if (!res.ok) {
        throw new Error(data.detail || 'Could not create event.');
      }

      closeAddEventModal();
      await loadDashboardData();
    } catch (err) {
      addEventError.classList.remove('hidden');
      addEventError.textContent = err.message;
    }
  });

  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
  }

  // Initial load
  loadDashboardData();
});
