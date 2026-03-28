// Login Page
Router.register('/login', async (container) => {
    container.innerHTML = `
        <div class="auth-page">
            <div class="auth-container fade-in">
                <div class="auth-hero">
                    <div class="auth-logo">
                        <span class="material-icons-round">auto_stories</span>
                    </div>
                    <h1 class="auth-title">Login</h1>
                    <p class="auth-subtitle">Enter your credentials to continue</p>
                </div>
                <div class="glass-card"></div>
            </div>
        </div>`;

    let isOtpMode = false;
    let otpSent = false;

    const authContent = container.querySelector('.glass-card');
    const updateUI = () => {
        authContent.innerHTML = `
            <div class="form-group">
                <label class="form-label">Email Address</label>
                <div class="input-wrapper">
                    <span class="material-icons-round">email</span>
                    <input type="email" id="login-email" class="form-input" placeholder="Enter your email" value="${emailInput?.value || ''}">
                </div>
            </div>
            ${!isOtpMode ? `
                <div class="form-group">
                    <label class="form-label">Password</label>
                    <div class="input-wrapper has-toggle">
                        <span class="material-icons-round">lock</span>
                        <input type="password" id="login-password" class="form-input" placeholder="Enter your password">
                        <span class="material-icons-round toggle-password" id="login-password-toggle">visibility_off</span>
                    </div>
                </div>
                <div style="text-align:right;margin-bottom:16px">
                    <a href="#/forgot-password" class="text-link" style="font-weight: 600; color: #475569;">Forgot Password?</a>
                </div>
                <button id="login-submit" class="btn btn-primary" style="margin-bottom:16px;">Login</button>
                <button id="toggle-mode" class="btn btn-outline" style="margin-bottom:24px;">Login with OTP</button>
            ` : `
                ${otpSent ? `
                    <div class="form-group slide-in-bottom">
                        <label class="form-label">6-Digit OTP</label>
                        <div class="input-wrapper">
                            <span class="material-icons-round">vpn_key</span>
                            <input type="text" id="login-otp" class="form-input" placeholder="Enter OTP" maxlength="6">
                        </div>
                    </div>
                ` : ''}
                <button id="otp-action" class="btn btn-primary" style="margin-bottom:16px;">${otpSent ? 'Verify & Login' : 'Send Login OTP'}</button>
                <button id="toggle-mode" class="btn btn-outline" style="margin-bottom:24px;">Login with Password</button>
            `}
            <div class="divider-text" style="margin-bottom: 24px;">OR</div>
            <button class="btn btn-outline" onclick="location.hash='#/register'">Create Account</button>
        `;

        attachEventListeners();
    };

    const attachEventListeners = () => {
        const toggleBtn = container.querySelector('#toggle-mode');
        toggleBtn.onclick = () => {
            isOtpMode = !isOtpMode;
            otpSent = false;
            updateUI();
        };

        if (!isOtpMode) {
            const form = container.querySelector('#login-submit');
            const emailInp = container.querySelector('#login-email');
            const passInp = container.querySelector('#login-password');
            const togglePass = container.querySelector('#login-password-toggle');

            if (togglePass) {
                togglePass.onclick = () => {
                    const type = passInp.getAttribute('type') === 'password' ? 'text' : 'password';
                    passInp.setAttribute('type', type);
                    togglePass.textContent = type === 'password' ? 'visibility_off' : 'visibility';
                };
            }

            form.onclick = async () => {
                const email = emailInp.value.trim();
                const password = passInp.value;
                if (!email || !password) return showToast('Please enter credentials', 'error');
                
                form.disabled = true;
                form.textContent = 'Logging in...';
                try {
                    await API.login(email, password);
                    showToast('Welcome back!', 'success');
                    location.hash = API.isAdmin() ? '#/admin' : '#/dashboard';
                } catch (e) {
                    showToast(e.message, 'error');
                    form.disabled = false;
                    form.textContent = 'Login';
                }
            };
        } else {
            const otpBtn = container.querySelector('#otp-action');
            const emailInp = container.querySelector('#login-email');
            const otpInp = container.querySelector('#login-otp');

            otpBtn.onclick = async () => {
                const email = emailInp.value.trim();
                if (!email) return showToast('Email required', 'error');

                if (!otpSent) {
                    otpBtn.disabled = true;
                    otpBtn.textContent = 'Sending...';
                    try {
                        await API.sendLoginOTP(email);
                        showToast('OTP sent to your email!', 'success');
                        otpSent = true;
                        updateUI();
                    } catch (e) {
                        showToast(e.message, 'error');
                        otpBtn.disabled = false;
                        otpBtn.textContent = 'Send Login OTP';
                    }
                } else {
                    const otp = otpInp.value.trim();
                    if (otp.length !== 6) return showToast('Enter 6-digit OTP', 'error');
                    
                    otpBtn.disabled = true;
                    otpBtn.textContent = 'Verifying...';
                    try {
                        await API.loginWithOTP(email, otp);
                        showToast('Login Successful!', 'success');
                        location.hash = API.isAdmin() ? '#/admin' : '#/dashboard';
                    } catch (e) {
                        showToast(e.message, 'error');
                        otpBtn.disabled = false;
                        otpBtn.textContent = 'Verify & Login';
                    }
                }
            };
        }
    };

    let emailInput = null; 
    updateUI();
});
