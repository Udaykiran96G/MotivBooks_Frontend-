// Admin Chapters — Add and manage chapters for a book
Router.register('/admin/chapters/:bookId', async (container, params) => {
    if (!API.isAdmin()) { location.hash = '#/dashboard'; return; }
    const bookId = params.bookId;

    container.innerHTML = `
        <div class="page-wrapper">
            <div class="page-header">
                <button class="btn-back" onclick="location.hash='#/admin'"><span class="material-icons-round">arrow_back</span></button>
                <div>
                    <h1><span class="material-icons-round" style="vertical-align:middle;margin-right:8px">edit_note</span>Manage Chapters</h1>
                    <p class="page-subtitle" id="chapter-book-title">Book #${bookId}</p>
                </div>
            </div>

            <div class="admin-grid">
                <!-- Add Chapter Form -->
                <div class="glass-card admin-upload-card">
                    <h2 class="section-title"><span class="material-icons-round">add_circle</span> Add Chapter</h2>
                    <div class="form-group">
                        <label class="form-label">Chapter Title *</label>
                        <div class="input-wrapper"><span class="material-icons-round">title</span><input type="text" id="chap-title" class="form-input" placeholder="e.g. Introduction"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Order *</label>
                        <div class="input-wrapper"><span class="material-icons-round">format_list_numbered</span><input type="number" id="chap-order" class="form-input" placeholder="1" min="1" value="1"></div>
                    </div>
                    <div class="form-group">
                        <label class="form-label">Content *</label>
                        <textarea id="chap-content" class="form-input form-textarea" placeholder="Write the chapter content here..." rows="8"></textarea>
                    </div>
                    <button id="chap-add-btn" class="btn btn-primary"><span class="material-icons-round">add</span> Add Chapter</button>
                </div>

                <!-- Chapter List -->
                <div class="glass-card admin-list-card">
                    <h2 class="section-title"><span class="material-icons-round">list</span> Existing Chapters</h2>
                    <div id="chapters-list" class="admin-books-list">
                        <div class="loading-container"><div class="spinner"></div><p>Loading chapters...</p></div>
                    </div>
                </div>
            </div>
        </div>`;

    // Load book title
    try {
        const book = await API.getBookDetails(bookId);
        container.querySelector('#chapter-book-title').textContent = book.title + ' by ' + book.author;
    } catch (e) {}

    // Load chapters
    async function loadChapters() {
        const listEl = container.querySelector('#chapters-list');
        try {
            const chapters = await API.adminListChapters(bookId);
            if (!chapters.length) {
                listEl.innerHTML = '<div class="empty-state"><span class="material-icons-round">notes</span><p>No chapters yet. Add one above!</p></div>';
                // Auto-set order to 1
                container.querySelector('#chap-order').value = 1;
                return;
            }
            // Auto-set order to next
            const maxOrder = Math.max(...chapters.map(c => c.order));
            container.querySelector('#chap-order').value = maxOrder + 1;

            listEl.innerHTML = chapters.map(c => `
                <div class="admin-chapter-item">
                    <div class="admin-chapter-order">${c.order}</div>
                    <div class="admin-chapter-info">
                        <h3>${c.title}</h3>
                        <p class="admin-chapter-preview">${c.content.substring(0, 120)}${c.content.length > 120 ? '...' : ''}</p>
                    </div>
                </div>
            `).join('');
        } catch (e) {
            listEl.innerHTML = `<div class="empty-state"><span class="material-icons-round">error</span><p>${e.message}</p></div>`;
        }
    }

    loadChapters();

    // Add chapter handler
    container.querySelector('#chap-add-btn').onclick = async () => {
        const title = container.querySelector('#chap-title').value.trim();
        const order = parseInt(container.querySelector('#chap-order').value);
        const content = container.querySelector('#chap-content').value.trim();

        if (!title || !content) return showToast('Title and Content are required', 'error');
        if (!order || order < 1) return showToast('Order must be a positive number', 'error');

        const btn = container.querySelector('#chap-add-btn');
        btn.disabled = true; btn.innerHTML = '<span class="material-icons-round spin">sync</span> Adding...';

        try {
            await API.adminAddChapter(bookId, { title, order, content });
            showToast('Chapter added!', 'success');
            container.querySelector('#chap-title').value = '';
            container.querySelector('#chap-content').value = '';
            loadChapters();
        } catch (e) {
            showToast(e.message, 'error');
        } finally {
            btn.disabled = false; btn.innerHTML = '<span class="material-icons-round">add</span> Add Chapter';
        }
    };
});
