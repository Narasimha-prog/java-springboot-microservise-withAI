package com.ln.fitness.activity_service.controller;

import com.ln.fitness.activity_service.dto.ActivityRequest;
import com.ln.fitness.activity_service.dto.ActivityResponse;
import com.ln.fitness.activity_service.service.IActivityService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class  ActivityController {

final private IActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse> tractActivity(@RequestBody ActivityRequest request,@RequestHeader("X-User-ID") String userId){
        if(userId != null){
            request.setUserId(userId);
        }
  return  ResponseEntity.ok(activityService.trackActivity(request));
    }
    @GetMapping
    public ResponseEntity<List<ActivityResponse>> getUserActivity(@RequestHeader("X-User-ID") String userId){
        return  ResponseEntity.ok(activityService.getUserActivity(userId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId){
        return  ResponseEntity.ok(activityService.getActivity(activityId));
    }


}
