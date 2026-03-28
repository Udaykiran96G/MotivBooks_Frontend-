// Library Page
Router.register('/library', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading library...</p></div></div>';

    let currentCategory = null;
    let books = [];

    try {
        books = await API.getLibrary();
        render();
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load library</h3><p>${error.message}</p></div></div>`;
    }

    async function loadBooks(category) {
        currentCategory = category;
        try {
            books = await API.getLibrary(category ? { category } : {});
            render();
        } catch (e) {
            showToast(e.message, 'error');
        }
    }

    function render() {
        const categories = [
            { key: null, label: 'All Books' },
            { key: 'TOP', label: 'Top Books' },
            { key: 'MONTH', label: 'This Month' },
            { key: 'TRENDING', label: 'Trending' },
            { key: 'RECOMMENDED', label: 'AI Picks' }
        ];

        container.innerHTML = `
            <div class="page-wrapper">
                <div class="greeting-header fade-in">
                    <div class="greeting-info">
                        <h2>Library 📚</h2>
                        <p>Discover books that inspire growth</p>
                    </div>
                </div>

                <div class="library-tabs fade-in stagger-1">
                    ${categories.map(c => `
                        <button class="tab-btn ${currentCategory === c.key ? 'active' : ''}" data-cat="${c.key}">${c.label}</button>
                    `).join('')}
                </div>

                <div class="books-grid fade-in stagger-2">
                    ${books.length === 0 ? `
                        <div class="empty-state" style="grid-column:1/-1">
                            <span class="material-icons-round">library_books</span>
                            <h3>No books found</h3>
                            <p>Check back later for new additions</p>
                        </div>
                    ` : books.map(book => `
                        <div class="book-grid-item" onclick="location.hash='#/reader/${book.id}'">
                            <div class="book-grid-cover">
                                ${book.cover_url ? `<img src="${book.cover_url}" alt="${book.title}" onerror="this.parentElement.innerHTML='<span class=\\'material-icons-round\\' style=\\'font-size:48px;color:var(--primary-light);opacity:0.4\\'>auto_stories</span>'">` : '<span class="material-icons-round" style="font-size:48px;color:var(--primary-light);opacity:0.4">auto_stories</span>'}
                                ${book.is_premium ? '<span class="premium-badge">Premium</span>' : ''}
                            </div>
                            <div class="book-grid-title">${book.title}</div>
                            <div class="book-grid-author">${book.author}</div>
                        </div>
                    `).join('')}
                </div>
            </div>`;

        // Tab click handlers
        container.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                const cat = btn.dataset.cat === 'null' ? null : btn.dataset.cat;
                loadBooks(cat);
            });
        });
    }
});
