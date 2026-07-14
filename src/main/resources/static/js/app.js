/* AgriTrace JS Core v2 — Closure Pattern */
(function(){
  "use strict";

  /* ===== State ===== */
  var API_BASE = "/api/v1";
  var token = localStorage.getItem("agri_token") || null;
  var user = null;
  try { user = JSON.parse(localStorage.getItem("agri_user") || "null"); } catch(e) { user = null; }

  /* ===== HTTP Helpers ===== */
  function apiFetch(method, path, body) {
    var headers = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = "Bearer " + token;
    var opts = { method: method, headers: headers };
    if (body) opts.body = JSON.stringify(body);
    return fetch(API_BASE + path, opts).then(function(res) {
      if (res.status === 401) {
        doLogout();
        throw new Error("登录已过期");
      }
      if (!res.ok) {
        return res.json().catch(function() { return {}; }).then(function(e) {
          throw new Error(e.message || e.error || "请求失败");
        });
      }
      return res.json();
    });
  }

  function doGet(p)          { return apiFetch("GET", p); }
  function doPost(p, b)      { return apiFetch("POST", p, b); }
  function doPut(p, b)       { return apiFetch("PUT", p, b); }
  function doDel(p)          { return apiFetch("DELETE", p); }

  function extractList(data) {
    if (!data) return [];
    if (Array.isArray(data)) return data;
    if (data.data && Array.isArray(data.data)) return data.data;
    if (data.data && data.data.items) return data.data.items;
    if (data.data && data.data.content) return data.data.content;
    if (data.items) return data.items;
    if (data.content) return data.content;
    return [];
  }

  function doLogin(u, t) {
    token = t;
    user = u;
    localStorage.setItem("agri_token", t);
    localStorage.setItem("agri_user", JSON.stringify(u));
  }

  function doLogout() {
    token = null;
    user = null;
    localStorage.removeItem("agri_token");
    localStorage.removeItem("agri_user");
    window.location.href = "/page/login";
  }

  function isLoggedIn()  { return !!token; }
  function hasRole(r)    { return user && user.role === r; }
  function getUser()     { return user; }

  /* ===== Toast ===== */
  var Toast = {};
  var _toastWrap = null;

  function ensureToastWrap() {
    if (!_toastWrap) {
      _toastWrap = document.createElement("div");
      _toastWrap.className = "toast-wrap";
      document.body.appendChild(_toastWrap);
    }
    return _toastWrap;
  }

  Toast.show = function(msg, type, dur) {
    dur = dur || 3200;
    var el = document.createElement("div");
    el.className = "toast toast-" + type;
    var icon = type === "success" ? "✓" : type === "error" ? "✗" : "ℹ";
    el.textContent = icon + " " + msg;
    ensureToastWrap().appendChild(el);
    setTimeout(function() {
      el.style.opacity = "0";
      el.style.transition = "opacity .3s";
      setTimeout(function() { el.remove(); }, 300);
    }, dur);
  };
  Toast.ok   = function(msg) { Toast.show(msg, "success"); };
  Toast.err  = function(msg) { Toast.show(msg, "error", 5000); };
  Toast.info = function(msg) { Toast.show(msg, "info"); };

  /* ===== Chat ===== */
  var Chat = {
    sessionId: "sess_" + Date.now(),
    busy: false,
    messagesEl: null,
    inputEl: null,
    sendBtn: null,

    init: function(opts) {
      opts = opts || {};
      this.messagesEl = opts.messagesEl || document.getElementById("cm");
      this.inputEl    = opts.inputEl    || document.getElementById("ci");
      this.sendBtn    = opts.sendBtn    || document.getElementById("cs");
      if (opts.sessionId) this.sessionId = opts.sessionId;
      this._bindEvents();
    },

    _bindEvents: function() {
      var self = this;
      if (this.sendBtn) {
        this.sendBtn.addEventListener("click", function() { self.send(); });
      }
      if (this.inputEl) {
        this.inputEl.addEventListener("keydown", function(e) {
          if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            self.send();
          }
        });
      }
    },

    send: function() {
      var self = this;
      var query = this.inputEl ? this.inputEl.value.trim() : "";
      if (!query || this.busy) return;
      this.inputEl.value = "";
      this.busy = true;
      if (this.sendBtn) this.sendBtn.disabled = true;

      self._addMsg("user", query);
      var typingEl = self._addTyping();

      var headers = { "Content-Type": "application/json" };
      if (token) headers["Authorization"] = "Bearer " + token;

      fetch(API_BASE + "/agent/chat/stream", {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
          session_id: self.sessionId,
          query: query,
          user_id: (user && user.id) ? user.id : 1
        })
      }).then(function(resp) {
        if (!resp.ok) throw new Error("服务响应异常");
        typingEl.remove();
        var assistantEl = self._addMsg("assistant", "");
        var bubble = assistantEl.querySelector(".chat-bubble-assistant");
        var toolsRow = assistantEl.querySelector(".msg-tools");
        var reader = resp.body.getReader();
        var dec = new TextDecoder();
        var buf = "";

        function pump() {
          reader.read().then(function(result) {
            if (result.done) {
              if (!bubble.textContent.trim()) bubble.textContent = "抱歉，暂时无法回答，请稍后重试。";
              self._scroll();
              return;
            }
            buf += dec.decode(result.value, { stream: true });
            var lines = buf.split("\n");
            buf = lines.pop() || "";
            for (var i = 0; i < lines.length; i++) {
              var line = lines[i];
              if (line.indexOf("data:") === 0) {
                var d = line.slice(5).trim();
                if (d === "[DONE]") continue;
                try {
                  var j = JSON.parse(d);
                  if (j.type === "content") {
                    bubble.textContent += (j.data || "");
                  } else if (j.type === "tool") {
                    var s = document.createElement("span");
                    s.className = "badge badge-success";
                    s.style.cssText = "margin-right:4px";
                    s.textContent = "🔧 " + j.data;
                    toolsRow.appendChild(s);
                  } else if (j.type === "source") {
                    var src = document.createElement("span");
                    src.className = "badge badge-info";
                    src.style.cssText = "margin-right:4px";
                    src.textContent = "📋 " + j.data;
                    toolsRow.appendChild(src);
                  } else if (j.type === "error") {
                    bubble.textContent += "\n❌" + j.data;
                  }
                } catch(e) {}
              }
            }
            pump();
          });
        }
        pump();
      }).catch(function(e) {
        typingEl.remove();
        self._addMsg("assistant", "❌ 连接失败：" + e.message + "\n\n请确认后端服务已启动");
      }).finally(function() {
        self.busy = false;
        if (self.sendBtn) self.sendBtn.disabled = false;
        if (self.inputEl) self.inputEl.focus();
      });
    },

    _addMsg: function(role, content) {
      var el = document.createElement("div");
      el.className = "msg-row " + role;
      var av = role === "user" ? ((user && user.username) ? user.username[0] : "U") : "🌾";
      var bubbleCls = role === "user" ? "chat-bubble-user" : "chat-bubble-assistant";
      var bgStyle = role === "user"
        ? "background:linear-gradient(135deg,#2d5a27,#4a7c3f)"
        : "background:linear-gradient(135deg,#f5c842,#d4920e)";
      el.innerHTML = '<div style="width:32px;height:32px;border-radius:50%;' + bgStyle + ';display:flex;align-items:center;justify-content:center;color:#fff;font-size:14px;flex-shrink:0">' + av + '</div><div><div class="' + bubbleCls + '">' + escHtml(content) + '</div><div class="msg-tools flex gap-1" style="margin-top:4px"></div><div style="font-size:10px;color:#c9b896;margin-top:3px">' + new Date().toLocaleTimeString() + '</div></div>';
      if (this.messagesEl) this.messagesEl.appendChild(el);
      this._scroll();
      return el;
    },

    _addTyping: function() {
      var el = document.createElement("div");
      el.className = "msg-row assistant";
      el.innerHTML = '<div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,#f5c842,#d4920e);display:flex;align-items:center;justify-content:center;color:#fff;font-size:14px;flex-shrink:0">🌾</div><div><div class="chat-bubble-assistant"><span class="typing-dot"></span> <span class="typing-dot"></span> <span class="typing-dot"></span></div></div>';
      if (this.messagesEl) this.messagesEl.appendChild(el);
      this._scroll();
      return el;
    },

    _scroll: function() {
      var self = this;
      setTimeout(function() {
        if (self.messagesEl) self.messagesEl.scrollTop = self.messagesEl.scrollHeight;
      }, 60);
    }
  };

  function escHtml(t) {
    var d = document.createElement("div");
    d.textContent = t;
    return d.innerHTML;
  }

  /* ===== Modal ===== */
  var Modal = {
    open: function(id) {
      var el = document.getElementById(id);
      if (el) el.classList.remove("hidden");
    },
    close: function(id) {
      var el = document.getElementById(id);
      if (el) el.classList.add("hidden");
    },
    _init: function() {
      document.querySelectorAll("[data-modal-close]").forEach(function(el) {
        el.addEventListener("click", function() {
          Modal.close(el.dataset.modalClose);
        });
      });
      document.querySelectorAll(".modal-bg").forEach(function(ov) {
        ov.addEventListener("click", function(e) {
          if (e.target === ov) ov.classList.add("hidden");
        });
      });
    }
  };

  /* ===== Tabs ===== */
  var Tabs = {
    _init: function() {
      document.querySelectorAll(".tabs").forEach(function(g) {
        g.querySelectorAll(".tab-btn").forEach(function(b) {
          b.addEventListener("click", function() {
            g.querySelectorAll(".tab-btn").forEach(function(x) { x.classList.remove("active"); });
            b.classList.add("active");
            document.querySelectorAll("[data-tab-content]").forEach(function(el) {
              el.classList.toggle("hidden", el.dataset.tabContent !== b.dataset.tab);
            });
          });
        });
      });
    }
  };

  /* ===== Expose ===== */
  window.$ = {
    get: doGet,
    post: doPost,
    put: doPut,
    del: doDel,
    extractList: extractList,
    login: doLogin,
    logout: doLogout,
    loggedIn: isLoggedIn,
    hasRole: hasRole,
    getUser: getUser,
    get token() { return token; },
    get user() { return user; }
  };

  window.Toast = Toast;
  window.Chat  = Chat;
  window.Modal = Modal;
  window.Tabs  = Tabs;

  /* ===== DOM Ready ===== */
  document.addEventListener("DOMContentLoaded", function() {
    Modal._init();
    Tabs._init();
  });

})();