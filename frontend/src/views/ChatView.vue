<template>
  <div class="page-container" style="max-width:900px;">
    <h1 style="font-size:24px;color:#1a6b3c;margin-bottom:16px;">🤖 AI 溯源助手</h1>
    <p style="color:#666;margin-bottom:20px;">
      像聊天一样查询农产品溯源信息、农残检测、物流追踪和商品推荐。
    </p>

    <div class="chat-container" ref="chatContainer">
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, i) in messages" :key="i">
          <div v-if="msg.role === 'agent'" class="agent-message" v-html="renderMarkdown(msg.content)"></div>
          <div v-else class="user-message" style="text-align:right;">
            <el-tag type="success" style="margin-bottom:4px;">你</el-tag>
            <div>{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="loading" style="text-align:center;padding:20px;">
          <el-icon class="is-loading" :size="24"><Loading /></el-icon>
          <p style="color:#999;margin-top:8px;">AI 正在思考...</p>
        </div>
      </div>

      <div class="chat-input-area">
        <el-row :gutter="8">
          <el-col :span="20">
            <el-input v-model="inputMessage" placeholder="输入您的问题，例如：山东苹果的农残达标吗？" size="large"
              @keyup.enter="sendMessage" :disabled="loading" clearable />
          </el-col>
          <el-col :span="4">
            <el-button type="primary" size="large" style="width:100%;" @click="sendMessage" :loading="loading">
              发送
            </el-button>
          </el-col>
        </el-row>
        <div style="margin-top:8px;display:flex;gap:8px;flex-wrap:wrap;">
          <el-tag v-for="q in quickQuestions" :key="q" style="cursor:pointer;" @click="askQuestion(q)" size="small">
            {{ q }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { chatApi } from '@/api'
import { marked } from 'marked'

const messages = ref([
  { role: 'agent', content: "您好！我是农溯AI智能助手。您可以问我以下问题：\n\n1️⃣ **溯源查询** — \"查询批次BATCH20260701001的溯源信息\"\n2️⃣ **农残检测** — \"山东苹果的农残达标吗？\"\n3️⃣ **商品推荐** — \"推荐几款有机农产品\"\n4️⃣ **合规标准** — \"水果的国标是什么？\"\n\n请告诉我您想了解什么？" }
])

const inputMessage = ref('')
const loading = ref(false)
const sessionId = ref('')
const messagesRef = ref(null)

const quickQuestions = [
  '山东苹果的农残达标吗？',
  '推荐几款水果',
  '水果的合规标准是什么？',
  '查询BATCH20260701001'
]

const sendMessage = async () => {
  const msg = inputMessage.value.trim()
  if (!msg || loading.value) return

  messages.value.push({ role: 'user', content: msg })
  inputMessage.value = ''
  loading.value = true

  try {
    const res = await chatApi.send({ message: msg, sessionId: sessionId.value })
    if (res.code === 200) {
      sessionId.value = res.data.sessionId
      messages.value.push({ role: 'agent', content: res.data.answer })
    }
  } catch (e) {
    messages.value.push({ role: 'agent', content: '抱歉，请求失败了，请稍后重试。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const askQuestion = (q) => {
  inputMessage.value = q
  sendMessage()
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const renderMarkdown = (text) => {
  return marked(text)
}

onMounted(scrollToBottom)
</script>
