package vn.aeoc.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"vn.aeoc.rbac, vn.aeoc"})
public class EcomApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcomApplication.class, args);
    }

}
