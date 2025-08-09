import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router'; // Import useNavigate
import { getActivityDetails, getActivityDetail, deleteActivity } from '../servises/api';
import { Box, Button, Card, CardContent, Divider, Typography, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material'; // Import Dialog components

const ActivityDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate(); // Initialize useNavigate hook
  const [activity, setActivity] = useState(null);
  const[recommendation, setRecommendation] = useState({}); // Initialize recommendation state
  // recommendation is part of activity object, no need for separate state
  const [openConfirm, setOpenConfirm] = useState(false); // State for confirmation dialog


  useEffect(() => {
    const fetchActivityData = async () => { 
      try {
        // Assuming getActivityDetail provides the main activity data (duration, calories)
        // and getActivityDetails provides the recommendation part.
        // It's usually better to have one endpoint return all details if possible.
        const response1 = await getActivityDetail(id);
         setActivity(response1.data);
        console.log("Activity Data:", response1.data);

        // Fetch recommendation separately if not part of activityData initially
        // Otherwise, if response1.data already contains recommendation, this call might be redundant
        const responseDetails = await getActivityDetails(id);
        console.log("Recommendation Data:", responseDetails.data);
        if (responseDetails.data && responseDetails.data.recommendation) {
           setRecommendation(responseDetails.data)
        }

        

      } catch (error) {
        console.error("Error fetching activity details:", error);
      }
    };

    fetchActivityData();
  }, [id]);

  // Handle confirmation dialog
  const handleClickOpen = () => {
    setOpenConfirm(true);
  };

  const handleClose = () => {
    setOpenConfirm(false);
  };

  // Handle actual deletion
  const handleDelete = async () => {
    handleClose(); // Close the dialog
    try {
      await deleteActivity(id); // Pass the id to the deleteActivity function
      navigate('/activities'); // Redirect to the activities list after deletion
    } catch (error) {
      console.error("Error deleting activity:", error);
      // Optionally, show an error message to the user
    }
  };


  if (!activity) {
    return <Typography>Loading....</Typography>;
  }

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>Activity Details</Typography>
          <Typography>Type: {activity.type}</Typography>
          <Typography>Duration: {activity.duration} minutes</Typography>
          <Typography>Calories Burned: {activity.caloriesBurned}</Typography>
          {activity.createdAt && ( // Check if createdAt exists before using
              <Typography>Date: {new Date(activity.createdAt).toLocaleString()}</Typography>
          )}
        </CardContent>
      </Card>

      {recommendation && Object.keys(recommendation).length > 0 && ( // Check if recommendation exists and is not empty
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom>AI Recommendation</Typography>
            <Typography variant="h6">Analysis</Typography>
            <Typography paragraph>{recommendation.analysis}</Typography>
            
            <Divider sx={{ my: 2 }} />
            
            <Typography variant="h6">Improvements</Typography>
            {recommendation.improvements && recommendation.improvements.map((improvement, index) => (
              <Typography key={index} paragraph>• {improvement}</Typography>
            ))}
            
            <Divider sx={{ my: 2 }} />
            
            <Typography variant="h6">Suggestions</Typography>
            {recommendation.suggestions && recommendation.suggestions.map((suggestion, index) => (
              <Typography key={index} paragraph>• {suggestion}</Typography>
            ))}
            
            <Divider sx={{ my: 2 }} />
            
            <Typography variant="h6">Safety Guidelines</Typography>
            {recommendation.safety && recommendation.safety.map((safety, index) => (
              <Typography key={index} paragraph>• {safety}</Typography>
            ))}
          </CardContent>
        </Card>
      )}

      {/* Delete Button */}
      <Button variant="contained" color="error" onClick={handleClickOpen} sx={{ mt: 2 }}>
        Delete Activity
      </Button>

      {/* Confirmation Dialog */}
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