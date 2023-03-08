package mate.academy.secure.impl;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.secure.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> user = userService.findByEmail(email);
        if (userService.findByEmail(email).isEmpty()
                || !HashUtil.hashPassword(password, user.get().getSalt())
                .equals(user.get().getPassword())) {
            throw new AuthenticationException("Email or password incorrect!! Email " + email);
        }
        return user.get();
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("User with this email already registered");
        }
        if (password.isEmpty() || email.isEmpty()) {
            throw new RegistrationException("Incorrect data entered");
        }
        User user = new User();
        user.setEmail(email);
        user.setSalt(HashUtil.generateSalt());
        user.setPassword(HashUtil.hashPassword(password, user.getSalt()));
        return userService.add(user);
    }
}
