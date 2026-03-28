// Badges Page
Router.register('/badges', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading badges...</p></div></div>';

    try {
        const badges = await API.getBadges();
        render(badges);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load badges</h3><p>${error.message}</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.hash='#/dashboard'">Back</button></div></div>`;
    }

    function render(badges) {
        const unlocked = badges.filter(b => b.unlocked);
        const locked = badges.filter(b => !b.unlocked);

        const iconMap = {
            'LocalFireDepartment': 'local_fire_department',
            'MenuBook': 'menu_book',
            'FormatQuote': 'format_quote',
            'EditNote': 'edit_note',
            'MilitaryTech': 'military_tech',
            'Star': 'star'
        };
        const tintMap = {
            'Orange': '#F59E0B',
            'Primary': '#2563EB',
            'Purple': '#7C3AED',
            'Green': '#10B981',
            'Yellow': '#EAB308'
        };
        const bgMap = {
            'LightOrange': 'var(--cream-white)',
            'LightBlue': 'var(--soft-blue)',
            'LightPurple': 'var(--muted-lavender)',
            'LightGreen': 'var(--soft-sage)',
            'LightYellow': '#FEF9C3'
        };

        container.innerHTML = `
            <div class="page-wrapper">
                <div class="greeting-header fade-in">
                    <div class="greeting-info">
                        <h2>Badges 🏅</h2>
                        <p>Complete achievements to earn badges</p>
                    </div>
                    <button class="btn-icon" onclick="location.hash='#/dashboard'" title="Back">
                        <span class="material-icons-round">arrow_back</span>
                    </button>
                </div>

                <!-- Summary -->
                <div class="card fade-in stagger-1" style="text-align:center;background:linear-gradient(135deg, var(--soft-blue), var(--muted-lavender));border:none">
                    <div style="font-size:2rem;font-weight:800;color:var(--primary-dark)">${unlocked.length} / ${badges.length}</div>
                    <div style="font-size:0.9rem;color:var(--text-secondary)">Badges Unlocked</div>
                </div>

                <!-- Unlocked -->
                ${unlocked.length > 0 ? `
                    <div class="section-header fade-in stagger-2"><h3 class="section-title">🎉 Unlocked</h3></div>
                    <div class="badges-grid">
                        ${unlocked.map((b, i) => renderBadge(b, i, true, iconMap, tintMap, bgMap)).join('')}
                    </div>
                ` : ''}

                <!-- In Progress -->
                ${locked.length > 0 ? `
                    <div class="section-header fade-in stagger-3" style="margin-top:24px"><h3 class="section-title">🔒 In Progress</h3></div>
                    <div class="badges-grid">
                        ${locked.map((b, i) => renderBadge(b, i + unlocked.length, false, iconMap, tintMap, bgMap)).join('')}
                    </div>
                ` : ''}
            </div>`;
    }

    function renderBadge(b, index, isUnlocked, iconMap, tintMap, bgMap) {
        const icon = iconMap[b.icon_name] || 'emoji_events';
        const tint = tintMap[b.tint_color] || '#2563EB';
        const bg = bgMap[b.bg_color] || 'var(--soft-blue)';
        const progressPct = Math.round((b.progress || 0) * 100);

        return `
            <div class="badge-card fade-in" style="animation-delay:${index * 0.06}s;${!isUnlocked ? 'opacity:0.65' : ''}">
                <div class="badge-icon" style="background:${bg}">
                    <span class="material-icons-round" style="color:${tint};font-size:28px">${icon}</span>
                </div>
                <div class="badge-title">${b.title}</div>
                <div class="badge-desc">${b.description}</div>
                <div class="badge-progress-bar">
                    <div class="badge-progress-fill" style="width:${progressPct}%;background:${tint}"></div>
                </div>
                <div class="badge-progress-text">${b.current_value} / ${b.target_value} · ${progressPct}%</div>
            </div>`;
    }
});
