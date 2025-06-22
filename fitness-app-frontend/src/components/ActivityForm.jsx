import { Box, Button, duration, FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material'
import React, { useState } from 'react'
import { addActivity } from '../servises/api'



const ActivityForm = ({onActivityAdded}) => {
    const[activity,setActivity]=useState({
        type: "RUNNING", duration: '', caloriesBurned: '',additionalMetrics: {}
    })

    const handleSubmit= async (e)=>{
          e.preventDefault();
    try {
       await addActivity(activity);
        onActivityAdded();
        setActivity({type: "RUNNING", duration: '', caloriesBurned: ''})
    } catch (error) {
        console.error(error);
    }
}
  return (
     <Box component="form" onSubmit={handleSubmit} sx={{mb: 4}}>
      <FormControl fullWidth sx={{mb: 2}}>
        <InputLabel>Activity Type</InputLabel>
        <Select value={activity.type}
                onChange={(e)=>setActivity({...activity, type: e.target.value} )}
        > 
        <MenuItem value="RUNNING">RUNNING</MenuItem>    
         <MenuItem value="WALKING">WALKING</MenuItem>    
          <MenuItem value="CYCLING">CYCLING</MenuItem>    
        </Select>
      </FormControl>
      <TextField
                  fullWidth
                   label="Duration in (minutes)"
                   type='number'
                   sx={{mb:2}}
                   value={activity.duration}
                   onChange={(e)=>setActivity({...activity, duration: e.target.value})}
      />
         <TextField
                  fullWidth
                   label="Calories burn"
                   type='number'
                   sx={{mb:2}}
                   value={activity.caloriesBurned}
                   onChange={(e)=>setActivity({...activity, caloriesBurned: e.target.value})}
      />
      
      <Button type='submit' variant='contained' >Add Activity</Button>
 
    </Box>
  )
}

export default ActivityForm