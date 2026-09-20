import { useState, useEffect } from 'react';
import ServerCard from './ServerCard';

function Dashboard() {
  const [servers, setServers] = useState([]);
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    const fetchServers = () => {
      fetch('http://localhost:8080/api/servers')
        .then(response => response.json())
        .then(data => setServers(data))
        .catch(error => console.error('Failed to fetch servers:', error));
    };

    const fetchAlerts = () => {
      fetch('http://localhost:8080/api/alerts')
        .then(response => response.json())
        .then(data => setAlerts(data))
        .catch(error => console.error('Failed to fetch alerts:', error));
    };

    fetchServers();
    fetchAlerts();

    const intervalId = setInterval(() => {
      fetchServers();
      fetchAlerts();
    }, 10000);

    return () => clearInterval(intervalId);
  }, []);

  const upCount = servers.filter(s => s.status === 'UP').length;
  const downCount = servers.length - upCount;

  return (
    <div style={{ minHeight: '100vh', display: 'flex', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: '1100px', padding: '2rem' }}>
        <h1 style={{ color: 'var(--color-primary)' }}>Server Monitoring Dashboard</h1>

        <div style={{ display: 'flex', gap: '2rem', marginBottom: '2rem' }}>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            Total Servers: <span style={{ color: 'var(--color-text)', fontWeight: 'bold' }}>{servers.length}</span>
          </div>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            Up: <span style={{ color: 'var(--color-success)', fontWeight: 'bold' }}>{upCount}</span>
          </div>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            Down: <span style={{ color: 'var(--color-critical)', fontWeight: 'bold' }}>{downCount}</span>
          </div>
        </div>

        <div
          style={{
            backgroundColor: 'var(--color-card)',
            border: '1px solid var(--color-border)',
            borderRadius: '8px',
            padding: '1rem 1.5rem',
            marginBottom: '2rem',
            color: 'var(--color-text-secondary)',
            fontSize: '0.9rem'
          }}
        >
          {alerts.length === 0 ? (
            'No active alerts'
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              {alerts.map(alert => (
                <div
                  key={alert.id}
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    color: 'var(--color-critical)'
                  }}
                >
                  <span>
                    <strong>{alert.hostname}</strong> — {alert.message}
                  </span>
                  <span style={{ color: 'var(--color-text-secondary)', fontSize: '0.8rem' }}>
                    {new Date(alert.triggered_at).toLocaleTimeString()}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))',
            gap: '1.25rem'
          }}
        >
          {servers.map(server => (
            <ServerCard key={server.id} server={server} />
          ))}
        </div>
      </div>
    </div>
  );
}

export default Dashboard;