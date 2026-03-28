// AI Coach Page
Router.register('/ai-coach', async (container) => {
    let messages = [
        { role: 'ai', text: "Hello! I'm your AI reading coach. 📚 Ask me anything about books, reading strategies, or personal growth. I can also help you with:\n\n• Book recommendations\n• Reading strategies\n• Motivational insights\n• Summary of your progress" }
    ];

    // Try to load initial insight
    try {
        const insight = await API.getAIInsight();
        if (insight && insight.insight) {
            messages.push({ role: 'ai', text: `💡 **Today's Insight:** ${insight.insight}` });
        }
    } catch (e) {
        // Silent fail - insight is optional
    }

    render();

    function render() {
        container.innerHTML = `
            <div class="page-wrapper" style="height:100vh;display:flex;flex-direction:column;padding-bottom:0">
                <div class="greeting-header fade-in" style="flex-shrink:0">
                    <div class="greeting-info">
                        <h2>AI Coach 🧠</h2>
                        <p>Your personal reading companion</p>
                    </div>
                    <div style="display:flex;gap:8px">
                        <button class="btn-icon" id="strategy-btn" title="Get Strategy" style="background:var(--muted-lavender)">
                            <span class="material-icons-round" style="font-size:20px;color:#7C3AED">lightbulb</span>
                        </button>
                    </div>
                </div>

                <div class="chat-messages" id="chat-messages">
                    ${messages.map(m => renderMessage(m)).join('')}
                </div>

                <div class="chat-input-area" style="flex-shrink:0">
                    <input type="text" class="chat-input" id="chat-input" placeholder="Ask your AI coach anything..." autocomplete="off">
                    <button class="chat-send-btn" id="chat-send">
                        <span class="material-icons-round">send</span>
                    </button>
                </div>
            </div>`;

        // Scroll to bottom
        const msgContainer = container.querySelector('#chat-messages');
        msgContainer.scrollTop = msgContainer.scrollHeight;

        // Send message
        const input = container.querySelector('#chat-input');
        const sendBtn = container.querySelector('#chat-send');

        async function sendMessage() {
            const text = input.value.trim();
            if (!text) return;

            messages.push({ role: 'user', text });
            input.value = '';
            render();

            try {
                const response = await API.aiChat(text);
                messages.push({ role: 'ai', text: response.response || response.message || 'I appreciate your question! Let me think about that...' });
                render();
            } catch (e) {
                messages.push({ role: 'ai', text: `Sorry, I encountered an error: ${e.message}. Please try again.` });
                render();
            }
        }

        sendBtn.addEventListener('click', sendMessage);
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') sendMessage();
        });
        input.focus();

        // Strategy button
        container.querySelector('#strategy-btn').addEventListener('click', async () => {
            messages.push({ role: 'user', text: 'Give me a reading strategy for today' });
            render();
            try {
                const strategy = await API.getAIStrategy();
                messages.push({ role: 'ai', text: strategy.strategy || strategy.response || 'Focus on reading for at least 15 minutes today. Choose a quiet spot and set a timer. Quality over quantity!' });
                render();
            } catch (e) {
                messages.push({ role: 'ai', text: 'Try reading for 15 minutes today in a quiet spot. Start with the chapter you left off and take notes on key ideas. 📝' });
                render();
            }
        });
    }

    function renderMessage(msg) {
        if (msg.role === 'ai') {
            return `
                <div class="chat-message ai">
                    <div class="chat-avatar">
                        <span class="material-icons-round">psychology</span>
                    </div>
                    <div class="chat-bubble">${formatMsg(msg.text)}</div>
                </div>`;
        } else {
            return `
                <div class="chat-message user">
                    <div class="chat-avatar" style="background:var(--primary);color:white">
                        <span class="material-icons-round">person</span>
                    </div>
                    <div class="chat-bubble">${escapeHtml(msg.text)}</div>
                </div>`;
        }
    }

    function formatMsg(text) {
        return text
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\n/g, '<br>')
            .replace(/• /g, '<br>• ');
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
});
