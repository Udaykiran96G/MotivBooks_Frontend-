// Daily Boost Page
Router.register('/daily-boost', async (container) => {
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading daily boost...</p></div></div>';

    try {
        const boost = await API.getDailyBoost();
        container.innerHTML = `
            <div class="page-wrapper">
                <div class="greeting-header fade-in">
                    <div class="greeting-info">
                        <h2>Daily Boost ⚡</h2>
                        <p>Your dose of daily motivation</p>
                    </div>
                    <button class="btn-icon" onclick="location.hash='#/dashboard'" title="Back">
                        <span class="material-icons-round">arrow_back</span>
                    </button>
                </div>

                <!-- Insight -->
                <div class="card fade-in stagger-1" style="background:linear-gradient(135deg, var(--primary-dark), var(--primary));color:white;border:none">
                    <div style="display:flex;align-items:center;gap:8px;margin-bottom:12px">
                        <span class="material-icons-round" style="font-size:24px;color:#F59E0B">auto_awesome</span>
                        <span style="font-size:0.9rem;font-weight:600;opacity:0.8">${boost.insight_title || "Today's Insight"}</span>
                    </div>
                    <p style="font-size:1rem;line-height:1.6;opacity:0.95">${boost.ai_reflection || 'Focus on growth today!'}</p>
                </div>

                <!-- Quote -->
                <div class="card fade-in stagger-2" style="background:linear-gradient(135deg, var(--cream-white), var(--soft-peach));border:none;text-align:center;position:relative;">
                    <button class="btn-icon" id="save-quote-btn" title="Save Quote" style="position:absolute;top:12px;right:12px;background:rgba(255,255,255,0.6)">
                        <span class="material-icons-round" style="color:var(--text-secondary);font-size:20px;">bookmark_border</span>
                    </button>
                    <span class="material-icons-round" style="font-size:36px;color:#D97706;opacity:0.5">format_quote</span>
                    <p style="font-size:1.15rem;font-weight:600;font-style:italic;color:var(--text-primary);margin:12px 0;line-height:1.6">${boost.quote_text || '"Every book is a new opportunity to grow."'}</p>
                    <p style="font-size:0.9rem;color:var(--text-secondary)">— ${boost.quote_author || 'Unknown'}</p>
                </div>

                <!-- Article Preview -->
                <div class="card fade-in stagger-3">
                    <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px">
                        <span class="material-icons-round" style="font-size:20px;color:#2563EB">article</span>
                        <span style="font-size:0.85rem;font-weight:600;color:#2563EB">Featured Read</span>
                    </div>
                    <h3 style="font-size:1.1rem;font-weight:700;margin-bottom:4px">${boost.article_title || 'The Power of Reading'}</h3>
                    <p style="font-size:0.9rem;color:var(--text-secondary);line-height:1.5">${boost.article_preview || 'Discover how daily reading transforms your mindset.'}</p>
                </div>
            </div>`;

        // Handle Save Quote
        container.querySelector('#save-quote-btn')?.addEventListener('click', async (e) => {
            const btn = e.currentTarget;
            const icon = btn.querySelector('.material-icons-round');
            
            // Prevent duplicate saves if already saved locally
            if (icon.textContent === 'bookmark') return;

            icon.textContent = 'bookmark';
            icon.style.color = '#D97706';
            try {
                await API.saveQuote(
                    boost.quote_text || "Every book is a new opportunity to grow.",
                    boost.quote_author || "Unknown",
                    "Daily Boost"
                );
                showToast('Quote saved to your progress!');
            } catch (err) {
                showToast('Failed to save quote', 'error');
                icon.textContent = 'bookmark_border';
                icon.style.color = 'var(--text-secondary)';
            }
        });

    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load daily boost</h3><p>${error.message}</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.hash='#/dashboard'">Back</button></div></div>`;
    }
});
