// Forgot Password Page
Router.register('/forgot-password', async (container) => {
    let step = 'email'; // email -> otp -> reset
    let userEmail = '';
    let userOtp = '';

    function renderEmailStep() {
        container.innerHTML = `
            <div class="auth-page">
                <div class="auth-container fade-in">
                    <div class="auth-hero">
                        <div class="auth-logo">
                            <span class="material-icons-round">lock_reset</span>
                        </div>
                        <h1 class="auth-title">Reset Password</h1>
                        <p class="auth-subtitle">Enter your email to receive an OTP</p>
                    </div>
                    <div class="glass-card">
                        <div class="form-group">
                            <label class="form-label">Email Address</label>
                            <div class="input-wrapper">
                                <span class="material-icons-round">email</span>
                                <input type="email" id="fp-email" class="form-input" placeholder="Enter your email">
                            </div>
                        </div>
                        <button id="fp-submit" class="btn btn-primary">Send OTP</button>
                        <div style="text-align:center;margin-top:16px">
                            <a href="#/login" class="text-link">Back to Login</a>
                        </div>
                    </div>
                </div>
            </div>`;

        container.querySelector('#fp-submit').addEventListener('click', async () => {
            const email = container.querySelector('#fp-email').value.trim();
            if (!email) { showToast('Please enter your email', 'error'); return; }
            
            const btn = container.querySelector('#fp-submit');
            btn.disabled = true; btn.textContent = 'Sending...';
            try {
                await API.forgotPassword(email);
                userEmail = email;
                step = 'otp';
                showToast('OTP sent to your email!', 'success');
                renderOtpStep();
            } catch (e) {
                showToast(e.message, 'error');
                btn.disabled = false; btn.textContent = 'Send OTP';
            }
        });
    }

    function renderOtpStep() {
        container.innerHTML = `
            <div class="auth-page">
                <div class="auth-container fade-in">
                    <div class="auth-hero">
                        <div class="auth-logo">
                            <span class="material-icons-round">pin</span>
                        </div>
                        <h1 class="auth-title">Verify OTP</h1>
                        <p class="auth-subtitle">Enter the code sent to ${userEmail}</p>
                    </div>
                    <div class="glass-card">
                        <div class="form-group">
                            <label class="form-label">OTP Code</label>
                            <div class="input-wrapper">
                                <span class="material-icons-round">dialpad</span>
                                <input type="text" id="fp-otp" class="form-input" placeholder="Enter 6-digit OTP" maxlength="6">
                            </div>
                        </div>
                        <button id="fp-verify" class="btn btn-primary">Verify</button>
                    </div>
                </div>
            </div>`;

        container.querySelector('#fp-verify').addEventListener('click', async () => {
            const otp = container.querySelector('#fp-otp').value.trim();
            if (!otp) { showToast('Please enter the OTP', 'error'); return; }
            
            const btn = container.querySelector('#fp-verify');
            btn.disabled = true; btn.textContent = 'Verifying...';
            try {
                await API.verifyOtp(userEmail, otp);
                userOtp = otp;
                step = 'reset';
                showToast('OTP verified!', 'success');
                renderResetStep();
            } catch (e) {
                showToast(e.message, 'error');
                btn.disabled = false; btn.textContent = 'Verify';
            }
        });
    }

    function renderResetStep() {
        container.innerHTML = `
            <div class="auth-page">
                <div class="auth-container fade-in">
                    <div class="auth-hero">
                        <div class="auth-logo">
                            <span class="material-icons-round">lock_open</span>
                        </div>
                        <h1 class="auth-title">New Password</h1>
                        <p class="auth-subtitle">Create a new password for your account</p>
                    </div>
                    <div class="glass-card">
                        <div class="form-group">
                            <label class="form-label">New Password</label>
                            <div class="input-wrapper">
                                <span class="material-icons-round">lock</span>
                                <input type="password" id="fp-newpass" class="form-input" placeholder="Enter new password">
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Confirm Password</label>
                            <div class="input-wrapper">
                                <span class="material-icons-round">lock</span>
                                <input type="password" id="fp-confirm" class="form-input" placeholder="Confirm new password">
                            </div>
                        </div>
                        <button id="fp-reset" class="btn btn-primary">Reset Password</button>
                    </div>
                </div>
            </div>`;

        container.querySelector('#fp-reset').addEventListener('click', async () => {
            const newPass = container.querySelector('#fp-newpass').value;
            const confirm = container.querySelector('#fp-confirm').value;
            if (!newPass || !confirm) { showToast('Please fill in both fields', 'error'); return; }

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

            if (newPass !== confirm) { showToast('Passwords do not match', 'error'); return; }

            const btn = container.querySelector('#fp-reset');
            btn.disabled = true; btn.textContent = 'Resetting...';
            try {
                await API.resetPassword(userEmail, userOtp, newPass);
                showToast('Password reset successfully!', 'success');
                location.hash = '#/login';
            } catch (e) {
                showToast(e.message, 'error');
                btn.disabled = false; btn.textContent = 'Reset Password';
            }
        });
    }

    renderEmailStep();
});
