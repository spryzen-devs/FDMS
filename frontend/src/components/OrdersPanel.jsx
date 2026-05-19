import { useState } from 'react';

export default function OrdersPanel({
  users,
  restaurants,
  orders,
  restaurantMenu,
  orderForm,
  setOrderForm,
  orderItemsList,
  addOrderItemLocally,
  handleRestaurantChange,
  handlePlaceOrder,
  updateToPreparing
}) {
  return (
    <div>
      <div className="form-panel">
        <h3>Place Customer Food Order</h3>
        <form onSubmit={handlePlaceOrder}>
          <div className="form-grid">
            <div className="form-group">
              <label>Select Customer:</label>
              <select value={orderForm.customerId} onChange={e => setOrderForm({...orderForm, customerId: e.target.value})} required>
                <option value="">-- Select Customer --</option>
                {users.filter(u => u.role === 'CUSTOMER').map(c => (
                  <option key={c.id} value={c.id}>{c.name} (ID: {c.id})</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Select Restaurant:</label>
              <select value={orderForm.restaurantId} onChange={e => handleRestaurantChange(e.target.value)} required>
                <option value="">-- Select Restaurant --</option>
                {restaurants.map(r => (
                  <option key={r.id} value={r.id}>{r.name} (Location: {r.location})</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Delivery Address:</label>
              <input type="text" placeholder="123 Main St" value={orderForm.deliveryAddress} onChange={e => setOrderForm({...orderForm, deliveryAddress: e.target.value})} required />
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
              <h4>Select Menu Items</h4>
              <div className="form-grid">
                <div className="form-group">
                  <label>Food Item:</label>
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
                      <th>Quantity</th>
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

      <h3>Active Orders Lifecycle</h3>
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Customer</th>
              <th>Restaurant</th>
              <th>Total Amount</th>
              <th>Status</th>
              <th>Address</th>
              <th>Coordinates</th>
              <th>Estimated Delivery</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id}>
                <td>{o.id}</td>
                <td>{o.customerName}</td>
                <td>{o.restaurantName}</td>
                <td>${o.totalAmount}</td>
                <td>
                  <span style={{ 
                    fontWeight: 'bold', 
                    padding: '3px 8px', 
                    borderRadius: '3px',
                    backgroundColor: o.orderStatus === 'DELIVERED' ? '#d4edda' : o.orderStatus === 'PLACED' ? '#fff3cd' : '#d1ecf1',
                    color: o.orderStatus === 'DELIVERED' ? '#155724' : o.orderStatus === 'PLACED' ? '#856404' : '#0c5460'
                  }}>{o.orderStatus}</span>
                </td>
                <td>{o.deliveryAddress}</td>
                <td>({o.deliveryX}, {o.deliveryY})</td>
                <td>{o.estimatedDeliveryTime}</td>
                <td>
                  {o.orderStatus === 'PLACED' && (
                    <button className="btn btn-success" onClick={() => updateToPreparing(o.id)}>Start Preparing</button>
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
