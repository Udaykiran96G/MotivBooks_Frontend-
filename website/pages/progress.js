// Progress Page
Router.register('/progress', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading progress...</p></div></div>';

    try {
        const [progress, analytics, stats] = await Promise.all([
            API.getProgress().catch(() => null),
            API.getReadingAnalytics().catch(() => null),
            API.getGrowthStats().catch(() => null)
        ]);

        renderProgress(container, progress, analytics, stats);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load progress</h3><p>${error.message}</p></div></div>`;
    }
});

function renderProgress(container, progress, analytics, stats) {
    const p = progress || {};
    const a = analytics || {};
    const s = stats || {};

    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    const dayProgress = s.dailyProgress || [
        a.mon_progress || 0, a.tue_progress || 0, a.wed_progress || 0,
        a.thu_progress || 0, a.fri_progress || 0, a.sat_progress || 0, a.sun_progress || 0
    ];
    const maxVal = Math.max(...dayProgress, 1);

    container.innerHTML = `
        <div class="page-wrapper">
            <div class="greeting-header fade-in">
                <div class="greeting-info">
                    <h2>Your Progress 📊</h2>
                    <p>Track your reading journey</p>
                </div>
            </div>

            <!-- Stats Grid -->
            <div class="stats-grid fade-in stagger-1">
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--soft-peach)">
                        <span class="material-icons-round" style="color:#D97706;font-size:24px">local_fire_department</span>
                    </div>
                    <div class="stat-value">${p.current_streak || s.streakDays || 0}</div>
                    <div class="stat-label">Day Streak</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--soft-blue)">
                        <span class="material-icons-round" style="color:#2563EB;font-size:24px">menu_book</span>
                    </div>
                    <div class="stat-value">${p.total_books_read || s.booksRead || 0}</div>
                    <div class="stat-label">Books Read</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--soft-sage)">
                        <span class="material-icons-round" style="color:#16A34A;font-size:24px">schedule</span>
                    </div>
                    <div class="stat-value">${p.total_hours_read || 0}</div>
                    <div class="stat-label">Hours Read</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--muted-lavender)">
                        <span class="material-icons-round" style="color:#7C3AED;font-size:24px">format_quote</span>
                    </div>
                    <div class="stat-value">${p.total_quotes_saved || s.quotesSaved || 0}</div>
                    <div class="stat-label">Quotes Saved</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--cream-white)">
                        <span class="material-icons-round" style="color:#D97706;font-size:24px">edit_note</span>
                    </div>
                    <div class="stat-value">${p.total_notes_taken || s.notesTaken || 0}</div>
                    <div class="stat-label">Notes Taken</div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background:var(--soft-blue)">
                        <span class="material-icons-round" style="color:#2563EB;font-size:24px">highlight</span>
                    </div>
                    <div class="stat-value">${p.total_highlights_made || 0}</div>
                    <div class="stat-label">Highlights</div>
                </div>
            </div>

            <!-- Active Goal -->
            <div class="card goal-card fade-in stagger-2">
                <div class="card-header">
                    <div class="card-title">Active Goal</div>
                </div>
                <div style="font-size:1.1rem;font-weight:600;margin-bottom:4px">${p.active_goal_title || 'Set a Goal'}</div>
                <div style="font-size:0.85rem;color:var(--text-secondary)">${p.active_goal_subtitle || ''}</div>
                <div class="goal-progress-bar" style="margin-top:12px">
                    <div class="goal-progress-fill" style="width:${p.active_goal_total_books > 0 ? ((p.active_goal_books_completed / p.active_goal_total_books) * 100) : 0}%"></div>
                </div>
                <div style="display:flex;justify-content:space-between;font-size:0.8rem;color:var(--text-secondary)">
                    <span>${p.active_goal_books_completed || 0} of ${p.active_goal_total_books || 1} ${p.active_goal_unit || 'books'}</span>
                </div>
            </div>

            <!-- Reading Analytics -->
            <div class="card fade-in stagger-3">
                <div class="card-header">
                    <div class="card-title">Weekly Reading Activity</div>
                    ${a.weekly_improvement_percentage ? `<span style="color:var(--success);font-size:0.85rem;font-weight:600">+${a.weekly_improvement_percentage}%</span>` : ''}
                </div>
                <div style="display:flex;gap:16px;margin-bottom:16px">
                    <div style="text-align:center">
                        <div style="font-size:1.5rem;font-weight:700">${a.daily_average_minutes || 0}</div>
                        <div style="font-size:0.75rem;color:var(--text-secondary)">Daily Avg (min)</div>
                    </div>
                    <div style="text-align:center">
                        <div style="font-size:1.5rem;font-weight:700">${a.longest_session_minutes || 0}</div>
                        <div style="font-size:0.75rem;color:var(--text-secondary)">Longest Session</div>
                    </div>
                    <div style="text-align:center">
                        <div style="font-size:1.5rem;font-weight:700">${a.pages_read || s.pagesRead || 0}</div>
                        <div style="font-size:0.75rem;color:var(--text-secondary)">Pages Read</div>
                    </div>
                </div>
                <div class="chart-container">
                    ${days.map((day, i) => `
                        <div class="chart-bar-wrapper">
                            <div class="chart-bar" style="height:${maxVal > 0 ? (dayProgress[i] / maxVal) * 100 : 0}%"></div>
                            <span class="chart-label">${day}</span>
                        </div>
                    `).join('')}
                </div>
            </div>

            <!-- Current Book -->
            ${p.current_book_title ? `
                <div class="card fade-in stagger-4">
                    <div class="card-header">
                        <div class="card-title">Currently Reading</div>
                    </div>
                    <div style="font-weight:600">${p.current_book_title}</div>
                    <div style="font-size:0.85rem;color:var(--text-secondary)">${p.current_book_author || ''}</div>
                    <div class="book-progress-bar" style="background:var(--bg-secondary);margin-top:8px">
                        <div class="book-progress-fill" style="background:var(--primary-light);width:${(p.current_book_progress || 0) * 100}%"></div>
                    </div>
                </div>
            ` : ''}

            <!-- Saved Quotes List -->
            <div class="card fade-in stagger-5" style="margin-top:20px;">
                <div class="card-header">
                    <div class="card-title">Saved Quotes</div>
                </div>
                <div class="quotes-list" id="saved-quotes-container">
                    <div style="text-align:center;padding:16px;color:var(--text-secondary)"><div class="spinner" style="width:24px;height:24px;border-width:2px;border-top-color:var(--primary);margin:0 auto"></div></div>
                </div>
            </div>
        </div>`;

    // Fetch and render saved quotes
    API.getSavedQuotes().then(quotes => {
        const container = document.getElementById('saved-quotes-container');
        if (!quotes || quotes.length === 0) {
            container.innerHTML = '<div style="text-align:center;padding:16px;color:var(--text-secondary)">No quotes saved yet.</div>';
            return;
        }

        container.innerHTML = quotes.map(q => `
            <div class="quote-item" style="padding:16px;background:var(--bg-secondary);border-radius:12px;margin-bottom:12px;position:relative;">
                <span class="material-icons-round" style="position:absolute;top:16px;left:16px;color:#D97706;opacity:0.5;font-size:24px">format_quote</span>
                <div style="padding-left:36px;margin-bottom:8px;font-style:italic;color:var(--text-primary);line-height:1.5">"${q.quote}"</div>
                <div style="padding-left:36px;font-size:0.85rem;color:var(--text-secondary);display:flex;justify-content:space-between">
                    <span>— ${q.author || 'Unknown'} <span style="opacity:0.5;margin-left:8px">${q.book ? 'from ' + q.book : ''}</span></span>
                    <button class="btn-icon delete-quote-btn" data-id="${q.id}" style="font-size:18px;color:#EF4444;background:rgba(239,68,68,0.1)">
                        <span class="material-icons-round" style="font-size:16px">delete</span>
                    </button>
                </div>
            </div>
        `).join('');

        // Attach delete handlers
        container.querySelectorAll('.delete-quote-btn').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const quoteId = btn.dataset.id;
                if (confirm('Delete this quote?')) {
                    try {
                        await API.deleteSavedQuote(quoteId);
                        e.currentTarget.closest('.quote-item').remove();
                        if (container.querySelectorAll('.quote-item').length === 0) {
                            container.innerHTML = '<div style="text-align:center;padding:16px;color:var(--text-secondary)">No quotes saved yet.</div>';
                        }
                    } catch (err) {
                        alert('Failed to delete quote');
                    }
                }
            });
        });
    }).catch(err => {
        const container = document.getElementById('saved-quotes-container');
        if (container) container.innerHTML = '<div style="text-align:center;padding:16px;color:#EF4444">Failed to load quotes</div>';
    });
}
