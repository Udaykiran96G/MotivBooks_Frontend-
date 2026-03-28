// MotivBooks App Entry Point

document.addEventListener('DOMContentLoaded', () => {
    // Global Sidebar Navigation Helper
    document.addEventListener('click', (e) => {
        const navItem = e.target.closest('.nav-item');
        if (navItem && navItem.tagName === 'A') {
            const href = navItem.getAttribute('href');
            if (href && href.startsWith('#')) {
                // Let the browser handle the hash change, but we ensure it happens
                window.location.hash = href;
            }
        }
    });

    // Logout button handler
    document.getElementById('logout-btn')?.addEventListener('click', (e) => {
        e.preventDefault();
        try {
            API.logout();
            showToast('Logged out successfully', 'info');
        } catch (error) {
            console.error('Logout error:', error);
            // Fallback
            localStorage.clear();
            window.location.hash = '#/login';
        }
    });

    // Initialize router
    Router.init();
});
