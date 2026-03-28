// Admin Login Page
Router.register('/admin-login', async (container) => {
    container.innerHTML = `
        <div class="auth-page" style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background: linear-gradient(135deg, #EFF6FF 0%, #E0F2FE 50%, #F0FDF4 100%); position: relative; overflow: hidden; font-family: 'Outfit', sans-serif;">
            
            <!-- Abstract Background Shapes -->
            <div style="position: absolute; top: -10%; left: -10%; width: 50vw; height: 50vw; border-radius: 50%; background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, transparent 70%); filter: blur(60px); opacity: 0.8; pointer-events: none;"></div>
            <div style="position: absolute; bottom: -10%; right: -10%; width: 60vw; height: 60vw; border-radius: 50%; background: radial-gradient(circle, rgba(16, 185, 129, 0.08) 0%, transparent 70%); filter: blur(80px); opacity: 0.8; pointer-events: none;"></div>
            <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 80vw; height: 80vw; border-radius: 50%; background: radial-gradient(circle, rgba(56, 189, 248, 0.05) 0%, transparent 70%); filter: blur(100px); pointer-events: none;"></div>

            <div class="glass-card auth-container" style="max-width: 460px; width: 100%; padding: 48px 40px; text-align: center; border-radius: 32px; background: rgba(255, 255, 255, 0.65); backdrop-filter: blur(24px); -webkit-backdrop-filter: blur(24px); box-shadow: 0 24px 48px rgba(30, 41, 59, 0.08), inset 0 2px 6px rgba(255, 255, 255, 0.8); border: 1.5px solid rgba(255,255,255,0.8); position: relative; z-index: 10; margin: 24px;">
                
                <div class="auth-header" style="margin-bottom: 40px; display: flex; flex-direction: column; align-items: center;">
                    <div style="width: 88px; height: 88px; background: linear-gradient(135deg, rgba(255,255,255,0.9), rgba(248,250,252,0.9)); border-radius: 28px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 24px; box-shadow: 0 16px 32px rgba(99, 102, 241, 0.15), inset 0 2px 4px white; border: 1px solid rgba(255,255,255,0.8);">
                        <span class="material-icons-round" style="font-size: 44px; color: #6366F1;">admin_panel_settings</span>
                    </div>
                    <h1 style="font-size: 2.2rem; font-weight: 800; color: #1E293B; letter-spacing: -0.02em; margin-bottom: 12px;">Admin Portal</h1>
                    <p style="font-size: 1.05rem; color: #64748B; font-weight: 600;">Secure management access</p>
                </div>

                <form id="admin-login-form" class="auth-form" style="text-align: left;">
                    <div class="form-group" style="margin-bottom: 24px;">
                        <label style="display: block; font-size: 0.9rem; font-weight: 700; color: #475569; margin-bottom: 10px;">Admin Email</label>
                        <div class="input-wrapper" style="position: relative;">
                            <span class="material-icons-round" style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: 20px; color: #6366F1;">email</span>
                            <input type="email" id="email" class="form-input" placeholder="admin@motivbooks.com" required style="width: 100%; padding: 16px 16px 16px 48px; border-radius: 16px; border: 1.5px solid rgba(99, 102, 241, 0.15); background: rgba(255,255,255,0.8); outline: none; transition: all 0.2s; font-size: 0.95rem; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);">
                        </div>
                    </div>

                    <div class="form-group" style="margin-bottom: 32px;">
                        <label style="display: block; font-size: 0.9rem; font-weight: 700; color: #475569; margin-bottom: 10px;">Password</label>
                        <div class="input-wrapper" style="position: relative;">
                            <span class="material-icons-round" style="position: absolute; left: 16px; top: 50%; transform: translateY(-50%); font-size: 20px; color: #6366F1;">lock</span>
                            <input type="password" id="password" class="form-input" placeholder="••••••••" required style="width: 100%; padding: 16px 16px 16px 48px; border-radius: 16px; border: 1.5px solid rgba(99, 102, 241, 0.15); background: rgba(255,255,255,0.8); outline: none; transition: all 0.2s; font-size: 0.95rem; letter-spacing: 0.1em; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary" id="login-btn" style="background: linear-gradient(135deg, #6366F1, #3B82F6); color: white; border: none; padding: 18px; border-radius: 16px; font-weight: 700; width: 100%; display: flex; align-items: center; justify-content: center; gap: 10px; transition: all 0.3s; box-shadow: 0 12px 24px rgba(99, 102, 241, 0.25);">
                        <span style="font-size: 1.1rem; letter-spacing: 0.5px;">Verify Identity</span>
                        <span class="material-icons-round" style="font-size: 20px;">security</span>
                    </button>
                </form>

                <div class="auth-footer" style="margin-top: 40px;">
                    <a href="#/login" style="font-size: 0.95rem; color: #64748B; font-weight: 700; display: flex; align-items: center; justify-content: center; gap: 6px; text-decoration: none;">
                        <span class="material-icons-round" style="font-size: 18px;">arrow_back</span>
                        User Login
                    </a>
                </div>
            </div>
        </div>
    `;

    const form = document.getElementById('admin-login-form');
    const loginBtn = document.getElementById('login-btn');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        loginBtn.disabled = true;
        loginBtn.innerHTML = '<div class="spinner spinner-sm"></div><span>Logging in...</span>';

        try {
            const response = await API.adminLogin(email, password);
            showToast('Admin Login Successful!', 'success');
            
            // Redirect based on role (should be admin)
            if (API.isAdmin()) {
                window.location.hash = '#/admin';
            } else {
                window.location.hash = '#/dashboard';
            }
        } catch (error) {
            showToast(error.message, 'error');
            loginBtn.disabled = false;
            loginBtn.innerHTML = '<span style="font-size: 1.1rem; letter-spacing: 0.5px;">Verify Identity</span><span class="material-icons-round" style="font-size: 20px;">security</span>';
        }
    });
});
