export default function AgentPanel({
  currentUser,
  assignments,
  pickupOrder,
  deliverOrder
}) {
  return (
    <div>
      <h3>My Active Assignments</h3>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Assignment ID</th>
              <th>Order ID</th>
              <th>Assigned At</th>
              <th>Picked Up At</th>
              <th>Status</th>
              <th>Action Actions</th>
            </tr>
          </thead>
          <tbody>
            {assignments.filter(a => a.deliveryAgentId === currentUser.id && a.deliveryStatus !== 'DELIVERED').map(a => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{a.orderId}</td>
                <td>{a.assignedAt}</td>
                <td>{a.pickedUpAt || "Pending Pickup"}</td>
                <td>
                  <span style={{ fontWeight: 'bold', color: '#d35400' }}>{a.deliveryStatus}</span>
                </td>
                <td>
                  {a.deliveryStatus === 'ASSIGNED' && (
                    <button className="btn btn-warning" onClick={() => pickupOrder(a.id)}>Confirm Order Pickup</button>
                  )}
                  {a.deliveryStatus === 'PICKED_UP' && (
                    <button className="btn btn-success" onClick={() => deliverOrder(a.id)}>Confirm Delivered</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h3>Completed Deliveries History</h3>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Assignment ID</th>
              <th>Order ID</th>
              <th>Assigned At</th>
              <th>Delivered At</th>
              <th>Rating Received</th>
            </tr>
          </thead>
          <tbody>
            {assignments.filter(a => a.deliveryAgentId === currentUser.id && a.deliveryStatus === 'DELIVERED').map(a => (
              <tr key={a.id}>
                <td>{a.id}</td>
                <td>{a.orderId}</td>
                <td>{a.assignedAt}</td>
                <td>{a.deliveredAt}</td>
                <td>{a.deliveryRating ? `${a.deliveryRating} / 5.0` : "No rating"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
