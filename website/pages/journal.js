// Journal Page
Router.register('/journal', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading journal...</p></div></div>';

    let journalData = null;

    try {
        journalData = await API.getJournalEntries();
        render();
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load journal</h3><p>${error.message}</p></div></div>`;
    }

    function render() {
        const entries = journalData?.entries || [];
        const todayPrompt = journalData?.today_prompt || "What's on your mind today?";

        container.innerHTML = `
            <div class="page-wrapper">
                <div class="greeting-header fade-in">
                    <div class="greeting-info">
                        <h2>Journal ✍️</h2>
                        <p>Reflect on your reading journey</p>
                    </div>
                    <button class="btn btn-accent btn-sm" id="new-entry-btn" style="width:auto">
                        <span class="material-icons-round" style="font-size:18px">add</span> New Entry
                    </button>
                </div>

                <!-- Today's Prompt -->
                <div class="card fade-in stagger-1" style="background:linear-gradient(135deg, var(--cream-white), var(--soft-peach));border:none">
                    <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
                        <span class="material-icons-round" style="color:#D97706;font-size:20px">auto_awesome</span>
                        <span style="font-size:0.85rem;font-weight:600;color:#D97706">Today's Prompt</span>
                    </div>
                    <p style="font-size:0.95rem;font-style:italic;color:var(--text-primary)">"${todayPrompt}"</p>
                </div>

                <!-- Entries -->
                ${entries.length === 0 ? `
                    <div class="empty-state">
                        <span class="material-icons-round">edit_note</span>
                        <h3>No journal entries yet</h3>
                        <p>Start writing to reflect on your reading</p>
                    </div>
                ` : entries.map((entry, i) => `
                    <div class="card journal-entry fade-in" style="animation-delay:${i * 0.05}s" data-id="${entry.id}">
                        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px">
                            <span style="font-size:0.8rem;color:var(--text-secondary)">${entry.date_created || ''}</span>
                            <span class="journal-mood mood-${entry.mood || 'okay'}">${getMoodEmoji(entry.mood)} ${entry.mood || 'okay'}</span>
                        </div>
                        <div style="font-size:1rem;font-weight:600;margin-bottom:4px">${entry.title || 'Untitled'}</div>
                        <div style="font-size:0.9rem;color:var(--text-secondary);display:-webkit-box;-webkit-line-clamp:3;-webkit-box-orient:vertical;overflow:hidden">${entry.content}</div>
                    </div>
                `).join('')}
            </div>`;

        // New entry button
        container.querySelector('#new-entry-btn').addEventListener('click', () => {
            showNewEntryModal(todayPrompt);
        });
    }

    function showNewEntryModal(prompt) {
        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `
            <div class="modal-content">
                <h3 style="font-size:1.2rem;font-weight:700;margin-bottom:20px">New Journal Entry</h3>
                ${prompt ? `<p style="font-size:0.85rem;font-style:italic;color:var(--text-secondary);margin-bottom:16px">Prompt: "${prompt}"</p>` : ''}
                <div class="form-group">
                    <label class="form-label">Title</label>
                    <div class="input-wrapper">
                        <span class="material-icons-round">title</span>
                        <input type="text" class="form-input" id="journal-title" placeholder="Daily Reflection" value="Daily Reflection">
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">How are you feeling?</label>
                    <div style="display:flex;gap:8px">
                        <button class="tab-btn" data-mood="rough">😔 Rough</button>
                        <button class="tab-btn active" data-mood="okay">😊 Okay</button>
                        <button class="tab-btn" data-mood="great">🌟 Great</button>
                    </div>
                </div>
                <div class="form-group">
                    <label class="form-label">Your Thoughts</label>
                    <textarea class="form-input" id="journal-content" rows="6" placeholder="Write your thoughts..." style="padding-left:14px;resize:vertical"></textarea>
                </div>
                <div style="display:flex;gap:12px;margin-top:20px">
                    <button class="btn btn-outline" style="flex:1" onclick="this.closest('.modal-overlay').remove()">Cancel</button>
                    <button class="btn btn-primary" id="journal-save" style="flex:1">Save Entry</button>
                </div>
            </div>`;
        document.body.appendChild(overlay);
        overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });

        let selectedMood = 'okay';
        overlay.querySelectorAll('[data-mood]').forEach(btn => {
            btn.addEventListener('click', () => {
                overlay.querySelectorAll('[data-mood]').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                selectedMood = btn.dataset.mood;
            });
        });

        overlay.querySelector('#journal-save').addEventListener('click', async () => {
            const title = document.getElementById('journal-title').value.trim();
            const content = document.getElementById('journal-content').value.trim();
            if (!content) { showToast('Please write something', 'error'); return; }

            const btn = overlay.querySelector('#journal-save');
            btn.disabled = true; btn.textContent = 'Saving...';

            try {
                await API.createJournalEntry(title || 'Daily Reflection', content, selectedMood, prompt || '');
                showToast('Journal entry saved! 📝', 'success');
                overlay.remove();
                // Reload
                journalData = await API.getJournalEntries();
                render();
            } catch (e) {
                showToast(e.message, 'error');
                btn.disabled = false; btn.textContent = 'Save Entry';
            }
        });
    }

    function getMoodEmoji(mood) {
        return mood === 'great' ? '🌟' : mood === 'rough' ? '😔' : '😊';
    }
});
