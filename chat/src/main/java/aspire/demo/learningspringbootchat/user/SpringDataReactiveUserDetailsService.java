package aspire.demo.learningspringbootchat.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
public class SpringDataReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public SpringDataReactiveUserDetailsService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername()).password(passwordEncoder.encode(u.getPassword()))
                        .authorities(AuthorityUtils.createAuthorityList(u.getRoles()))
                        .build()
                );
    }

}
