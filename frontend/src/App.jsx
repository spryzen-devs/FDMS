import { useState, useEffect } from 'react';
import AuthPanel from './components/AuthPanel';
import CustomerPanel from './components/CustomerPanel';
import ManagerPanel from './components/ManagerPanel';
import AgentPanel from './components/AgentPanel';
import AdminPanel from './components/AdminPanel';

const API_BASE = "http://localhost:8080/api";

function App() {
  const [currentUser, setCurrentUser] = useState(() => {
    const stored = localStorage.getItem('currentUser');
    return stored ? JSON.parse(stored) : null;
  });
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const [users, setUsers] = useState([]);
  const [restaurants, setRestaurants] = useState([]);
  const [orders, setOrders] = useState([]);
  const [assignments, setAssignments] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [selectedOrderRec, setSelectedOrderRec] = useState('');
  const [restaurantMenu, setRestaurantMenu] = useState([]);

  const [reports, setReports] = useState({
    topAgents: [],
    delayedDeliveries: [],
    restaurantDelays: [],
    averageTime: 0
  });

  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  const [regForm, setRegForm] = useState({
    name: '', email: '', password: '', phone: '', role: 'CUSTOMER', x: '0', y: '0', zone: 'Downtown'
  });

  const [restForm, setRestForm] = useState({
    name: '', location: 'Downtown', averagePreparationTime: '15', rating: '5.0', x: '0', y: '0'
  });

  const [foodForm, setFoodForm] = useState({
    name: '', price: '', category: 'Main Course'
  });

  const [orderForm, setOrderForm] = useState({
    restaurantId: '', deliveryAddress: '', deliveryX: '0', deliveryY: '0', foodItemId: '', quantity: '1'
  });
  const [orderItemsList, setOrderItemsList] = useState([]);

  const fetchUsers = () => {
    fetch(`${API_BASE}/auth/users`)
      .then(res => res.json())
      .then(data => setUsers(data))
      .catch(err => console.error(err));
  };

  const fetchRestaurants = () => {
    fetch(`${API_BASE}/restaurants`)
      .then(res => res.json())
      .then(data => setRestaurants(data))
      .catch(err => console.error(err));
  };

  const fetchOrders = () => {
    fetch(`${API_BASE}/orders`)
      .then(res => res.json())
      .then(data => setOrders(data))
      .catch(err => console.error(err));
  };

  const fetchAssignments = () => {
    fetch(`${API_BASE}/assignments`)
      .then(res => res.json())
      .then(data => setAssignments(data))
      .catch(err => console.error(err));
  };

  const fetchReports = () => {
    fetch(`${API_BASE}/reports/top-agents`).then(res => res.json()).then(data => setReports(prev => ({ ...prev, topAgents: data }))).catch(err => console.error(err));
    fetch(`${API_BASE}/reports/delayed-deliveries`).then(res => res.json()).then(data => setReports(prev => ({ ...prev, delayedDeliveries: data }))).catch(err => console.error(err));
    fetch(`${API_BASE}/reports/restaurant-delays`).then(res => res.json()).then(data => setReports(prev => ({ ...prev, restaurantDelays: data }))).catch(err => console.error(err));
    fetch(`${API_BASE}/reports/average-delivery-time`).then(res => res.json()).then(data => setReports(prev => ({ ...prev, averageTime: data.averageDeliveryTimeMinutes || 0 }))).catch(err => console.error(err));
  };

  useEffect(() => {
    fetchUsers();
    fetchRestaurants();
    fetchOrders();
    fetchAssignments();
    fetchReports();
  }, []);

  const triggerAlert = (success, message) => {
    if (success) {
      setSuccessMsg(message);
      setErrorMsg('');
      setTimeout(() => setSuccessMsg(''), 5000);
    } else {
      setErrorMsg(message);
      setSuccessMsg('');
      setTimeout(() => setErrorMsg(''), 7000);
    }
  };

  const handleLogin = (e) => {
    e.preventDefault();
    fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: loginEmail, password: loginPassword })
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Invalid credentials") });
        return res.json();
      })
      .then(data => {
        setCurrentUser(data);
        localStorage.setItem('currentUser', JSON.stringify(data));
        triggerAlert(true, `Welcome back, ${data.name}!`);
        setLoginEmail('');
        setLoginPassword('');
        fetchOrders();
        fetchAssignments();
        fetchReports();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const handleRegister = (e) => {
    e.preventDefault();
    const payload = {
      ...regForm,
      x: parseInt(regForm.x),
      y: parseInt(regForm.y)
    };

    fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Registration failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Registration successful! You can now log in using your credentials.");
        setRegForm({ name: '', email: '', password: '', phone: '', role: 'CUSTOMER', x: '0', y: '0', zone: 'Downtown' });
        fetchUsers();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem('currentUser');
    triggerAlert(true, "Logged out successfully.");
  };

  const handleRestaurantChange = (restaurantId) => {
    setOrderForm(prev => ({ ...prev, restaurantId, foodItemId: '' }));
    setOrderItemsList([]);
    setRestaurantMenu([]);

    if (restaurantId) {
      fetch(`${API_BASE}/restaurants/${restaurantId}/menu`)
        .then(res => res.json())
        .then(data => setRestaurantMenu(data))
        .catch(err => console.error(err));
    }
  };

  const addOrderItemLocally = () => {
    if (!orderForm.foodItemId) {
      triggerAlert(false, "Please select a valid food item to add");
      return;
    }
    
    const food = restaurantMenu.find(f => f.id === parseInt(orderForm.foodItemId));
    if (!food) {
      triggerAlert(false, "Selected food item not found");
      return;
    }

    const newItem = {
      foodItemId: food.id,
      foodItemName: food.name,
      price: food.price,
      quantity: parseInt(orderForm.quantity)
    };

    setOrderItemsList(prev => [...prev, newItem]);
    setOrderForm(prev => ({ ...prev, foodItemId: '', quantity: '1' }));
  };

  const handlePlaceOrder = (e) => {
    e.preventDefault();
    if (orderItemsList.length === 0) {
      triggerAlert(false, "Please select and add food items locally first.");
      return;
    }

    const payload = {
      customerId: currentUser.id,
      restaurantId: parseInt(orderForm.restaurantId),
      deliveryAddress: orderForm.deliveryAddress,
      deliveryX: parseInt(orderForm.deliveryX),
      deliveryY: parseInt(orderForm.deliveryY),
      items: orderItemsList.map(item => ({
        foodItemId: item.foodItemId,
        quantity: item.quantity
      }))
    };

    fetch(`${API_BASE}/orders`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed to place order") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Order placed successfully!");
        setOrderForm({ restaurantId: '', deliveryAddress: '', deliveryX: '0', deliveryY: '0', foodItemId: '', quantity: '1' });
        setOrderItemsList([]);
        fetchOrders();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const handleAddRestaurant = (e) => {
    e.preventDefault();
    const payload = {
      ...restForm,
      averagePreparationTime: parseInt(restForm.averagePreparationTime),
      rating: parseFloat(restForm.rating),
      managerId: currentUser.id,
      x: parseInt(restForm.x),
      y: parseInt(restForm.y)
    };

    fetch(`${API_BASE}/restaurants`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed to create restaurant") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, `Successfully registered your restaurant: ${restForm.name}`);
        setRestForm({ name: '', location: 'Downtown', averagePreparationTime: '15', rating: '5.0', x: '0', y: '0' });
        fetchRestaurants();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const handleAddFoodItem = (e, restId) => {
    e.preventDefault();
    const payload = {
      ...foodForm,
      price: parseFloat(foodForm.price),
      restaurantId: restId
    };

    fetch(`${API_BASE}/restaurants/menu`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed to add item") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, `Successfully added menu item: ${foodForm.name}`);
        setFoodForm({ name: '', price: '', category: 'Main Course' });
        fetchRestaurants();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const updateToPreparing = (orderId) => {
    fetch(`${API_BASE}/orders/${orderId}/status?status=PREPARING`, {
      method: 'PUT'
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, `Order status set to PREPARING`);
        fetchOrders();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const getAgentRecommendations = (orderId) => {
    setSelectedOrderRec(orderId);
    fetch(`${API_BASE}/assignments/recommend?orderId=${orderId}`)
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(data => {
        setRecommendations(data);
        if (data.length === 0) {
          triggerAlert(false, "No active available agents are nearby!");
        }
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const assignAgent = (agentId) => {
    fetch(`${API_BASE}/assignments/assign?orderId=${selectedOrderRec}&agentId=${agentId}`, {
      method: 'POST'
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Agent assigned successfully!");
        setRecommendations([]);
        setSelectedOrderRec('');
        fetchOrders();
        fetchAssignments();
        fetchUsers();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const pickupOrder = (assignId) => {
    fetch(`${API_BASE}/assignments/${assignId}/pickup`, {
      method: 'PUT'
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Order picked up from restaurant!");
        fetchOrders();
        fetchAssignments();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const deliverOrder = (assignId) => {
    fetch(`${API_BASE}/assignments/${assignId}/deliver`, {
      method: 'PUT'
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Order delivery confirmed! Waiting for customer confirmation and rating.");
        fetchOrders();
        fetchAssignments();
        fetchReports();
        fetchUsers();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const customerConfirmAndRate = (assignId) => {
    const rating = prompt("Please rate delivery performance (1.0 to 5.0):", "5.0");
    if (rating === null) return;

    fetch(`${API_BASE}/assignments/${assignId}/customer-confirm?rating=${parseFloat(rating)}`, {
      method: 'PUT'
    })
      .then(res => {
        if (!res.ok) return res.json().then(d => { throw new Error(d.message || "Failed") });
        return res.json();
      })
      .then(() => {
        triggerAlert(true, "Thank you! Delivery confirmed and agent rating updated.");
        fetchOrders();
        fetchAssignments();
        fetchReports();
        fetchUsers();
      })
      .catch(err => triggerAlert(false, err.message));
  };

  const stepSimulation = () => {
    fetch(`${API_BASE}/simulation/step`, {
      method: 'POST'
    })
      .then(res => res.json())
      .then(() => {
        triggerAlert(true, "Coordinate step advanced: active agents moved closer to their destinations");
        fetchUsers();
        fetchAssignments();
        fetchOrders();
      })
      .catch(err => triggerAlert(false, "Failed to simulate movement tick"));
  };

  return (
    <div className="app-container">
      <div className="header">
        <h1>FOOD DELIVERY MANAGEMENT SYSTEM</h1>
        <p>Mini Project Assignment Service layer -- Logged role-based views</p>
        
        {currentUser && (
          <div style={{ marginTop: '15px', backgroundColor: '#eef2f3', padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}>
            <strong>Welcome: </strong> {currentUser.name} | <strong>Role: </strong> {currentUser.role} | <strong>Coordinates: </strong> ({currentUser.x}, {currentUser.y})
            <button className="btn btn-danger" style={{ marginLeft: '20px', padding: '4px 10px' }} onClick={handleLogout}>Log Out</button>
          </div>
        )}
      </div>

      {successMsg && <div className="alert alert-success">{successMsg}</div>}
      {errorMsg && <div className="alert alert-error">Error: {errorMsg}</div>}

      {!currentUser && (
        <AuthPanel
          loginEmail={loginEmail}
          setLoginEmail={setLoginEmail}
          loginPassword={loginPassword}
          setLoginPassword={setLoginPassword}
          handleLogin={handleLogin}
          regForm={regForm}
          setRegForm={setRegForm}
          handleRegister={handleRegister}
        />
      )}

      {currentUser?.role === 'CUSTOMER' && (
        <CustomerPanel
          currentUser={currentUser}
          restaurants={restaurants}
          orders={orders}
          restaurantMenu={restaurantMenu}
          orderForm={orderForm}
          setOrderForm={setOrderForm}
          orderItemsList={orderItemsList}
          addOrderItemLocally={addOrderItemLocally}
          handleRestaurantChange={handleRestaurantChange}
          handlePlaceOrder={handlePlaceOrder}
          assignments={assignments}
          customerConfirmAndRate={customerConfirmAndRate}
        />
      )}

      {currentUser?.role === 'RESTAURANT_MANAGER' && (
        <ManagerPanel
          currentUser={currentUser}
          restaurants={restaurants}
          orders={orders}
          assignments={assignments}
          users={users}
          recommendations={recommendations}
          selectedOrderRec={selectedOrderRec}
          restForm={restForm}
          setRestForm={setRestForm}
          foodForm={foodForm}
          setFoodForm={setFoodForm}
          handleAddRestaurant={handleAddRestaurant}
          handleAddFoodItem={handleAddFoodItem}
          updateToPreparing={updateToPreparing}
          getAgentRecommendations={getAgentRecommendations}
          assignAgent={assignAgent}
          stepSimulation={stepSimulation}
        />
      )}

      {currentUser?.role === 'DELIVERY_AGENT' && (
        <AgentPanel
          currentUser={currentUser}
          assignments={assignments}
          pickupOrder={pickupOrder}
          deliverOrder={deliverOrder}
        />
      )}

      {currentUser?.role === 'ADMIN' && (
        <AdminPanel
          reports={reports}
          users={users}
        />
      )}

      <div style={{ marginTop: '30px', textAlign: 'center', fontSize: '12px', color: '#888888', borderTop: '1px solid #eeeeee', paddingTop: '15px' }}>
        &copy; 2026 Food Delivery Management System Project -- Computer Science Engineering Dept.
      </div>
    </div>
  );
}

export default App;
