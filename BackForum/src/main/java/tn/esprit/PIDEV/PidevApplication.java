package tn.esprit.PIDEV;


import javax.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
public class PidevApplication {

	public static void main(String[] args) {
		SpringApplication.run(PidevApplication.class, args);
	}

	@PreDestroy
	public void onExit() {
		String filePath1 = "C:\\Users\\MSII\\Desktop\\PI\\ACTUAL WORK\\Front\\FontPiDev\\src\\assets\\linkedin-jobs-noduplicates.csv";
		String filePath2 = "C:\\Users\\MSII\\Desktop\\4SAE\\Pidevbackspring-main\\Pidevbackspring-main\\linkedin-jobs.csv";
		deleteFile(filePath1);
		deleteFile(filePath2);
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				System.out.println("File " + filePath + " deleted successfully.");
			} else {
				System.out.println("Failed to delete file " + filePath);
			}
		} else {
			System.out.println("File " + filePath + " does not exist.");
		}
	}
}
