// Set Goal Page
Router.register('/set-goal', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading goal...</p></div></div>';

    try {
        const goalData = await API.getGoalDetails();
        renderGoal(container, goalData);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load goal</h3><p>${error.message}</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.hash='#/dashboard'">Back</button></div></div>`;
    }
});

function renderGoal(container, goalData) {
    const g = goalData || {};
    const progress = g.total_books > 0 ? (g.books_completed / g.total_books) * 100 : 0;

    const presetGoals = [
        { title: 'Read 6 Books this Year', books: 6, desc: 'A gentle start — one book every two months.' },
        { title: 'Read 12 Books this Year', books: 12, desc: 'A great starting point for building a habit.' },
        { title: 'Read 24 Books this Year', books: 24, desc: 'Two books a month — for committed readers.' },
        { title: 'Read 52 Books this Year', books: 52, desc: 'A book a week! The ultimate challenge.' }
    ];

    container.innerHTML = `
        <div class="page-wrapper">
            <div class="greeting-header fade-in">
                <div class="greeting-info">
                    <h2>Set Goal 🎯</h2>
                    <p>Define your reading targets</p>
                </div>
                <button class="btn-icon" onclick="location.hash='#/dashboard'" title="Back">
                    <span class="material-icons-round">arrow_back</span>
                </button>
            </div>

            <!-- Current Goal -->
            <div class="card goal-card fade-in stagger-1">
                <div style="font-size:0.75rem;font-weight:700;text-transform:uppercase;letter-spacing:1px;color:var(--primary-light);margin-bottom:8px">Active Goal</div>
                <div style="font-size:1.2rem;font-weight:700">${g.title || 'No goal set'}</div>
                <div style="font-size:0.85rem;color:var(--text-secondary);margin-top:2px">${g.subtitle || 'Set a reading goal to stay motivated'}</div>
                <div class="goal-progress-bar" style="margin-top:16px">
                    <div class="goal-progress-fill" style="width:${progress}%"></div>
                </div>
                <div style="display:flex;justify-content:space-between;font-size:0.85rem;color:var(--text-secondary);margin-top:4px">
                    <span>${g.books_completed || 0} / ${g.total_books || 0} books</span>
                    <span>${Math.round(progress)}%</span>
                </div>
                ${g.encouragement ? `<div style="margin-top:8px;font-size:0.85rem;font-style:italic;color:var(--text-secondary)">${g.encouragement}</div>` : ''}
            </div>

            <!-- Goal Stats -->
            <div class="stats-grid fade-in stagger-2" style="grid-template-columns:repeat(3,1fr)">
                <div class="stat-card">
                    <div class="stat-value">${g.challenges_done || 0}</div>
                    <div class="stat-label">Challenges Done</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${g.days_active || 0}</div>
                    <div class="stat-label">Days Active</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${g.books_completed || 0}</div>
                    <div class="stat-label">Books Done</div>
                </div>
            </div>

            <!-- Choose Goal -->
            <div class="section-header fade-in stagger-3" style="margin-top:24px">
                <h3 class="section-title">Choose a Goal</h3>
            </div>

            ${presetGoals.map((pg, i) => `
                <div class="card fade-in goal-option" style="animation-delay:${(i + 3) * 0.06}s;cursor:pointer;transition:all 0.2s" data-title="${pg.title}" data-books="${pg.books}">
                    <div style="display:flex;align-items:center;gap:12px">
                        <div style="width:48px;height:48px;border-radius:50%;background:var(--soft-blue);display:flex;align-items:center;justify-content:center;flex-shrink:0">
                            <span class="material-icons-round" style="color:#2563EB;font-size:24px">flag</span>
                        </div>
                        <div style="flex:1">
                            <div style="font-weight:700;font-size:1rem">${pg.title}</div>
                            <div style="font-size:0.85rem;color:var(--text-secondary)">${pg.desc}</div>
                        </div>
                        <span class="material-icons-round" style="color:var(--text-secondary);font-size:20px">chevron_right</span>
                    </div>
                </div>
            `).join('')}

            <!-- Custom Goal -->
            <div class="card fade-in stagger-4" style="margin-top:8px">
                <div style="font-weight:700;margin-bottom:12px">Custom Goal</div>
                <div class="form-group">
                    <label class="form-label">Goal Title</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">edit</span>
                        <input type="text" class="form-input" id="custom-goal-title" placeholder="e.g. Read 15 Books">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Number of Books</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">book</span>
                        <input type="number" class="form-input" id="custom-goal-books" placeholder="e.g. 15" min="1" max="100">
                    </div>
                </div>
                <button class="btn btn-primary" id="set-custom-goal">Set Custom Goal</button>
            </div>
        </div>`;

    // Preset goal click
    container.querySelectorAll('.goal-option').forEach(card => {
        card.addEventListener('click', async () => {
            const title = card.dataset.title;
            const books = parseInt(card.dataset.books);
            await setGoal(title, books);
        });
    });

    // Custom goal
    container.querySelector('#set-custom-goal').addEventListener('click', async () => {
        const title = document.getElementById('custom-goal-title').value.trim();
        const books = parseInt(document.getElementById('custom-goal-books').value);
        if (!title || !books || books < 1) {
            showToast('Please enter a title and number of books', 'error');
            return;
        }
        await setGoal(title, books);
    });

    async function setGoal(title, books) {
        try {
            await API.updateProgress({
                active_goal_title: title,
                active_goal_total_books: books,
                active_goal_subtitle: `Read ${books} books this year`
            });
            showToast('Goal set: ' + title + ' 🎯', 'success');
            location.hash = '#/dashboard';
        } catch (e) {
            showToast(e.message, 'error');
        }
    }
}
