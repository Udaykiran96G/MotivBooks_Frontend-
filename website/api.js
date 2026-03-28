// MotivBooks API Layer — Connects to the same Django backend as the Android app

const API = (() => {
    const BASE_URL = (location.port && location.port !== '8000') ? 'http://localhost:8000/api/users/' : '/api/users/';

    function getToken() { return localStorage.getItem('auth_token'); }
    function setToken(token) { localStorage.setItem('auth_token', token); }
    function clearToken() { localStorage.removeItem('auth_token'); localStorage.removeItem('is_staff'); }
    function isLoggedIn() { return !!getToken(); }
    function isAdmin() { return localStorage.getItem('is_staff') === 'true'; }

    async function request(endpoint, options = {}) {
        const url = BASE_URL + endpoint;
        const headers = { 'Content-Type': 'application/json', ...options.headers };
        const token = getToken();
        if (token && !options.noAuth) headers['Authorization'] = `Bearer ${token}`;

        try {
            const response = await fetch(url, { ...options, headers });
            if (response.status === 401 && !options.noAuth) {
                clearToken(); window.location.hash = '#/login';
                throw new Error('Session expired. Please login again.');
            }
            const data = response.status !== 204 ? await response.json().catch(() => null) : null;
            if (!response.ok) {
                const errorMsg = data?.detail || data?.error || data?.message || (typeof data === 'object' ? JSON.stringify(data) : 'Request failed');
                throw new Error(errorMsg);
            }
            return data;
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch'))
                throw new Error('Cannot connect to server. Make sure the Django backend is running on localhost:8000');
            throw error;
        }
    }

    // ===== Auth =====
    async function login(email, password) {
        const data = await request('login/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase(), password }), noAuth: true });
        setToken(data.access);
        localStorage.setItem('is_staff', data.is_staff ? 'true' : 'false');
        return data;
    }

    async function sendLoginOTP(email) {
        return request('login/send-otp/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase() }), noAuth: true });
    }

    async function loginWithOTP(email, otp) {
        const data = await request('login/verify-otp/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase(), otp }), noAuth: true });
        setToken(data.access);
        localStorage.setItem('is_staff', data.is_staff ? 'true' : 'false');
        return data;
    }

    async function register(name, email, password, confirmPassword, phone, isStaff = false) {
        const data = await request('register/', { method: 'POST', body: JSON.stringify({ name, email: email.trim().toLowerCase(), password, confirmPassword, phone, is_staff: isStaff }), noAuth: true });
        setToken(data.access);
        localStorage.setItem('is_staff', data.is_staff ? 'true' : 'false');
        return data;
    }

    async function adminLogin(email, password) {
        const data = await request('admin/login/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase(), password }), noAuth: true });
        setToken(data.access);
        localStorage.setItem('is_staff', 'true');
        return data;
    }

    async function adminRegister(name, email, password, confirmPassword, phone) {
        const data = await request('admin/register/', { method: 'POST', body: JSON.stringify({ name, email: email.trim().toLowerCase(), password, confirmPassword, phone }), noAuth: true });
        setToken(data.access);
        localStorage.setItem('is_staff', 'true');
        return data;
    }

    async function forgotPassword(email) { return request('forgot-password/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase() }), noAuth: true }); }
    async function verifyOtp(email, otp) { return request('verify-otp/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase(), otp }), noAuth: true }); }
    async function resetPassword(email, otp, newPassword) { return request('reset-password/', { method: 'POST', body: JSON.stringify({ email: email.trim().toLowerCase(), otp, new_password: newPassword }), noAuth: true }); }
    async function changePassword(oldPassword, newPassword, confirmPassword) { return request('change-password/', { method: 'POST', body: JSON.stringify({ old_password: oldPassword, new_password: newPassword, confirm_password: confirmPassword }) }); }

    // ===== Dashboard =====
    async function getDashboard() { return request('dashboard/'); }
    async function getDailyBoost() { return request('dashboard/daily-boost/'); }
    async function getBadges() { return request('dashboard/badges/'); }
    async function getJournalEntries() { return request('dashboard/journal/'); }
    async function createJournalEntry(title, content, mood, prompt) { return request('dashboard/journal/', { method: 'POST', body: JSON.stringify({ title, content, mood, prompt }) }); }

    // ===== Feed =====
    async function getHomeFeed() { return request('feed/'); }

    // ===== Library =====
    async function getLibrary(params = {}) {
        const query = new URLSearchParams();
        Object.entries(params).forEach(([k, v]) => { if (v) query.set(k, v); });
        const qs = query.toString();
        return request('library/' + (qs ? '?' + qs : ''));
    }
    async function getBookDetails(bookId) { return request(`books/${bookId}/`); }
    async function getChapters(bookId) { return request(`books/${bookId}/chapters/`); }
    async function trackBookOpen(bookId) { return request('books/track-open/', { method: 'POST', body: JSON.stringify({ book_id: bookId }) }); }
    async function updateReadingProgress(bookId, chapterOrder) { return request('books/track-progress/', { method: 'POST', body: JSON.stringify({ book_id: bookId, chapter_order: chapterOrder }) }); }
    async function getCompletedBooks() { return request('library/completed/'); }

    // ===== Progress =====
    async function getProgress() { return request('progress/'); }
    async function updateProgress(progressData) { return request('progress/', { method: 'PUT', body: JSON.stringify(progressData) }); }
    async function getGoalDetails() { return request('progress/goal-details/'); }
    async function getReadingAnalytics() { return request('progress/reading-analytics/'); }
    async function saveQuote(quote, author, book) { return request('progress/saved-quotes/', { method: 'POST', body: JSON.stringify({ quote, author, book }) }); }
    async function getSavedQuotes() { return request('progress/saved-quotes/'); }
    async function deleteSavedQuote(quoteId) { return request(`progress/saved-quotes/${quoteId}/`, { method: 'DELETE' }); }
    async function getMoodGraph() { return request('progress/mood-graph/'); }

    // ===== Challenges =====
    async function getChallenges() { return request('challenges/'); }
    async function updateChallenge(challengeId, completed) { return request(`challenges/${challengeId}/update/`, { method: 'POST', body: JSON.stringify({ is_completed: completed }) }); }

    // ===== Profile =====
    async function getProfile() { return request('profile/'); }
    async function updateProfile(profileData) { return request('profile/', { method: 'PATCH', body: JSON.stringify(profileData) }); }
    async function getProfileDetail() { return request('profile/detail/'); }
    async function getPreferences() { return request('profile/preferences/'); }
    async function updatePreferences(prefs) { return request('profile/preferences/', { method: 'PATCH', body: JSON.stringify(prefs) }); }
    async function getSubscription() { return request('profile/subscription/'); }
    async function getGrowthStats() { return request('profile/growth-stats/'); }
    async function deleteAccount() { return request('profile/delete/', { method: 'DELETE' }); }

    // ===== Notifications & Settings =====
    async function getNotifications() { return request('notifications/'); }
    async function getNotificationSettings() { return request('settings/notifications/'); }
    async function updateNotificationSettings(settings) { return request('settings/notifications/', { method: 'POST', body: JSON.stringify(settings) }); }

    // ===== AI Coach =====
    async function getAIInsight() { return request('ai-coach/insight/'); }
    async function aiChat(message) { return request('ai-coach/chat/', { method: 'POST', body: JSON.stringify({ query: message }) }); }
    async function getAIStrategy() { return request('ai-coach/strategy/', { method: 'POST' }); }
    async function getAISummary(text) { return request('ai-coach/summary/', { method: 'POST', body: JSON.stringify({ content: text }) }); }
    async function translateText(text, targetLanguage) { return request('translate/', { method: 'POST', body: JSON.stringify({ text, target_language: targetLanguage }) }); }

    // ===== Admin =====
    async function adminListBooks() { return request('admin/books/'); }
    async function adminUploadBook(bookData) { return request('admin/books/', { method: 'POST', body: JSON.stringify(bookData) }); }
    async function adminDeleteBook(bookId) { return request(`admin/books/${bookId}/`, { method: 'DELETE' }); }
    async function adminListChapters(bookId) { return request(`admin/books/${bookId}/chapters/`); }
    async function adminAddChapter(bookId, chapterData) { return request(`admin/books/${bookId}/chapters/`, { method: 'POST', body: JSON.stringify(chapterData) }); }

    function logout() { clearToken(); window.location.hash = '#/login'; }

    return {
        isLoggedIn, isAdmin, getToken, setToken, clearToken, logout,
        login, register, forgotPassword, verifyOtp, resetPassword, changePassword,
        getDashboard, getDailyBoost, getBadges, getJournalEntries, createJournalEntry,
        getHomeFeed,
        getLibrary, getBookDetails, getChapters, trackBookOpen, updateReadingProgress, getCompletedBooks,
        getProgress, updateProgress, getGoalDetails, getReadingAnalytics, saveQuote, getSavedQuotes, deleteSavedQuote, getMoodGraph,
        sendLoginOTP, loginWithOTP,
        getChallenges, updateChallenge,
        getProfile, updateProfile, getProfileDetail, getPreferences, updatePreferences, getSubscription, getGrowthStats, deleteAccount,
        getNotifications, getNotificationSettings, updateNotificationSettings,
        getAIInsight, aiChat, getAIStrategy, getAISummary, translateText,
        adminListBooks, adminUploadBook, adminDeleteBook, adminListChapters, adminAddChapter,
        adminLogin, adminRegister
    };
})();
