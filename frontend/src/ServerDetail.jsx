import { useParams, Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { getColorForPercent } from './colors';

function StatCard({ label, value }) {
  return (
    <div
      style={{
        backgroundColor: 'var(--color-card)',
        border: '1px solid var(--color-border)',
        borderRadius: '8px',
        padding: '1rem 1.5rem',
        flex: '1',
        minWidth: '150px'
      }}
    >
      <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.8rem', marginBottom: '0.25rem' }}>
        {label}
      </div>
      <div style={{ color: 'var(--color-text)', fontSize: '1.4rem', fontWeight: 'bold' }}>
        {value}
      </div>
    </div>
  );
}

function UsageBar({ label, usedLabel, totalLabel, percent }) {
  const color = getColorForPercent(percent);
  return (
    <div style={{ flex: '1', minWidth: '220px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.4rem' }}>
        <span style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>{label}</span>
        <span style={{ color, fontWeight: 'bold', fontSize: '0.9rem' }}>{percent.toFixed(0)}%</span>
      </div>
      <div
        style={{
          height: '8px',
          borderRadius: '4px',
          backgroundColor: 'var(--color-border)',
          overflow: 'hidden'
        }}
      >
        <div
          style={{
            height: '100%',
            width: `${Math.min(percent, 100)}%`,
            backgroundColor: color,
            borderRadius: '4px',
            transition: 'width 0.4s ease'
          }}
        />
      </div>
      <div style={{ color: 'var(--color-text-secondary)', fontSize: '0.8rem', marginTop: '0.35rem' }}>
        {usedLabel} / {totalLabel}
      </div>
    </div>
  );
}

function ServerDetail() {
  const { id } = useParams();
  const [history, setHistory] = useState([]);
  const [latestMetric, setLatestMetric] = useState(null);
  const [range, setRange] = useState('24h');

  useEffect(() => {
    fetch(`http://localhost:8080/api/servers/${id}/metrics`)
      .then(res => {
        if (!res.ok) throw new Error('No latest metric');
        return res.json();
      })
      .then(data => setLatestMetric(data))
      .catch(() => setLatestMetric(null));
  }, [id]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/servers/${id}/metrics/history?range=${range}`)
      .then(res => {
        if (!res.ok) throw new Error('Failed to fetch history');
        return res.json();
      })
      .then(data => setHistory(data))
      .catch(() => setHistory([]));
  }, [id, range]);

  return (
    <div style={{ minHeight: '100vh', display: 'flex', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: '1100px', padding: '2rem', color: 'var(--color-text)' }}>
        <Link to="/" style={{ color: 'var(--color-primary)', textDecoration: 'none' }}>
          ← Back to Dashboard
        </Link>
        <h1>Server Detail</h1>

        <div style={{ marginBottom: '1.5rem' }}>
  <select
    value={range}
    onChange={(e) => setRange(e.target.value)}
    style={{
      padding: '0.4rem 0.8rem',
      borderRadius: '6px',
      border: '1px solid var(--color-border)',
      backgroundColor: 'var(--color-card)',
      color: 'var(--color-text)',
      cursor: 'pointer',
      fontSize: '0.9rem'
    }}
  >
    <option value="24h">Last 24 hours</option>
    <option value="7d">Last 7 days</option>
    <option value="30d">Last 30 days</option>
  </select>
</div>

        {latestMetric && (
          <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', flexWrap: 'wrap' }}>
            <StatCard label="Uptime" value={`${latestMetric.uptime_hours?.toFixed(1)} hrs`} />
            <StatCard label="Network Sent" value={`${latestMetric.network_sent_mb?.toFixed(1)} MB`} />
            <StatCard label="Network Received" value={`${latestMetric.network_received_mb?.toFixed(1)} MB`} />
            <StatCard label="Processes" value={latestMetric.process_count} />
          </div>
        )}

        {history.length === 0 ? (
          <p style={{ color: 'var(--color-text-secondary)' }}>No metric history yet.</p>
        ) : (
          <>
            <h3 style={{ color: 'var(--color-text-secondary)', fontWeight: 'normal', marginBottom: '0.5rem' }}>
              CPU / RAM / Disk Usage
            </h3>
            <ResponsiveContainer width="100%" height={350}>
              <LineChart data={history}>
                <CartesianGrid stroke="var(--color-border)" />
                <XAxis
                  dataKey="timestamp"
                  tickFormatter={(value) => new Date(value).toLocaleTimeString()}
                  stroke="var(--color-text-secondary)"
                />
                <YAxis stroke="var(--color-text-secondary)" />
                <Tooltip
                  labelFormatter={(value) => new Date(value).toLocaleString()}
                  contentStyle={{ backgroundColor: 'var(--color-card)', border: '1px solid var(--color-border)' }}
                />
                <Legend />
                <Line type="monotone" dataKey="cpu_percent" stroke="var(--color-primary)" name="CPU %" dot={false} />
                <Line type="monotone" dataKey="ram_percent" stroke="var(--color-success)" name="RAM %" dot={false} />
                <Line type="monotone" dataKey="disk_percent" stroke="var(--color-warning)" name="Disk %" dot={false} />
              </LineChart>
            </ResponsiveContainer>

            <h3 style={{ color: 'var(--color-text-secondary)', fontWeight: 'normal', margin: '2rem 0 0.5rem 0' }}>
              Network Activity
            </h3>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={history}>
                <CartesianGrid stroke="var(--color-border)" />
                <XAxis
                  dataKey="timestamp"
                  tickFormatter={(value) => new Date(value).toLocaleTimeString()}
                  stroke="var(--color-text-secondary)"
                />
                <YAxis stroke="var(--color-text-secondary)" />
                <Tooltip
                  labelFormatter={(value) => new Date(value).toLocaleString()}
                  contentStyle={{ backgroundColor: 'var(--color-card)', border: '1px solid var(--color-border)' }}
                />
                <Legend />
                <Line type="monotone" dataKey="network_sent_mb" stroke="var(--color-primary)" name="Sent (MB)" dot={false} />
                <Line type="monotone" dataKey="network_received_mb" stroke="var(--color-warning)" name="Received (MB)" dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </>
        )}

        {latestMetric && latestMetric.ram_total_gb && (
          <>
            <h3 style={{ color: 'var(--color-text-secondary)', fontWeight: 'normal', margin: '2rem 0 0.5rem 0' }}>
              Storage Details
            </h3>
            <div
              style={{
                backgroundColor: 'var(--color-card)',
                border: '1px solid var(--color-border)',
                borderRadius: '8px',
                padding: '1.5rem',
                marginBottom: '2rem',
                display: 'flex',
                gap: '2.5rem',
                flexWrap: 'wrap'
              }}
            >
              <UsageBar
                label="Memory"
                usedLabel={`${latestMetric.ram_used_gb?.toFixed(2)} GB`}
                totalLabel={`${latestMetric.ram_total_gb?.toFixed(2)} GB`}
                percent={(latestMetric.ram_used_gb / latestMetric.ram_total_gb) * 100}
              />
              <UsageBar
                label="Disk"
                usedLabel={`${latestMetric.disk_used_gb?.toFixed(2)} GB`}
                totalLabel={`${latestMetric.disk_total_gb?.toFixed(2)} GB`}
                percent={(latestMetric.disk_used_gb / latestMetric.disk_total_gb) * 100}
              />
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default ServerDetail;