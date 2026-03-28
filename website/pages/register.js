// Register Page
Router.register('/register', async (container) => {
    container.innerHTML = `
        <div class="auth-page">
            <div class="auth-container fade-in">
                <div class="auth-hero">
                    <div class="auth-logo">
                        <span class="material-icons-round">auto_stories</span>
                    </div>
                    <h1 class="auth-title">Create Account</h1>
                    <p class="auth-subtitle">Join the MotivBooks community</p>
                </div>
                <div class="glass-card">
                    <div class="form-group">
                        <label class="form-label">Full Name</label>
                        <div class="input-wrapper">
                            <span class="material-icons-round">person</span>
                            <input type="text" id="reg-name" class="form-input" placeholder="Enter your name">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Email Address</label>
                        <div class="input-wrapper">
                            <span class="material-icons-round">email</span>
                            <input type="email" id="reg-email" class="form-input" placeholder="Enter your email">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Mobile Number</label>
                        <div class="input-wrapper">
                            <span class="material-icons-round">phone</span>
                            <input type="tel" id="reg-phone" class="form-input" placeholder="10-digit mobile number" maxlength="10">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Password</label>
                        <div class="input-wrapper has-toggle">
                            <span class="material-icons-round">lock</span>
                            <input type="password" id="reg-password" class="form-input" placeholder="Create a password">
                            <span class="material-icons-round toggle-password" id="reg-password-toggle">visibility_off</span>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Confirm Password</label>
                        <div class="input-wrapper has-toggle">
                            <span class="material-icons-round">lock</span>
                            <input type="password" id="reg-confirm" class="form-input" placeholder="Confirm your password">
                            <span class="material-icons-round toggle-password" id="reg-confirm-toggle">visibility_off</span>
                        </div>
                    </div>
                    <button id="reg-submit" class="btn btn-primary" style="margin-top:8px">Sign Up</button>
                    <div class="divider-text">OR</div>
                    <p style="text-align:center; margin-bottom:16px"><a href="#/admin-register" class="text-link" style="font-weight:600">Administrator Registration</a></p>
                    <button class="btn btn-outline" onclick="location.hash='#/login'">Already have an account? Login</button>
                </div>
                <p class="auth-footer">By continuing you agree to Terms & Privacy</p>
            </div>
        </div>`;

    const form = container.querySelector('#reg-submit');
    const passwordInput = container.querySelector('#reg-password');
    const confirmInput = container.querySelector('#reg-confirm');
    const togglePassword = container.querySelector('#reg-password-toggle');
    const toggleConfirm = container.querySelector('#reg-confirm-toggle');

    togglePassword.addEventListener('click', () => {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        togglePassword.textContent = type === 'password' ? 'visibility_off' : 'visibility';
    });

    toggleConfirm.addEventListener('click', () => {
        const type = confirmInput.getAttribute('type') === 'password' ? 'text' : 'password';
        confirmInput.setAttribute('type', type);
        toggleConfirm.textContent = type === 'password' ? 'visibility_off' : 'visibility';
    });

    form.addEventListener('click', async () => {
        const name = container.querySelector('#reg-name').value.trim();
        const email = container.querySelector('#reg-email').value.trim();
        const phone = container.querySelector('#reg-phone').value.trim();
        const password = container.querySelector('#reg-password').value;
        const confirm = container.querySelector('#reg-confirm').value;

        if (!name || !email || !phone || !password || !confirm) {
            showToast('Please fill in all fields', 'error');
            return;
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            showToast('Please enter a valid email address', 'error');
            return;
        }

        // Phone validation
        if (!/^\d{10}$/.test(phone)) {
            showToast('Mobile number must be exactly 10 digits', 'error');
            return;
        }

        // Password complexity: 8+ chars, uppercase, lowercase, number, symbol
        if (password.length < 8) {
            showToast('Password must be at least 8 characters long', 'error');
            return;
        }
        if (!/[A-Z]/.test(password)) {
            showToast('Password must contain an uppercase letter', 'error');
            return;
        }
        if (!/[a-z]/.test(password)) {
            showToast('Password must contain a lowercase letter', 'error');
            return;
        }
        if (!/[0-9]/.test(password)) {
            showToast('Password must contain a number', 'error');
            return;
        }
        if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
            showToast('Password must contain a symbol', 'error');
            return;
        }

        if (password !== confirm) {
            showToast('Passwords do not match', 'error');
            return;
        }

        form.disabled = true;
        form.textContent = 'Creating account...';

        try {
            await API.register(name, email, password, confirm, phone, false);
            showToast('Account created successfully!', 'success');
            location.hash = '#/dashboard';
        } catch (error) {
            showToast(error.message, 'error');
            form.disabled = false;
            form.textContent = 'Sign Up';
        }
    });
});
