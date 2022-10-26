package fa.training.components;

import fa.training.components.security.AuditorAwareImpl;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ComponentsApplication {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}
	@Bean
	public AuditorAwareImpl auditorProvider() {
		return new AuditorAwareImpl();
	}

	public static void main(String[] args) {
		SpringApplication.run(ComponentsApplication.class, args);
		System.out.println("************************************** Hello world **************************************");
	}

}
