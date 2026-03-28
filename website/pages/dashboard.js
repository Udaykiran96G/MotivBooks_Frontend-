// Dashboard Page
Router.register('/dashboard', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading dashboard...</p></div></div>';

    try {
        const data = await API.getDashboard();
        renderDashboard(container, data);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load dashboard</h3><p>${error.message}</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.reload()">Retry</button></div></div>`;
    }
});

function renderDashboard(container, data) {
    const goalProgress = data.goalTotalBooks > 0 ? (data.goalBooksRead / data.goalTotalBooks) * 100 : 0;
    const bookProgress = data.currentBook ? (data.currentBook.progress * 100) : 0;

    container.innerHTML = `
        <div class="page-wrapper">
            <!-- Greeting -->
            <div class="greeting-header fade-in">
                <div class="greeting-info">
                    <h2>Hello, ${data.userName || 'Reader'}! 👋</h2>
                    <p>${data.date || new Date().toLocaleDateString('en-US', {weekday:'long', month:'long', day:'numeric'})}</p>
                </div>
                <button class="notification-btn" onclick="location.hash='#/dashboard'" title="Notifications">
                    <span class="material-icons-round">notifications</span>
                    ${data.unreadNotificationCount > 0 ? `<span class="notification-badge">${data.unreadNotificationCount}</span>` : ''}
                </button>
            </div>

            <!-- Quick Actions -->
            <div class="quick-actions-grid fade-in stagger-2">
                <button class="quick-action-btn" onclick="location.hash='#/daily-boost'">
                    <div class="action-icon" style="background:#EFF6FF;color:#2563EB"><span class="material-icons-round">bolt</span></div>
                    <span>Daily Boost</span>
                </button>
                <button class="quick-action-btn" onclick="location.hash='#/set-goal'">
                    <div class="action-icon" style="background:#F5F3FF;color:#8B5CF6"><span class="material-icons-round">track_changes</span></div>
                    <span>Set Goal</span>
                </button>
                <button class="quick-action-btn" onclick="location.hash='#/badges'">
                    <div class="action-icon" style="background:#ECFDF5;color:#10B981"><span class="material-icons-round">badge</span></div>
                    <span>Badges</span>
                </button>
                <button class="quick-action-btn" onclick="location.hash='#/journal'">
                    <div class="action-icon" style="background:#FFFBEB;color:#F59E0B"><span class="material-icons-round">edit_note</span></div>
                    <span>Journal</span>
                </button>
            </div>

            <!-- Active Goal -->
            <div class="card goal-card fade-in stagger-2">
                <div class="card-header">
                    <div>
                        <div class="card-title">${data.goalTitle || 'Set a Goal'}</div>
                        <div class="card-subtitle">${data.goalSubtitle || ''}</div>
                    </div>
                    <button class="btn-icon" title="Edit Goal" style="background:rgba(255,255,255,0.5)">
                        <span class="material-icons-round" style="font-size:18px;color:var(--text-secondary)">edit</span>
                    </button>
                </div>
                <div class="goal-progress-bar">
                    <div class="goal-progress-fill" style="width:${goalProgress}%"></div>
                </div>
                <div style="display:flex;justify-content:space-between;font-size:0.85rem;color:var(--text-secondary)">
                    <span>${data.goalBooksRead || 0} of ${data.goalTotalBooks || 1} ${data.goalUnit || 'books'}</span>
                    <span>${Math.round(goalProgress)}%</span>
                </div>
            </div>

            <!-- Current Book -->
            ${renderCurrentBook(data.currentBook)}

            <!-- Books of the Month -->
            ${renderBookSection('Books of the Month', data.monthBooks)}

            <!-- Trending in Motivation -->
            ${renderBookSection('Trending in Motivation', data.trendingBooks)}

            <!-- Top Books -->
            ${renderBookSection('Top Books', data.topBooks)}
        </div>`;
}

function renderCurrentBook(book) {
    if (!book) {
        return `
            <div class="card book-card-current fade-in stagger-3" style="cursor:pointer" onclick="location.hash='#/library'">
                <div style="display:flex;align-items:center;gap:12px">
                    <span class="material-icons-round" style="font-size:40px;opacity:0.5">menu_book</span>
                    <div>
                        <div class="card-title" style="color:white">Start Reading</div>
                        <div class="card-subtitle">Browse the library to find your next book</div>
                    </div>
                </div>
            </div>`;
    }

    const progress = (book.progress * 100);
    return `
        <div class="card book-card-current fade-in stagger-3">
            <div class="card-subtitle">Currently Reading</div>
            <div class="card-title" style="color:white;font-size:1.2rem;margin:4px 0">${book.title}</div>
            <div class="card-subtitle">${book.author}</div>
            ${book.totalChapters > 0 ? `<div class="card-subtitle" style="margin-top:4px">Chapter ${book.currentChapter} of ${book.totalChapters}</div>` : ''}
            <div class="book-progress-bar">
                <div class="book-progress-fill" style="width:${progress}%"></div>
            </div>
            <div style="display:flex;justify-content:space-between;font-size:0.8rem;color:rgba(255,255,255,0.6)">
                <span>${Math.round(progress)}% complete</span>
                ${book.isPremium ? '<span style="color:#F59E0B">⭐ Premium</span>' : ''}
            </div>
            <div class="book-actions">
                <button class="btn btn-continue" onclick="location.hash='#/reader/${book.id}'">
                    <span class="material-icons-round" style="font-size:18px">play_arrow</span> Continue Reading
                </button>
            </div>
        </div>`;
}

function renderBookSection(title, books) {
    if (!books || books.length === 0) return '';
    return `
        <div style="margin-top:8px" class="fade-in stagger-4">
            <div class="section-header">
                <h3 class="section-title">${title}</h3>
                <a href="#/library" class="view-all-btn">View All</a>
            </div>
            <div class="book-carousel">
                ${books.map(book => `
                    <div class="book-item" onclick="location.hash='#/reader/${book.id}'">
                        <div class="book-cover">
                            ${book.coverUrl || book.cover_url ? `<img src="${book.coverUrl || book.cover_url}" alt="${book.title}" onerror="this.parentElement.innerHTML='<span class=\\'material-icons-round\\'>auto_stories</span>'">` : '<span class="material-icons-round">auto_stories</span>'}
                        </div>
                        <div class="book-item-title">${book.title}</div>
                        <div class="book-item-author">${book.author}</div>
                    </div>
                `).join('')}
            </div>
        </div>`;
}

function cssVar(name) {
    return getComputedStyle(document.documentElement).getPropertyValue('--' + name).trim();
}
