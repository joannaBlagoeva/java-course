package bg.sofia.uni.fmi.mjt.myfitnesspal.diary;

import bg.sofia.uni.fmi.mjt.myfitnesspal.exception.UnknownFoodException;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfo;
import bg.sofia.uni.fmi.mjt.myfitnesspal.nutrition.NutritionInfoAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DailyFoodDiaryTest {

	@Mock
	private NutritionInfoAPI nutritionInfoAPIMock;

	@InjectMocks
	private DailyFoodDiary dailyFoodDiaryMock;

	@Test
	public void testDialyFoodDiaryAddNullMeal() {

		assertThrows(
				IllegalArgumentException.class,
				() -> dailyFoodDiaryMock.addFood(null, "test", 0),
				"Null meal is not a legal argument."
		);
	}

	@Test
	public void testDialyFoodDiaryAddNullFoodName() {

		assertThrows(
				IllegalArgumentException.class,
				() -> dailyFoodDiaryMock.addFood(Meal.BREAKFAST, " ", 0),
				"Null or blank foodName is not a legal argument."
		);
	}

	@Test
	public void testDialyFoodDiaryAddNegativeServingSize() {

		assertThrows(
				IllegalArgumentException.class,
				() -> dailyFoodDiaryMock.addFood(Meal.BREAKFAST, "test", -1),
				"Negative serving size is not a legal argument."
		);
	}

	@Test
	public void testDialyFoodDiaryAddFoodUnknownNutricionValue() throws UnknownFoodException {

		when(nutritionInfoAPIMock.getNutritionInfo("test"))
				.thenThrow(UnknownFoodException.class);

		assertThrows(
				UnknownFoodException.class,
				() -> dailyFoodDiaryMock.addFood(Meal.BREAKFAST, "test", 1),
				"FoodName has no nutricinal value."
		);
	}


	@Test
	public void testDialyFoodDiaryAddValidFoodEntry() throws UnknownFoodException {

		NutritionInfo expectedNutritionInfo = new NutritionInfo(100, 0, 0);
		String foodName = "test";
		int servingSize = 1;

		when(nutritionInfoAPIMock.getNutritionInfo(foodName))
				.thenReturn(expectedNutritionInfo);

		FoodEntry result = dailyFoodDiaryMock.addFood(Meal.BREAKFAST, foodName, servingSize);

		assertEquals(
				1,
				dailyFoodDiaryMock.getAllFoodEntries().size(),
				String.format("A food entry was not added correctly. Expected list size was $%d, but in reality is %d",
						1,
						dailyFoodDiaryMock.getAllFoodEntries().size()
				)
		);

		assertTrue(
				dailyFoodDiaryMock.getAllFoodEntries()
						.contains(result),
				"AllFoodEntries must contain the returned foodEntry"
		);
	}

	@Test
	public void testGetAllFoodEntriesIsUnmodifiable() {

		assertThrows(UnsupportedOperationException.class,
				() -> dailyFoodDiaryMock.getAllFoodEntries().add(null),
				"List is modifiable");
	}

	@Test
	public void testGetAllFoodEntriesByProteinContentIsUnmodifiable() {

		assertThrows(UnsupportedOperationException.class,
				() -> dailyFoodDiaryMock.getAllFoodEntriesByProteinContent().add(null),
				"List is modifiable");
	}

	@Test
	public void testGetAllFoodEntriesByProteinContentSortedByAscendng() throws UnknownFoodException {

		NutritionInfo test1Nutrition = new NutritionInfo(2, 0, 98);
		NutritionInfo test2Nutrition = new NutritionInfo(1, 0, 99);
		String foodTest1 = "foodTest1";
		String foodTest2 = "foodTest2";

		List<FoodEntry> expected = new ArrayList<>();
		expected.add(new FoodEntry(foodTest1, 1, test1Nutrition));
		expected.add(new FoodEntry(foodTest2, 1, test2Nutrition));

		when(nutritionInfoAPIMock.getNutritionInfo(foodTest1))
				.thenReturn(test1Nutrition);
		when(nutritionInfoAPIMock.getNutritionInfo(foodTest2))
				.thenReturn(test2Nutrition);

		dailyFoodDiaryMock.addFood(Meal.BREAKFAST, foodTest2, 1);
		dailyFoodDiaryMock.addFood(Meal.BREAKFAST, foodTest1, 1);

		List<FoodEntry> result = dailyFoodDiaryMock.getAllFoodEntriesByProteinContent();

		assertArrayEquals(expected.toArray(), result.toArray(), "Food entries are sorted by serving*ProteinContent");
	}

	@Test
	public void testGetDailyCaloriesIntakeWithNoMeals() {

		assertEquals(0, dailyFoodDiaryMock.getDailyCaloriesIntake(), "Calories intake is zero.");
	}

	@Test
	public void testGetDailyCaloriesIntakeWithZeroMeals() {

		assertEquals(0,
				dailyFoodDiaryMock.getDailyCaloriesIntake(),
				"Calorie intake is zero when no food was added."
		);
	}

	@Test
	public void testGetDailyCaloriesIntakeWithMeals() throws UnknownFoodException {

		NutritionInfo testNutrition = new NutritionInfo(0, 0, 100);

		when(nutritionInfoAPIMock.getNutritionInfo(any()))
				.thenReturn(testNutrition);

		DailyFoodDiary positiveDailyFoodDiary = new PositiveDailyFoodDiaryStub(nutritionInfoAPIMock);

		positiveDailyFoodDiary.addFood(Meal.BREAKFAST, "test1", 1);
		positiveDailyFoodDiary.addFood(Meal.LUNCH, "test2", 1);

		assertEquals(2,
				positiveDailyFoodDiary.getDailyCaloriesIntake(),
				"Calorie intake is two for the two added meals."
		);
	}

	@Test
	public void testGetDailyCaloriesIntakePerMealWithNullMeal() {

		assertThrows(IllegalArgumentException.class,
				() -> dailyFoodDiaryMock.getDailyCaloriesIntakePerMeal(null),
				"Null meal is not a valid argument"
		);
	}

	@Test
	public void testGetDailyCaloriesIntakePerMealWithZeroMeals() {

		assertEquals(0,
				dailyFoodDiaryMock.getDailyCaloriesIntakePerMeal(Meal.BREAKFAST),
				"Calorie intake is zero when no food was added."
		);
	}

	@Test
	public void testGetDailyCaloriesIntakePerMealWithMeals() throws UnknownFoodException {

		int protein = 100;
		int carbohydrates = 0;
		int fats = 0;

		NutritionInfo test1Nutrition = new NutritionInfo(carbohydrates, fats, protein);
		String foodTest1 = "foodTest1";

		when(nutritionInfoAPIMock.getNutritionInfo(any()))
				.thenReturn(test1Nutrition);

		dailyFoodDiaryMock.addFood(Meal.BREAKFAST, foodTest1, 1);
		dailyFoodDiaryMock.addFood(Meal.BREAKFAST, foodTest1, 1);

		assertEquals(test1Nutrition.calories() * 2,
				dailyFoodDiaryMock.getDailyCaloriesIntakePerMeal(Meal.BREAKFAST),
				"Calorie intake is zero when no food was added."
		);
	}

	class PositiveDailyFoodDiaryStub extends DailyFoodDiary {
		public PositiveDailyFoodDiaryStub(NutritionInfoAPI nutritionInfoAPI) {
			super(nutritionInfoAPI);
		}

		@Override
		public double getDailyCaloriesIntakePerMeal(Meal meal) {
			return 1;
		}
	}
}
