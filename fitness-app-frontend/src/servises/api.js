import axios from "axios";

const API_URLS='http://localhost:4004/api/';

const api=axios.create({
    baseURL:API_URLS
})

api.interceptors.request.use((config)=>{
    const userId= localStorage.getItem('userId');
        const token= localStorage.getItem('token');
        if(token){
           config.headers['Authorization']=`Bearer ${token}`; 
        }
    if(userId){
        config.headers['X-User-ID']=userId;
    }
    return config;
   
})

export const deleteActivity=(id)=>api.delete(`/activities/${id}`);
export const getActivityDetail=(id)=>api.get(`/activities/${id}`);
export const getActivities=()=>api.get('/activities');
export const addActivity=(activity)=>api.post('/activities',activity);
export const getActivityDetails=(id)=>api.get(`/recommendations/activity/${id}`);