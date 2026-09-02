import { useState } from 'react'
import './App.css'

const API_BASE = 'http://localhost:9091'

function parseErrorMessage(responseText) {
  if (!responseText) {
    return 'Failed to shorten URL'
  }

  try {
    const parsed = JSON.parse(responseText)

    if (typeof parsed === 'string') return parsed
    if (parsed.error) return parsed.error
    if (parsed.url) return parsed.url
    if (parsed.message) return parsed.message

    return JSON.stringify(parsed)
  } catch {
    return responseText
  }
}

function validateUrlInput(value) {
  const trimmed = value.trim()

  if (!trimmed) {
    return 'Please enter a URL to shorten.'
  }

  if (!/^https?:\/\//i.test(trimmed)) {
    return 'URL must start with http:// or https://'
  }

  const normalized = trimmed.toLowerCase()
  if (
    normalized.includes('localhost:9091') ||
    normalized.includes('127.0.0.1:9091') ||
    normalized.includes('0.0.0.0:9091')
  ) {
    return 'Please use a real external URL. This app cannot shorten a URL that points back to itself.'
  }

  return ''
}

async function shortenUrl(url) {
  const response = await fetch(`${API_BASE}/api/shorten`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ url }),
  })

  const text = await response.text()

  if (!response.ok) {
    throw new Error(parseErrorMessage(text))
  }

  return text
}

async function fetchAnalytics(shortCode) {
  const response = await fetch(`${API_BASE}/api/analytics/${shortCode}`)

  if (!response.ok) {
    throw new Error('Unable to load analytics for this short code')
  }

  return response.json()
}

function App() {
  const [url, setUrl] = useState('https://example.com/very/long/path')
  const [shortCode, setShortCode] = useState('')
  const [analytics, setAnalytics] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const shortUrl = shortCode ? `${API_BASE}/${shortCode}` : ''

  const handleShorten = async (event) => {
    event.preventDefault()
    setLoading(true)
    setError('')
    setMessage('')

    const validationError = validateUrlInput(url)
    if (validationError) {
      setError(validationError)
      setLoading(false)
      return
    }

    try {
      const createdCode = await shortenUrl(url)
      setShortCode(createdCode)
      setMessage(`Short code created: ${createdCode}`)
      setAnalytics(null)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleAnalytics = async () => {
    if (!shortCode) {
      setError('Create a short URL first')
      return
    }

    try {
      const data = await fetchAnalytics(shortCode)
      setAnalytics(data)
      setError('')
    } catch (err) {
      setError(err.message)
    }
  }

  const handleRedirect = () => {
    if (!shortUrl) {
      setError('No short link available yet')
      return
    }

    window.open(shortUrl, '_blank', 'noopener,noreferrer')
  }

  return (
    <main className="app-shell">
      <section className="panel">
        <p className="eyebrow">URL Shortener Demo</p>
        <h1>Turn long links into short ones</h1>

        <form onSubmit={handleShorten} className="shortener-form">
          <label htmlFor="long-url">Original URL</label>
          <input
            id="long-url"
            type="text"
            value={url}
            onChange={(event) => setUrl(event.target.value)}
            placeholder="https://example.com/very/long/path"
          />
          <p className="field-hint">
            Use a real external URL. Avoid links that point back to localhost or this app.
          </p>

          <button type="submit" disabled={loading}>
            {loading ? 'Creating...' : 'Create short URL'}
          </button>
        </form>

        {message && <div className="success-box">{message}</div>}
        {error && <div className="error-box">{error}</div>}

        {shortCode && (
          <div className="result-box">
            <p className="label">Short link</p>
            <a href={shortUrl} target="_blank" rel="noreferrer">
              {shortUrl}
            </a>

            <div className="actions">
              <button type="button" onClick={handleRedirect} className="secondary">
                Open redirect
              </button>
              <button type="button" onClick={handleAnalytics} className="secondary">
                View analytics
              </button>
            </div>
          </div>
        )}
      </section>

      <section className="panel analytics-panel">
        <h2>Analytics</h2>

        {!analytics ? (
          <p className="empty-state">
            Create a short URL and fetch analytics to view data here.
          </p>
        ) : (
          <div className="analytics-grid">
            <div>
              <span className="metric-label">Short code</span>
              <strong>{analytics.shortCode}</strong>
            </div>
            <div>
              <span className="metric-label">Clicks</span>
              <strong>{analytics.clickCount}</strong>
            </div>
            <div className="wide">
              <span className="metric-label">Original URL</span>
              <strong>{analytics.originalUrl}</strong>
            </div>
            <div className="wide">
              <span className="metric-label">Created</span>
              <strong>{new Date(analytics.createdAtEpochMillis).toLocaleString()}</strong>
            </div>
          </div>
        )}
      </section>
    </main>
  )
}

export default App
