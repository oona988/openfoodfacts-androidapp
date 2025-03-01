package openfoodfacts.github.scrachx.openfood

import android.content.Context
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import openfoodfacts.github.scrachx.openfood.features.search.ProductSearchActivity
import openfoodfacts.github.scrachx.openfood.models.SearchInfo
import openfoodfacts.github.scrachx.openfood.test.ScreenshotActivityTestRule
import openfoodfacts.github.scrachx.openfood.test.ScreenshotParameter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Take screenshots...
 */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class TakeScreenshotIncompleteProductsTest : AbstractScreenshotTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Rule
    var incompleteRule = ScreenshotActivityTestRule(
            ProductSearchActivity::class.java,
            "incompleteProducts",
            context
    )

    @Test
    fun testTakeScreenshot() = startForAllLocales(createSearchIntent, listOf(incompleteRule), context)

    private val createSearchIntent: (ScreenshotParameter) -> List<Intent?> = {
        listOf(Intent(context, ProductSearchActivity::class.java).apply {
            putExtra(ProductSearchActivity.SEARCH_INFO, SearchInfo.emptySearchInfo())
        })
    }
}
