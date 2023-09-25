package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exceptions.MyApplicationExceptions;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new MyApplicationExceptions(String.format("User %s not found", userId), HttpStatus.NOT_FOUND));

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getUsersByIds(Set<Long> ids, Pageable page) {
        return ids == null || ids.isEmpty()
                ? userRepository.findAll(page).getContent()
                : userRepository.findAllByIdIn(ids, page);
    }
}