import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { LineChart, Line, ResponsiveContainer } from 'recharts';

function getColorForPercent(percent) {
  if (percent < 50) return 'var(--color-success)';
  if (percent < 80) return 'var(--color-warning)';
  return 'var(--color-critical)';
}

function ServerCard({ server }) {
  const [metric, setMetric] = useState(null);
  const [lastFetched, setLastFetched] = useState(null);
  const [history, setHistory] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchMetric = () => {
      fetch(`http://localhost:8080/api/servers/${server.id}/metrics`)
        .then(response => {
          if (!response.ok) {
            throw new Error('No metrics yet');
          }
          return response.json();
        })
        .then(data => {
          setMetric(data);
          setLastFetched(new Date());
        })
        .catch(error => setMetric(null));
    };

    const fetchHistory = () => {
      fetch(`http://localhost:8080/api/servers/${server.id}/metrics/history`)
        .then(response => response.json())
        .then(data => setHistory(data.slice(-15))) // last 15 points only
        .catch(error => setHistory([]));
    };

    fetchMetric();
    fetchHistory();
    const intervalId = setInterval(() => {
      fetchMetric();
      fetchHistory();
    }, 10000);

    return () => clearInterval(intervalId);
  }, [server.id]);

  return (
    <div
      onClick={() => navigate(`/servers/${server.id}`)}
      style={{
        backgroundColor: 'var(--color-card)',
        border: '1px solid var(--color-border)',
        borderRadius: '8px',
        padding: '1.5rem',
        minWidth: '220px',
        cursor: 'pointer'
      }}
    >
      <h3 style={{ margin: '0 0 0.5rem 0', color: 'var(--color-text)' }}>{server.hostname}</h3>
      <span
        style={{
          color: server.status === 'UP' ? 'var(--color-success)' : 'var(--color-critical)',
          fontWeight: 'bold'
        }}
      >
        ● {server.status}
      </span>

      {metric ? (
        <div style={{ marginTop: '1rem', fontSize: '0.9rem' }}>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            CPU: <span style={{ color: getColorForPercent(metric.cpu_percent), fontWeight: 'bold' }}>{metric.cpu_percent}%</span>
          </div>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            RAM: <span style={{ color: getColorForPercent(metric.ram_percent), fontWeight: 'bold' }}>{metric.ram_percent}%</span>
          </div>
          <div style={{ color: 'var(--color-text-secondary)' }}>
            Disk: <span style={{ color: getColorForPercent(metric.disk_percent), fontWeight: 'bold' }}>{metric.disk_percent}%</span>
          </div>

          {history.length > 1 && (
            <div style={{ height: '40px', marginTop: '0.75rem' }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={history}>
                  <Line
                    type="monotone"
                    dataKey="cpu_percent"
                    stroke="var(--color-primary)"
                    strokeWidth={2}
                    dot={false}
                    isAnimationActive={false}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}

          {lastFetched && (
            <div style={{ marginTop: '0.5rem', color: 'var(--color-text-secondary)', fontSize: '0.75rem' }}>
              Updated: {lastFetched.toLocaleTimeString()}
            </div>
          )}
        </div>
      ) : (
        <div style={{ marginTop: '1rem', color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
          No metrics yet
        </div>
      )}
    </div>
  );
}

export default ServerCard;