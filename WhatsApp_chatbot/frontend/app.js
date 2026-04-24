const API_BASE_URL = "http://localhost:8080";

const chatWindow = document.getElementById("chatWindow");
const messageInput = document.getElementById("messageInput");
const sendButton = document.getElementById("sendButton");
const fromInput = document.getElementById("fromUser");
const errorBox = document.getElementById("errorBox");

function appendMessage(text, type) {
  // type: "user" | "bot"
  const row = document.createElement("div");
  row.className = `message-row ${type}`;

  const bubble = document.createElement("div");
  bubble.className = `message-bubble ${type}`;
  bubble.textContent = text;

  row.appendChild(bubble);
  chatWindow.appendChild(row);

  // scroll to bottom
  chatWindow.scrollTop = chatWindow.scrollHeight;
}

function setError(message) {
  if (!message) {
    errorBox.style.display = "none";
    errorBox.textContent = "";
  } else {
    errorBox.style.display = "block";
    errorBox.textContent = message;
  }
}

async function sendMessage() {
  setError("");

  const text = messageInput.value.trim();
  const from = fromInput.value.trim();

  if (!from) {
    setError("Please enter your number/name (from field).");
    return;
  }
  if (!text) {
    return; // ignore empty messages
  }

  appendMessage(text, "user");
  messageInput.value = "";
  sendButton.disabled = true;

  try {
    const response = await fetch(`${API_BASE_URL}/webhook`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        from,
        message: text,
      }),
    });

    if (!response.ok) {
      const body = await response.text();
      throw new Error(`Server error ${response.status}: ${body}`);
    }

    const data = await response.json();
    appendMessage(data.reply ?? "(no reply)", "bot");
  } catch (err) {
    console.error(err);
    setError("Failed to send message or receive reply. Check backend and console.");
  } finally {
    sendButton.disabled = false;
  }
}

sendButton.addEventListener("click", sendMessage);

messageInput.addEventListener("keydown", (e) => {
  if (e.key === "Enter" && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
});