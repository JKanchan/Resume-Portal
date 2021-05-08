package com.learning.resumeportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.learning.resumeportal.dao.UserProfileRepository;
import com.learning.resumeportal.dao.UserRepository;
import com.learning.resumeportal.model.Education;
import com.learning.resumeportal.model.Job;
import com.learning.resumeportal.model.NewUser;
import com.learning.resumeportal.model.User;
import com.learning.resumeportal.model.UserProfile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    UserProfileRepository userProfileRepository;
    
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }
    @GetMapping("/register")
    public String register(Model model) {
    	NewUser newUser = new NewUser();
    	model.addAttribute(newUser);
        return "register";
    }
    
    @PostMapping("/register")
    public String submitForm(@ModelAttribute("newUser") NewUser newUser) {
    	User user = new User();
    	user.setUserName(newUser.getUserName());
    	user.setPassword(newUser.getPassword());
    	user.setActive(true);
    	user.setRoles("USER");
    	userRepository.save(user);
    	
    	UserProfile userProfile = new UserProfile();
    	userProfile.setUserName(newUser.getUserName());
    	userProfile.setFirstName(newUser.getFirstName());
    	userProfile.setLastName(newUser.getLastName());
    	userProfile.setDesignation(newUser.getDesignation());
    	userProfile.setPhone(newUser.getPhone());
    	userProfile.setEmail(newUser.getEmail());
    	userProfile.setTheme(1);
    	userProfile.setSummary("Summary");
    	userProfileRepository.save(userProfile);
    	 return "redirect:/login";
    }

    @GetMapping("/edit")
    public String edit(Model model, Principal principal, @RequestParam(required = false) String jobs, @RequestParam(required = false) String educations, @RequestParam(required = false) String skills) {
        String userId = principal.getName();
        model.addAttribute("userId", userId);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userId);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userId));
        UserProfile userProfile = userProfileOptional.get();
        if ("job".equals(jobs)) {
            userProfile.getJobs().add(new Job());
        } 
        if ("education".equals(educations)) {
            userProfile.getEducations().add(new Education());
        } 
        if ("skill".equals(skills)) {
            userProfile.getSkills().add("");
        }

        model.addAttribute("userProfile", userProfile);
        return "profile-edit";
    }

    @GetMapping("/delete")
    public String delete(Model model, Principal principal, @RequestParam String type, @RequestParam int index) {
        String userId = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userId);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userId));
        UserProfile userProfile = userProfileOptional.get();
        if ("job".equals(type)) {
            userProfile.getJobs().remove(index);
        } else if ("education".equals(type)) {
            userProfile.getEducations().remove(index);
        } else if ("skill".equals(type)) {
            userProfile.getSkills().remove(index);
        }
        userProfileRepository.save(userProfile);
        return "redirect:/edit";
    }

    @PostMapping("/edit")
    public String postEdit(Model model, Principal principal, @ModelAttribute UserProfile userProfile) {
        String userName = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userName);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userName));
        UserProfile savedUserProfile = userProfileOptional.get();
        userProfile.setId(savedUserProfile.getId());
        userProfile.setUserName(userName);
        userProfileRepository.save(userProfile);
        return "redirect:/view/" + userName;
    }

    @GetMapping("/view/{userId}")
    public String view(Principal principal, @PathVariable String userId, Model model) {
        if (principal != null && principal.getName() != "") {
            boolean currentUsersProfile = principal.getName().equals(userId);
            model.addAttribute("currentUsersProfile", currentUsersProfile);
        }
        String userName = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userId);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userId));

        model.addAttribute("userId", userId);
        UserProfile userProfile = userProfileOptional.get();
        model.addAttribute("userProfile", userProfile);
        System.out.println(userProfile.getJobs());

        return "profile-templates/" + userProfile.getTheme() + "/index";
    }

}
