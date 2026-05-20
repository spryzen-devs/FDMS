import { useState } from 'react';

export default function CustomerPanel({
  currentUser,
  restaurants,
  orders,
  restaurantMenu,
  orderForm,
  setOrderForm,
  orderItemsList,
  addOrderItemLocally,
  handleRestaurantChange,
  handlePlaceOrder,
  assignments,
  customerConfirmAndRate
}) {
  return (
    <div>
      <div className="form-panel">
        <h3>Place Customer Food Order</h3>
        <form onSubmit={handlePlaceOrder}>
          <div className="form-grid">
            <div className="form-group">
              <label>Select Target Restaurant:</label>
              <select value={orderForm.restaurantId} onChange={e => handleRestaurantChange(e.target.value)} required>
                <option value="">-- Select Restaurant --</option>
                {restaurants.map(r => (
                  <option key={r.id} value={r.id}>{r.name} (Location: {r.location})</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Delivery Address:</label>
              <input type="text" placeholder="e.g. 789 Pine Ave" value={orderForm.deliveryAddress} onChange={e => setOrderForm({...orderForm, deliveryAddress: e.target.value})} required />
            </div>

            <div className="form-group">
              <label>Delivery X (0-100):</label>
              <input type="number" min="0" max="100" value={orderForm.deliveryX} onChange={e => setOrderForm({...orderForm, deliveryX: e.target.value})} required />
            </div>

            <div className="form-group">
              <label>Delivery Y (0-100):</label>
              <input type="number" min="0" max="100" value={orderForm.deliveryY} onChange={e => setOrderForm({...orderForm, deliveryY: e.target.value})} required />
            </div>
          </div>

          {orderForm.restaurantId && (
            <div style={{ border: '1px solid #ddd', padding: '10px', margin: '15px 0', backgroundColor: '#fff', borderRadius: '4px' }}>
              <h4>Select Food Items to Order</h4>
              <div className="form-grid">
                <div className="form-group">
                  <label>Select Food Item:</label>
                  <select value={orderForm.foodItemId} onChange={e => setOrderForm({...orderForm, foodItemId: e.target.value})}>
                    <option value="">-- Select Item --</option>
                    {restaurantMenu.map(item => (
                      <option key={item.id} value={item.id}>{item.name} (${item.price.toFixed(2)})</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Quantity:</label>
                  <input type="number" min="1" value={orderForm.quantity} onChange={e => setOrderForm({...orderForm, quantity: e.target.value})} />
                </div>
              </div>
              <button type="button" className="btn btn-info" onClick={addOrderItemLocally}>Add Item to List</button>

              {orderItemsList.length > 0 && (
                <table className="data-table" style={{ marginTop: '10px' }}>
                  <thead>
                    <tr>
                      <th>Item Name</th>
                      <th>Qty</th>
                      <th>Price</th>
                    </tr>
                  </thead>
                  <tbody>
                    {orderItemsList.map((item, idx) => (
                      <tr key={idx}>
                        <td>{item.foodItemName}</td>
                        <td>{item.quantity}</td>
                        <td>${item.price}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}

          <button type="submit" className="btn btn-primary">Place Order</button>
        </form>
      </div>

      <h3>My Placed Order History</h3>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Restaurant</th>
              <th>Total Amount</th>
              <th>Status</th>
              <th>Address</th>
              <th>Coordinates</th>
              <th>Estimated Delivery</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {orders.filter(o => o.customerId === currentUser.id).map(o => {
              const assignment = assignments.find(a => a.orderId === o.id);
              return (
                <tr key={o.id}>
                  <td>{o.id}</td>
                  <td>{o.restaurantName}</td>
                  <td>${o.totalAmount}</td>
                  <td>
                    <span style={{ fontWeight: 'bold' }}>{o.orderStatus}</span>
                  </td>
                  <td>{o.deliveryAddress}</td>
                  <td>({o.deliveryX}, {o.deliveryY})</td>
                  <td>{o.estimatedDeliveryTime}</td>
                  <td>
                    {o.orderStatus === 'DELIVERED' && assignment && (
                      !assignment.customerConfirmed ? (
                        <button className="btn" onClick={() => customerConfirmAndRate(assignment.id)}>Confirm & Rate</button>
                      ) : (
                        <span style={{ fontWeight: 'bold' }}>Confirmed ({assignment.deliveryRating} Stars)</span>
                      )
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div className="coordinate-grid-map">
        <h3>Visual Delivery Simulation Map</h3>
        <div className="grid-canvas">
          <div className="grid-dot customer" style={{ left: `${currentUser.x}%`, top: `${100 - currentUser.y}%` }}>
            <span className="grid-label">You ({currentUser.x}, {currentUser.y})</span>
          </div>

          {orders.filter(o => o.customerId === currentUser.id && o.orderStatus !== 'DELIVERED').map(o => (
            <div key={o.id}>
              <div className="grid-dot restaurant" style={{ left: `${o.deliveryX}%`, top: `${100 - o.deliveryY}%` }}>
                <span className="grid-label">Dest ID: {o.id} ({o.deliveryX}, {o.deliveryY})</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
