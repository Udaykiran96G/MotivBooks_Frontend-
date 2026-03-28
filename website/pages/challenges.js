// Challenges Page
Router.register('/challenges', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading challenges...</p></div></div>';

    let challenges = [];

    try {
        challenges = await API.getChallenges();
        render();
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load challenges</h3><p>${error.message}</p></div></div>`;
    }

    function render() {
        const active = challenges.filter(c => !c.is_completed);
        const completed = challenges.filter(c => c.is_completed);

        container.innerHTML = `
            <div class="page-wrapper">
                <div class="greeting-header fade-in">
                    <div class="greeting-info">
                        <h2>Challenges 🏆</h2>
                        <p>Complete challenges to earn XP</p>
                    </div>
                    <div style="background:var(--cream-white);padding:8px 16px;border-radius:var(--radius-full);font-weight:700;color:#D97706;font-size:0.9rem">
                        ${completed.length}/${challenges.length} Done
                    </div>
                </div>

                ${active.length > 0 ? `
                    <div class="section-header fade-in stagger-1">
                        <h3 class="section-title">Active Challenges</h3>
                    </div>
                    ${active.map((c, i) => renderChallenge(c, i)).join('')}
                ` : ''}

                ${completed.length > 0 ? `
                    <div class="section-header fade-in stagger-2" style="margin-top:24px">
                        <h3 class="section-title">Completed</h3>
                    </div>
                    ${completed.map((c, i) => renderChallenge(c, i + active.length)).join('')}
                ` : ''}

                ${challenges.length === 0 ? `
                    <div class="empty-state">
                        <span class="material-icons-round">emoji_events</span>
                        <h3>No challenges yet</h3>
                        <p>Check back later for new challenges</p>
                    </div>
                ` : ''}
            </div>`;

        // Attach click handlers
        container.querySelectorAll('.challenge-check').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = parseInt(btn.dataset.id);
                const completed = btn.dataset.completed === 'true';
                try {
                    await API.updateChallenge(id, !completed);
                    // Update local state
                    const challenge = challenges.find(c => c.id === id);
                    if (challenge) challenge.is_completed = !completed;
                    render();
                    showToast(completed ? 'Challenge unchecked' : 'Challenge completed! 🎉', 'success');
                } catch (e) {
                    showToast(e.message, 'error');
                }
            });
        });
    }

    function renderChallenge(c, index) {
        const icons = ['fitness_center', 'timer', 'menu_book', 'lightbulb', 'favorite', 'star'];
        const colors = ['#F59E0B', '#3B82F6', '#22C55E', '#8B5CF6', '#EF4444', '#06B6D4'];
        const bgColors = ['var(--cream-white)', 'var(--soft-blue)', 'var(--soft-sage)', 'var(--muted-lavender)', '#FEE2E2', 'var(--sky-blue)'];
        const idx = index % icons.length;

        return `
            <div class="challenge-item fade-in" style="animation-delay:${index * 0.05}s;${c.is_completed ? 'opacity:0.7' : ''}">
                <div class="challenge-icon" style="background:${bgColors[idx]}">
                    <span class="material-icons-round" style="color:${colors[idx]}">${icons[idx]}</span>
                </div>
                <div class="challenge-info">
                    <div class="challenge-title" style="${c.is_completed ? 'text-decoration:line-through' : ''}">${c.title || c.challenge_title || 'Challenge'}</div>
                    <div class="challenge-desc">${c.description || c.challenge_description || ''}</div>
                    <div class="challenge-xp">+${c.reward_xp || c.xp || 50} XP</div>
                </div>
                <div class="challenge-check ${c.is_completed ? 'completed' : ''}" data-id="${c.id}" data-completed="${c.is_completed}">
                    ${c.is_completed ? '<span class="material-icons-round" style="font-size:20px">check</span>' : ''}
                </div>
            </div>`;
    }
});
