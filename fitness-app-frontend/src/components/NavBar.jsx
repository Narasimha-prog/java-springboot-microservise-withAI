
import { AppBar, Toolbar, Typography, Button, Box, IconButton } from '@mui/material';
import { useNavigate } from 'react-router';
import { User } from 'lucide-react';
import { useSelector } from 'react-redux';
import { AuthContext } from 'react-oauth2-code-pkce';

const Navbar = ({ onLogout }) => {
  const KEYCLOAK_PROFILE_URL = "http://localhost:8180/realms/fitness/account/";
  const navigate = useNavigate();

  const user = useSelector(state => state.auth.user);


  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" sx={{ flexGrow: 1, cursor: 'pointer' }} onClick={() => navigate('/')}>
          FitnessApp
        </Typography>

        {!user ? (
          <Button color="inherit" >
            Log In
          </Button>
        ) : (
          <Box>
            <IconButton
              color="inherit"
              onClick={() => window.open(KEYCLOAK_PROFILE_URL, "_blank")}
              aria-label="Edit Profile"
              title="Edit Profile"
              size="large"
              sx={{ mr: 1 }}
            >
              <User />
            </IconButton>

            <Button color="inherit" onClick={onLogout}>
              Logout
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
