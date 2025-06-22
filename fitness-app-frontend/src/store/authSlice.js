import { createSlice } from '@reduxjs/toolkit'


const authSlice = createSlice({
  name: 'auth',
  initialState: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    userId: localStorage.getItem('userId') || null,
  },
  reducers: {
    setCredetials: (state,action) => {
     
    },
    logout: (state) => {
      
    }
  }
})

export const { setCredetials,logout } = authSlice.actions;
export default authSlice.reducer;