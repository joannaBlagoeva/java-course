package bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NutritionInfoTest {

	@Test
	public void testNutritionInfoNegativeValues() {
		assertThrows(
				IllegalArgumentException.class,
				() -> new NutritionInfo(-2, 12, 12),
				"Negative macronutrient are not a legal argument.");
	}

	@Test
	public void testNutritionInfoNon100Sum() {
		assertThrows(
				IllegalArgumentException.class,
				() -> new NutritionInfo(12, 12, 12),
				"Macronutrient values do not sum 100.");
	}

	@Test
	public void testCalories() {
		int proteins = 20;
		int fats = 30;
		int carbohydrates = 50;

		NutritionInfo nutritionInfo = new NutritionInfo(proteins, fats, carbohydrates);

		int expected = proteins * MacroNutrient.PROTEIN.calories +
				fats * MacroNutrient.FAT.calories +
				carbohydrates * MacroNutrient.CARBOHYDRATE.calories;

		assertEquals(
				expected,
				nutritionInfo.calories(),
				"Total calories is the sum of MacroNutrients multiplied by the macronutients calories.");
	}


}
