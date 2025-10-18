import { Card, CardContent, Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getActivities } from '../servises/api';

const ActivityList = () => {
  const [activities, setActivities] = useState([]);
  const navigate = useNavigate();

  const fetchActivities = async () => {
    try {
      const response = await getActivities();
      setActivities(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  return (
    <Grid 
      container 
      // Reduced spacing on extra-small screens
      spacing={{ xs: 1, sm: 2, md: 3 }} 
      sx={{ p: { xs: 1, sm: 2 } }} // Added responsive padding to the container
    >
      {activities.map((activity) => (
        <Grid item xs={12} sm={6} md={4} key={activity.id}>
          <Card
            sx={{
              cursor: 'pointer',
              height: '100%',
              display: 'flex',
              flexDirection: 'column',
              '&:hover': {
                boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
                transform: 'translateY(-5px)',
                transition: 'transform 0.2s, box-shadow 0.2s',
              },
            }}
            onClick={() => navigate(`/activities/${activity.id}`)}
          >
            <CardContent sx={{ p: { xs: 1.5, sm: 2 } }}> {/* Responsive padding for card content */}
              <Typography variant='h6' sx={{ textTransform: 'uppercase', fontSize: { xs: '1rem', sm: '1.25rem' } }}>
                {activity.type}
              </Typography>
              <Typography sx={{ fontSize: { xs: '0.875rem', sm: '1rem' } }}>
                Duration: {activity.duration} minutes
              </Typography>
              <Typography sx={{ fontSize: { xs: '0.875rem', sm: '1rem'} }}>
                Calories Burned: {activity.caloriesBurned}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

export default ActivityList;