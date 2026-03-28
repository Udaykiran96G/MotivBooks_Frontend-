// Change Password Page
Router.register('/change-password', async (container) => {
    container.innerHTML = `
        <div class="page-wrapper">
            <div class="greeting-header fade-in">
                <div class="greeting-info">
                    <h2>Change Password 🔒</h2>
                    <p>Update your account password</p>
                </div>
                <button class="btn-icon" onclick="location.hash='#/profile'" title="Back">
                    <span class="material-icons-round">arrow_back</span>
                </button>
            </div>
            <div class="card fade-in stagger-1">
                <div class="form-group">
                    <label class="form-label">Current Password</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">lock</span>
                        <input type="password" class="form-input" id="cp-old" placeholder="Enter current password">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">New Password</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">lock_open</span>
                        <input type="password" class="form-input" id="cp-new" placeholder="Enter new password">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Confirm New Password</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">lock</span>
                        <input type="password" class="form-input" id="cp-confirm" placeholder="Confirm new password">
                    </div>
                </div>
                <button class="btn btn-primary" id="cp-submit" style="margin-top:8px">Update Password</button>
            </div>
        </div>`;

    container.querySelector('#cp-submit').addEventListener('click', async () => {
        const oldPass = container.querySelector('#cp-old').value;
        const newPass = container.querySelector('#cp-new').value;
        const confirm = container.querySelector('#cp-confirm').value;

        if (!oldPass || !newPass || !confirm) { showToast('Please fill in all fields', 'error'); return; }

        // Password complexity: 8+ chars, uppercase, lowercase, number, symbol
        if (newPass.length < 8) {
            showToast('New password must be at least 8 characters long', 'error');
            return;
        }
        if (!/[A-Z]/.test(newPass)) {
            showToast('New password must contain an uppercase letter', 'error');
            return;
        }
        if (!/[a-z]/.test(newPass)) {
            showToast('New password must contain a lowercase letter', 'error');
            return;
        }
        if (!/[0-9]/.test(newPass)) {
            showToast('New password must contain a number', 'error');
            return;
        }
        if (!/[!@#$%^&*(),.?":{}|<>]/.test(newPass)) {
            showToast('New password must contain a symbol', 'error');
            return;
        }

        if (newPass !== confirm) { showToast('New passwords do not match', 'error'); return; }

        const btn = container.querySelector('#cp-submit');
        btn.disabled = true; btn.textContent = 'Updating...';
        try {
            await API.changePassword(oldPass, newPass, confirm);
            showToast('Password updated! 🔒', 'success');
            location.hash = '#/profile';
        } catch (e) {
            showToast(e.message, 'error');
            btn.disabled = false; btn.textContent = 'Update Password';
        }
    });
});
