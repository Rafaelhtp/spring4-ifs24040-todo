package org.delcom.starter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
// Pastikan impor ini ada
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest; 
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Anotasi @SpringBootTest ini KRITIS untuk lolos JaCoCo
@SpringBootTest
class ApplicationTest {

  @Test
  @Timeout(10)
  void mainMethod_ShouldRunSpringApplication() {
    // Mock SpringApplication.run untuk test main method
    try (var mockedSpring = mockStatic(SpringApplication.class)) {
      ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
      mockedSpring.when(() -> SpringApplication.run(Application.class, new String[] {}))
          .thenReturn(mockContext);

      // Jalankan main method
      assertDoesNotThrow(() -> Application.main(new String[] {}));

      // Verify SpringApplication.run dipanggil
      mockedSpring.verify(() -> SpringApplication.run(Application.class, new String[] {}));
    }
  }

  @Test
  void contextLoads_ShouldNotThrowException() {
    // Test bahwa Spring context bisa dimuat
    assertDoesNotThrow(() -> {
      // Test basic class loading
      Class<?> clazz = Class.forName("org.delcom.starter.Application");
      assertNotNull(clazz);
    });
  }

  @Test
  void todoApplication_ShouldHaveSpringBootAnnotation() {
    // Test bahwa class memiliki annotation @SpringBootApplication
    assertNotNull(Application.class
        .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
  }

  @Test
  void todoApplication_CanBeInstantiated() {
    // Test bahwa kita bisa membuat instance Application
    assertDoesNotThrow(() -> {
      Application app = new Application();
      assertNotNull(app);
    });
  }
}

