// Admin Dashboard — Book Upload & Management
Router.register('/admin', async (container) => {
    if (!API.isAdmin()) { location.hash = '#/dashboard'; return; }

    container.innerHTML = `
        <div class="page-wrapper admin-dashboard">
            <div class="greeting-header" style="margin-bottom: 40px; background: white; padding: 24px 32px; border-radius: 20px; box-shadow: var(--shadow-sm); display: flex; align-items: center; justify-content: space-between;">
                <div class="greeting-info">
                    <h1 style="font-size: 2.2rem; font-weight: 800; color: #1E293B; letter-spacing: -0.03em;">Admin Control Center</h1>
                    <p style="color: #64748B; font-weight: 500; margin-top: 4px;">Manage your content ecosystem and library</p>
                </div>
                <div class="header-actions" style="display: flex; gap: 12px;">
                    <button class="btn btn-outline" style="width: auto; border-radius: 12px; padding: 10px 20px;" onclick="window.location.hash = '#/dashboard'">
                        <span class="material-icons-round">space_dashboard</span>
                        <span>User View</span>
                    </button>
                    <button class="btn btn-primary" style="width: auto; border-radius: 12px; padding: 10px 20px; background: #EF4444; border: none; box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);" onclick="API.logout()">
                        <span class="material-icons-round">logout</span>
                        <span>Sign Out</span>
                    </button>
                </div>
            </div>

            <div class="admin-grid">
                <!-- Upload Form -->
                <div class="glass-card admin-upload-card" style="border: 1px solid rgba(79, 70, 229, 0.1);">
                    <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 24px;">
                        <div style="width: 44px; height: 44px; background: rgba(79, 70, 229, 0.1); border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #4F46E5;">
                            <span class="material-icons-round">add_circle</span>
                        </div>
                        <h2 style="font-size: 1.25rem; font-weight: 700; color: #1E293B;">Upload New Book</h2>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Book Title *</label>
                        <div class="input-wrapper"><span class="material-icons-round">menu_book</span><input type="text" id="admin-title" class="form-input" placeholder="e.g. Deep Work"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Author *</label>
                        <div class="input-wrapper"><span class="material-icons-round">person</span><input type="text" id="admin-author" class="form-input" placeholder="e.g. Cal Newport"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Description</label>
                        <textarea id="admin-desc" class="form-input" style="height: 100px; padding-top: 12px;" placeholder="Tell readers what this book is about..."></textarea>
                    </div>
                    <div class="form-row" style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
                        <div class="form-group">
                            <label class="form-label">Category</label>
                            <select id="admin-category" class="form-input" style="padding-left: 12px;">
                                <option value="TOP">Top Books</option>
                                <option value="MONTH">Monthly Special</option>
                                <option value="TRENDING" selected>Trending</option>
                                <option value="RECOMMENDED">AI Selection</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label class="form-label">Genre</label>
                            <div class="input-wrapper"><input type="text" id="admin-genre" class="form-input" style="padding-left: 14px;" placeholder="Self-Help"></div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Cover Image URL</label>
                        <div class="input-wrapper"><span class="material-icons-round">image</span><input type="url" id="admin-cover" class="form-input" placeholder="Paste image link here"></div>
                    </div>
                    <div class="form-group" style="display: flex; align-items: center; gap: 10px; margin: 16px 0;">
                        <input type="checkbox" id="admin-premium" style="width: 20px; height: 20px; cursor: pointer;">
                        <label for="admin-premium" style="font-weight: 600; color: #475569; cursor: pointer;">Mark as Premium Content</label>
                    </div>
                    <button id="admin-upload-btn" class="btn btn-primary" style="background: var(--grad-primary); padding: 16px; font-weight: 700;">
                        <span class="material-icons-round">publish</span>
                        <span>Deploy Content</span>
                    </button>
                </div>

                <!-- Book List -->
                <div class="glass-card admin-list-card" style="border: 1px solid rgba(16, 185, 129, 0.1);">
                    <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                        <div style="display: flex; align-items: center; gap: 12px;">
                            <div style="width: 44px; height: 44px; background: rgba(16, 185, 129, 0.1); border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #10B981;">
                                <span class="material-icons-round">list_alt</span>
                            </div>
                            <h2 style="font-size: 1.25rem; font-weight: 700; color: #1E293B;">Existing Library</h2>
                        </div>
                        <div id="book-count-badge" class="badge" style="background: #F1F5F9; color: #64748B; font-weight: 700; padding: 4px 12px; border-radius: 8px;">-- Books</div>
                    </div>
                    
                    <div id="admin-books-list" class="admin-books-list">
                        <div style="display: flex; flex-direction: column; align-items: center; padding: 40px; color: #94A3B8;">
                            <div class="spinner"></div>
                            <p style="margin-top: 16px; font-weight: 500;">Retrieving collection...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>`;

    // Load book list
    async function loadBooks() {
        const listEl = container.querySelector('#admin-books-list');
        const countBadge = container.querySelector('#book-count-badge');
        try {
            const books = await API.adminListBooks();
            if (countBadge) countBadge.innerText = `${books.length} Books`;
            
            if (!books.length) {
                listEl.innerHTML = '<div class="empty-state"><span class="material-icons-round">inbox</span><p>No books uploaded yet</p></div>';
                return;
            }
            listEl.innerHTML = books.map(b => `
                <div class="admin-book-item" data-id="${b.id}">
                    <div class="admin-book-cover">
                        ${b.cover_url ? `<img src="${b.cover_url}" alt="${b.title}">` : `<span class="material-icons-round">menu_book</span>`}
                    </div>
                    <div class="admin-book-info">
                        <h3>${b.title}</h3>
                        <p class="admin-book-author">${b.author}</p>
                        <span class="admin-book-cat badge-${b.category}">${b.category}</span>
                        ${b.is_premium ? '<span class="admin-badge-premium">★ Premium</span>' : ''}
                    </div>
                    <div class="admin-book-actions">
                        <button class="btn-icon admin-chapters-btn" data-id="${b.id}" data-title="${b.title}" title="Manage Chapters">
                            <span class="material-icons-round">edit_note</span>
                        </button>
                        <button class="btn-icon admin-delete-btn" data-id="${b.id}" title="Delete">
                            <span class="material-icons-round">delete</span>
                        </button>
                    </div>
                </div>
            `).join('');

            // Attach events
            listEl.querySelectorAll('.admin-delete-btn').forEach(btn => {
                btn.onclick = async () => {
                    if (!confirm('Delete this book and all its chapters?')) return;
                    try {
                        await API.adminDeleteBook(btn.dataset.id);
                        showToast('Book deleted', 'success');
                        loadBooks();
                    } catch (e) { showToast(e.message, 'error'); }
                };
            });
            listEl.querySelectorAll('.admin-chapters-btn').forEach(btn => {
                btn.onclick = () => location.hash = `#/admin/chapters/${btn.dataset.id}`;
            });
        } catch (e) {
            listEl.innerHTML = `<div class="empty-state"><span class="material-icons-round">error</span><p>${e.message}</p></div>`;
        }
    }

    loadBooks();

    // Upload handler
    container.querySelector('#admin-upload-btn').onclick = async () => {
        const title = container.querySelector('#admin-title').value.trim();
        const author = container.querySelector('#admin-author').value.trim();
        const description = container.querySelector('#admin-desc').value.trim();
        const category = container.querySelector('#admin-category').value;
        const genre = container.querySelector('#admin-genre').value.trim();
        const cover_url = container.querySelector('#admin-cover').value.trim();
        const is_premium = container.querySelector('#admin-premium').checked;

        if (!title || !author) return showToast('Title and Author are required', 'error');

        const btn = container.querySelector('#admin-upload-btn');
        btn.disabled = true; btn.innerHTML = '<span class="material-icons-round spin">sync</span> Uploading...';

        try {
            await API.adminUploadBook({ title, author, description, category, genre, cover_url, is_premium });
            showToast('Book uploaded successfully!', 'success');
            // Clear form
            ['#admin-title','#admin-author','#admin-desc','#admin-genre','#admin-cover'].forEach(s => container.querySelector(s).value = '');
            container.querySelector('#admin-premium').checked = false;
            loadBooks();
        } catch (e) {
            showToast(e.message, 'error');
        } finally {
            btn.disabled = false; btn.innerHTML = '<span class="material-icons-round">publish</span> Upload Book';
        }
    };
});
