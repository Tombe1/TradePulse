package com.tradepulse.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
	// זהו ה"טסט" שלנו: יצירת מניה ושמירה שלה ב-DB
	/* @Bean
	CommandLineRunner commandLineRunner(AssetRepository repository, StockPriceService stockService) { // הוספנו את ה-Service כפרמטר
		return args -> {
			String symbol = "PLTR";

			System.out.println("Fetching real price for " + symbol + "...");
			// 1. שליפת המחיר האמיתי מהאינטרנט
			var realPrice = stockService.fetchPrice(symbol);

			// 2. יצירת האובייקט עם המחיר העדכני
			Asset pltr = new Asset(
					symbol,
					"Palantir Technologies",
					realPrice, // המחיר האמיתי!
					LocalDateTime.now()
			);

			// 3. שמירה ב-DB
			repository.save(pltr);

			System.out.println(">>> SUCCESS: Saved " + symbol + " at price: $" + realPrice);
		};
	}
	 */
}
