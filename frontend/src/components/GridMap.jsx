export default function GridMap({
  users,
  restaurants,
  stepSimulation
}) {
  return (
    <div className="coordinate-grid-map">
      <h3>Simulated Coordinate Grid Map</h3>
      <p>Displays Locations of Customers (Blue), Restaurants (Green), and Delivery Agents (Red).</p>
      
      <div style={{ marginBottom: '15px' }}>
        <button className="btn btn-danger" onClick={stepSimulation}>Trigger Coordinates Movement Step (Tick)</button>
      </div>

      <div className="grid-canvas">
        {users.filter(u => u.role === 'CUSTOMER').map(c => (
          <div key={c.id} className="grid-dot customer" style={{ left: `${c.x}%`, top: `${100 - c.y}%` }}>
            <span className="grid-label">{c.name} ({c.x}, {c.y})</span>
          </div>
        ))}

        {restaurants.map(r => (
          <div key={r.id} className="grid-dot restaurant" style={{ left: `${r.x}%`, top: `${100 - r.y}%` }}>
            <span className="grid-label">{r.name} ({r.x}, {r.y})</span>
          </div>
        ))}

        {users.filter(u => u.role === 'DELIVERY_AGENT').map(a => (
          <div key={a.id} className="grid-dot agent" style={{ left: `${a.x}%`, top: `${100 - a.y}%` }}>
            <span className="grid-label">{a.name} ({a.x}, {a.y})</span>
          </div>
        ))}
      </div>
    </div>
  );
}
