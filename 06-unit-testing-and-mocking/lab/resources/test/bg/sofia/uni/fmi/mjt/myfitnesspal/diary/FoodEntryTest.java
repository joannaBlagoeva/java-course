package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FoodEntryTest {

	@Test
	public void testFoodEntryNullFoodThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> {
					new FoodEntry(null, 0, new NutritionInfo(100, 0, 0));
				},
				"Null food is not a legal argument."
		);
	}

	@Test
	public void testFoodEntryBlankFoodThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> {
					new FoodEntry(" ", 0, new NutritionInfo(100, 0, 0));
				},
				"Blank food is not a legal argument."
		);
	}

	@Test
	public void testFoodEntryNullNutritionalInfoThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> {
					new FoodEntry("test", 0, null);
				},
				"Null nutritionInfo is not a legal argument"
		);
	}

	@Test
	public void testFoodEntryNegativeServingSizeInfoThrowsException() {
		assertThrows(
				IllegalArgumentException.class,
				() -> {
					new FoodEntry("test", -1, new NutritionInfo(100,0,0));
				},
				"Negative serving size is not a legal argument"
		);
	}

}
