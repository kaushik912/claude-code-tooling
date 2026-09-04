import { useState } from 'react'

function App() {
  const [text, setText] = useState('')
  const [message, setMessage] = useState('')

  async function sayHello() {
    const res = await fetch(`http://localhost:3001/hello?text=${encodeURIComponent(text)}`)
    const data = await res.json()
    setMessage(data.message)
  }

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Herdr Demo</h1>
      <input
        type="text"
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Enter your name"
      />
      <button onClick={sayHello}>Say Hello</button>
      {message && <p>{message}</p>}
    </div>
  )
}

export default App
