export default function ManagerPanel({
  currentUser,
  restaurants,
  orders,
  assignments,
  users,
  recommendations,
  selectedOrderRec,
  restForm,
  setRestForm,
  foodForm,
  setFoodForm,
  handleAddRestaurant,
  handleAddFoodItem,
  updateToPreparing,
  getAgentRecommendations,
  assignAgent,
  stepSimulation
}) {
  const myRestaurant = restaurants.find(r => r.managerId === currentUser.id);

  return (
    <div>
      {!myRestaurant ? (
        <div className="form-panel">
          <h3>Setup Your Restaurant</h3>
          <p>You do not have a restaurant registered yet. Register your business below:</p>
          <form onSubmit={handleAddRestaurant}>
            <div className="form-grid">
              <div className="form-group">
                <label>Restaurant Name:</label>
                <input type="text" placeholder="Italian Pizzeria" value={restForm.name} onChange={e => setRestForm({...restForm, name: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Average Prep Time (mins):</label>
                <input type="number" value={restForm.averagePreparationTime} onChange={e => setRestForm({...restForm, averagePreparationTime: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Base Rating:</label>
                <input type="number" step="0.1" min="1" max="5" value={restForm.rating} onChange={e => setRestForm({...restForm, rating: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Location Area Zone:</label>
                <input type="text" value={restForm.location} onChange={e => setRestForm({...restForm, location: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Grid Coord X (0-100):</label>
                <input type="number" min="0" max="100" value={restForm.x} onChange={e => setRestForm({...restForm, x: e.target.value})} required />
              </div>
              <div className="form-group">
                <label>Grid Coord Y (0-100):</label>
                <input type="number" min="0" max="100" value={restForm.y} onChange={e => setRestForm({...restForm, y: e.target.value})} required />
              </div>
            </div>
            <button type="submit" className="btn btn-success">Register Restaurant</button>
          </form>
        </div>
      ) : (
        <div>
          <div className="flex-container">
            <div className="form-panel col-half">
              <h3>Restaurant Configuration</h3>
              <table className="data-table">
                <tbody>
                  <tr><td><strong>Name:</strong></td><td>{myRestaurant.name}</td></tr>
                  <tr><td><strong>Location Zone:</strong></td><td>{myRestaurant.location}</td></tr>
                  <tr><td><strong>Coordinates:</strong></td><td>({myRestaurant.x}, {myRestaurant.y})</td></tr>
                  <tr><td><strong>Rating:</strong></td><td>{myRestaurant.rating} / 5.0</td></tr>
                  <tr>
                    <td><strong>Prep Delays Count:</strong></td>
                    <td style={{ fontWeight: 'bold' }}>
                      {myRestaurant.delayCount} violations
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div className="form-panel col-half">
              <h3>Add Menu Item to Restaurant</h3>
              <form onSubmit={e => handleAddFoodItem(e, myRestaurant.id)}>
                <div className="form-grid">
                  <div className="form-group">
                    <label>Item Name:</label>
                    <input type="text" placeholder="French Fries" value={foodForm.name} onChange={e => setFoodForm({...foodForm, name: e.target.value})} required />
                  </div>
                  <div className="form-group">
                    <label>Category:</label>
                    <select value={foodForm.category} onChange={e => setFoodForm({...foodForm, category: e.target.value})}>
                      <option value="Main Course">Main Course</option>
                      <option value="Beverages">Beverages</option>
                      <option value="Starters">Starters</option>
                      <option value="Desserts">Desserts</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Price ($):</label>
                    <input type="number" step="0.01" placeholder="4.99" value={foodForm.price} onChange={e => setFoodForm({...foodForm, price: e.target.value})} required />
                  </div>
                </div>
                <button type="submit" className="btn btn-warning">Add Menu Item</button>
              </form>
            </div>
          </div>

          <h3>Incoming Customer Orders</h3>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Customer Name</th>
                  <th>Total Amount</th>
                  <th>Status</th>
                  <th>Address</th>
                  <th>Coordinates</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {orders.filter(o => o.restaurantId === myRestaurant.id).map(o => (
                  <tr key={o.id}>
                    <td>{o.id}</td>
                    <td>{o.customerName}</td>
                    <td>${o.totalAmount}</td>
                    <td>
                      <span style={{ fontWeight: 'bold' }}>{o.orderStatus}</span>
                    </td>
                    <td>{o.deliveryAddress}</td>
                    <td>({o.deliveryX}, {o.deliveryY})</td>
                    <td>
                      {o.orderStatus === 'PLACED' && (
                        <button className="btn btn-success" onClick={() => updateToPreparing(o.id)}>Start Preparing</button>
                      )}
                      {o.orderStatus === 'PREPARING' && (
                        <button className="btn btn-primary" onClick={() => getAgentRecommendations(o.id)}>Select Delivery Agent</button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {recommendations.length > 0 && (
            <div className="form-panel" style={{ marginTop: '20px' }}>
              <h3>Agent Recommendations for Order ID: {selectedOrderRec}</h3>
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Agent ID</th>
                    <th>Name</th>
                    <th>Rating</th>
                    <th>Distance to Your Restaurant</th>
                    <th>Score (Rules Engine)</th>
                    <th>Assign</th>
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
                      <td style={{ fontWeight: 'bold' }}>{rec.score.toFixed(1)}</td>
                      <td>
                        <button className="btn" onClick={() => assignAgent(rec.agentId)}>Assign Agent</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          <div className="coordinate-grid-map">
            <h3>Live Delivery Coordination Map</h3>
            <div style={{ marginBottom: '15px' }}>
              <button className="btn btn-danger" onClick={stepSimulation}>Trigger Coordinates Movement Step (Tick)</button>
            </div>
            <div className="grid-canvas">
              <div className="grid-dot restaurant" style={{ left: `${myRestaurant.x}%`, top: `${100 - myRestaurant.y}%` }}>
                <span className="grid-label">{myRestaurant.name} ({myRestaurant.x}, {myRestaurant.y})</span>
              </div>

              {users.filter(u => u.role === 'DELIVERY_AGENT').map(a => (
                <div key={a.id} className="grid-dot agent" style={{ left: `${a.x}%`, top: `${100 - a.y}%` }}>
                  <span className="grid-label">{a.name} ({a.x}, {a.y})</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
