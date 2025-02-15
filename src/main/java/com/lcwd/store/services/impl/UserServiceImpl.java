package com.lcwd.store.services.impl;

import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.dtos.RoleDto;
import com.lcwd.store.dtos.ScreenPermissionRequest;
import com.lcwd.store.dtos.UserDto;
import com.lcwd.store.entities.Role;
import com.lcwd.store.entities.ScreenPermission;
import com.lcwd.store.entities.User;
import com.lcwd.store.exceptions.ResourceNotFoundException;
import com.lcwd.store.helper.HelperUtils;
import com.lcwd.store.repositories.RoleRepository;
import com.lcwd.store.repositories.UserRepository;
import com.lcwd.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    @Value("${normal.role.id}")
    private String normalRoleId;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = dtoToEntity(userDto);
        Role role = roleRepository.findById(normalRoleId).get();
        user.setRoles(Collections.singleton(role));
        User savedUser = userRepository.save(user);
        UserDto newDto = entityToDto(user);
        return newDto;
    }

    private UserDto entityToDto(User user) {

        return modelMapper.map(user,UserDto.class) ;
    }
 private RoleDto roleToDto(Role user) {

        return modelMapper.map(user,RoleDto.class) ;
    }

    private User dtoToEntity(UserDto userDto) {
       return modelMapper.map(userDto,User.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        if(userDto.getPassword()!=null && !userDto.getPassword().isEmpty() && !user.getPassword().equals(userDto.getPassword()))
        {
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setRoles( userDto.getRoles().stream()
                .map(roleDto -> modelMapper.map(roleDto, Role.class))
                .collect(Collectors.toSet()));
        user.setImageName(userDto.getImageName());
        User updatedUser = userRepository.save(user);
        UserDto updatedUserDto = entityToDto(updatedUser);
        return updatedUserDto;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return entityToDto(user);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
        // delete user profile image
        String fullPath = imageUploadPath + user.getImageName();
        Path path = Paths.get(fullPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
        PageableResponse<UserDto> response = HelperUtils.getPageableResponse(page, UserDto.class);
        return response;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> userDtos = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public Optional<User> getUserByEmailForGoogleAuth(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateScreenPermissions(String userId, Set<ScreenPermissionRequest> screenPermissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update or create ScreenPermission entities based on the request
        Set<ScreenPermission> screenPermissionEntities = screenPermissions.stream()
                .map(request -> {
                    ScreenPermission screenPermission = user.getScreenPermissions().stream()
                            .filter(existingPermission -> existingPermission.getScreenName().equals(request.getScreenName()))
                            .findFirst()
                            .orElse(new ScreenPermission());

                    screenPermission.setScreenName(request.getScreenName());
                    screenPermission.setCanRead(request.isCanRead());
                    screenPermission.setCanWrite(request.isCanWrite());
                    screenPermission.setCanUpdate(request.isCanUpdate());
                    screenPermission.setCanDelete(request.isCanDelete());
                    User user1=new User();
                    user1.setUserId(userId);
screenPermission.setUser(user1);
                    return screenPermission;
                })
                .collect(Collectors.toSet());

        user.setScreenPermissions(screenPermissionEntities);

        userRepository.save(user);
    }

    @Override
    public List<RoleDto> getRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleDto> roleDtos = roles.stream().map(role -> roleToDto(role)).collect(Collectors.toList());
        return roleDtos;
    }

    @Override
    public PageableResponse<UserDto> getAllUsersByRole(int pageNumber, int pageSize, String sortBy, String sortDir, String roleName) {
        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? (Sort.by(sortBy).ascending()) : (Sort.by(sortBy).descending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Role role =roleRepository.findByRoleName(roleName).orElseThrow(()-> new ResourceNotFoundException("Role Not found with role name"+roleName));
        Page<User> page = userRepository.findByRoles(pageable,role);
        PageableResponse<UserDto> response = HelperUtils.getPageableResponse(page, UserDto.class);
        return response;
    }
}
