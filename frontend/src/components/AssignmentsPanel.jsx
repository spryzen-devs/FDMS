export default function AssignmentsPanel({
  orders,
  assignments,
  recommendations,
  selectedOrderRec,
  setSelectedOrderRec,
  getAgentRecommendations,
  assignAgent,
  pickupOrder,
  deliverOrder
}) {
  return (
    <div>
      <div className="form-panel">
        <h3>Recommend Agent for Order</h3>
        <div className="form-grid" style={{ alignItems: 'flex-end' }}>
          <div className="form-group">
            <label>Select Active PREPARING Order:</label>
            <select value={selectedOrderRec} onChange={e => setSelectedOrderRec(e.target.value)}>
              <option value="">-- Select Order --</option>
              {orders.filter(o => o.orderStatus === 'PREPARING').map(o => (
                <option key={o.id} value={o.id}>Order ID: {o.id} (from: {o.restaurantName})</option>
              ))}
            </select>
          </div>
          <button className="btn btn-primary" onClick={() => getAgentRecommendations(selectedOrderRec)}>Get Recommended Agents</button>
        </div>

        {recommendations.length > 0 && (
          <div style={{ marginTop: '20px' }}>
            <h4>Recommended Agents (Sorted by Scoring Engine)</h4>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Agent ID</th>
                  <th>Name</th>
                  <th>Rating</th>
                  <th>Distance to Restaurant</th>
                  <th>Score</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {recommendations.map((rec, idx) => (
                  <tr key={rec.agentId}>
                    <td>{idx + 1}</td>
                    <td>{rec.agentId}</td>
                    <td>{rec.agentName}</td>
                    <td>{rec.rating} / 5.0</td>
                    <td>{rec.distance.toFixed(1)} units</td>
                    <td style={{ fontWeight: 'bold', color: '#27ae60' }}>{rec.score.toFixed(1)}</td>
                    <td>
                      <button className="btn btn-success" onClick={() => assignAgent(rec.agentId)}>Assign Agent</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <h3>Active Assignments Tracking</h3>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Assignment ID</th>
              <th>Order ID</th>
              <th>Delivery Agent</th>
              <th>Assigned At</th>
              <th>Picked Up At</th>
              <th>Delivered At</th>
              <th>Delivery Status</th>
              <th>Rating</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {assignments.map(a => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{a.orderId}</td>
                <td>{a.deliveryAgentName} (ID: {a.deliveryAgentId})</td>
                <td>{a.assignedAt}</td>
                <td>{a.pickedUpAt || "Pending"}</td>
                <td>{a.deliveredAt || "Pending"}</td>
                <td>
                  <span style={{ 
                    fontWeight: 'bold',
                    color: a.deliveryStatus === 'DELIVERED' ? '#27ae60' : a.deliveryStatus === 'PICKED_UP' ? '#d35400' : '#2980b9'
                  }}>{a.deliveryStatus}</span>
                </td>
                <td>{a.deliveryRating ? `${a.deliveryRating} / 5.0` : "N/A"}</td>
                <td>
                  {a.deliveryStatus === 'ASSIGNED' && (
                    <button className="btn btn-warning" onClick={() => pickupOrder(a.id)}>Confirm Pickup</button>
                  )}
                  {a.deliveryStatus === 'PICKED_UP' && (
                    <button className="btn btn-success" onClick={() => deliverOrder(a.id)}>Confirm Delivery</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
