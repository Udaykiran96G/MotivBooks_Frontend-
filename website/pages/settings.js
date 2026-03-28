// Settings Page
Router.register('/settings', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading settings...</p></div></div>';

    try {
        const profile = await API.getProfile();
        renderSettings(container, profile);
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load settings</h3><p>${error.message}</p></div></div>`;
    }
});

function renderSettings(container, profile) {
    container.innerHTML = `
        <div class="page-wrapper">
            <div class="header-with-back" style="display:flex;align-items:center;gap:12px;margin-bottom:24px">
                <button class="btn-icon" onclick="history.back()">
                    <span class="material-icons-round">arrow_back</span>
                </button>
                <h2 style="font-size:1.5rem;font-weight:700">Settings</h2>
            </div>

            <!-- Account Settings -->
            <div class="card fade-in">
                <div class="card-title" style="margin-bottom:12px">Account</div>
                <div class="settings-list">
                    <div class="settings-item" id="edit-profile-btn">
                        <div class="settings-item-icon" style="background:var(--soft-blue)">
                            <span class="material-icons-round" style="color:#2563EB;font-size:20px">edit</span>
                        </div>
                        <span class="settings-item-label">Edit Profile</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                    <div class="settings-item" onclick="location.hash='#/change-password'">
                        <div class="settings-item-icon" style="background:var(--muted-lavender)">
                            <span class="material-icons-round" style="color:#7C3AED;font-size:20px">lock</span>
                        </div>
                        <span class="settings-item-label">Change Password</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                </div>
            </div>

            <!-- Preferences -->
            <div class="card fade-in stagger-1">
                <div class="card-title" style="margin-bottom:12px">Preferences</div>
                <div class="settings-list">
                    <div class="settings-item" id="reading-prefs-btn">
                        <div class="settings-item-icon" style="background:var(--soft-sage)">
                            <span class="material-icons-round" style="color:#16A34A;font-size:20px">tune</span>
                        </div>
                        <span class="settings-item-label">Reading Preferences</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                    <div class="settings-item" id="notification-settings-btn">
                        <div class="settings-item-icon" style="background:var(--soft-peach)">
                            <span class="material-icons-round" style="color:#D97706;font-size:20px">notifications</span>
                        </div>
                        <span class="settings-item-label">Notifications</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                </div>
            </div>

            <!-- Support -->
            <div class="card fade-in stagger-2">
                <div class="card-title" style="margin-bottom:12px">Support</div>
                <div class="settings-list">
                    <div class="settings-item" id="help-support-btn">
                        <div class="settings-item-icon" style="background:var(--soft-blue)">
                            <span class="material-icons-round" style="color:#2563EB;font-size:20px">help</span>
                        </div>
                        <span class="settings-item-label">Help & Support</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                    <div class="settings-item" id="privacy-policy-btn">
                        <div class="settings-item-icon" style="background:var(--muted-lavender)">
                            <span class="material-icons-round" style="color:#7C3AED;font-size:20px">privacy_tip</span>
                        </div>
                        <span class="settings-item-label">Privacy Policy</span>
                        <span class="material-icons-round arrow">chevron_right</span>
                    </div>
                </div>
            </div>

            <!-- Danger Zone -->
            <div class="card fade-in stagger-3" style="border:1px solid #FCA5A5">
                <div class="card-title" style="margin-bottom:12px;color:var(--danger)">Danger Zone</div>
                <div class="settings-list">
                    <div class="settings-item" id="delete-account-btn" style="color:var(--danger)">
                        <div class="settings-item-icon" style="background:#FEE2E2">
                            <span class="material-icons-round" style="color:#EF4444;font-size:20px">delete_forever</span>
                        </div>
                        <span class="settings-item-label" style="color:var(--danger)">Delete Account</span>
                        <span class="material-icons-round arrow" style="color:#FCA5A5">chevron_right</span>
                    </div>
                </div>
            </div>
        </div>`;

    // Event Listeners
    container.querySelector('#edit-profile-btn').addEventListener('click', () => showEditProfileModal(profile));
    container.querySelector('#reading-prefs-btn').addEventListener('click', async () => {
        try {
            const prefs = await API.getPreferences();
            showPreferencesModal(prefs);
        } catch (e) { showToast(e.message, 'error'); }
    });
    container.querySelector('#notification-settings-btn').addEventListener('click', () => showSettingsModal());
    container.querySelector('#help-support-btn').addEventListener('click', () => showHelpModal());
    container.querySelector('#privacy-policy-btn').addEventListener('click', () => showPrivacyModal());
    container.querySelector('#delete-account-btn').addEventListener('click', () => {
        if (confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
            API.deleteAccount()
                .then(() => { showToast('Account deleted', 'success'); API.logout(); })
                .catch(e => showToast(e.message, 'error'));
        }
    });
}

// Modal functions moved from profile.js
function showEditProfileModal(profile) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-content">
            <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">Edit Profile</h3>
            <div class="form-group">
                <label class="form-label">Name</label>
                <div class="input-wrapper">
                    <span class="material-icons-round">person</span>
                    <input type="text" class="form-input" id="edit-name" value="${profile.name || ''}">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Phone</label>
                <div class="input-wrapper">
                    <span class="material-icons-round">phone</span>
                    <input type="text" class="form-input" id="edit-phone" value="${profile.phone || ''}">
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Date of Birth</label>
                <div class="input-wrapper">
                    <span class="material-icons-round">cake</span>
                    <input type="date" class="form-input" id="edit-dob" value="${profile.dob || ''}">
                </div>
            </div>
            <div style="display:flex;gap:12px;margin-top:20px">
                <button class="btn btn-outline" id="edit-cancel" style="flex:1">Cancel</button>
                <button class="btn btn-primary" id="edit-save" style="flex:1">Save</button>
            </div>
        </div>`;
    document.body.appendChild(overlay);

    overlay.querySelector('#edit-cancel').addEventListener('click', () => overlay.remove());
    overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });

    overlay.querySelector('#edit-save').addEventListener('click', async () => {
        const data = {
            name: document.getElementById('edit-name').value || null,
            phone: document.getElementById('edit-phone').value || null,
            dob: document.getElementById('edit-dob').value || null
        };
        try {
            await API.updateProfile(data);
            showToast('Profile updated!', 'success');
            overlay.remove();
            Router.refresh();
        } catch (e) { showToast(e.message, 'error'); }
    });
}

function showPreferencesModal(prefs) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-content">
            <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">Reading Preferences</h3>
            <div class="form-group">
                <label class="form-label">Font Size</label>
                <input type="range" min="12" max="28" value="${prefs.font_size || 16}" id="pref-font" style="width:100%">
                <span id="pref-font-val" style="font-size:0.85rem;color:var(--text-secondary)">${prefs.font_size || 16}px</span>
            </div>
            <div class="form-group">
                <label class="form-label">Theme</label>
                <div style="display:flex;gap:8px">
                    ${['Light', 'Sepia', 'Dark'].map(t => `
                        <button class="tab-btn ${prefs.theme === t ? 'active' : ''}" data-theme="${t}">${t}</button>
                    `).join('')}
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">Language</label>
                <div class="input-wrapper">
                    <span class="material-icons-round">language</span>
                    <input type="text" class="form-input" id="pref-lang" value="${prefs.language || 'English'}">
                </div>
            </div>
            <div style="display:flex;gap:12px;margin-top:20px">
                <button class="btn btn-outline" id="pref-cancel" style="flex:1">Cancel</button>
                <button class="btn btn-primary" id="pref-save" style="flex:1">Save</button>
            </div>
        </div>`;
    document.body.appendChild(overlay);

    let selectedTheme = prefs.theme || 'Light';
    overlay.querySelectorAll('[data-theme]').forEach(btn => {
        btn.addEventListener('click', () => {
            overlay.querySelectorAll('[data-theme]').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            selectedTheme = btn.dataset.theme;
        });
    });

    const fontSlider = overlay.querySelector('#pref-font');
    fontSlider.addEventListener('input', () => {
        overlay.querySelector('#pref-font-val').textContent = fontSlider.value + 'px';
    });

    overlay.querySelector('#pref-cancel').addEventListener('click', () => overlay.remove());
    overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });

    overlay.querySelector('#pref-save').addEventListener('click', async () => {
        try {
            await API.updatePreferences({
                font_size: parseInt(fontSlider.value),
                theme: selectedTheme,
                language: document.getElementById('pref-lang').value
            });
            showToast('Preferences saved!', 'success');
            overlay.remove();
        } catch (e) { showToast(e.message, 'error'); }
    });
}

function showSettingsModal() {
    API.getNotificationSettings().then(settings => {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `
            <div class="modal-content">
                <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">Notification Settings</h3>
                <div class="form-group">
                    <div class="settings-list">
                        <div class="settings-item">
                            <span class="settings-item-label">Daily Reminders</span>
                            <label class="switch">
                                <input type="checkbox" id="notify-daily" ${settings.daily_reminders ? 'checked' : ''}>
                                <span class="slider round"></span>
                            </label>
                        </div>
                        <div class="settings-item">
                            <span class="settings-item-label">New Book Alerts</span>
                            <label class="switch">
                                <input type="checkbox" id="notify-books" ${settings.new_book_alerts ? 'checked' : ''}>
                                <span class="slider round"></span>
                            </label>
                        </div>
                        <div class="settings-item">
                            <span class="settings-item-label">AI Insights</span>
                            <label class="switch">
                                <input type="checkbox" id="notify-ai" ${settings.ai_insights ? 'checked' : ''}>
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>
                </div>
                <div style="display:flex;gap:12px;margin-top:20px">
                    <button class="btn btn-outline" id="settings-cancel" style="flex:1">Cancel</button>
                    <button class="btn btn-primary" id="settings-save" style="flex:1">Save</button>
                </div>
            </div>`;
        document.body.appendChild(overlay);

        overlay.querySelector('#settings-cancel').addEventListener('click', () => overlay.remove());
        overlay.querySelector('#settings-save').addEventListener('click', async () => {
            const data = {
                daily_reminders: document.getElementById('notify-daily').checked,
                new_book_alerts: document.getElementById('notify-books').checked,
                ai_insights: document.getElementById('notify-ai').checked
            };
            try {
                await API.updateNotificationSettings(data);
                showToast('Settings saved!', 'success');
                overlay.remove();
            } catch (e) { showToast(e.message, 'error'); }
        });
    }).catch(e => showToast('Failed to load settings', 'error'));
}

function showHelpModal() {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-content">
            <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">Help & Support</h3>
            <div class="settings-list">
                <div class="settings-item"><div class="settings-item-icon" style="background:var(--soft-blue)"><span class="material-icons-round" style="color:#2563EB;font-size:20px">quiz</span></div><span class="settings-item-label">FAQs</span></div>
                <div class="settings-item"><div class="settings-item-icon" style="background:var(--soft-sage)"><span class="material-icons-round" style="color:#16A34A;font-size:20px">contact_support</span></div><span class="settings-item-label">Contact Support</span></div>
                <div class="settings-item"><div class="settings-item-icon" style="background:var(--cream-white)"><span class="material-icons-round" style="color:#D97706;font-size:20px">bug_report</span></div><span class="settings-item-label">Report a Bug</span></div>
                <div class="settings-item"><div class="settings-item-icon" style="background:var(--muted-lavender)"><span class="material-icons-round" style="color:#7C3AED;font-size:20px">lightbulb</span></div><span class="settings-item-label">Suggest a Feature</span></div>
            </div>
            <button class="btn btn-outline" style="margin-top:20px" onclick="this.closest('.modal-overlay').remove()">Close</button>
        </div>`;
    document.body.appendChild(overlay);
    overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });
}

function showPrivacyModal() {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-content" style="max-height:80vh;overflow-y:auto">
            <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">Privacy Policy</h3>
            <div style="font-size:0.9rem;line-height:1.7;color:var(--text-secondary)">
                <p><strong>MotivBooks</strong> takes your privacy seriously. We collect only essential data to provide you with the best reading experience.</p>
                <h4 style="margin-top:16px;color:var(--text-primary)">Data We Collect</h4>
                <p>• Email address and name for account creation<br>• Reading progress and preferences<br>• Journal entries (stored securely)</p>
                <h4 style="margin-top:16px;color:var(--text-primary)">How We Use It</h4>
                <p>• To personalize your reading experience<br>• To track your progress and goals<br>• To provide AI-powered recommendations</p>
                <h4 style="margin-top:16px;color:var(--text-primary)">Your Rights</h4>
                <p>You can delete your account and all associated data at any time from the Settings page.</p>
            </div>
            <button class="btn btn-outline" style="margin-top:20px" onclick="this.closest('.modal-overlay').remove()">Close</button>
        </div>`;
    document.body.appendChild(overlay);
    overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });
}
