// Profile Page
Router.register('/profile', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading profile...</p></div></div>';

    try {
        const profile = await API.getProfile();
        renderProfile(container, profile);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load profile</h3><p>${error.message}</p></div></div>`;
    }
});

function renderProfile(container, profile) {
    const initial = (profile.name || 'U').charAt(0).toUpperCase();

    container.innerHTML = `
        <div class="page-wrapper">
            <!-- Profile Header -->
            <div class="card profile-header-card fade-in">
                <div class="profile-avatar">${initial}</div>
                <div class="profile-name">${profile.name || 'User'}</div>
                <div class="profile-email">${profile.email || ''}</div>
                <div class="profile-member-since">Member since ${profile.member_since || 'N/A'}</div>
            </div>

            <!-- Navigation -->
            <div class="card fade-in stagger-1" style="margin-top:16px">
                <div class="settings-list">
                    <div class="settings-item" onclick="location.hash='#/settings'">
                        <div class="settings-item-icon" style="background:var(--soft-blue)">
                            <span class="material-icons-round" style="color:#2563EB;font-size:20px">settings</span>
                        </div>
                        <span class="settings-item-label">Settings</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                </div>
            </div>

            <!-- Goal & Stats -->
            <div class="card fade-in stagger-2">
                <div class="card-title" style="margin-bottom:12px">Your Growth</div>
                <div class="settings-list">
                    <div class="settings-item" onclick="location.hash='#/progress'">
                        <div class="settings-item-icon" style="background:var(--soft-sage)">
                            <span class="material-icons-round" style="color:#16A34A;font-size:20px">bar_chart</span>
                        </div>
                        <span class="settings-item-label">Growth Stats</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                </div>
            </div>

            <!-- Logout -->
            <button class="btn btn-outline fade-in stagger-3" style="margin-top:16px;border-color:var(--danger);color:var(--danger)" id="profile-logout-btn">
                <span class="material-icons-round" style="font-size:20px">logout</span> Logout
            </button>
        </div>`;

    // Logout
    container.querySelector('#profile-logout-btn').addEventListener('click', () => {
        API.logout();
        showToast('Logged out', 'info');
    });
}
