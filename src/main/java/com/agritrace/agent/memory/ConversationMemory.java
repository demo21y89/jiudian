package com.agritrace.agent.memory;

import java.util.*;

/**
 * 会话记忆：存储多轮对话上下文
 */
public class ConversationMemory {

    private static final int MAX_HISTORY = 20;

    private final String sessionId;
    private final List<Message> messages = new ArrayList<>();
    private final Map<String, Object> userPreferences = new HashMap<>();

    public ConversationMemory(String sessionId) {
        this.sessionId = sessionId;
    }

    public void addMessage(String role, String content) {
        messages.add(new Message(role, content, System.currentTimeMillis()));
        if (messages.size() > MAX_HISTORY) {
            messages.remove(0);
        }
    }

    public List<Message> getRecentMessages(int count) {
        int size = messages.size();
        int start = Math.max(0, size - count);
        return messages.subList(start, size);
    }

    public List<Message> getAllMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void setPreference(String key, Object value) {
        userPreferences.put(key, value);
    }

    public Object getPreference(String key) {
        return userPreferences.get(key);
    }

    public Map<String, Object> getUserPreferences() {
        return Collections.unmodifiableMap(userPreferences);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void clear() {
        messages.clear();
        userPreferences.clear();
    }

    public static class Message {
        private final String role;
        private final String content;
        private final long timestamp;

        public Message(String role, String content, long timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
    }
}
