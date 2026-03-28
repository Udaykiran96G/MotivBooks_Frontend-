// MotivBooks SPA Router

const Router = (() => {
    const routes = {};
    let currentPage = null;

    function register(path, renderFn) {
        routes[path] = renderFn;
    }

    function navigate(hash) {
        window.location.hash = hash;
    }

    function getRoute() {
        const hash = window.location.hash.slice(1) || '/welcome';
        return hash;
    }

    function parseRoute(hash) {
        const parts = hash.split('/').filter(Boolean);
        // Exact match first
        if (routes['/' + parts.join('/')]) {
            return { path: '/' + parts.join('/'), params: {} };
        }
        // Pattern match (e.g., /reader/:id)
        for (const pattern of Object.keys(routes)) {
            const patternParts = pattern.split('/').filter(Boolean);
            if (patternParts.length !== parts.length) continue;

            const params = {};
            let match = true;
            for (let i = 0; i < patternParts.length; i++) {
                if (patternParts[i].startsWith(':')) {
                    params[patternParts[i].slice(1)] = decodeURIComponent(parts[i]);
                } else if (patternParts[i] !== parts[i]) {
                    match = false;
                    break;
                }
            }
            if (match) return { path: pattern, params };
        }
        return null;
    }

    async function handleRoute() {
        const hash = getRoute();
        const authPages = ['/welcome', '/login', '/register', '/forgot-password', '/admin-login'];
        const isAuthPage = authPages.includes(hash.split('?')[0]);

        // Auth guard
        if (!API.isLoggedIn() && !isAuthPage) {
            window.location.hash = '#/welcome';
            return;
        }

        // If logged in and on auth page, redirect appropriately
        if (API.isLoggedIn() && isAuthPage) {
            window.location.hash = API.isAdmin() ? '#/admin' : '#/dashboard';
            return;
        }

        const result = parseRoute(hash);
        if (!result) {
            window.location.hash = API.isLoggedIn() ? '#/dashboard' : '#/welcome';
            return;
        }

        const container = document.getElementById('page-container');
        const sidebar = document.getElementById('sidebar');
        const mainContent = document.getElementById('main-content');

        const isAdminPage = hash.startsWith('/admin');

        // Show/hide sidebar
        if (isAuthPage || isAdminPage) {
            sidebar.classList.add('hidden');
            mainContent.classList.remove('with-sidebar');
        } else {
            sidebar.classList.remove('hidden');
            mainContent.classList.add('with-sidebar');
        }

        // Update active nav item
        document.querySelectorAll('.nav-item').forEach(item => {
            const route = item.dataset.route;
            if (route && hash.includes(route)) {
                item.classList.add('active');
            } else {
                item.classList.remove('active');
            }
        });

        // Render the page
        container.innerHTML = '<div class="loading-container"><div class="spinner"></div><p>Loading...</p></div>';
        
        try {
            await routes[result.path](container, result.params);
        } catch (error) {
            console.error('Route error:', error);
            container.innerHTML = `
                <div class="page-wrapper">
                    <div class="empty-state">
                        <span class="material-icons-round">error_outline</span>
                        <h3>Something went wrong</h3>
                        <p>${error.message}</p>
                        <button class="btn btn-primary btn-sm" style="width:auto;margin-top:16px" onclick="location.reload()">Retry</button>
                    </div>
                </div>`;
        }
    }

    function refresh() {
        handleRoute();
    }
    function init() {
        window.addEventListener('hashchange', handleRoute);
        handleRoute();
    }

    return { register, navigate, refresh, init, getRoute };
})();

// Toast notification helper
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const icon = type === 'success' ? 'check_circle' : type === 'error' ? 'error' : 'info';
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `<span class="material-icons-round" style="font-size:20px">${icon}</span>${message}`;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(40px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
