package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FoodEntryProteinContentComparatorTest {

	private FoodEntryProteinContentComparator foodEntryProteinContentComparator;

	@BeforeEach
	void SetUp() {
		foodEntryProteinContentComparator = new FoodEntryProteinContentComparator();
	}

	@Test
	public void testFoodEntryProteinContentComparatorEquals() {

		FoodEntry o1 = new FoodEntry("o1", 1, new NutritionInfo(98, 0, 2));
		FoodEntry o2 = new FoodEntry("o2", 2, new NutritionInfo(99, 0, 1));

		int result = foodEntryProteinContentComparator.compare(o1, o2);
		assertEquals(result, 0, "Food entries are equal");
	}

	@Test
	public void testFoodEntryProteinContentComparatorLargerThan() {

		FoodEntry o1 = new FoodEntry("o1", 1, new NutritionInfo(98, 0, 2));
		FoodEntry o2 = new FoodEntry("o2", 1, new NutritionInfo(99, 0, 1));

		int result = foodEntryProteinContentComparator.compare(o1, o2);
		assertEquals(result, 1, "First was food entry is larger.");
	}

	@Test
	public void testFoodEntryProteinContentComparatorLessThan() {

		FoodEntry o1 = new FoodEntry("o1", 1, new NutritionInfo(99, 0, 1));
		FoodEntry o2 = new FoodEntry("o2", 2, new NutritionInfo(99, 0, 1));

		int result = foodEntryProteinContentComparator.compare(o1, o2);
		assertEquals(result, -1, "First food entry is lesser.");
	}

}
