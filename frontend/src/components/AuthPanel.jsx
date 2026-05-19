import { useState } from 'react';

export default function AuthPanel({
  loginEmail,
  setLoginEmail,
  loginPassword,
  setLoginPassword,
  handleLogin,
  regForm,
  setRegForm,
  handleRegister
}) {
  return (
    <div className="flex-container">
      <div className="form-panel col-half">
        <h3>Member Login</h3>
        <form onSubmit={handleLogin}>
          <div className="form-group" style={{ marginBottom: '15px' }}>
            <label>Email Address:</label>
            <input type="email" placeholder="enter email" value={loginEmail} onChange={e => setLoginEmail(e.target.value)} required />
          </div>
          <div className="form-group" style={{ marginBottom: '15px' }}>
            <label>Password:</label>
            <input type="password" placeholder="enter password" value={loginPassword} onChange={e => setLoginPassword(e.target.value)} required />
          </div>
          <button type="submit" className="btn btn-primary">Sign In</button>
        </form>
      </div>

      <div className="form-panel col-half">
        <h3>Account Registration</h3>
        <form onSubmit={handleRegister}>
          <div className="form-grid">
            <div className="form-group">
              <label>Full Name:</label>
              <input type="text" placeholder="John Doe" value={regForm.name} onChange={e => setRegForm({...regForm, name: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Email:</label>
              <input type="email" placeholder="john@mail.com" value={regForm.email} onChange={e => setRegForm({...regForm, email: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Password:</label>
              <input type="password" placeholder="pass" value={regForm.password} onChange={e => setRegForm({...regForm, password: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Phone Number:</label>
              <input type="text" placeholder="9876543210" value={regForm.phone} onChange={e => setRegForm({...regForm, phone: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>User Role:</label>
              <select value={regForm.role} onChange={e => setRegForm({...regForm, role: e.target.value})}>
                <option value="CUSTOMER">CUSTOMER</option>
                <option value="DELIVERY_AGENT">DELIVERY_AGENT</option>
                <option value="RESTAURANT_MANAGER">RESTAURANT_MANAGER</option>
                <option value="ADMIN">ADMIN</option>
              </select>
            </div>
            <div className="form-group">
              <label>Start X (0-100):</label>
              <input type="number" min="0" max="100" value={regForm.x} onChange={e => setRegForm({...regForm, x: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Start Y (0-100):</label>
              <input type="number" min="0" max="100" value={regForm.y} onChange={e => setRegForm({...regForm, y: e.target.value})} required />
            </div>
            <div className="form-group">
              <label>Service Area/Zone:</label>
              <input type="text" placeholder="Downtown" value={regForm.zone} onChange={e => setRegForm({...regForm, zone: e.target.value})} required />
            </div>
          </div>
          <button type="submit" className="btn btn-success">Register</button>
        </form>
      </div>
    </div>
  );
}
