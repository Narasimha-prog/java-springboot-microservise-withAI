import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getActivityDetails, getActivityDetail, deleteActivity } from '../servises/api';
import { Box, Button, Card, CardContent, Divider, Typography, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';

const ActivityDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [activity, setActivity] = useState(null);
  const [recommendation, setRecommendation] = useState({});
  const [openConfirm, setOpenConfirm] = useState(false);

  useEffect(() => {
    const fetchActivityData = async () => {
      try {
        const response1 = await getActivityDetail(id);
        setActivity(response1.data);
        console.log("Activity Data:", response1.data);

        const responseDetails = await getActivityDetails(id);
        console.log("Recommendation Data:", responseDetails.data);
        if (responseDetails.data && responseDetails.data.recommendation) {
          setRecommendation(responseDetails.data);
        }
      } catch (error) {
        console.error("Error fetching activity details:", error);
      }
    };

    fetchActivityData();
  }, [id]);

  const handleClickOpen = () => {
    setOpenConfirm(true);
  };

  const handleClose = () => {
    setOpenConfirm(false);
  };

  const handleDelete = async () => {
    handleClose();
    try {
      await deleteActivity(id);
      navigate('/activities');
    } catch (error) {
      console.error("Error deleting activity:", error);
    }
  };

  if (!activity) {
    return <Typography>Loading....</Typography>;
  }

  return (
    <Box 
      sx={{ 
        // Sets responsive maxWidth for the container
        maxWidth: { xs: '100%', sm: 600, md: 800 },
        // Adjusts horizontal margin to remove extra space on mobile
        mx: { xs: 0, sm: 'auto' },
        // Reduced padding for a tighter layout
        p: { xs: 1, sm: 2 },
      }}
    >
      <Card sx={{ mb: { xs: 1, sm: 2 } }}>
        <CardContent sx={{ p: { xs: 1.5, sm: 2 } }}>
          <Typography 
            variant="h5" 
            gutterBottom 
            sx={{ fontSize: { xs: '1.5rem', sm: '1.75rem' } }}
          >
            Activity Details
          </Typography>
          <Typography>Type: {activity.type}</Typography>
          <Typography>Duration: {activity.duration} minutes</Typography>
          <Typography>Calories Burned: {activity.caloriesBurned}</Typography>
          {activity.createdAt && (
            <Typography>Date: {new Date(activity.createdAt).toLocaleString()}</Typography>
          )}
        </CardContent>
      </Card>

      {recommendation && Object.keys(recommendation).length > 0 && (
        <Card>
          <CardContent sx={{ p: { xs: 1.5, sm: 2 } }}>
            <Typography 
              variant="h5" 
              gutterBottom
              sx={{ fontSize: { xs: '1.5rem', sm: '1.75rem' } }}
            >
              AI Recommendation
            </Typography>
            <Typography 
              variant="h6"
              sx={{ fontSize: { xs: '1.2rem', sm: '1.4rem' } }}
            >
              Analysis
            </Typography>
            <Typography paragraph>{recommendation.analysis}</Typography>
            
            <Divider sx={{ my: 2 }} />
            
            <Typography 
              variant="h6"
              sx={{ fontSize: { xs: '1.2rem', sm: '1.4rem' } }}
            >
              Improvements
            </Typography>
            {recommendation.improvements && recommendation.improvements.map((improvement, index) => (
              <Typography key={index} paragraph>• {improvement}</Typography>
            ))}
            
            <Divider sx={{ my: 2 }} />
            
            <Typography 
              variant="h6"
              sx={{ fontSize: { xs: '1.2rem', sm: '1.4rem' } }}
            >
              Suggestions
            </Typography>
            {recommendation.suggestions && recommendation.suggestions.map((suggestion, index) => (
              <Typography key={index} paragraph>• {suggestion}</Typography>
            ))}
            
            <Divider sx={{ my: 2 }} />
            
            <Typography 
              variant="h6"
              sx={{ fontSize: { xs: '1.2rem', sm: '1.4rem' } }}
            >
              Safety Guidelines
            </Typography>
            {recommendation.safety && recommendation.safety.map((safety, index) => (
              <Typography key={index} paragraph>• {safety}</Typography>
            ))}
          </CardContent>
        </Card>
      )}

      <Button variant="contained" color="error" onClick={handleClickOpen} sx={{ mt: { xs: 1, sm: 2 } }}>
        Delete Activity
      </Button>

      <Dialog
        open={openConfirm}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm Deletion"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to delete this activity? This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleDelete} autoFocus color="error">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default ActivityDetails;