import { Box } from "@mui/material";
import { useContext, useEffect, useState } from "react";
// ✅ Correct import path for react-router-dom
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { AuthContext } from "react-oauth2-code-pkce";
import { setCredentials, logout } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetails from "./components/ActivityDetails";
import Navbar from "./components/NavBar";
import Footer from "./components/Footer";
import { HomePage } from "./components/Home";
import { ScrollToTop } from "./components/ScrollToTop";

// (Your existing components)
function ActivitiesPage() {
  return (
    <Box component="section" sx={{ p: 2, border: "1px dashed grey" }}>
      <ActivityForm onActivityAdded={() => window.location.reload()} />
      <ActivityList />
    </Box>
  );
}

function App() {
  const { token: contextToken, tokenData, logIn, logOut } = useContext(AuthContext);
  const dispatch = useDispatch();
  // ✅ Get both the token and user from Redux
  const token = useSelector((state) => state.auth.token);
  const user = useSelector((state) => state.auth.user);
  
  const [authChecked, setAuthChecked] = useState(false);

  // ✅ 1. This useEffect now correctly saves token and user to localStorage
  useEffect(() => {
    if (contextToken) {
      dispatch(setCredentials({ token: contextToken, user: tokenData }));
      localStorage.setItem("token", contextToken);
      localStorage.setItem("user", JSON.stringify(tokenData));
    }
  }, [contextToken, tokenData, dispatch]);

  // ✅ 2. This useEffect now correctly restores Redux state from localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem("token");
    const savedUser = localStorage.getItem("user");
    if (savedToken && savedUser) {
      dispatch(
        setCredentials({
          token: savedToken,
          user: JSON.parse(savedUser),
        })
      );
    }
    setAuthChecked(true);
  }, [dispatch]);

  if (!authChecked) {
    return <div>Loading...</div>;
  }

  const handleLogout = () => {
    dispatch(logout());
    logOut();
    // ✅ 3. Clear localStorage on logout
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  };

  return (
    <Router>
      <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
        <Navbar
          isAuthenticated={!!token}
          onLogout={handleLogout}
          onLogin={logIn}
        />
        <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Routes>
          
          <Route 
            path="/" 
            // ✅ 4. Pass correct boolean and user object from Redux to HomePage
            element={<HomePage onLogin={logIn} isAuthenticated={!!token} user={user} />} 
          />
          <Route
            path="/activities"
            element={token ? <ActivitiesPage /> : <Navigate to="/" replace />}
          />
          <Route
            path="/activities/:id"
            element={token ? <ActivityDetails /> : <Navigate to="/" replace />}
          />
        </Routes>
        </Box>
        <Footer />
        <ScrollToTop/>
      </Box>
    </Router>
  );
}

export default App;