import React, { useState } from 'react';
import { Box, Button, Container, FormControl, InputLabel, MenuItem, Select, TextField, Stack } from '@mui/material';
import { addActivity } from '../servises/api';

const ActivityForm = ({ onActivityAdded }) => {
  const [activity, setActivity] = useState({
    type: "RUNNING",
    duration: '',
    caloriesBurned: '',
    additionalMetrics: {}
  });

  const addAdditionalMetric = () => {
    setActivity(prev => ({
      ...prev,
      additionalMetrics: {
        ...prev.additionalMetrics,
        '': ''
      }
    }));
  };

  const updateAdditionalMetric = (oldKey, newKey, newValue) => {
    setActivity(prev => {
      const newMetrics = { ...prev.additionalMetrics };
      if (oldKey !== newKey) {
        delete newMetrics[oldKey];
      }
      newMetrics[newKey] = newValue;
      return { ...prev, additionalMetrics: newMetrics };
    });
  };

  const removeAdditionalMetric = (key) => {
    setActivity(prev => {
      const newMetrics = { ...prev.additionalMetrics };
      delete newMetrics[key];
      return { ...prev, additionalMetrics: newMetrics };
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await addActivity(activity);
      onActivityAdded();
      setActivity({ type: "RUNNING", duration: '', caloriesBurned: '', additionalMetrics: {} });
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Box component="form" onSubmit={handleSubmit} sx={{ mb: 1 }}>
      {/* ... (Your other form elements) */}
      <FormControl fullWidth sx={{ mb: 2 }}>
        <InputLabel id="activity-type-label">Activity Type</InputLabel>
        <Select
          labelId="activity-type-label"
          id="activity-type-select"
          value={activity.type}
          label="Activity Type"
          onChange={(e) => setActivity({ ...activity, type: e.target.value })}
        >
          <MenuItem value="RUNNING">RUNNING</MenuItem>
          <MenuItem value="WALKING">WALKING</MenuItem>
          <MenuItem value="CYCLING">CYCLING</MenuItem>
          <MenuItem value="SWIMMING">SWIMMING</MenuItem>
          <MenuItem value="WEIGHT_TRAINING">WEIGHT_TRAINING</MenuItem>
          <MenuItem value="YOGA">YOGA</MenuItem>
          <MenuItem value="CARDIO">CARDIO</MenuItem>
          <MenuItem value="STRETCHING">STRETCHING</MenuItem>
          <MenuItem value="OTHER">OTHER</MenuItem>
        </Select>
      </FormControl>

      <TextField
        fullWidth
        label="Duration in (minutes)"
        required
        type='number'
        sx={{ mb: 2 }}
        value={activity.duration}
        onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
      />

      <TextField
        fullWidth
        required
        label="Calories burn"
        type='number'
        sx={{
          mb: 2,
          '& .MuiInputBase-input': {
            fontSize: { xs: '1rem', sm: '1.2rem', md: '1.5rem' }
          }
        }}
        value={activity.caloriesBurned}
        onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
      />

      {/* Render additionalMetrics as dynamic key-value pairs */}
      {Object.entries(activity.additionalMetrics).map(([key, value], index) => (
        <Stack
          key={index}
          // Correct responsive direction
          direction={{ xs: 'column', md: 'row' }}
          spacing={1}
          sx={{ mb: 2 }}
        >
          <TextField
            label="Key"
            value={key}
            onChange={(e) => updateAdditionalMetric(key, e.target.value, value)}
            sx={{ flex: 1 }}
          />
          <TextField
            label="Value"
            value={value}
            onChange={(e) => updateAdditionalMetric(key, key, e.target.value)}
            sx={{ flex: 2 }}
          />
          <Button
            color="error"
            onClick={() => removeAdditionalMetric(key)}
            sx={{ flexShrink: 0 }}
          >
            Remove
          </Button>
        </Stack>
      ))}

      <Button
        type="button"
        color='success'
        variant='contained'
        onClick={addAdditionalMetric}
        
        sx={{ mb: 2,  fontSize: { xs: '0.7rem', sm: '1rem', md: '1.25rem' } }}
      >
        Add Additional Detail
      </Button>

      <Container sx={{ display: 'flex', justifyContent: 'flex-end', p: 0,  }}>
        <Button type='submit' variant='contained' sx={{fontSize: { xs: '0.7rem', sm: '1rem', md: '1.25rem' }}} >Add Activity</Button>
      </Container>
    </Box>
  );
};

export default ActivityForm;