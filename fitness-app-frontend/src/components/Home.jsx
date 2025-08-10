import { Box, Button, Typography } from "@mui/material"; 
import { useNavigate } from "react-router-dom";


export function HomePage({ onLogin, isAuthenticated, user }) {
    const navigate = useNavigate();
  return (
    <Box  
      sx={{
           minHeight: "80vh",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
      }}
    >
      {isAuthenticated ? (
        // ✅ Show this content if the user is logged in
        <Box>
          <Typography variant="h4" gutterBottom>
            Welcome, {user?.name || user?.email}!
          </Typography>
          <Typography variant="subtitle1" sx={{ mb: 3 }}>
            You are logged in Click <Button color="info" onClick={() => navigate('/activities')}>
              My Activities
            </Button> to see your activities.
          </Typography>
        </Box>
      ) : (
        // ✅ Show this content if the user is logged out
        <Box>
          <Typography variant="h4" gutterBottom>
            Welcome to the Fitness Tracker App
          </Typography>
          <Typography variant="subtitle1" sx={{ mb: 3 }}>
            Please login to access your activities
          </Typography>
          <Button variant="contained" color="primary" size="large" onClick={() => onLogin()}>
            LOGIN
          </Button>
        </Box>
      )}
    </Box>
  );
}

