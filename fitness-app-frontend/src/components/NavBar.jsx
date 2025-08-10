import { useState } from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  Button,
  Box,
  IconButton,
  Drawer,
  List,
  ListItem,
  ListItemText,
} from "@mui/material";
import MenuIcon from "@mui/icons-material/Menu";
import CloseIcon from "@mui/icons-material/Close";
import { useNavigate, useLocation } from "react-router-dom";
import { User } from "lucide-react";
import { useSelector } from "react-redux";

const Navbar = ({ onLogout, onLogin }) => {
  const KEYCLOAK_PROFILE_URL = "http://localhost:8180/realms/fitness/account/";
  const navigate = useNavigate();
  const location = useLocation();

  const user = useSelector((state) => state.auth.user);
  const [mobileOpen, setMobileOpen] = useState(false);

  // Check if the current path is exactly '/activities'
  const isActivitiesPage = location.pathname === "/activities";

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const drawerContent = (
    <Box
      onClick={handleDrawerToggle}
      sx={{ textAlign: "center" }}
    >
      <Box
        sx={{
          display: "flex",
          justifyContent: "flex-end",
          p: 2,
        }}
      >
        <IconButton onClick={handleDrawerToggle}>
          <CloseIcon />
        </IconButton>
      </Box>
      <List>
        {!isActivitiesPage && (
          <ListItem button onClick={() => navigate("/activities")}>
            <ListItemText primary="My Activities" />
          </ListItem>
        )}
        <ListItem
          button
          onClick={() => window.open(KEYCLOAK_PROFILE_URL, "_blank")}
        >
          <ListItemText primary="Edit Profile" />
        </ListItem>
        <ListItem button onClick={onLogout}>
          <ListItemText primary="Logout" />
        </ListItem>
      </List>
    </Box>
  );

  return (
    <AppBar position="static">
      <Toolbar>
        {/* Mobile Menu Button (visible on small screens) */}
        {user && (
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { md: "none" } }}
          >
            <MenuIcon />
          </IconButton>
        )}

        <Typography
          variant="h6"
          sx={{ flexGrow: 1, cursor: "pointer" }}
          onClick={() => navigate("/")}
        >
          FitnessApp
        </Typography>

        {/* Desktop Links (visible on medium and up screens) */}
        {!user ? (
          <Button
            color="inherit"
            onClick={() => onLogin()}
            sx={{ display: { xs: "none", md: "block" } }}
          >
            Log In
          </Button>
        ) : (
          <Box sx={{ display: { xs: "none", md: "flex" } }}>
            {!isActivitiesPage && (
              <Button color="inherit" onClick={() => navigate("/activities")}>
                My Activities
              </Button>
            )}
            <IconButton
              color="inherit"
              onClick={() => window.open(KEYCLOAK_PROFILE_URL, "_blank")}
              aria-label="Edit Profile"
              title="Edit Profile"
              size="large"
              sx={{ ml: 1 }}
            >
              <User />
            </IconButton>
            <Button color="inherit" onClick={() => onLogout()}>
              Logout
            </Button>
          </Box>
        )}
      </Toolbar>

      {/* The Mobile Drawer itself */}
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onClose={handleDrawerToggle}
        ModalProps={{
          keepMounted: true, // Better performance on mobile
        }}
        sx={{
          display: { xs: "block", md: "none" },
          "& .MuiDrawer-paper": { boxSizing: "border-box", width: 240 },
        }}
      >
        {drawerContent}
      </Drawer>
    </AppBar>
  );
};

export default Navbar;