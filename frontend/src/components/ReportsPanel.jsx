export default function ReportsPanel({ reports }) {
  return (
    <div>
      <h3>Operational Performance Analytics</h3>
      
      <div className="flex-container" style={{ marginBottom: '25px' }}>
        <div className="report-summary-box">
          <h2>{reports.averageTime.toFixed(1)} mins</h2>
          <p>Average Delivery Travel Time</p>
        </div>
        <div className="report-summary-box" style={{ backgroundColor: '#27ae60' }}>
          <h2>{reports.topAgents.length}</h2>
          <p>Active Top Performers Count</p>
        </div>
        <div className="report-summary-box" style={{ backgroundColor: '#c0392b' }}>
          <h2>{reports.delayedDeliveries.length}</h2>
          <p>Late Orders Triggered</p>
        </div>
      </div>

      <h4>Top performing Agents</h4>
      <table className="data-table">
        <thead>
          <tr>
            <th>Agent Name</th>
            <th>Avg Rating</th>
            <th>Completed Deliveries</th>
            <th>On-Time Count</th>
            <th>On-Time Rate</th>
          </tr>
        </thead>
        <tbody>
          {reports.topAgents.map(a => (
            <tr key={a.agentId}>
              <td>{a.name}</td>
              <td>{a.currentRating.toFixed(1)} / 5.0</td>
              <td>{a.completedDeliveriesCount}</td>
              <td>{a.onTimeDeliveriesCount}</td>
              <td style={{ color: '#27ae60', fontWeight: 'bold' }}>{a.onTimeRate.toFixed(1)}%</td>
            </tr>
          ))}
        </tbody>
      </table>

      <h4 style={{ marginTop: '20px' }}>Delayed Orders Analysis Log</h4>
      <table className="data-table">
        <thead>
          <tr>
            <th>Order ID</th>
            <th>Restaurant Name</th>
            <th>Agent</th>
            <th>Est Duration</th>
            <th>Act Duration</th>
            <th>Delay Duration</th>
          </tr>
        </thead>
        <tbody>
          {reports.delayedDeliveries.map((d, idx) => (
            <tr key={idx}>
              <td>{d.orderId}</td>
              <td>{d.restaurantName}</td>
              <td>{d.agentName}</td>
              <td>{d.estimatedMinutes} mins</td>
              <td>{d.actualMinutes} mins</td>
              <td style={{ color: '#c0392b', fontWeight: 'bold' }}>{d.delayMinutes} mins Late</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
