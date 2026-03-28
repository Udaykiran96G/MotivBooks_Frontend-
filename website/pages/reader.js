// Reader Page
Router.register('/reader/:id', async (container, params) => {
    const bookId = parseInt(params.id);
    container.innerHTML = '<div class="page-wrapper"><div class="loading-container"><div class="spinner"></div><p>Loading book...</p></div></div>';

    let chapters = [];
    let currentChapterIndex = 0;
    let fontSize = 18;
    let theme = 'light'; // light, sepia, dark
    let currentLanguage = 'English';

    try {
        // Track book open
        API.trackBookOpen(bookId).catch(() => {});
        
        // Load chapters
        chapters = await API.getChapters(bookId);
        if (!chapters || chapters.length === 0) {
            container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">menu_book</span><h3>No chapters available</h3><p>This book has no content yet</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.hash='#/library'">Back to Library</button></div></div>`;
            return;
        }
        renderReader();
    } catch (error) {
        container.innerHTML = `<div class="page-wrapper"><div class="empty-state"><span class="material-icons-round">error_outline</span><h3>Failed to load book</h3><p>${error.message}</p><button class="btn btn-primary btn-sm" style="width:auto;margin-top:12px" onclick="location.hash='#/library'">Back to Library</button></div></div>`;
    }

    function renderReader() {
        const chapter = chapters[currentChapterIndex];
        const bgClass = theme === 'sepia' ? 'background:#FDF6E3;color:#5C4A32' : theme === 'dark' ? 'background:#0F172A;color:#E2E8F0' : '';
        const toolbarBg = theme === 'dark' ? 'background:#1E293B;color:#E2E8F0' : '';

        container.innerHTML = `
            <div class="reader-container fade-in" id="reader-root" style="${bgClass};min-height:100vh;padding:0">
                <div class="reader-toolbar" style="${toolbarBg};padding:12px 24px;position:sticky;top:0;z-index:10;backdrop-filter:blur(12px)">
                    <div class="reader-nav">
                        <button class="btn-icon" onclick="location.hash='#/library'" title="Back to Library" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" style="font-size:20px">arrow_back</span>
                        </button>
                        <span style="font-size:0.85rem;font-weight:600;margin-left:8px">
                            Chapter ${currentChapterIndex + 1} of ${chapters.length}
                        </span>
                    </div>
                    <div class="reader-settings">
                        <button class="btn-icon" id="font-decrease" title="Decrease font size" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" style="font-size:18px">text_decrease</span>
                        </button>
                        <button class="btn-icon" id="font-increase" title="Increase font size" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" style="font-size:18px">text_increase</span>
                        </button>
                        <button class="btn-icon" id="audio-toggle" title="Listen (TTS)" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" id="audio-icon" style="font-size:18px">volume_up</span>
                        </button>
                        <button class="btn-icon" id="translate-btn" title="Translate Page" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" style="font-size:18px">translate</span>
                        </button>
                        <button class="btn-icon" id="theme-toggle" title="Toggle theme" style="${theme === 'dark' ? 'background:rgba(255,255,255,0.1)' : ''}">
                            <span class="material-icons-round" style="font-size:18px">${theme === 'dark' ? 'light_mode' : theme === 'sepia' ? 'dark_mode' : 'auto_fix_high'}</span>
                        </button>
                    </div>
                </div>
                
                <div style="padding:24px;max-width:720px;margin:0 auto">
                    <h2 class="reader-chapter-title" style="${theme === 'dark' ? 'color:#F8FAFC' : theme === 'sepia' ? 'color:#5C4A32' : ''}">${chapter.title}</h2>
                    <div class="reader-content" id="chapter-content" style="font-size:${fontSize}px;position:relative">${formatContent(chapter.content)}</div>
                    
                    <!-- Audio Controls (shown when listening) -->
                    <div id="audio-controls" class="card fade-in" style="display:none;position:fixed;bottom:24px;left:50%;transform:translateX(-50%);z-index:100;width:90%;max-width:400px;background:var(--midnight-blue);color:white;padding:16px;border-radius:20px;box-shadow:var(--shadow-lg);flex-direction:column;gap:12px">
                        <div style="display:flex;justify-content:space-between;align-items:center">
                            <span style="font-size:0.8rem;font-weight:600;opacity:0.8">Audio Mode</span>
                            <button id="close-audio" style="color:white;opacity:0.6"><span class="material-icons-round">close</span></button>
                        </div>
                        <div style="display:flex;align-items:center;justify-content:center;gap:24px">
                            <button id="audio-prev" style="color:white"><span class="material-icons-round">skip_previous</span></button>
                            <button id="audio-play-pause" style="width:56px;height:56px;border-radius:50%;background:var(--soft-teal);color:var(--midnight-blue);display:flex;align-items:center;justify-content:center">
                                <span class="material-icons-round" id="play-pause-icon" style="font-size:32px">pause</span>
                            </button>
                            <button id="audio-next" style="color:white"><span class="material-icons-round">skip_next</span></button>
                        </div>
                        <div style="text-align:center;font-size:0.75rem;opacity:0.7" id="audio-status">Reading: ${chapter.title}</div>
                    </div>

                    <div id="selection-toolbar" class="card" style="display:none;position:absolute;padding:8px;z-index:100;box-shadow:var(--shadow-lg);border-radius:var(--radius-md);flex-direction:row;gap:8px">
                        <button class="btn btn-primary btn-sm" id="save-selection-btn" style="width:auto">
                            <span class="material-icons-round" style="font-size:16px">format_quote</span> Save Quote
                        </button>
                    </div>

                    <div class="chapter-nav" style="${theme === 'dark' ? 'border-color:rgba(255,255,255,0.1)' : ''}">
                        ${currentChapterIndex > 0 ? `<button class="btn btn-outline btn-sm" id="prev-chapter" style="width:auto;${theme === 'dark' ? 'color:#E2E8F0;border-color:rgba(255,255,255,0.2)' : ''}"><span class="material-icons-round" style="font-size:16px">chevron_left</span> Previous</button>` : '<div></div>'}
                        ${currentChapterIndex < chapters.length - 1 ? `<button class="btn btn-accent btn-sm" id="next-chapter" style="width:auto">Next <span class="material-icons-round" style="font-size:16px">chevron_right</span></button>` : '<button class="btn btn-accent btn-sm" style="width:auto" onclick="location.hash=\'#/library\'">Finish Book</button>'}
                    </div>
                </div>
            </div>

            <!-- Translate Modal -->
            <div class="modal-overlay" id="translate-modal" style="display:none;">
                <div class="modal fade-in-up">
                    <h3 style="margin-bottom:16px">Translate Page</h3>
                    <div class="form-group">
                        <label class="form-label">Select Language</label>
                        <div class="input-wrapper">
                            <select class="form-input" id="translate-lang">
                                <option value="English">English (Original)</option>
                                <option value="Tamil">Tamil</option>
                                <option value="Hindi">Hindi</option>
                                <option value="Telugu">Telugu</option>
                                <option value="Spanish">Spanish</option>
                                <option value="French">French</option>
                                <option value="German">German</option>
                            </select>
                        </div>
                    </div>
                    <div style="display:flex;gap:12px;margin-top:24px;justify-content:flex-end">
                        <button class="btn btn-outline" id="close-translate" style="flex:1">Cancel</button>
                        <button class="btn btn-primary" id="apply-translate" style="flex:1">Translate</button>
                    </div>
                </div>
            </div>`;

        // Set the selected language in the dropdown
        const translateLangSelect = container.querySelector('#translate-lang');
        if (translateLangSelect) {
            translateLangSelect.value = currentLanguage;
        }

        // Font size controls
        container.querySelector('#font-decrease')?.addEventListener('click', () => {
            fontSize = Math.max(14, fontSize - 2);
            renderReader();
        });
        container.querySelector('#font-increase')?.addEventListener('click', () => {
            fontSize = Math.min(28, fontSize + 2);
            renderReader();
        });

        // Theme toggle
        container.querySelector('#theme-toggle')?.addEventListener('click', () => {
            theme = theme === 'light' ? 'sepia' : theme === 'sepia' ? 'dark' : 'light';
            renderReader();
        });

        // ----- Audio Mode (TTS) Implementation -----
        let isSpeaking = false;
        let currentUtterance = null;
        const synth = window.speechSynthesis;

        const audioToggle = container.querySelector('#audio-toggle');
        const audioControls = container.querySelector('#audio-controls');
        const playPauseBtn = container.querySelector('#audio-play-pause');
        const playPauseIcon = container.querySelector('#play-pause-icon');
        const closeAudioBtn = container.querySelector('#close-audio');

        function stopSpeaking() {
            synth.cancel();
            isSpeaking = false;
            if (playPauseIcon) playPauseIcon.textContent = 'play_arrow';
            audioControls.style.display = 'none';
        }

        function speak() {
            if (synth.speaking) synth.cancel();
            
            const textToSpeak = chapter.content;
            currentUtterance = new SpeechSynthesisUtterance(textToSpeak);
            currentUtterance.rate = 1.0;
            currentUtterance.pitch = 1.0;
            
            // Try to find a nice voice
            const voices = synth.getVoices();
            const preferredVoice = voices.find(v => v.name.includes('Google') || v.name.includes('Natural')) || voices[0];
            if (preferredVoice) currentUtterance.voice = preferredVoice;

            currentUtterance.onstart = () => {
                isSpeaking = true;
                playPauseIcon.textContent = 'pause';
                audioControls.style.display = 'flex';
            };

            currentUtterance.onend = () => {
                isSpeaking = false;
                playPauseIcon.textContent = 'play_arrow';
                audioControls.style.display = 'none';
            };

            synth.speak(currentUtterance);
        }

        audioToggle?.addEventListener('click', () => {
            if (isSpeaking) {
                stopSpeaking();
            } else {
                speak();
            }
        });

        playPauseBtn?.addEventListener('click', () => {
            if (synth.paused) {
                synth.resume();
                playPauseIcon.textContent = 'pause';
            } else if (synth.speaking) {
                synth.pause();
                playPauseIcon.textContent = 'play_arrow';
            } else {
                speak();
            }
        });

        closeAudioBtn?.addEventListener('click', stopSpeaking);
        
        // Navigation buttons within audio controls
        container.querySelector('#audio-prev')?.addEventListener('click', () => {
            stopSpeaking();
            container.querySelector('#prev-chapter')?.click();
        });
        container.querySelector('#audio-next')?.addEventListener('click', () => {
            stopSpeaking();
            container.querySelector('#next-chapter')?.click();
        });

        // Clean up audio on navigate
        const originalUnload = window.onhashchange;
        window.onhashchange = (e) => {
            synth.cancel();
            if (originalUnload) originalUnload(e);
        };

        // ----- End Audio Mode -----

        // Translate button and modal controls
        const translateModal = container.querySelector('#translate-modal');
        container.querySelector('#translate-btn')?.addEventListener('click', () => {
            translateModal.style.display = 'flex';
        });
        container.querySelector('#close-translate')?.addEventListener('click', () => {
            translateModal.style.display = 'none';
        });
        container.querySelector('#apply-translate')?.addEventListener('click', async () => {
            const lang = container.querySelector('#translate-lang').value;
            if (lang === 'English') {
                currentLanguage = 'English';
                translateModal.style.display = 'none';
                renderReader();
                return;
            }

            translateModal.style.display = 'none';
            const contentDiv = container.querySelector('#chapter-content');
            contentDiv.innerHTML = '<div style="text-align:center;padding:40px"><div class="spinner"></div><p style="margin-top:16px;color:var(--text-secondary)">Translating to ' + lang + '...</p></div>';

            try {
                const res = await API.translateText(chapter.content, lang);
                currentLanguage = lang;
                contentDiv.innerHTML = `
                    <div style="background:rgba(37,99,235,0.1);color:#2563EB;padding:12px 16px;border-radius:8px;margin-bottom:16px;font-size:0.9rem;display:flex;justify-content:space-between;align-items:center">
                        <span>Translated to ${lang} (AI)</span>
                        <button id="show-original" style="background:none;border:none;color:#2563EB;font-weight:700;cursor:pointer">Show Original</button>
                    </div>
                    ${formatContent(res.translated_text)}
                `;
                
                container.querySelector('#show-original').addEventListener('click', () => {
                    currentLanguage = 'English';
                    renderReader();
                });
            } catch (e) {
                contentDiv.innerHTML = `<div style="text-align:center;color:red;padding:20px;background:rgba(255,0,0,0.1);border-radius:8px;margin-bottom:16px">Translation failed: ${e.message}</div>
                                        ${formatContent(chapter.content)}`;
            }
        });

        // Chapter navigation
        container.querySelector('#prev-chapter')?.addEventListener('click', () => {
            synth.cancel();
            currentChapterIndex = Math.max(0, currentChapterIndex - 1);
            currentLanguage = 'English'; // Reset language on chapter change
            renderReader();
            window.scrollTo(0, 0);
        });
        container.querySelector('#next-chapter')?.addEventListener('click', () => {
            synth.cancel();
            if (currentChapterIndex < chapters.length - 1) {
                currentChapterIndex++;
                currentLanguage = 'English'; // Reset language on chapter change
                // Track progress
                API.updateReadingProgress(bookId, chapters[currentChapterIndex].order).catch(() => {});
                renderReader();
                window.scrollTo(0, 0);
            }
        });

        // Text selection for saving quotes
        const toolbar = container.querySelector('#selection-toolbar');
        const contentDiv = container.querySelector('#chapter-content');
        
        contentDiv.addEventListener('mouseup', (e) => {
            const selection = window.getSelection();
            const selectedText = selection.toString().trim();
            
            if (selectedText.length > 5) {
                const range = selection.getRangeAt(0);
                const rect = range.getBoundingClientRect();
                
                toolbar.style.display = 'flex';
                toolbar.style.top = (window.scrollY + rect.top - 50) + 'px';
                toolbar.style.left = (window.scrollX + rect.left) + 'px';
                
                // Temporarily store the text
                toolbar.dataset.text = selectedText;
            } else {
                toolbar.style.display = 'none';
            }
        });

        document.addEventListener('mousedown', (e) => {
            if (!toolbar.contains(e.target) && !contentDiv.contains(e.target)) {
                toolbar.style.display = 'none';
            }
        });

        container.querySelector('#save-selection-btn').addEventListener('click', async () => {
            const quote = toolbar.dataset.text;
            if (!quote) return;
            
            try {
                await API.saveQuote(quote, chapter.author || 'Unknown', chapter.title);
                showToast('Quote saved to Progress!', 'success');
                toolbar.style.display = 'none';
                window.getSelection().removeAllRanges();
            } catch (e) {
                showToast('Failed to save quote', 'error');
            }
        });
    }

    function formatContent(content) {
        // Convert line breaks to paragraphs and basic formatting
        return content
            .split('\n\n')
            .map(p => p.trim())
            .filter(p => p)
            .map(p => `<p style="margin-bottom:1.2em">${p.replace(/\n/g, '<br>')}</p>`)
            .join('');
    }
});
