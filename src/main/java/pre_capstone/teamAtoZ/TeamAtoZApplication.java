package pre_capstone.teamAtoZ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class TeamAtoZApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeamAtoZApplication.class, args);
	}
}
