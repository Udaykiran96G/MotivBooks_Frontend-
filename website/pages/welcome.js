// Welcome/Role Selection Page
Router.register('/welcome', async (container) => {
    container.innerHTML = `
        <div class="auth-page" style="display: flex; align-items: center; justify-content: center; min-height: 100vh; background: var(--background); position: relative; overflow: hidden;">
            
            <!-- Abstract Background Shapes -->
            <div style="position: absolute; top: -10%; left: -10%; width: 500px; height: 500px; border-radius: 50%; background: radial-gradient(circle, rgba(79, 70, 229, 0.1) 0%, transparent 70%); filter: blur(40px);"></div>
            <div style="position: absolute; bottom: -10%; right: -10%; width: 600px; height: 600px; border-radius: 50%; background: radial-gradient(circle, rgba(16, 185, 129, 0.05) 0%, transparent 70%); filter: blur(60px);"></div>

            <div class="glass-card fade-in" style="max-width: 500px; width: 100%; padding: 48px 32px; text-align: center; border-radius: 24px; z-index: 10; border: 1px solid rgba(0,0,0,0.05);">
                
                <div style="margin-bottom: 40px;">
                    <div style="width: 80px; height: 80px; background: linear-gradient(135deg, var(--primary), var(--primary-dark)); border-radius: 24px; display: inline-flex; align-items: center; justify-content: center; box-shadow: 0 12px 24px rgba(79, 70, 229, 0.25); margin-bottom: 24px;">
                        <span class="material-icons-round" style="color: white; font-size: 40px;">auto_stories</span>
                    </div>
                    <h1 style="font-size: 2.25rem; font-weight: 800; color: var(--text-primary); letter-spacing: -0.03em; margin-bottom: 8px;">MotivBooks</h1>
                    <p style="font-size: 1.1rem; color: var(--text-secondary); font-weight: 400; letter-spacing: 0.05em;">Read. Grow. Become.</p>
                </div>

                <div style="margin-bottom: 32px;">
                    <h2 style="font-size: 1.25rem; font-weight: 600; color: var(--text-primary); margin-bottom: 24px;">Select your portal</h2>
                    
                    <div style="display: flex; flex-direction: column; gap: 16px;">
                        <button onclick="location.hash='#/login'" class="btn" style="background: var(--surface); color: var(--text-primary); border: 2px solid rgba(0,0,0,0.05); padding: 20px; border-radius: 16px; display: flex; align-items: center; justify-content: flex-start; gap: 16px; text-align: left; transition: all 0.3s; cursor: pointer; border-bottom: 4px solid rgba(0,0,0,0.05);" onmouseover="this.style.borderColor='var(--primary)'; this.style.transform='translateY(-2px)';" onmouseout="this.style.borderColor='rgba(0,0,0,0.05)'; this.style.transform='translateY(0)';">
                            <div style="width: 48px; height: 48px; background: rgba(79, 70, 229, 0.1); border-radius: 12px; display: flex; align-items: center; justify-content: center;">
                                <span class="material-icons-round" style="color: var(--primary); font-size: 24px;">face</span>
                            </div>
                            <div style="flex: 1;">
                                <div style="font-size: 1.1rem; font-weight: 700; margin-bottom: 4px;">Reader Login</div>
                                <div style="font-size: 0.85rem; color: var(--text-secondary);">Access your library and progress</div>
                            </div>
                            <span class="material-icons-round" style="color: rgba(0,0,0,0.3);">chevron_right</span>
                        </button>

                        <button onclick="location.hash='#/admin-login'" class="btn" style="background: var(--surface); color: var(--text-primary); border: 2px solid rgba(0,0,0,0.05); padding: 20px; border-radius: 16px; display: flex; align-items: center; justify-content: flex-start; gap: 16px; text-align: left; transition: all 0.3s; cursor: pointer; border-bottom: 4px solid rgba(0,0,0,0.05);" onmouseover="this.style.borderColor='#0F172A'; this.style.transform='translateY(-2px)';" onmouseout="this.style.borderColor='rgba(0,0,0,0.05)'; this.style.transform='translateY(0)';">
                            <div style="width: 48px; height: 48px; background: rgba(15, 23, 42, 0.1); border-radius: 12px; display: flex; align-items: center; justify-content: center;">
                                <span class="material-icons-round" style="color: #0F172A; font-size: 24px;">admin_panel_settings</span>
                            </div>
                            <div style="flex: 1;">
                                <div style="font-size: 1.1rem; font-weight: 700; margin-bottom: 4px;">Admin Portal</div>
                                <div style="font-size: 0.85rem; color: var(--text-secondary);">Manage books and platform</div>
                            </div>
                            <span class="material-icons-round" style="color: rgba(0,0,0,0.3);">chevron_right</span>
                        </button>
                    </div>
                </div>

                <div style="margin-top: 24px; padding-top: 24px; border-top: 1px solid rgba(0,0,0,0.05);">
                    <p style="font-size: 0.85rem; opacity: 0.6;">By continuing you agree to Terms & Privacy</p>
                </div>

            </div>
        </div>
    `;
});
